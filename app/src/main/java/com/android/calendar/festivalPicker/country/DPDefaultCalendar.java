package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yongwen.huang on 2016/12/13.
 */
public class DPDefaultCalendar extends DPCommonFestival{

    private static final String[][] FESTIVAL_G = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
    };

    private static final int[][] FESTIVAL_G_DATE = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
    };
    private static final String[][] HOLIDAY = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
    };

    @Override
    public String[][] buildMonthFestival(int year, int month) {
        String[][] gregorianMonth = buildMonthG(year, month);
        String tmp[][] = new String[6][7];
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[0].length; j++) {
                tmp[i][j] = "";
                String day = gregorianMonth[i][j];
                if (!TextUtils.isEmpty(day)) {
                    tmp[i][j] = getFestivalG(month, Integer.valueOf(day));
                }
            }
        }
        return tmp;
    }

    @Override
    public Set<String> buildMonthHoliday(int year, int month) {
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, HOLIDAY[month - 1]);
        return tmp;
    }

    private String getFestivalG(int month, int day) {
        String tmp = "";
        int[] daysInMonth = FESTIVAL_G_DATE[month - 1];
        for (int i = 0; i < daysInMonth.length; i++) {
            if (day == daysInMonth[i]) {
                tmp = FESTIVAL_G[month - 1][i];
            }
        }
        return tmp;
    }
}
