package com.moment.moment_BE.service;


import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moment.moment_BE.dto.request.FriendInviteRequest;
import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.entity.Profile;
import com.moment.moment_BE.enums.FriendStatus;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.FriendErrorCode;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.ProfileMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.FriendRepository;
import com.moment.moment_BE.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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

        account.setCreatedAt(LocalDateTime.now());
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

        Pageable pageable = PageRequest.of(0,5);
        List<Account> listAccount = accountRepository.findByUserNameContainingOrPhoneNumberContainingOrEmailContainingOrProfile_NameContainingAndStatus(valueSearch,
                valueSearch,
                valueSearch,
                valueSearch,
                1,pageable);
        List<AccountResponse> responseList = new ArrayList<>();
        for (Account account : listAccount) {
            Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(accountLogin.getId(), account.getId()).orElse(null);
            if (friend == null)
                responseList.add(toAccountResponse(account, "none", null, false));
            else
                responseList.add(toAccountResponse(account, friend.getStatus(), friend.getRequestedAt(), friend.getAccountInitiator() == accountLogin));

        }
        return responseList;
    }

    public AccountResponse toAccountResponse(Account account, String status, LocalDateTime requestedAt, boolean isInitiator) {
        AccountResponse accountResponse = accountMapper.toAccountResponse(account);
        switch (status) {
            case "accepted":
                accountResponse.setFriendStatus(FriendStatus.accepted);
                break;
            case "pending":
                if (isInitiator) accountResponse.setFriendStatus(FriendStatus.sent);
                else accountResponse.setFriendStatus(FriendStatus.invited);
                break;
            default:
                accountResponse.setFriendStatus(FriendStatus.none);
                break;
        }
        accountResponse.setUrlPhoto(photoService.getUrlAvtAccount(account.getId()));
        accountResponse.setUrlProfile("/" + account.getUserName());
        accountResponse.setRequestedAt(requestedAt + "");
        return accountResponse;
    }

    public List<AccountResponse> getListAccountFriend(int status) {
        Account account = authenticationService.getMyAccount(status);
        List<String> listStatus = new ArrayList<>();
        listStatus.add("accepted");
        listStatus.add("pending");

        Pageable pageable = PageRequest.of(0, 10);

        List<Friend> friends = friendRepository.findByAccountUser_IdAndStatusIn(account.getId(), listStatus, pageable);

        List<AccountResponse> listAccountResponse = new ArrayList<>();

        for (Friend friend : friends) {
            AccountResponse accountResponse = toAccountResponse(friend.getAccountFriend(), friend.getStatus(), friend.getRequestedAt(), friend.getAccountInitiator() == account);
            listAccountResponse.add(accountResponse);
        }

        return listAccountResponse;
    }

    @Transactional
    public AccountResponse addFriend(FriendInviteRequest friendInviteRequest, int status) {
        Account account = authenticationService.getMyAccount(status);
        Account accountFriend = findByIdAndStatus(friendInviteRequest.getAccountFriendId(), 1);
        if(account==accountFriend ) {throw new AppException(FriendErrorCode.FRIEND_ERROR);}

        Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                account.getId(),
                accountFriend.getId()).orElse(null);
        if (friend != null) {
            switch (friend.getStatus()) {
                case "accepted" -> throw new AppException(FriendErrorCode.FRIEND_ACCEPTED);
                case "blocked" -> throw new AppException(FriendErrorCode.FRIEND_BLOCKED);
                case "pending" -> throw new AppException(FriendErrorCode.FRIEND_PENDING);
                default -> throw new AppException(FriendErrorCode.FRIEND_ERROR);
            }
        } else {
            friend = new Friend();
            friend.setAccountUser(account);
            friend.setAccountFriend(accountFriend);
            friend.setRequestedAt(getCurrentTimeInSystemLocalTime());
            friend.setStatus("pending");
            friend.setAccountInitiator(account);
            friendRepository.save(friend);

            Friend friendRP = new Friend();
            friendRP.setAccountUser(accountFriend);
            friendRP.setAccountFriend(account);
            friendRP.setRequestedAt(getCurrentTimeInSystemLocalTime());
            friendRP.setStatus("pending");
            friendRP.setAccountInitiator(account);
            friendRepository.save(friendRP);
        }
        return toAccountResponse(friend.getAccountFriend(), friend.getStatus(), friend.getRequestedAt(), friend.getAccountInitiator() == account);
    }

    @Transactional
    public AccountResponse changeStatusFriend(FriendInviteRequest friendInviteRequest, int status) {
        Account account = authenticationService.getMyAccount(status);
        Account accountFriend = findByIdAndStatus(friendInviteRequest.getAccountFriendId(), 1);

        Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                account.getId(),
                accountFriend.getId()).orElseThrow(
                () -> new AppException(FriendErrorCode.FRIEND_NOT_FOUND));
        Friend friendRP = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                accountFriend.getId(),
                account.getId()).orElseThrow(
                () -> new AppException(FriendErrorCode.FRIEND_NOT_FOUND));
        if (friendInviteRequest.getStatus() == FriendStatus.deleted) {

            friendRepository.deleteById(friend.getId());
            friendRepository.deleteById(friendRP.getId());
            return null;
        }

        if (friendInviteRequest.getStatus() == FriendStatus.blocked) {
            friend.setStatus("blocked");
            friendRP.setStatus("blocked");
        }
        if (friendInviteRequest.getStatus() == FriendStatus.accepted) {
            friend.setStatus("accepted");
            friendRP.setStatus("accepted");
        }
            if (friendInviteRequest.getStatus() == FriendStatus.pending) {
                friend.setStatus("pending");
                friendRP.setStatus("pending");
            }

        friend.setAcceptedAt(getCurrentTimeInSystemLocalTime());
        friendRP.setAcceptedAt(getCurrentTimeInSystemLocalTime());
        friendRepository.save(friend);
        friendRepository.save(friendRP);
        return toAccountResponse(friend.getAccountFriend(), friend.getStatus(), friend.getRequestedAt(), friend.getAccountInitiator() == account);
    }
}
