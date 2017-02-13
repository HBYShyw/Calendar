package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 喀麦隆
 * Created by yongwen.huang on 2016/12/6.
 */
public class DPCameroonCalendar extends DPCommonFestival{

    //喀麦隆节日
    public static final String[][] FESTIVAL_CAMEROON = new String[12][31];
    /*private static final String[][] FESTIVAL_CAMEROON = {
            {getString(R.string.independence_day)},
            {getString(R.string.youth_day)},
            {},
            {},
            {getString(R.string.national_day)},
            {},
            {},
            {getString(R.string.assumption_day)},
            {},
            {getString(R.string.gameroons_independence_day)},
            {},
            {}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {1},
            {11},
            {},
            {},
            {20},
            {},
            {},
            {15},
            {},
            {1},
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
                    String generalBurkinaFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheCameroon, FESTIVAL_CAMEROON,FESTIVAL_NUM, RELIGION_GENERAL);

                    String[][] comFestival = obtainComFestival(year, month);
                    /*if(comFestival[i][j].equals(String.valueOf(R.string.new_year))){
                        comFestival[i][j] = String.valueOf(R.string.new_year)+"&"+String.valueOf(R.string.independence_day);
                    }*/
                    result = comFestival[i][j];

                    if(generalBurkinaFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = generalBurkinaFestival;
                        }else {
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
                if(month == 1 && gregorianMonth[i][j].equals("1")){
                    Log.d("6666", "DPCameroonCalendar_111_tmp[i][j]:"+tmp[i][j]);
                }
            }
        }
        return tmp;
    }
}

