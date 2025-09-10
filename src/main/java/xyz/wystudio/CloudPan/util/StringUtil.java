package xyz.wystudio.CloudPan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String extractBetween(String text, String left, String right) {
        String regex = Pattern.quote(left) + "(.*?)" + Pattern.quote(right);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }
}
