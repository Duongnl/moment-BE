package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.request.NotiViewRequest;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Noti;
import com.moment.moment_BE.entity.NotiView;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.NotiErrorCode;
import com.moment.moment_BE.exception.NotiViewErrorCode;
import com.moment.moment_BE.repository.NotiRepository;
import com.moment.moment_BE.repository.NotiViewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotiViewService {

    NotiRepository notiRepository;
    NotiViewRepository notiViewRepository;
    AuthenticationService authenticationService;

    public void saveNotiViews(NotiViewRequest notiViewRequest) {
        Account account = authenticationService.getMyAccount(1);

        for (Integer id : notiViewRequest.getNotiIds()) {

            NotiView notiView = new NotiView();
            Noti noti = notiRepository.findById(id).orElseThrow( () -> new AppException(NotiErrorCode.NOTI_NOT_FOUND));

            notiView.setNoti(noti);
            notiView.setAccount(account);
            notiView.setStatus("unread");
            LocalDateTime localDateTime = getCurrentTimeInSystemLocalTime();
            notiView.setViewedAt(localDateTime);

            try {
                notiViewRepository.save(notiView);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new AppException(NotiViewErrorCode.SAVE_NOTI_VIEW_FAIL);
            }


        }

    }




}
