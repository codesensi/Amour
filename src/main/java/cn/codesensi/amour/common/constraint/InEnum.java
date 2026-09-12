package cn.codesensi.amour.common.constraint;

import cn.codesensi.amour.common.enums.BaseEnum;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.Arrays;

/**
 * 枚举取值校验：被标注字段的值必须命中目标枚举的某个 code，
 * 合法值集合以枚举类为唯一事实来源（枚举加值后校验自动放行，无需另行同步）。
 *
 * @author codesensi
 * @since 1.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {InEnum.InEnumValidator.class})
public @interface InEnum {

    /**
     * 目标枚举类型（须实现 BaseEnum）
     */
    Class<? extends BaseEnum<?>> enumClass();

    /**
     * 校验失败提示
     */
    String message() default "取值不在指定范围内";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 校验器：空值放行（必填由 @NotBlank 控制），非空值逐个比对枚举 code
     */
    class InEnumValidator implements ConstraintValidator<InEnum, String> {

        private Class<? extends BaseEnum<?>> enumClass;

        @Override
        public void initialize(InEnum annotation) {
            this.enumClass = annotation.enumClass();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isBlank()) {
                return true;
            }
            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(e -> String.valueOf(e.getCode()).equals(value));
        }
    }
}
