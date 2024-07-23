package com.sideproject.withpt.common.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateUtility {

    public static List<String> getSundays(int year, int month) {
        List<String> sundays = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (date.getMonthValue() == month) {
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                sundays.add(date.format(formatter));
            }
            date = date.plusDays(1);
        }

        return sundays;
    }

    public static List<String> getAllDates(int year, int month) {
        List<String> allDates = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (date.getMonthValue() == month) {
            allDates.add(date.format(formatter));
            date = date.plusDays(1);
        }

        return allDates;
    }

    public static List<String> getWeekFromDate(LocalDate date) {
        List<String> weekDates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            weekDates.add(date.format(formatter));
            date = date.plusDays(1);
        }

        return weekDates;
    }
}
