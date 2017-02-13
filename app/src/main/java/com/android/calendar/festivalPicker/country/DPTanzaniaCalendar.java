package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 坦桑尼亚
 * Created by yongwen.huang on 2016/12/5.
 */
public class DPTanzaniaCalendar extends DPCommonFestival{

    //坦桑尼亚节日
    public static final String[][] FESTIVAL_TANZANIA = new String[12][31];
    /*private static final String[][] FESTIVAL_TANZANIA = {
            {getString(R.string.revolution_day)},
            {},
            {},
            {getString(R.string.karume_day), getString(R.string.union_day)},
            {},
            {},
            {getString(R.string.saba_saba_day)},
            {getString(R.string.farmer_day)},
            {},
            {getString(R.string.mwalimu_nyerere_day)},
            {},
            {getString(R.string.independence_day)}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {12},
            {},
            {},
            {7, 26},
            {},
            {},
            {7},
            {8},
            {},
            {14},
            {},
            {9}};

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
        //if (year == 2015) {
        Collections.addAll(tmp, HOLIDAY[month - 1]);
        //}
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
                    String generalTanzaniaFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheTanzania, FESTIVAL_TANZANIA,FESTIVAL_NUM, RELIGION_GENERAL);

                    String[][] comFestival = obtainComFestival(year, month);//获得公共节日
                    result = comFestival[i][j];

                    if(generalTanzaniaFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = generalTanzaniaFestival;
                        }else {
//                            String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_TANZANIA, FESTIVAL_NUM);
                            result = getStringFestival(result, generalTanzaniaFestival);
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
