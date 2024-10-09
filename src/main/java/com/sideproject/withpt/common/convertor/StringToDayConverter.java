package com.sideproject.withpt.common.convertor;

import com.sideproject.withpt.common.type.Day;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToDayConverter implements Converter<String, Day> {

    @Override
    public Day convert(String source) {

        try {
            return Day.valueOf(source);
        } catch (IllegalArgumentException e) {
            log.info("StringToDayConverter 에러 : {}", e.toString());
            throw new IllegalArgumentException("해당 필드의 타입에서 지원하지 않는 값입니다. MON|TUE|WED|THU|FRI|SAT|SUN");
        }
    }
}
