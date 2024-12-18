package com.moment.moment_BE.utils;

import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    /**
     * Chuyển đổi thời gian từ UTC sang múi giờ người dùng.
     *
     * @param utcTime      Chuỗi thời gian ISO 8601 trong UTC (vd: "2023-12-18T15:30:00Z").
     * @param userTimezone Múi giờ của người dùng (vd: "Asia/Ho_Chi_Minh").
     * @return Thời gian trong múi giờ người dùng dưới dạng LocalDateTime.
     */
    public static LocalDateTime convertUtcToUserLocalTime(String utcTime, String userTimezone) {
        // Bước 1: Phân tích chuỗi thời gian UTC
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME);

        // Bước 2: Chuyển đổi sang múi giờ người dùng
        ZoneId userZoneId = ZoneId.of(userTimezone);
        return utcZonedDateTime.withZoneSameInstant(userZoneId).toLocalDateTime();
    }

    /**
     * Chuyển đổi thời gian từ UTC sang múi giờ mặc định của hệ thống.
     *
     * @param utcTime Chuỗi thời gian ISO 8601 trong UTC (vd: "2023-12-18T15:30:00Z").
     * @return Thời gian trong múi giờ hệ thống dưới dạng LocalDateTime.
     */
    public static LocalDateTime convertUtcToSystemLocalTime(String utcTime) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME);

        // Mặc định sử dụng múi giờ của hệ thống (Asia/Ho_Chi_Minh)
        ZoneId systemZoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        return utcZonedDateTime.withZoneSameInstant(systemZoneId).toLocalDateTime();
    }

    /**
     * Lấy thời gian hiện tại từ server và chuyển đổi sang múi giờ mặc định của hệ thống.
     *
     * @return Thời gian hiện tại trong múi giờ hệ thống dưới dạng LocalDateTime.
     */
    public static LocalDateTime getCurrentTimeInSystemLocalTime() {
        // Bước 1: Lấy thời gian hiện tại của server (UTC)
        ZonedDateTime serverTime = ZonedDateTime.now(ZoneId.systemDefault());

        // Bước 2: Chuyển đổi sang múi giờ hệ thống (Asia/Ho_Chi_Minh)
        ZoneId systemZoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        return serverTime.withZoneSameInstant(systemZoneId).toLocalDateTime();
    }
}

