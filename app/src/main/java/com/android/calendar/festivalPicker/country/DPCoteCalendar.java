package com.android.calendar.festivalPicker.country;

import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.festivalPicker.bizs.DPCommonFestival;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 科特
 * 12月13号，纪念穆斯林诞生日；比算的多一天
 *
 * Created by yongwen.huang on 2016/11/29.
 */
public class DPCoteCalendar extends DPCommonFestival{

    //科特普通节日
    public static final String[][] FESTIVAL_COTE = new String[12][31];
    /*private static final String[][] FESTIVAL_COTE = {
            {},
            {},
            {},
            {},
            {},
            {getString(R.string.music)},
            {},
            {getString(R.string.independence_day), getString(R.string.assumption_day)},
            {},
            {},
            {getString(R.string.all_saints_day), getString(R.string.national_peace_day)},
            {}
    };*/

    public static final int[][] FESTIVAL_NUM = {
            {},
            {},
            {},
            {},
            {},
            {21},
            {},
            {7, 15},
            {},
            {},
            {1, 15},
            {}};

    public static final String[][] ISLAMIC_COTE = new String[12][31];
    /*public static final String[][] ISLAMIC_COTE = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {getString(R.string.islamic_laila_tou_kadr)},
            {},
            {},
            {}
    };*/

    public static final int[][] ISLAMIC_COTE_DATE = {
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {27},
            {},
            {},
            {}
    };

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

    public String[][] buildFestival(int year, int month){
        Log.d("1111", "DPCoteCalendar_start");
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
                    String result = null;
                    String islamicCoteFestival = null;
                    //公历节日转化为回历,获得回历对象
                    Islamic l = GTI(g);
                    if(l != null){
                        islamicCoteFestival = obtainFestivalDateOfReligion(l, islamicFestivalCacheCote, ISLAMIC_COM, ISLAMIC_COTE_DATE, RELIGION_ISLAMIC);
                    }
                    String generalCoteFestival = obtainFestivalDateOfReligion(g, generalFestivalCacheCote, FESTIVAL_COTE,FESTIVAL_NUM, RELIGION_GENERAL);

                    String[][] comFestival = obtainComFestival(year, month);
                    if(comFestival != null){
                        result = comFestival[i][j];
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

                    if(islamicCoteFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = islamicCoteFestival;
                        }else {
                        /*String festival = null;
                        Islamic l = GTI(g);
                        if(l != null){
                            festival = getFestivalIslamic(l.m, l.d, ISLAMIC_COTE, ISLAMIC_COTE_DATE);
                        }*/
                            result = getStringFestival(result, islamicCoteFestival);
                        }
                    }

                    if(TextUtils.isEmpty(result)){
                        int[] mPentecost = getAscensionFestival(year, 1);
                        if(g.m == mPentecost[0] && g.d == mPentecost[1]){
                            result = christianityFestival[5];
                        }
                    }else {
                        String festival = null;
                        int[] mPentecost = getAscensionFestival(year, 1);
                        if(g.m == mPentecost[0] && g.d == mPentecost[1]){
                            festival = christianityFestival[5];
                        }
                        result = getStringFestival(result, festival);
                    }

                    if(generalCoteFestival != null){
                        if(TextUtils.isEmpty(result)){
                            result = generalCoteFestival;
                        }else {
//                            String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_COTE, FESTIVAL_NUM);
                            result = getStringFestival(result, generalCoteFestival);
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

    @Override
    public Set<String> buildMonthHoliday(int year, int month){
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, HOLIDAY[month - 1]);
        return tmp;
    }
}
