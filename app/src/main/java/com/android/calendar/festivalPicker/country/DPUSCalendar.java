package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;
import com.android.calendar.festivalPicker.utils.ResourceStringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 美国月历，包含美国节日处理的相关方法
 *
 * Calendar of America
 *
 * @author HYW 2015-12-25
 */
public class DPUSCalendar extends DPCommonFestival{

    private static ResourceStringUtil resourceStringUtil = ResourceStringUtil.getInstance();

    public static final String[][] FESTIVAL_US = new String[12][31];
    /*private static final String[][] FESTIVAL_G = {
            {getString(R.string.new_year)},
            {getString(R.string.valentine_day)},
            {getString(R.string.women_day)},
            {getString(R.string.fools_day)},
            {getString(R.string.labour_day)},
            {},
            {getString(R.string.independence_day)},
            {},
            {},
            {},
            {getString(R.string.all_saints_day)},
            {getString(R.string.christmas), getString(R.string.boxing_day)}
    };*/

    public static final int[][] FESTIVAL_US_DATE = {
            {1},
            {14},
            {8},
            {1},
            {1},
            {},
            {4},
            {},
            {},
            {},
            {1},
            {25, 26}
    };
    private static final String[][] HOLIDAY = {
            {"1"},
            {""},
            {""},
            {""},
            {""},
            {""},
            {""},
            {""},
            {""},
            {""},
            {""},
            {"25", "26", "27"}
    };

    public static String getString(int resId){
        return resourceStringUtil.getString(resId);
    }

    @Override
    public String[][] buildMonthFestival(int year, int month) {
        String[][] gregorianMonth = buildMonthG(year, month);//生成某年某月的公历天数数组
        String tmp[][] = new String[6][7];
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[0].length; j++) {
                tmp[i][j] = "";
                String day = gregorianMonth[i][j];//定位这一天的位置的节日
                if (!TextUtils.isEmpty(day)) {
                    tmp[i][j] = getFestivalG(month, Integer.valueOf(day));//传月，日
                    //获得节日字符串
                }
            }
        }
        return tmp;//返回的是包含本年本月的所有节日的数组
    }

    @Override
    public Set<String> buildMonthHoliday(int year, int month) {
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, HOLIDAY[month - 1]);
        return tmp;
    }

    private String getFestivalG(int month, int day) { // 月，日
        String tmp = "";
        int[] daysInMonth = FESTIVAL_US_DATE[month - 1];//得到当月中所有节日的日子数组
        for (int i = 0; i < daysInMonth.length; i++) {
            if (day == daysInMonth[i]) {
                tmp = FESTIVAL_US[month - 1][i]; //遍历匹配节日，返回节日字符串
            }
        }
        return tmp;
    }
}
