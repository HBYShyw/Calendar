package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DPEgyptCalendar extends DPCommonFestival{

    //埃及节日
    public static final String[][] FESTIVAL_EGYPT = new String[12][31];
    /*private static final String[][] FESTIVAL_EGYPT = {
            {getString(R.string.new_year), getString(R.string.coptic_christmas_day), getString(R.string.revolution_day)},
            {},
            {},
            {getString(R.string.sinai_liberation_day)},
            {getString(R.string.labour_day)},
            {getString(R.string.revolution_day)},
            {getString(R.string.revolution_day)},
            {},
            {},
            {getString(R.string.armed_forces_day)},
            {},
            {}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {1, 7, 25},
            {},
            {},
            {25},
            {1},
            {30},
            {23},
            {},
            {},
            {6},
            {},
            {}
    };

    //节假日
    public static final String[][] HOLIDAY = {
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
        /*getInitFestivalData(R.array.egypt,FESTIVAL_NUM,FESTIVAL_EGYPT);
        getInitFestivalData(R.array.islamic, ISLAMIC_COM_DATE, ISLAMIC_COM);
        initOrthodoxFestival();*/
        return buildFestival(year, month);
    }

    @Override
    public Set<String> buildMonthHoliday(int year, int month){
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, HOLIDAY[month - 1]);
        return tmp;
    }

    public String[][] buildFestival(int year, int month){
        Log.d("1111", "DPEgyptCalendar_coming");
        String[][] gregorianMonth = buildMonthG(year, month);//生成某年某月的公历天数数组
        Gregorian g = new Gregorian();
        String tmp[][] = new String[6][7];
        //重要的是这节日下的缓存
        for(int i = 0; i < tmp.length; i++){
            for(int j = 0; j < tmp[0].length; j++){
                tmp[i][j] = "";
                if(!TextUtils.isEmpty(gregorianMonth[i][j])){
                    g.y = year;
                    g.m = month;
                    g.d = Integer.valueOf(gregorianMonth[i][j]);
                    String result = null;
                    String orthodoxFestival = obtainFestivalDateOfFactions(g, orthodoxFestivalDateCache, FACTIONS_ORTHODOX);
                    String islamicEgyptFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheEgypt, FESTIVAL_EGYPT,FESTIVAL_NUM, RELIGION_GENERAL);

                    Islamic l = GTI(g);
                    if(l != null){
//                        result = getFestivalIslamic(l.m, l.d, ISLAMIC_COM, ISLAMIC_COM_DATE);
                        result = obtainFestivalDateOfReligion(l, islamicFestivalDateCache, ISLAMIC_COM,ISLAMIC_COM_DATE, RELIGION_ISLAMIC);
                    }

                    if(islamicEgyptFestival != null){
                        if(TextUtils.isEmpty(result)){
                            //result = getFestivalGregorian(g.m, g.d, FESTIVAL_EGYPT, FESTIVAL_NUM);
                            result = islamicEgyptFestival;
                        }else{
                            //String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_EGYPT, FESTIVAL_NUM);
                            result = getStringFestival(result, islamicEgyptFestival);
                        }
                    }

                    if(orthodoxFestival != null){
                        if(TextUtils.isEmpty(result)){
                            //result = getOrthodoxFestival(year, g);
                            result = orthodoxFestival;
                        }else{
                            //String festival = getOrthodoxFestival(year, g);
                            //String festival = obtainFestivalDateOfFactions(g, orthodoxFestivalDateCache, FACTIONS_ORTHODOX);
                            result = getStringFestival(result, orthodoxFestival);
                        }
                    }

                    if(!TextUtils.isEmpty(result)){
                        tmp[i][j] = result;
                    }

                    /*if(result != null){
                        Log.d("8888", "buildFestival****result:"+result);
                    }*/
                }
            }
        }
        return tmp;
    }
}
