package com.moment.moment_BE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProfileResponse {

    int id;
    String idAccount;
    String userName;
    String name;
    List<PhotoResponse> listPhotoProfile;
    String urlAvt;
    int quantityFriend;
    private String friendStatus;
}
