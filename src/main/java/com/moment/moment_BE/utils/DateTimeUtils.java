package com.moment.moment_BE.utils;

import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static LocalDateTime convertUtcToUserLocalTime(String utcTime) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("clienttime duong >>> " + utcZonedDateTime);
        ZoneId userZoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        return utcZonedDateTime.withZoneSameInstant(userZoneId).toLocalDateTime();
    }

    public static LocalDateTime getCurrentTimeInSystemLocalTime() {
        ZonedDateTime serverTime = ZonedDateTime.now(ZoneId.systemDefault());
         System.out.println("serverTime duong >>> " + serverTime);
        ZoneId systemZoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        return serverTime.withZoneSameInstant(systemZoneId).toLocalDateTime();
    }
}

