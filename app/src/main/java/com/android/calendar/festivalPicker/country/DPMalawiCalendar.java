package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.R;
import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 马拉维
 * Created by yongwen.huang on 2016/12/5.
 */
public class DPMalawiCalendar extends DPCommonFestival{

    //马拉维节日
    public static final String[][] FESTIVAL_MALAWI = new String[12][31];
    /*private static final String[][] FESTIVAL_MALAWI = {
            {},
            {},
            {getString(R.string.martyrs_day)},
            {},
            {getString(R.string.kamuzu_day)},
            {getString(R.string.freedom_day)},
            {getString(R.string.independence_day)},
            {},
            {},
            {getString(R.string.mother_day)},
            {},
            {}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {},
            {},
            {3},
            {},
            {14},
            {14},
            {6},
            {},
            {},
            {14},
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
                    String generalMalawiFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheMalawi, FESTIVAL_MALAWI,FESTIVAL_NUM, RELIGION_GENERAL);

                    String[][] comFestival = obtainComFestival(year, month);//获得公共节日
                    if(comFestival[i][j].equals(getString(R.string.mother_day))){
                        comFestival[i][j] = "";
                    }
                    result = comFestival[i][j];

                    if(generalMalawiFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = generalMalawiFestival;
                        }else {
//                          String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_MALAWI, FESTIVAL_NUM);
                            result = getStringFestival(result, generalMalawiFestival);
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

