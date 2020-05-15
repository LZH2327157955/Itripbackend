package cn.itrip.common;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
    private String datePattern;  //用于保存指定的日期格式，如：yyyy-MM-dd
    public StringToDateConverter(String datePattern) {
        this.datePattern = datePattern;  //datePattern的值 yyyy-MM-dd
    }

    public Date convert(String str) { //str需要转换成日期格式的字符串
        //完成将字符串转换成日期格式
        Date date = null;
        try {
            date = new SimpleDateFormat(datePattern).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
