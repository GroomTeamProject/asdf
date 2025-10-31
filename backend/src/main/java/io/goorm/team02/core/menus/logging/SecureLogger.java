package io.goorm.team02.core.menus.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class SecureLogger {

    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            ".*(password|pwd|secret|key|token|card|ssn|phone|email|address).*",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 민감정보 마스킹 로깅
     */
    public static void logSecurely(Logger logger, String level, String message, Object... args) {
        Object[] maskedArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            maskedArgs[i] = maskSensitiveData(args[i]);
        }

        switch (level.toLowerCase()) {
            case "info":
                logger.info(message, maskedArgs);
                break;
            case "warn":
                logger.warn(message, maskedArgs);
                break;
            case "error":
                logger.error(message, maskedArgs);
                break;
            case "debug":
                logger.debug(message, maskedArgs);
                break;
        }
    }

    /**
     * 민감 데이터 마스킹
     */
    private static Object maskSensitiveData(Object data) {
        if (data == null) return null;

        String str = data.toString();

        // 이메일 마스킹
        if (str.contains("@")) {
            return maskEmail(str);
        }

        // 전화번호 마스킹
        if (str.matches("\\d{3}-\\d{4}-\\d{4}")) {
            return maskPhoneNumber(str);
        }

        // 사업자번호 마스킹
        if (str.matches("\\d{3}-\\d{2}-\\d{5}")) {
            return maskBusinessNumber(str);
        }

        // 가격 정보는 마스킹하지 않음 (비즈니스 필요)
        if (data instanceof BigDecimal) {
            return data;
        }

        return data;
    }

    private static String maskEmail(String email) {
        if (email.length() <= 3) return "***";
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return "***@***";

        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private static String maskPhoneNumber(String phone) {
        if (phone.length() < 8) return "***";
        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }

    private static String maskBusinessNumber(String businessNumber) {
        if (businessNumber.length() < 6) return "***";
        return businessNumber.substring(0, 3) + "-**-***" + businessNumber.substring(businessNumber.length() - 2);
    }
}
