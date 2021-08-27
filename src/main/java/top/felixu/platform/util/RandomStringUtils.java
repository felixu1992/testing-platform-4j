package top.felixu.platform.util;

import java.util.Random;

/**
 * 根据需求，随机生成指定长度的字符串
 *
 * @author felixu
 * @since 2021.08.27
 */
public class RandomStringUtils {

    /**
     * 默认长度
     */
    private static final int DEFAULT_LENGTH = 32;

    /**
     * 符号池
     */
    private static final char[] SYMBOL;

    static {
        StringBuilder symbol = new StringBuilder();
        for (char character = '0'; character <= '9'; character++) {
            symbol.append(character);
        }
        for (char character = 'a'; character <= 'z'; character++) {
            symbol.append(character);
        }
        for (char character = 'A'; character <= 'Z'; character++) {
            symbol.append(character);
        }
        SYMBOL = symbol.toString().toCharArray();
    }

    /**
     * 生成的长度
     */
    private final int length;

    private RandomStringUtils(int length) {
        if (length <= 0)
            throw new IllegalArgumentException("A random string's length cannot be zero or negative");
        this.length = length;
    }

    /**
     * 生成一个默认长度的字符串
     * 使用默认字符池
     * 大小写字母和数字
     */
    public static String make() {
        return make(DEFAULT_LENGTH, SYMBOL);
    }

    /**
     * 生成一个默认长度的字符串
     * 使用指定字符池
     */
    public static String make(char[] symbol) {
        return make(DEFAULT_LENGTH, symbol);
    }

    /**
     * 生成一个指定长度的字符串
     * 使用默认字符池
     * 大小写字母和数字
     */
    public static String make(int length) {
        return make(length, SYMBOL);
    }

    /**
     * 生成一个指定长度的字符串
     * 使用指定字符池
     */
    public static String make(int length, char[] symbol) {
        return new RandomStringUtils(length).generate(symbol);
    }

    private String generate(char[] symbol) {
        char[] buffer = new char[length];
        for (int index = 0; index < length; index++) {
            buffer[index] = symbol[new Random().nextInt(symbol.length)];
        }
        return new String(buffer);
    }
}
