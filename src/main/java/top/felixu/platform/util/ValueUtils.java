package top.felixu.platform.util;

import org.apache.commons.lang3.StringUtils;
import top.felixu.platform.model.entity.Dependency;
import top.felixu.platform.model.entity.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author felixu
 * @since 2021.09.14
 */
public class ValueUtils {

    public static void setValue(Map<String, Object> params, String[] steps, Object value) {
        /*
         * 1. 从 content 中取值
         * 2. 往 params 中插值
         */
        Object target = getValue(new String[]{steps[0]}, params);
        for (int i = 1; i < steps.length; i++) {
            boolean last = i == steps.length - 1;
            Object temp = target;
            String step = steps[i];
            // 判断当前结果的数据类型，是对象，还是数组
            if (temp instanceof List && isNumber(step)) {
                List<Object> temp = ((ArrayList<Object>) result);
                int index = Integer.parseInt(step);
                if (temp.size() <= index)
                    return null;
                result = temp.get(index);
                // 基本类型还想取值，明显搞错了
            } else if (result instanceof String || result instanceof Integer) {
                return null;
                // 正常情况都是 Map
            } else {
                result = ((Map<String, Object>) result).get(step);
            }
        }
    }

    public static Object getValue(String[] steps, Map<String, Object> content) {
        Object result = content;
        // 循环取值步骤
        for (String step : steps) {
            if (result == null)
                return null;
            // 判断当前结果的数据类型，是对象，还是数组
            if (result instanceof List && isNumber(step)) {
                List<Object> temp = ((ArrayList<Object>) result);
                int index = Integer.parseInt(step);
                if (temp.size() <= index)
                    return null;
                result = temp.get(index);
                // 基本类型还想取值，明显搞错了
            } else if (result instanceof String || result instanceof Integer) {
                return null;
                // 正常情况都是 Map
            } else {
                result = ((Map<String, Object>) result).get(step);
            }
        }
        return result;
    }

    /**
     * 用于处理可能为null的值，避免判断null，并设置默认值
     *
     * @param value 待判断的值
     * @param def   默认值
     * @param <T>   范型对象
     * @return 返回对象值
     */
    public static <T> T nullAs(T value, T def) {
        if (value == null)
            return def;
        return value;
    }

    /**
     * 是否为数字
     *
     * @param str 字符串值
     * @return 是否为数字
     */
    public static boolean isNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }
}
