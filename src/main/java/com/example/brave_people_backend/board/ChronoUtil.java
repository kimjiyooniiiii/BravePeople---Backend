package com.example.brave_people_backend.board;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

//LocalDateTime -> 몇분 전, 몇시간 전, 며칠 전으로 출력하는 Util
public class ChronoUtil {
    public static String timesAgo(LocalDateTime dayBefore) {
        long gap = ChronoUnit.MINUTES.between(dayBefore, LocalDateTime.now());
        String word;
        if (gap == 0){
            word = "방금 전";
        }else if (gap < 60) {
            word = gap + "분 전";
        }else if (gap < 60 * 24){
            word = (gap/60) + "시간 전";
        }else if (gap < 60 * 24 * 30) {
            word = (gap/60/24) + "일 전";
        } else {
            word = dayBefore.format(DateTimeFormatter.ofPattern("MM월 dd일"));
        }
        return word;
    }
}