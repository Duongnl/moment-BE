package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.request.NotiFilterRequest;
import com.moment.moment_BE.dto.response.NotiResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.entity.Noti;
import com.moment.moment_BE.entity.Photo;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.NotiErrorCode;
import com.moment.moment_BE.exception.PhotoErrorCode;
import com.moment.moment_BE.mapper.NotiMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.NotiRepository;
import com.moment.moment_BE.repository.NotiViewRepository;
import com.moment.moment_BE.repository.PhotoRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.moment.moment_BE.utils.DateTimeUtils.convertUtcToUserLocalTime;
import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotiService {

    NotiRepository notiRepository;
    NotiMapper notiMapper;
    AuthenticationService authenticationService;
    NotiViewRepository notiViewRepository;
    SimpMessagingTemplate messagingTemplate;
    PhotoRepository photoRepository;
     SimpUserRegistry userRegistry;

    public List<NotiResponse> getNoti(NotiFilterRequest notiFilterRequest) {
//        lay thong tin nguoi dung dang dang nhap
        Account account = authenticationService.getMyAccount(1);

        List<String> accountsFriend = new ArrayList<>();
        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                accountsFriend.add(friend.getAccountFriend().getId());
            }
        }


        Pageable pageable = PageRequest.of(notiFilterRequest.getPageCurrent(), 6);
        LocalDateTime localDateTime = convertUtcToUserLocalTime(
                notiFilterRequest.getTime()
        );

        List<Noti> notiList = notiRepository.findByAccount_IdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(accountsFriend, localDateTime, pageable);

        List<NotiResponse> notiResponseList = new ArrayList<>();
        for (Noti noti : notiList) {
            NotiResponse notiRes = convertNotiToNotiResponse(noti);


            if (notiFilterRequest.getStatus().equals("unread")
                    && notiRes.getStatus().equals("unread")) {
                notiResponseList.add(notiRes);
            } else if (notiFilterRequest.getStatus().equals("all")) {
                notiResponseList.add(notiRes);
            }

        }

        return notiResponseList;
    }

    public int countNoti (NotiFilterRequest notiFilterRequest)  {
        int countNoti = 0;

        Account account = authenticationService.getMyAccount(1);

        List<String> accountsFriend = new ArrayList<>();
        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                accountsFriend.add(friend.getAccountFriend().getId());
            }
        }

        LocalDateTime localDateTime = convertUtcToUserLocalTime(
                notiFilterRequest.getTime()
        );

        if (notiFilterRequest.getStatus().equals("unread")) {
            List<Noti> notiList = notiRepository.findByAccount_IdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(accountsFriend, localDateTime);

            for (Noti noti : notiList) {
                if (!notiViewRepository.existsByAccount_IdAndNoti_Id
                        (account.getId(), noti.getId())) {
                    countNoti = countNoti + 1;
                }

            }
        } else if (notiFilterRequest.getStatus().equals("all")) {
            countNoti = notiRepository.countByAccount_IdInAndCreatedAtLessThanEqual(accountsFriend, localDateTime);
        }



        return countNoti;

    }

    public NotiResponse convertNotiToNotiResponse(Noti noti) {
        Account account = authenticationService.getMyAccount(1);
        NotiResponse notiRes = new NotiResponse();
        notiRes = notiMapper.toNotiResponse(noti);

        notiRes.setName(noti.getAccount().getProfile().getName());
        notiRes.setUserName(noti.getAccount().getUserName());

        String avt = null;
        Photo photoOptional = photoRepository.findByAccount_IdAndStatus(noti.getAccount().getId(), 2);
        if (photoOptional != null) {
            avt = photoOptional.getUrl();
        }
        notiRes.setUrlAvt(avt);
        notiRes.setUrlPhoto(noti.getPhoto().getUrl());

        if (notiViewRepository.existsByAccount_IdAndNoti_Id(account.getId(), noti.getId())) {
            notiRes.setStatus("read");
        } else {
            notiRes.setStatus("unread");
        }

        return notiRes;
    }

    public void pushNotiSocket (Photo photo) {
        Account account = authenticationService.getMyAccount(1);

        Noti noti = new Noti();
        noti.setAccount(account);
        noti.setPhoto(photo);
        LocalDateTime localDateTime = getCurrentTimeInSystemLocalTime();
        noti.setCreatedAt(localDateTime);
        noti.setMessage(account.getProfile().getName() + " đã đăng một ảnh mới.");

        try {
            notiRepository.save(noti);
        } catch (Exception e) {
            throw new AppException(NotiErrorCode.SAVE_NOTI_FAIL);
        }

        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                System.out.println( "userRegistry: " +  userRegistry.getUser(friend.getAccountFriend().getUserName()));
                if (userRegistry.getUser(friend.getAccountFriend().getUserName()) != null) {
                messagingTemplate.convertAndSendToUser(friend.getAccountFriend().getUserName(), "/queue/noti", convertNotiToNotiResponse(noti));
                }

            }
        }
    }

}
