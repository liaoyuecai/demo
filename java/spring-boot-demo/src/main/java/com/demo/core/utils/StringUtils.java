package com.demo.core.utils;

import java.util.regex.Pattern;

public final class StringUtils {
    public final static String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^_()&*-]).{8,}$";

    /**
     * 将驼峰命名的字符串转换为‘_’作为分隔的全小写字符串
     * 常用作bean属性转行为数据库字段
     *
     * @param camelCaseStr
     * @return
     */
    public static String toUnderscoreCase(String camelCaseStr) {
        StringBuilder underscoreStr = new StringBuilder();
        for (int i = 0; i < camelCaseStr.length(); i++) {
            char ch = camelCaseStr.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    underscoreStr.append('_');
                }
                underscoreStr.append(Character.toLowerCase(ch));
            } else {
                underscoreStr.append(ch);
            }
        }
        return underscoreStr.toString();
    }

    /**
     * 密码强度检测
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        return Pattern.compile(PASSWORD_PATTERN)
                .matcher(password).matches();
    }
}
