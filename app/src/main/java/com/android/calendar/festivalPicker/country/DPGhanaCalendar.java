package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 加纳
 * Created by yongwen.huang on 2016/12/5.
 */
public class DPGhanaCalendar extends DPCommonFestival{

    //加纳节日
    public static final String[][] FESTIVAL_GHANA = new String[12][31];
    /*private static final String[][] FESTIVAL_GHANA = {
            {},
            {},
            {getString(R.string.independence_day)},
            {},
            {getString(R.string.african_union_day)},
            {},
            {getString(R.string.republic_day)},
            {},
            {getString(R.string.founder_day)},
            {},
            {},
            {getString(R.string.new_year_eve)}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {},
            {},
            {6},
            {},
            {25},
            {},
            {1},
            {},
            {21},
            {},
            {},
            {31}};

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
            {""}
    };

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
                    String generalGhanaFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheGhana, FESTIVAL_GHANA,FESTIVAL_NUM, RELIGION_GENERAL);
                    //获得公共节日
                    String[][] comFestival = obtainComFestival(year, month);
                    result = comFestival[i][j];

                    if(generalGhanaFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = generalGhanaFestival;
                        }else {
//                          String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_GHANA, FESTIVAL_NUM);
                            result = getStringFestival(result, generalGhanaFestival);
                        }
                    }

                    if(TextUtils.isEmpty(result)){
                        if(g.m == 12){//农民日
                            int motherDay = getFarmerDay(g.y);
                            if(motherDay == g.d){
                                result = calculateFestival[2];
                            }
                        }
                    }else {
                        String festival = null;
                        if(g.m == 12){
                            int motherDay = getFarmerDay(g.y);
                            if(motherDay == g.d){
                                festival = calculateFestival[2];
                            }
                        }
                        result = getStringFestival(result, festival);
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
