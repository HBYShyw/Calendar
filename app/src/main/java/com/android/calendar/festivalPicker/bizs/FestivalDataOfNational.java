package com.android.calendar.festivalPicker.bizs;

/**
 * 国家节日数据
 * Created by yongwen.huang on 2017/1/13.
 */
public class FestivalDataOfNational {
    //general
    public static final String[][] FESTIVAL_COM = new String[12][31];
    /*public static final String[][] FESTIVAL_COM = {
            {getString(R.string.new_year)},
            {getString(R.string.valentine_day)},//情人节
            {getString(R.string.women_day)},
            {},
            {getString(R.string.labour_day)},
            {},
            {},
            {},
            {},
            {},
            {},
            {getString(R.string.christmas), getString(R.string.boxing_day)}
    };*/

    public static final int[][] FESTIVAL_COM_DATE = {
            {1},
            {14},
            {8},
            {},
            {1},
            {},
            {},
            {},
            {},
            {},
            {},
            {25, 26}
    };

    //基督教节日
    public String[] JDFestival = new String[6];
    /*public static final String[] FESTIVAL_JD = new String[]{
            getString(R.string.good_friday),
            getString(R.string.holy_saturday),
            getString(R.string.easter_day),
            getString(R.string.easter_monday),
            getString(R.string.ascension_day),
            getString(R.string.Whit_Monday)
    };*/

    //非common//东正教节日
    public String[] orthodoxFestival = new String[4];
    /*public static final String[] FESTIVAL_ORTHODOX = new String[]{
            getString(R.string.coptic_good_friday),
            getString(R.string.coptic_holy_saturday),
            getString(R.string.coptic_easter_sunday),
            getString(R.string.spring_festival)
    };*/

    public String[] calculateFestival = new String[3];
    /*public static final String[] CALCULATE_FESTIVAL = new String[]{
            getString(R.string.father_day),
            getString(R.string.mother_day),
            getString(R.string.farmer_day)};//Farmer's Day !common*/

    //伊斯兰教传统节日,每年不一样，需要换算
    public static final String[][] ISLAMIC_COM = new String[12][31];
    /*public static final String[][] ISLAMIC_COM = {
            {getString(R.string.islamic_new_year)},//伊斯兰教新年
            {},
            {getString(R.string.islamic_al_Nabi)},//圣纪节,默罕默德诞生日
            {},
            {},
            {},
            {},
            {},
            {},
            {getString(R.string.islamic_eid_al_fitr)},//开斋节
            {},
            {getString(R.string.islamic_eid_al_adha)}//宰牲节，古尔邦节
    };*/

    public static final int[][] ISLAMIC_COM_DATE = {
            {1},
            {},
            {12},
            {},
            {},
            {},
            {},
            {},
            {},
            {1},
            {},
            {10}
    };

    //节假日
    public static final String[][] HOLIDAY_COM = {
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
}
