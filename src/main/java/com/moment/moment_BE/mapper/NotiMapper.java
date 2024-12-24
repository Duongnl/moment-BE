package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.response.NotiResponse;
import com.moment.moment_BE.entity.Noti;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotiMapper {
    NotiResponse toNotiResponse(Noti noti);

}
