package com.example.brave_people_backend;

import com.example.brave_people_backend.board.DecimalUtil;
import org.junit.jupiter.api.Test;

public class DecimalUtilTest {

    @Test
    public void stringToIntTest() {

        System.out.println(DecimalUtil.stringToIntPrice("333,333"));
        System.out.println(DecimalUtil.intToStringPrice(44444));
    }
}
