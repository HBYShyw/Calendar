package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 布基纳法索
 * Created by yongwen.huang on 2016/12/6.
 */
public class DPBurkinaCalendar extends DPCommonFestival{

    //布基纳法索节日
    public static final String[][] FESTIVAL_BURKINA = new String[12][31];
    /*private static final String[][] FESTIVAL_BURKINA = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {getString(R.string.assumption_day)},
            {},
            {},
            {getString(R.string.all_saints_day)},
            {}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {15},
            {},
            {},
            {1},
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
                    String generalBurkinaFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheBurkina, FESTIVAL_BURKINA,FESTIVAL_NUM, RELIGION_GENERAL);

                    //获得公共节日
                    String[][] comFestival = obtainComFestival(year, month);
//                    String[][] comFestival = obtainComFestival(year, month);
                    result = comFestival[i][j];

                    if(generalBurkinaFestival != null){
                        if(TextUtils.isEmpty(result)){
                            //result = getFestivalGregorian(g.m, g.d, FESTIVAL_BURKINA, FESTIVAL_NUM);
                            result = generalBurkinaFestival;
                        }else {
                            //String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_BURKINA, FESTIVAL_NUM);
                            result = getStringFestival(result, generalBurkinaFestival);
                        }
                    }

                    if(TextUtils.isEmpty(result)){
                        int[] mAscension = getAscensionFestival(year,0);
                        if(g.m == mAscension[0] && g.d == mAscension[1]){
                            result = christianityFestival[4];
                        }
                    }else {
                        int[] mAscension = getAscensionFestival(year,0);
                        String festival = null;
                        if(g.m == mAscension[0] && g.d == mAscension[1]){
                            festival = christianityFestival[4];
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


