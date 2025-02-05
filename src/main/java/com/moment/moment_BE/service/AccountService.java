package com.moment.moment_BE.service;


import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.moment.moment_BE.dto.response.AccountResult;
import com.moment.moment_BE.dto.response.AccountSettingResponse;
import com.moment.moment_BE.utils.DateTimeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moment.moment_BE.dto.request.FriendFilterRequest;
import com.moment.moment_BE.dto.request.FriendInviteRequest;
import com.moment.moment_BE.dto.request.ChangePasswordRequest;
import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.dto.request.AccountInfoRequest;
import com.moment.moment_BE.entity.Profile;
import com.moment.moment_BE.enums.FriendStatus;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.FriendErrorCode;
import com.moment.moment_BE.exception.InValidErrorCode;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.ProfileMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.FriendRepository;
import com.moment.moment_BE.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    ProfileMapper profileMapper;
    AuthenticationService authenticationService;
    ProfileRepository profileRepository;
    PhotoService photoService;
    FriendRepository friendRepository;
    SimpMessagingTemplate messagingTemplate;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    //    login ne
    public AuthenticationResponse register(RegisterRequest request) {
        if (accountRepository.existsByUserName(request.getUserName())) {
            throw new AppException(AccountErrorCode.USER_NAME_EXISTED);
        }

//        khoi tao gan du lieu
        Account account = accountMapper.toAccount(request);
        Profile profile = profileMapper.toProfile(request);

        account.setCreatedAt(getCurrentTimeInSystemLocalTime());
        account.setStatus(1);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        account.setProfile(profile);
        profile.setAccount(account);

//        save
        try {
            profileRepository.save(profile);
        } catch (Exception e) {
            throw new AppException(AccountErrorCode.SAVE_USER_FAIL);
        }

//        tao token khi save thanh cong
        var token = authenticationService.generateToken(account);

        return
                AuthenticationResponse.builder()
                        .token(token)
                        .authenticated(true)
                        .build();

    }

    public Account findByIdAndStatus(String accountId, int status) {
        return accountRepository.findByIdAndStatus(accountId, status).orElseThrow(
                () -> new AppException(AccountErrorCode.USER_NOT_FOUND)
        );
    }

    public List<AccountResponse> searchAccount(String valueSearch, int status) {
        Account accountLogin = authenticationService.getMyAccount(status);

        Pageable pageable = PageRequest.of(0, 5);
        List<Account> listAccount = accountRepository.findByUserNameContainingOrPhoneNumberContainingOrEmailContainingOrProfile_NameContainingAndStatus(
                valueSearch, valueSearch, valueSearch, valueSearch, 1, pageable
        );

        // Lấy tất cả Friend liên quan
        List<String> accountIds = listAccount.stream()
                .map(Account::getId)
                .collect(Collectors.toList());

        List<Friend> friends = friendRepository.findByAccountUser_IdAndAccountFriend_IdInAndStatusNot(
                accountLogin.getId(), accountIds, "blocked"
        );

        // Tạo Map để tra cứu nhanh
        Map<String, Friend> friendMap = friends.stream()
                .collect(Collectors.toMap(friend -> friend.getAccountFriend().getId(), friend -> friend));

        // Xây dựng response list
        List<AccountResponse> responseList = new ArrayList<>();
        for (Account account : listAccount) {
            if (account == accountLogin) {
                responseList.add(toAccountResponse(account, "me", null, false));
            } else {
                Friend friend = friendMap.get(account.getId());
                if (friend == null) {
                    responseList.add(toAccountResponse(account, "none", null, false));
                } else {
                    responseList.add(toAccountResponse(
                            account,
                            friend.getStatus(),
                            friend.getRequestedAt(),
                            friend.getAccountInitiator() == accountLogin
                    ));
                }

            }
        }

        return responseList;
    }

    @Transactional
    public AccountResult getListAccountFriend(int status, FriendStatus friendStatus, FriendFilterRequest friendFilterRequest) {
        Account account = authenticationService.getMyAccount(status);

        LocalDateTime acceptedAt = Optional.ofNullable(friendFilterRequest.getTime())
                .map(DateTimeUtils::convertUtcToUserLocalTime)
                .orElseThrow(() -> new AppException(InValidErrorCode.TIME_ZONE_INVALID));

        Pageable pageable = PageRequest.of(friendFilterRequest.getPageCurrent(), 10);

        List<Friend> friends = getFriendsByStatus(account, friendStatus, acceptedAt, pageable);


        return AccountResult.builder()
                .countAccountFriend(countFriend(account.getId(), friendStatus))
                .accountResponseList(
                        friends.stream()
                                .map(friend -> toAccountResponse(
                                        friend.getAccountFriend(),
                                        friend.getStatus(),
                                        friend.getRequestedAt(),
                                        friend.getAccountInitiator() == account)
                                )
                                .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public AccountResult getListAccountFriendReceivedRecent(int status) {
        Account account = authenticationService.getMyAccount(status);

        Pageable pageable = PageRequest.of(0, 3);

        List<Friend> friends = friendRepository.findByAccountUser_IdAndAccountInitiator_IdNotAndStatusAndRequestedAtBetweenOrderByRequestedAtDesc(
                account.getId(), account.getId(), "pending", getCurrentTimeInSystemLocalTime().minusMonths(3), getCurrentTimeInSystemLocalTime(), pageable
        );


        return AccountResult.builder()
                .accountResponseList(
                        friends.stream()
                                .map(friend -> toAccountResponse(
                                        friend.getAccountFriend(),
                                        friend.getStatus(),
                                        friend.getRequestedAt(),
                                        friend.getAccountInitiator() == account)
                                )
                                .collect(Collectors.toList()))
                .build();
    }

    private List<Friend> getFriendsByStatus(Account account, FriendStatus friendStatus, LocalDateTime acceptedAt, Pageable pageable) {
        return switch (friendStatus) {
            case accepted ->
                    friendRepository.findByAccountUser_IdAndStatusAndAcceptedAtLessThanEqualOrderByAcceptedAtDesc(
                            account.getId(), "accepted", acceptedAt, pageable
                    );
            case sent ->
                    friendRepository.findByAccountUser_IdAndAccountInitiator_IdAndStatusAndRequestedAtLessThanEqualOrderByRequestedAtDesc(
                            account.getId(), account.getId(), "pending", acceptedAt, pageable
                    );
            case received ->
                    friendRepository.findByAccountUser_IdAndAccountInitiator_IdNotAndStatusAndRequestedAtLessThanEqualOrderByRequestedAtDesc(
                            account.getId(), account.getId(), "pending", acceptedAt, pageable
                    );
            default -> Collections.emptyList();
        };
    }

    public FriendStatus determineFriendStatus(String status, boolean isInitiator) {
        return switch (status) {
            case "me" -> FriendStatus.me;
            case "accepted" -> FriendStatus.accepted;
            case "pending" -> isInitiator ? FriendStatus.sent : FriendStatus.received;
            default -> FriendStatus.none;
        };
    }

    public AccountResponse toAccountResponse(Account account, String status, LocalDateTime requestedAt, boolean isInitiator) {
        AccountResponse accountResponse = accountMapper.toAccountResponse(account);
        accountResponse.setFriendStatus(determineFriendStatus(status, isInitiator));
        accountResponse.setUrlPhoto(photoService.getUrlAvtAccount(account.getId()));
        accountResponse.setUrlProfile("/" + account.getUserName());
        accountResponse.setRequestedAt(String.valueOf(requestedAt));
        return accountResponse;
    }

    private String CheckStatusFriend(Friend friend) {
        String status = friend.getStatus();
        if (Objects.equals(status, "pending")) {
            if (friend.getAccountInitiator() == friend.getAccountUser())
                return "sent";
            else return "received";
        }
        return status;
    }

    @Transactional
    public AccountResponse addFriend(FriendInviteRequest friendInviteRequest, int status) {
        Account account = authenticationService.getMyAccount(status);
        Account accountFriend = findByIdAndStatus(friendInviteRequest.getAccountFriendId(), status);

        // loi ket ban voi chinh minh
        if (account == accountFriend) {
            throw new AppException(FriendErrorCode.FRIEND_ERROR);
        }

        //kiem tra da co trong bang friend chua
        Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                account.getId(),
                accountFriend.getId()).orElse(null);

        //neu co trong bang friend thi kiem tra dang trong truong hop nao
        if (friend != null) {
            Map<String, FriendErrorCode> statusErrorMap = Map.of(
                    "accepted", FriendErrorCode.FRIEND_ACCEPTED,
                    "blocked", FriendErrorCode.FRIEND_BLOCKED,
                    "received", FriendErrorCode.FRIEND_RECEIVED,
                    "sent", FriendErrorCode.FRIEND_SENT
            );

            throw new AppException(statusErrorMap.getOrDefault(CheckStatusFriend(friend), FriendErrorCode.FRIEND_ERROR));
        }

        friendRepository.save(createFriend(account, accountFriend, account));
        friendRepository.save(createFriend(accountFriend, account, account));

        pushRequestFriendSocket("pending", account, accountFriend.getUserName());
        return toAccountResponse(accountFriend, "pending", getCurrentTimeInSystemLocalTime(), true);
    }

    private Friend createFriend(Account accountUser, Account accountFriend, Account accountInitiator) {
        Friend friend = new Friend();
        friend.setAccountUser(accountUser);
        friend.setAccountFriend(accountFriend);
        friend.setRequestedAt(getCurrentTimeInSystemLocalTime());
        friend.setStatus("pending");
        friend.setAccountInitiator(accountInitiator);
        return friend;
    }

    @Transactional
    public AccountResponse changeStatusFriend(FriendInviteRequest friendInviteRequest, int status) {
        Account account = authenticationService.getMyAccount(status);
        Account accountFriend = findByIdAndStatus(friendInviteRequest.getAccountFriendId(), 1);

        // loi ket ban voi chinh minh
        if (account == accountFriend) {
            throw new AppException(FriendErrorCode.FRIEND_ERROR);
        }

        Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                account.getId(),
                accountFriend.getId()).orElseThrow(
                () -> new AppException(FriendErrorCode.FRIEND_NOT_FOUND));

        Friend friendRP = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                accountFriend.getId(),
                account.getId()).orElseThrow(
                () -> new AppException(FriendErrorCode.FRIEND_NOT_FOUND));

        FriendStatus newStatus = friendInviteRequest.getStatus();

        switch (newStatus) {
            case accepted -> {
                if (Objects.equals(friend.getStatus(), "accepted") && Objects.equals(friendRP.getStatus(), "accepted")) {
                    throw new AppException(FriendErrorCode.FRIEND_ACCEPTED);
                }
                if (Objects.equals(friend.getStatus(), "pending") && Objects.equals(friendRP.getStatus(), "pending")) {
                    updateFriendStatus(friend, friendRP, "accepted");
                }

            }

            case blocked -> {
                if (Objects.equals(friend.getStatus(), "blocked") && Objects.equals(friendRP.getStatus(), "blocked")) {
                    throw new AppException(FriendErrorCode.FRIEND_BLOCKED);
                }
                updateFriendStatus(friend, friendRP, "blocked");
            }
            case deleted -> {
                if (
                        (
                                Objects.equals(friend.getStatus(), "pending") &&
                                        Objects.equals(friendRP.getStatus(), "pending"
                                        )
                        ) ||
                                (Objects.equals(friend.getStatus(), "accepted") && Objects.equals(friendRP.getStatus(), "accepted"))) {
                    friendRepository.deleteAll(List.of(friend, friendRP));
                    return null;
                }
            }
            default -> throw new AppException(FriendErrorCode.FRIEND_ERROR);
        }

        friendRepository.save(friend);
        friendRepository.save(friendRP);

        return toAccountResponse(friend.getAccountFriend(), friend.getStatus(), friend.getRequestedAt(), friend.getAccountInitiator() == account);
    }

    private void updateFriendStatus(Friend friend, Friend friendRP, String status) {
        friend.setStatus(status);
        friendRP.setStatus(status);
        if (status.equals("accepted")) {
            friend.setAcceptedAt(getCurrentTimeInSystemLocalTime());
            friendRP.setAcceptedAt(getCurrentTimeInSystemLocalTime());
        }
    }

    public void pushRequestFriendSocket(String status, Account accountFriend, String userName) {
        AccountResponse accountResponse = toAccountResponse(accountFriend, status, getCurrentTimeInSystemLocalTime(), false);
        messagingTemplate.convertAndSendToUser(userName, "/queue/friend", accountResponse);
    }

    public int countFriend(String accountId, FriendStatus status) {
        if (status == FriendStatus.accepted)
            return friendRepository.countFriend(accountId, "accepted");
        if (status == FriendStatus.sent)
            return friendRepository.countFriendSent(accountId, "pending");
        if (status == FriendStatus.received)
            return friendRepository.countFriendReceived(accountId, "pending");
        return 0;
    }

    //    -----------------------------------------------------
    public AccountSettingResponse getAccountInfo() {
        // Lấy tên người dùng hiện tại từ ngữ cảnh bảo mật
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();

        // Lấy thông tin Profile dựa trên userName hiện tại
        Profile profile = profileRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Xây dựng phản hồi AccountResponse từ Profile
        return AccountSettingResponse.builder()
                .name(profile.getName())
                .email(profile.getAccount().getEmail())
                .userName(profile.getAccount().getUserName())
                .birthday(profile.getBirthday())
                .sex(profile.getSex())
                .phoneNumber(profile.getAccount().getPhoneNumber())
                .address(profile.getAddress())
                .build();
    }

    public void updateAccountInfo(String userName, AccountInfoRequest updateRequest) {
        // Kiểm tra sự tồn tại của Profile
        Profile profile = profileRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Cập nhật thông tin Profile
        profileRepository.updateProfileByUserName(
                updateRequest.getName(),
                updateRequest.getBirthday(),
                updateRequest.getSex(),
                updateRequest.getAddress(),
                userName
        );
    }

    public void changUserName(String currentUserName, String newUserName) {
        if (accountRepository.existsByUserName(newUserName)) {
            throw new AppException(AccountErrorCode.USER_NAME_EXISTED);
        }
        Account account = accountRepository.findByUserNameAndStatus(currentUserName, 1)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));
        account.setUserName(newUserName);
        accountRepository.save(account);
    }

    public void changePassword(String userName, ChangePasswordRequest request) {
        // Tìm tài khoản dựa trên tên người dùng
        Account account = accountRepository.findByUserNameAndStatus(userName, 1)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu cũ
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new AppException(AccountErrorCode.OLD_PASSWORD_INCORRECT);
        }

        // Kiểm tra nếu mật khẩu cũ và mật khẩu mới giống nhau
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(AccountErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        // Cập nhật mật khẩu mới
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Lưu tài khoản vào cơ sở dữ liệu
        try {
            accountRepository.save(account);
        } catch (Exception e) {
            throw new AppException(AccountErrorCode.SAVE_PASSWORD_FAIL);
        }
    }


}
