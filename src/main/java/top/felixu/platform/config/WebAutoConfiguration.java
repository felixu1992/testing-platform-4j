package top.felixu.platform.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.MonthDayDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.MonthDaySerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import top.felixu.common.date.DateFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * @author felixu
 * @since 2021.08.15
 */
@Configuration
@PropertySource("classpath:/web-default.properties")
public class WebAutoConfiguration {

    @Bean
    public SimpleModule customJsr310Module() {
        SimpleModule customJsr310Module = new SimpleModule("customJsr310Module") {
            @Override
            public Object getTypeId() {
                return "customJsr310Module";
            }
        };

        DateTimeFormatter yearMonthFormatter = DateFormatter.CH_MONTH.getFormatter();
        customJsr310Module.addSerializer(YearMonth.class, new YearMonthSerializer(yearMonthFormatter));
        customJsr310Module.addDeserializer(YearMonth.class, new YearMonthDeserializer(yearMonthFormatter));

        DateTimeFormatter monthDayFormatter = DateFormatter.CH_MONTH_DATE.getFormatter();
        customJsr310Module.addSerializer(MonthDay.class, new MonthDaySerializer(monthDayFormatter));
        customJsr310Module.addDeserializer(MonthDay.class, new MonthDayDeserializer(monthDayFormatter));

        DateTimeFormatter localDateTimeFormatter = DateFormatter.DEFAULT.getFormatter();
        customJsr310Module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(localDateTimeFormatter));
        customJsr310Module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(localDateTimeFormatter));

        DateTimeFormatter localDateFormatter = DateFormatter.FULL_DATE.getFormatter();
        customJsr310Module.addSerializer(LocalDate.class, new LocalDateSerializer(localDateFormatter));
        customJsr310Module.addDeserializer(LocalDate.class, new LocalDateDeserializer(localDateFormatter));

        DateTimeFormatter localTimeFormatter = DateFormatter.FULL_TIME.getFormatter();
        customJsr310Module.addSerializer(LocalTime.class, new LocalTimeSerializer(localTimeFormatter));
        customJsr310Module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(localTimeFormatter));

        return customJsr310Module;
    }

    @Bean
    public Converter<String, YearMonth> stringYearMonthConverter() {
        return new StringYearMonthConverter();
    }

    @Bean
    public Converter<String, MonthDay> stringMonthDayConverter() {
        return new StringMonthDayConverter();
    }

    @Bean
    public Converter<String, LocalDate> stringLocalDateConverter() {
        return new StringLocalDateConverter();
    }

    @Bean
    public Converter<String, LocalDateTime> stringLocalDateTimeConverter() {
        return new StringLocalDateTimeConverter();
    }

    @Bean
    public Converter<String, LocalTime> stringLocalTimeConverter() {
        return new StringLocalTimeConverter();
    }

    private static class StringYearMonthConverter implements Converter<String, YearMonth> {
        @Override
        public YearMonth convert(@NonNull String source) {
            return YearMonth.parse(source, DateFormatter.CH_MONTH.getFormatter());
        }
    }

    private static class StringMonthDayConverter implements Converter<String, MonthDay> {
        @Override
        public MonthDay convert(@NonNull String source) {
            return MonthDay.parse(source, DateFormatter.CH_MONTH_DATE.getFormatter());
        }
    }

    private static class StringLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(@NonNull String source) {
            return DateFormatter.FULL_DATE.parseToLocalDate(source);
        }
    }

    private static class StringLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(@NonNull String source) {
            if (source.isEmpty()){
                return null;
            }
            return DateFormatter.DEFAULT.parseToLocalDateTime(source);
        }
    }

    private static class StringLocalTimeConverter implements Converter<String, LocalTime> {
        @Override
        public LocalTime convert(@NonNull String source) {
            if (source.isEmpty()){
                return null;
            }
            return DateFormatter.FULL_TIME.parseToLocalTime(source);
        }
    }
}
