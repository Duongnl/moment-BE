package com.moment.moment_BE.service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.moment.moment_BE.dto.request.PostRequest;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.PhotoErrorCode;
import com.moment.moment_BE.repository.AccountRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.moment.moment_BE.dto.request.PhotoFilterRequest;
import com.moment.moment_BE.dto.response.PhotoResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.entity.Photo;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.PhotoMapper;
import com.moment.moment_BE.repository.PhotoRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhotoService {

    PhotoRepository photoRepository;
    AuthenticationService authenticationService;
    PhotoMapper photoMapper;
    AccountMapper accountMapper;
    AccountRepository accountRepository;

    // lay anh cua ban be o pageCurrent voi so luong size tu thoi gian startTime voi status
    public List<PhotoResponse> getListPhotoMyFriends(PhotoFilterRequest photoFilterRequest, int status) {

        Account account = authenticationService.getMyAccount(status);
        List<String> accountsFriend = new ArrayList<>();

        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                accountsFriend.add(friend.getAccountFriend().getId());
            }
        }
        accountsFriend.add(account.getId());
        Pageable pageable = PageRequest.of(photoFilterRequest.getPageCurrent(), 5);


        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(photoFilterRequest.getTime(), DateTimeFormatter.ISO_DATE_TIME);

        // Bước 2: Chuyển đổi từ UTC sang múi giờ người dùng
        ZoneId userZoneId = ZoneId.of(photoFilterRequest.getTimezone());
        ZonedDateTime userZonedDateTime = utcZonedDateTime.withZoneSameInstant(userZoneId);
        
        List<Photo> photos = photoRepository.findByAccount_IdInAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(accountsFriend, 1, userZonedDateTime.toLocalDateTime(), pageable);
        List<PhotoResponse> photoResponses = new ArrayList<>();
        for (Photo photo : photos) {
            PhotoResponse photoResponse = photoMapper.toPhotoResponse(photo);
            accountMapper.toPhotoResponse(photoResponse, photo.getAccount());
            photoResponse.setUrlAvt(getUrlAvtAccount(photo.getAccount().getId()));

            photoResponses.add(photoResponse);
        }
        return photoResponses;
    }

    public String getUrlAvtAccount(String accountId) {
        Photo photoOptional = photoRepository.findByAccount_IdAndStatus(accountId, 2);
        if (photoOptional == null) {
            return null;
        }
        return photoOptional.getUrl();
    }

    public List<Photo> getListPhotoByAccount(int size, int pageCurrent, LocalDateTime startTime, int status) {
        Account account = authenticationService.getMyAccount(status);
        List<String> accountsFriend = new ArrayList<>();

        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                accountsFriend.add(friend.getAccountFriend().getId());
            }
        }
        Pageable pageable = PageRequest.of(pageCurrent, size);
        return photoRepository.findByAccount_IdInAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(accountsFriend, status, startTime, pageable);
    }

    public void post(PostRequest postRequest) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        Account account = accountRepository.findByUserNameAndStatus(name, 1).orElseThrow(
                () -> new AppException(AccountErrorCode.USER_NOT_FOUND)
        );


        Photo photo = photoMapper.toPhoto(postRequest);
        photo.setAccount(account);
        photo.setCreatedAt(LocalDateTime.now());
        photo.setStatus(1);

        try {
            photoRepository.save(photo);
        } catch (Exception e) {
            throw new AppException(PhotoErrorCode.SAVE_PHOTO_FAIL);
        }


    }
}