package com.moment.moment_BE.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// An nhung bien co  gi tri la null di
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    /*
    * Class nay de dinh dang quy uoc tra ve cho nguoi dung
    * */
    @Builder.Default
    Integer status = HttpStatus.OK.value() ;
    @Builder.Default
    String message = HttpStatus.OK.getReasonPhrase();
    T result;
    List<ErrorResponse> errors;
    @Builder.Default
    long timestamp = System.currentTimeMillis();

    // Phan trang
    Integer totalPages;
    Integer totalItems;
    Integer currentPage;


}


