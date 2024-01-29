package com.example.brave_people_backend.board;

import com.example.brave_people_backend.exception.CustomException;

import java.text.DecimalFormat;
import java.text.ParseException;

public class DecimalUtil {
    private final static DecimalFormat formatter = new DecimalFormat("###,###");

    public static String intToStringPrice(int price) {
        return formatter.format(price);
    }

    public static int stringToIntPrice(String price) {
        try {
            return formatter.parse(price).intValue();
        } catch (ParseException e) {
            throw new CustomException("DecimalUtil.stringToIntPrice() 예외 발생");
        }
    }
}
