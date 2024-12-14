package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.request.PostRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.moment.moment_BE.dto.response.PhotoResponse;
import com.moment.moment_BE.entity.Photo;

@Mapper(componentModel = "spring")
public interface PhotoMapper {

    @Mapping(source="url", target ="urlPhoto")
    public PhotoResponse toPhotoResponse(Photo photo);

    Photo toPhoto(PostRequest postRequest);

}
