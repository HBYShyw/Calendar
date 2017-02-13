package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 卢旺达
 * Created by yongwen.huang on 2016/12/5.
 */
public class DPRwandaCalendar extends DPCommonFestival{

    //卢旺达节日
    public static final String[][] FESTIVAL_RWANDA = new String[12][31];
    /*private static final String[][] FESTIVAL_RWANDA = {
            {},
            {getString(R.string.heroes_day)},
            {},
            {getString(R.string.genocide_memorial_day)},
            {},
            {},
            {getString(R.string.independence_day),getString(R.string.liberation_day)},
            {getString(R.string.assumption_day)},
            {},
            {},
            {},
            {}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {},
            {1},
            {},
            {7},
            {},
            {},
            {1,4},
            {15},
            {},
            {},
            {},
            {}};

    //节假日
    private static final String[][] HOLIDAY = {
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
            {""},
            {""}};

    @Override
    public String[][] buildMonthFestival(int year, int month){
        return buildFestival(year, month);
    }

    @Override
    public Set<String> buildMonthHoliday(int year, int month){
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, HOLIDAY[month - 1]);
        return tmp;
    }

    public String[][] buildFestival(int year, int month){
        String[][] gregorianMonth = buildMonthG(year, month);//生成某年某月的公历天数数组
        Gregorian g = new Gregorian();
        String tmp[][] = new String[6][7];
        for(int i = 0; i < tmp.length; i++){
            for(int j = 0; j < tmp[0].length; j++){
                tmp[i][j] = "";
                if(!TextUtils.isEmpty(gregorianMonth[i][j])){
                    g.y = year;
                    g.m = month;
                    g.d = Integer.valueOf(gregorianMonth[i][j]);
                    String result;
                    String generalRwandaFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheRwanda, FESTIVAL_RWANDA,FESTIVAL_NUM, RELIGION_GENERAL);

                    String[][] comFestival = obtainComFestival(year, month);//获得公共节日
                    result = comFestival[i][j];

                    if(generalRwandaFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = generalRwandaFestival;
                        }else {
//                            String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_RWANDA, FESTIVAL_NUM);
                            result = getStringFestival(result, generalRwandaFestival);
                        }
                    }

                    if(!TextUtils.isEmpty(result)){
                        tmp[i][j] = result;
                    }
                }
            }
        }
        return tmp;
    }
}
