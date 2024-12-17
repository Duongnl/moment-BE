package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.request.FriendInviteRequest;
import com.moment.moment_BE.dto.response.FriendResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.enums.FriendStatus;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.FriendErrorCode;
import com.moment.moment_BE.mapper.FriendMapper;
import com.moment.moment_BE.repository.FriendRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendService {
    FriendRepository friendRepository;
    AuthenticationService authenticationService;
    PhotoService photoService;

    FriendMapper friendMapper;
    AccountService accountService;

    public List<FriendResponse> getListFriends(int status) {
        Account account = authenticationService.getMyAccount(status);
        List<String> listStatus = new ArrayList<>();
        listStatus.add("accepted");
        listStatus.add("pending");

        Pageable pageable = PageRequest.of(0, 5);

        List<Friend> listFriend =  friendRepository.findByAccountUser_IdAndStatusInOrderByRequestedAtDesc(account.getId(), listStatus,pageable);
        List<FriendResponse> listFriendResponse = new ArrayList<>();

        for (Friend friend : listFriend) {
            FriendResponse friendResponse = friendMapper.toFriendResponse(friend);
            if(friend.getStatus().equals("accepted")){ friendResponse.setFriendStatus(FriendStatus.accepted);}
            if(friend.getStatus().equals("pending")){ friendResponse.setFriendStatus(FriendStatus.pending);}
            friendResponse.setName(friend.getAccountFriend().getProfile().getName());
            friendResponse.setUrlPhoto(photoService.getUrlAvtAccount(friend.getAccountFriend().getId()));
            friendResponse.setUrlProfile("/"+friend.getAccountFriend().getUserName());
            listFriendResponse.add(friendResponse);
        }
        return listFriendResponse;
    }

    @Transactional
    public Friend addFriend(FriendInviteRequest friendInviteRequest, int status) {
        Account account = authenticationService.getMyAccount(status);
        Account accountFriend = accountService.findByIdAndStatus(friendInviteRequest.getAccountFriendId(), 1);

        Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                account.getId(),
                accountFriend.getId()).orElse(null);
        if (friend != null) {
            if (friend.getStatus().equals("accepted")) {
                throw new AppException(FriendErrorCode.FRIEND_ACCEPTED);
            }
            if (friend.getStatus().equals("blocked")) {
                throw new AppException(FriendErrorCode.FRIEND_BLOCKED);
            }
            if (friend.getStatus().equals("pending")) {
                throw new AppException(FriendErrorCode.FRIEND_PENDING);
            }
            throw new AppException(FriendErrorCode.FRIEND_ERROR);
        } else {
            friend= new Friend();
            friend.setAccountUser(account);
            friend.setAccountFriend(accountFriend);
            friend.setRequestedAt(LocalDateTime.now());
            friend.setStatus("pending");
            friend.setAccountInitiator(account);
            friendRepository.save(friend);

            Friend friendRP = new Friend();
            friendRP.setAccountUser(accountFriend);
            friendRP.setAccountFriend(account);
            friendRP.setRequestedAt(LocalDateTime.now());
            friendRP.setStatus("pending");
            friendRP.setAccountInitiator(account);
            friendRepository.save(friendRP);
            return friend;
        }
    }

    @Transactional
    public Friend changeStatusFriend(FriendInviteRequest friendInviteRequest,int status) {
        Account account = authenticationService.getMyAccount(status);
        Account accountFriend = accountService.findByIdAndStatus(friendInviteRequest.getAccountFriendId(), 1);

        Friend friend = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                account.getId(),
                accountFriend.getId()).orElseThrow(
                () -> new AppException(FriendErrorCode.FRIEND_NOT_FOUND));
        Friend friendRP = friendRepository.findByAccountUser_IdAndAccountFriend_Id(
                accountFriend.getId(),
                account.getId()).orElseThrow(
                () -> new AppException(FriendErrorCode.FRIEND_NOT_FOUND));
    if(friendInviteRequest.getStatus()==FriendStatus.blocked){
        friend.setStatus("blocked");
        friendRP.setStatus("blocked");
    }
    if(friendInviteRequest.getStatus()==FriendStatus.accepted){
        friend.setStatus("accepted");
        friendRP.setStatus("accepted");
    }
    if(friendInviteRequest.getStatus()==FriendStatus.pending){
        friend.setStatus("pending");
        friendRP.setStatus("pending");
    }
        friendRepository.save(friend);
        friendRepository.save(friendRP);
        friend.setAcceptedAt(LocalDateTime.now());
        friendRP.setAcceptedAt(LocalDateTime.now());
        return friend;
    }




}
