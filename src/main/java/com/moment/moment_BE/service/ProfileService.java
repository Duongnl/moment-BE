package com.moment.moment_BE.service;

import com.moment.moment_BE.controller.AccountController;
import com.moment.moment_BE.controller.PhotoController;
import com.moment.moment_BE.dto.request.ProfileFilterRequest;
import com.moment.moment_BE.dto.response.PhotoResponse;
import com.moment.moment_BE.dto.response.ProfileResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.entity.Photo;
import com.moment.moment_BE.entity.Profile;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.InValidErrorCode;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.PhotoMapper;
import com.moment.moment_BE.mapper.ProfileMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.FriendRepository;
import com.moment.moment_BE.repository.PhotoRepository;
import com.moment.moment_BE.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.moment.moment_BE.utils.DateTimeUtils.convertUtcToUserLocalTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileMapper profileMapper;
    ProfileRepository profileRepository;
    AccountMapper accountMapper;
    AccountController accountController;
    AccountService accountService; // Thêm AccountService
    AccountRepository accountRepository;
    PhotoMapper photoMapper;
    PhotoController photoController;
    PhotoRepository photoRepository;
    PhotoService photoService;
    AuthenticationService authenticationService;
    FriendRepository friendRepository;

    public ProfileResponse getProfileByUserName(ProfileFilterRequest profileFilterRequest) {
        // Lấy thông tin tài khoản đang đăng nhập
        var context = SecurityContextHolder.getContext();
        String currentUsername = context.getAuthentication().getName();
        Account currentAccount = accountRepository.findByUserNameAndStatus(currentUsername, 1)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Lấy thông tin profile của tài khoản cần tìm
        Profile profile = profileRepository.findByAccount_UserName(profileFilterRequest.getUserName());
        if (profile == null) {
            throw new AppException(AccountErrorCode.USER_NOT_FOUND);
        }

        // Tạo đối tượng response
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setIdAccount(profile.getAccount().getId());
        profileResponse.setId(profile.getId());
        profileResponse.setUserName(profile.getAccount().getUserName());
        profileResponse.setName(profile.getName());
        profileResponse.setQuantityFriend(profile.getAccount().getFriends().size());

        // Xử lý thời gian
        LocalDateTime localDateTime;
        try {
            localDateTime = convertUtcToUserLocalTime(profileFilterRequest.getTime());
        } catch (Exception e) {
            throw new AppException(InValidErrorCode.TIME_ZONE_INVALID);
        }

        // Lấy trạng thái bạn bè
        String friendStatus;
        if (currentAccount.getId().equals(profile.getAccount().getId())) {
            friendStatus = "me";
        } else {
            friendStatus = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                    currentAccount.getId(), profile.getAccount().getId()
            ).map(Friend::getStatus).orElse("none");

            // Kiểm tra nếu đang chờ xác nhận từ người dùng (received)
            Optional<Friend> friendOptional = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                    currentAccount.getId(), profile.getAccount().getId()
            );
            boolean isInitiator = false;
            if (friendOptional.isPresent())
            {
                Friend friend = friendOptional.get();
//                isInitiator = friend.getAccountInitiator();
                if(friend.getAccountInitiator() == friend.getAccountUser())
                {
                    isInitiator = true;
                }
                else {
                    isInitiator = false;
                }

            }

//            System.out.println("this is init " + isInitiator);
//
            friendStatus = accountService.determineFriendStatus(friendStatus, isInitiator).name();
//            System.out.println("this is friendStatus " + friendStatus);

        }

        profileResponse.setFriendStatus(friendStatus);

        // Nếu là bạn bè hoặc chính chủ, lấy danh sách ảnh
        if (currentAccount.getId().equals(profile.getAccount().getId()) || "accepted".equals(friendStatus)) {
            Pageable pageable = PageRequest.of(profileFilterRequest.getPageCurrent(), 6);

            List<Photo> photos = photoRepository.findByAccount_IdAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                    profile.getAccount().getId(), 1, localDateTime, pageable
            );

            List<PhotoResponse> photoResponses = new ArrayList<>();
            for (Photo photo : photos) {
                PhotoResponse photoResponse = photoMapper.toPhotoResponse(photo);
                accountMapper.toPhotoResponse(photoResponse, photo.getAccount());
                photoResponse.setUrlAvt(photoService.getUrlAvtAccount(photo.getAccount().getId()));

                photoResponses.add(photoResponse);
            }
            profileResponse.setListPhotoProfile(photoResponses);
        }

        profileResponse.setUrlAvt(photoService.getUrlAvtAccount(profile.getAccount().getId()));

        return profileResponse;
    }
}
