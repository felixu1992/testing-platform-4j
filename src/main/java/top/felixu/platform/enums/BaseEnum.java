package top.felixu.platform.enums;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 所有需要传给前端或由前端传来的枚举都必须实现该接口，并放在该包内！<br>
 * 所有枚举类和实例都请务必好好起名，定好顺序，是要展示给用户和存入数据库的，枚举项顺序一旦确定将不允许再修改！
 *
 * @author felixu
 * @since 2021.08.06
 */
public interface BaseEnum {

    /**
     * 获得枚举的描述，该描述将被系统传给前端，用于显示给用户，请谨慎设置
     *
     * @return 对枚举的描述
     */
    String getDesc();

    /**
     * 根据描述还原出枚举项
     *
     * @param desc 描述
     * @param clazz 枚举类
     * @param <T> 泛型
     * @return 枚举项
     */
    static <T extends Enum> Optional<T> ofDesc(String desc, Class<T> clazz) {
        if (!clazz.isEnum() || !BaseEnum.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException();
        }
        try {
            Method method = clazz.getDeclaredMethod("values");
            method.setAccessible(true);
            BaseEnum[] enums = (BaseEnum[]) method.invoke(null);
            for (BaseEnum anEnum : enums) {
                if (Objects.equals(desc, anEnum.getDesc())) {
                    return Optional.of((T) anEnum);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}