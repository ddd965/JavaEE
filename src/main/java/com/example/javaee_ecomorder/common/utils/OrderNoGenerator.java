package com.example.javaee_ecomorder.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderNoGenerator() {
    }

    public static String generate() {
        String timePart = LocalDateTime.now().format(FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "ORD" + timePart + randomPart;
    }
}
