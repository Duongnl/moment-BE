package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.request.NotiFilterRequest;
import com.moment.moment_BE.dto.response.NotiResponse;
import com.moment.moment_BE.dto.response.NumberOfNotiResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.entity.Noti;
import com.moment.moment_BE.entity.Photo;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.InValidErrorCode;
import com.moment.moment_BE.exception.NotiErrorCode;
import com.moment.moment_BE.mapper.NotiMapper;
import com.moment.moment_BE.repository.NotiRepository;
import com.moment.moment_BE.repository.PhotoRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
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
    SimpMessagingTemplate messagingTemplate;
    PhotoRepository photoRepository;
     SimpUserRegistry userRegistry;

    public  List<NotiResponse> getNoti(NotiFilterRequest notiFilterRequest) {
//        lay thong tin nguoi dung dang dang nhap
        Account account = authenticationService.getMyAccount(1);

        List<String> accountsFriend = new ArrayList<>();
        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                accountsFriend.add(friend.getAccountFriend().getId());
            }
        }

        List<NotiResponse> notiResponseList = new ArrayList<>();
        if(notiFilterRequest.getStatus().equals("unread")) {
            List<Object[]> results = notiRepository.findNotiUnread(accountsFriend, account.getId(),notiFilterRequest.getTime(), notiFilterRequest.getLimit());
            for (Object[] result : results) {
                Noti noti = (Noti) result[0];

                NotiResponse notiRes = convertNotiToNotiResponseNoStatus(noti);
                notiRes.setStatus((String) result[1]);
                notiResponseList.add(notiRes);
            }

    } else if (notiFilterRequest.getStatus().equals("all")) {
            List<Object[]> results = notiRepository.findNotiAll(accountsFriend, account.getId(), notiFilterRequest.getTime(),  notiFilterRequest.getLimit());
            for (Object[] result : results) {
                Noti noti = (Noti) result[0];

                NotiResponse notiRes = convertNotiToNotiResponseNoStatus(noti);
                notiRes.setStatus((String) result[1]);
                notiResponseList.add(notiRes);
            }

        }

        return notiResponseList;
    }


    public NotiResponse convertNotiToNotiResponseNoStatus(Noti noti) {
        Account account = authenticationService.getMyAccount(1);
        NotiResponse notiRes = new NotiResponse();
        notiRes = notiMapper.toNotiResponse(noti);

        notiRes.setName(noti.getAccount().getProfile().getName());
        notiRes.setUserName(noti.getAccount().getUserName());
        notiRes.setSlug(noti.getPhoto().getSlug());

        String avt = null;
        Photo photoOptional = photoRepository.findByAccount_IdAndStatus(noti.getAccount().getId(), 2);
        if (photoOptional != null) {
            avt = photoOptional.getUrl();
        }
        notiRes.setUrlAvt(avt);
        notiRes.setUrlPhoto(noti.getPhoto().getUrl());

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

                if (userRegistry.getUser(friend.getAccountFriend().getUserName()) != null) {
                    NotiResponse notiResponse = convertNotiToNotiResponseNoStatus(noti);
                    notiResponse.setStatus("new");
                messagingTemplate.convertAndSendToUser(friend.getAccountFriend().getUserName(), "/queue/noti", notiResponse);
                }

            }
        }
    }


    public NumberOfNotiResponse countNoti  (LocalDateTime time)  {
        Account account = authenticationService.getMyAccount(1);

        List<String> accountsFriend = new ArrayList<>();
        for (Friend friend : account.getFriends()) {
            if (friend.getStatus().equals("accepted")) {
                accountsFriend.add(friend.getAccountFriend().getId());
            }
        }

        NumberOfNotiResponse numberOfNotiResponse = new NumberOfNotiResponse();
//        numberOfNotiResponse.setNumberOfNotiUnread(notiRepository.countNotiUnread(accountsFriend, account.getId(), localDateTime));
        numberOfNotiResponse.setNumberOfNotiNew(notiRepository.countNotiNew(accountsFriend, account.getId(), time));
//        numberOfNotiResponse.setNumberOfNotiAll(notiRepository.countNotiAll(accountsFriend, account.getId(), localDateTime));
        return numberOfNotiResponse;
    }


}
