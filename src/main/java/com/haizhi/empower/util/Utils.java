package com.haizhi.empower.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.haizhi.empower.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/3/30
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Slf4j
public class Utils {

    /**
     * @throws
     * @description: 校验身份证号正则
     * @param: ${tags}
     * @return: ${return_type}
     * @author CristianWindy
     * @date 2020-03-18 21:37
     */
    public static void checkIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard)) {
            throw new ServiceException();
        }
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        boolean matches = idCard.matches(regularExpression);
        if (matches) {
            if (idCard.length() == 18) {
                try {
                    char[] charArray = idCard.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (!idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        throw new ServiceException();
                    }
                } catch (Exception e) {
                    log.error("身份证校验异常", e);
                    throw new ServiceException();
                }
            }
        } else {
            throw new ServiceException();
        }
    }

    /**
     * @throws
     * @description: 检验手机号正则
     * @param: ${tags}
     * @return: ${return_type}
     * @author WangChengYu
     * @date 2020-03-18 21:37
     */
    public static void checkPhone(String phone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        if (phone.length() != 11) {
            throw new ServiceException();
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            if (!m.matches()) {
                throw new ServiceException();
            }
        }
    }

}
