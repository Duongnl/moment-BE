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
import java.util.Date;
import java.util.List;

import static com.moment.moment_BE.utils.DateTimeUtils.convertUtcToUserLocalTime;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileMapper profileMapper;
    ProfileRepository profileRepository;
    AccountMapper accountMapper;
    AccountController accountController;
    AccountRepository accountRepository;
    PhotoMapper photoMapper;
    PhotoController photoController;
    PhotoRepository photoRepository;
    PhotoService photoService;
    AuthenticationService authenticationService ;
    FriendRepository friendRepository;



    public ProfileResponse getProfileByUserName(ProfileFilterRequest profileFilterRequest)
    {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        Account account = accountRepository.findByUserNameAndStatus(name, 1).orElseThrow(
                () -> new AppException(AccountErrorCode.USER_NOT_FOUND)
        );




        Profile pro =  profileRepository.findByAccount_UserName(profileFilterRequest.getUserName());
        if (pro == null) {
            throw new AppException(AccountErrorCode.USER_NOT_FOUND);
        }



        ProfileResponse profileResponse =  new ProfileResponse();
        profileResponse.setIdAccount(pro.getAccount().getId());
        profileResponse.setId(pro.getId());
        profileResponse.setUserName(pro.getAccount().getUserName());
        profileResponse.setName(pro.getName());
        profileResponse.setQuantityFriend(pro.getAccount().getFriends().size());

        LocalDateTime localDateTime = null;
        try {
            localDateTime =  convertUtcToUserLocalTime(
                    profileFilterRequest.getTime()
            );
        }catch (Exception e) {
            throw new AppException(InValidErrorCode.TIME_ZONE_INVALID);
        };

        String friendStatus;
        if (account.getId().equals(pro.getAccount().getId()) )
        {
            friendStatus = "me";
        }
        else {
            friendStatus = friendRepository
                    .findByAccountUser_IdAndAccountFriend_Id(
                            account.getId(), pro.getAccount().getId()
                    )
                    .map(Friend::getStatus)
                    .orElse("none");
        }

        profileResponse.setFriendStatus(friendStatus);


        if ( account.getId().equals( pro.getAccount().getId())|| "accepted".equals(friendStatus) ||
                friendRepository.findByAccountUser_IdAndAccountFriend_IdAndStatus(account.getId() , pro.getAccount().getId(), "accepted").isPresent())
        {

        Pageable pageable = PageRequest.of(profileFilterRequest.getPageCurrent(), 6);

        List<Photo> photos = photoRepository.findByAccount_IdAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(pro.getAccount().getId(),
                1,
                localDateTime,
                pageable);
        List<PhotoResponse> photoResponses = new ArrayList<>();

        for (Photo photo : photos ) {
            PhotoResponse photoResponse = photoMapper.toPhotoResponse(photo);
            accountMapper.toPhotoResponse(photoResponse, photo.getAccount());
            photoResponse.setUrlAvt(photoService.getUrlAvtAccount(photo.getAccount().getId()));

            photoResponses.add(photoResponse);
        }
        profileResponse.setListPhotoProfile(photoResponses);

        }

        profileResponse.setUrlAvt(photoService.getUrlAvtAccount(pro.getAccount().getId()));

        return profileResponse;
    }
//    public String getStatus(String accountId)
//    {
//        return friendRepository.findByAccount_IdAndAccountFriend_IdOrAccountFriend_IdAndAccount_Id(
//                account.getId(), accountId, accountId, account.getId()
//                ).map(Friend::getStatus).orElse("not_friends");
//    }

}
