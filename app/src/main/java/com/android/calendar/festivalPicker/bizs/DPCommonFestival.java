package com.android.calendar.festivalPicker.bizs;

import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.R;
import com.android.calendar.festivalPicker.utils.ResourceStringUtil;

import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;

import java.util.Calendar;
import java.util.HashMap;

/**
 * 共同重要节日类
 * 共同重要节日：
 一月：新年
 三月或者四月：复活节  （根据春分月圆第一周来定具体时间）
 五月：劳动节
 七月：开斋节
 九月：宰牲节（穆斯林节日）
 十二月：圣诞节，节礼日

 * Created by yongwen.huang on 2016/11/29.
 */
public abstract class DPCommonFestival extends DPCalendar{

    private final int MAY = 5;
    private final int APRIL = 4;
    private final int JUNE = 6;
    private final int MARCH = 3;
    private static ResourceStringUtil resourceStringUtil = ResourceStringUtil.getInstance();

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
    public String[] christianityFestival = new String[6];
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

    //初始化共同节日数据
    public void initCommonFestival(){
        initGeneralFestival();
        initIslamicFestival();
        initJDFestival();
        initCalculateFestival();
    }

    public void initGeneralFestival(){
        getInitFestivalData(R.array.common, FESTIVAL_COM_DATE, FESTIVAL_COM);
    }

    public void initIslamicFestival(){
        getInitFestivalData(R.array.islamic, ISLAMIC_COM_DATE, ISLAMIC_COM);
    }

    public void initJDFestival(){
        christianityFestival = getStringArray(R.array.festival_jd);
    }

    public void initOrthodoxFestival(){
        orthodoxFestival = getStringArray(R.array.festival_orthodox);
    }

	public void initCalculateFestival(){
        calculateFestival = getStringArray(R.array.festival_calculate);
    }

    //获得缓存中的general festival
    public String[][] obtainComFestival(int year, int month){
        HashMap<Integer, String[][]> comFestivalOfYear = comFestivalDateCache.get(year);
        if(null != comFestivalOfYear && comFestivalOfYear.size() != 0){
            String[][] comFestivalOfMonth = comFestivalOfYear.get(month);
            if (comFestivalOfMonth != null) {
                //Log.d("1111", "null != dataFestivalOfYear****obtainComFestival"+"date_exist");
                return comFestivalOfMonth;
            }
            comFestivalOfMonth = buildComFestival(year, month);
            comFestivalOfYear.put(month, comFestivalOfMonth);
            return comFestivalOfMonth;
        }else if(comFestivalOfYear == null){
            comFestivalOfYear = new HashMap<>();
            String[][] comFestivalOfMonth = buildComFestival(year, month);
            comFestivalOfYear.put(month, comFestivalOfMonth);
            comFestivalDateCache.put(year, comFestivalOfYear);
            return comFestivalOfMonth;
        }
        return null;
    }

    //返回公共节日的字符串
    private String[][] buildComFestival(int year, int month){
        //Log.d("1111", "buildComFestival***start");
        String[][] gregorianMonth = buildMonthG(year, month);//生成某年某月的公历天数数组
        //公历
        Gregorian g = new Gregorian();
        String tmp[][] = new String[6][7];
        for(int i = 0; i < tmp.length; i++){
            for(int j = 0; j < tmp[0].length; j++){
                tmp[i][j] = "";
                if(!TextUtils.isEmpty(gregorianMonth[i][j])){
                    g.y = year;
                    g.m = month;
                    g.d = Integer.valueOf(gregorianMonth[i][j]);
                    String result = "";
                    String ChristianFestival = obtainFestivalDateOfFactions(g, ChristianFestivalDateCache, FACTIONS_JD);
                    String generalFestival = obtainFestivalDateOfReligion(g, generalFestivalDateCache, FESTIVAL_COM,FESTIVAL_COM_DATE, RELIGION_GENERAL);

                    if(year >= 1900 && year <= 2100){
                        Islamic l = GTI(g);//公历节日转化为回历,获得回历对象
                        if(l != null){
                            //Log.d("555", "111[  buildFestival---" + "," + l + l.m + "," + l.d);
//                          result = getFestivalIslamic(l.m, l.d, ISLAMIC_COM, ISLAMIC_COM_DATE);
                            result = obtainFestivalDateOfReligion(l, islamicFestivalDateCache, ISLAMIC_COM,ISLAMIC_COM_DATE, RELIGION_ISLAMIC);
                        }

                        if(ChristianFestival != null){
                            if(TextUtils.isEmpty(result)){
                                //计算西派耶稣相关节日
                                //result = getChristianityComFestival(year, g);
                                result = ChristianFestival;
                            }else {
                                //String festival = getChristianityComFestival(year, g);
                                //String festival =  obtainFestivalDateOfFactions(g, ChristianFestivalDateCache, FACTIONS_JD);
                                //Log.d("66666", "ChristianFestivalDateCache:"+ChristianFestivalDateCache);
                                //result:Islamic
                                result = getStringFestival(result, ChristianFestival);
                            }
                        }

                        if(generalFestival != null){
                            if(TextUtils.isEmpty(result)){
                                //result = getFestivalGregorian(g.m, g.d,FESTIVAL_COM,FESTIVAL_COM_DATE);
                                result = generalFestival;
                            }else{
                                //String festival = getFestivalGregorian(g.m, g.d, FESTIVAL_COM, FESTIVAL_COM_DATE);
                                //String festival = obtainFestivalDateOfReligion(g, generalFestivalDateCache, FESTIVAL_COM,FESTIVAL_COM_DATE, RELIGION_GENERAL);
                                //result:JD
                                //Log.d("66666", "generalFestivalDateCache:"+generalFestivalDateCache);
                                result = getStringFestival(result, generalFestival);
                            }
                        }

                        if(TextUtils.isEmpty(result)){//father’s day
                            if(g.m == JUNE){
                                int fatherDay = getFatherDay(g.y);
                                if(fatherDay == g.d){
                                    tmp[i][j] = calculateFestival[0];
                                }
                            }
                        }else {
                            String festival = null;
                            if(g.m == JUNE){
                                int fatherDay = getFatherDay(g.y);
                                if(fatherDay == g.d){
                                    festival = calculateFestival[0];
                                }
                            }
                            //general festival
                            result = getStringFestival(result, festival);
                        }

                        if(TextUtils.isEmpty(result)){//mother’s day 重复的需要，重点
                            if(g.m == MAY){
                                int motherDay = getMotherDay(g.y);
                                if(motherDay == g.d){
                                    tmp[i][j] = calculateFestival[1];
                                }
                            }
                        }else {
                            String festival = null;
                            if(g.m == MAY){
                                int motherDay = getMotherDay(g.y);
                                if(motherDay == g.d){
                                    festival = calculateFestival[1];
                                }
                            }
                            //father’s day
                            result = getStringFestival(result, festival);
                        }

                        if(!TextUtils.isEmpty(result)){
                            tmp[i][j] = result;
                        }
                    }
                }
            }
        }
        return tmp;
    }

    //派别
    public String obtainFestivalDateOfFactions(Gregorian g, HashMap<Integer, HashMap<Integer,
            HashMap<Integer, String>>> gFestivalDateCache, String factionsStr){ //东正教/西派
        //Log.d("333", "factionsStr=" + factionsStr + "g.y=" + g.y + ",g.m=" + g.m + ",g.d=" + g.d);
        HashMap<Integer, HashMap<Integer, String>> gFestivalOfYear = gFestivalDateCache.get(g.y);
        HashMap<Integer, String> gFestivalOfMonth;
        if(gFestivalOfYear != null && gFestivalOfYear.size() != 0){
            //Log.d("333", "gFestivalOfYear != null**" + "g.y=" + g.y);
            gFestivalOfMonth = gFestivalOfYear.get(g.m);
            if(gFestivalOfMonth != null && gFestivalOfMonth.size() != 0){
                //Log.d("333", "gFestivalOfMonth != null**" + "g.m=" + g.m);
                String gFestival = gFestivalOfMonth.get(g.d);
                if(gFestival != null){
                    //Log.d("333", "null != gFestival****obtainFestivalDateOfFactions_start_111" + "coming");
                    return gFestival;
                }
                //Log.d("333", "null == gFestival****obtainFestivalDateOfFactions_start_111" + "coming");
                return getFactionOfData(g, factionsStr, gFestivalOfMonth, gFestivalOfYear);
            }else{
                //Log.d("333", "gFestivalOfMonth == null**" + "g.m=" + g.m);
                gFestivalOfMonth = new HashMap<>();
                return getFactionOfData(g, factionsStr, gFestivalOfMonth, gFestivalOfYear);
            }
        }else{
            //Log.d("333", "gFestivalOfYear == null**" + "g.y=" + g.y);
            gFestivalOfYear = new HashMap<>();
            gFestivalOfMonth = new HashMap<>();
            Factions ft = new Factions();
            if(FACTIONS_JD.equals(factionsStr)){
                ft = getChristianityComFestival(g.y, g);
            }else if(FACTIONS_ORTHODOX.equals(factionsStr)){
                ft = getOrthodoxFestival(g.y, g);
            }
            if(!TextUtils.isEmpty(ft.festival)){
                gFestivalOfMonth.put(ft.day, ft.festival);
                gFestivalOfYear.put(ft.month, gFestivalOfMonth);
                gFestivalDateCache.put(g.y, gFestivalOfYear);
                return ft.festival;
            }
        }
        return null;
    }

    private String getFactionOfData(Gregorian g, String factionsStr, HashMap<Integer,
            String> gFestivalOfMonth, HashMap<Integer, HashMap<Integer, String>> gFestivalOfYear){
        Factions ft = new Factions();
        if(FACTIONS_JD.equals(factionsStr)){
            ft = getChristianityComFestival(g.y, g);
        }else if(FACTIONS_ORTHODOX.equals(factionsStr)){
            ft = getOrthodoxFestival(g.y, g);
        }
        if(!TextUtils.isEmpty(ft.festival)){
            //Log.d("333", "factionsStr=" + factionsStr + "g.y=" + g.y + ",g.m=" + g.m + ",g.d=" + g.d);
            //Log.d("333", "factionsStr=" + factionsStr + "g.y=" + g.y + ",ft.m=" + ft.month + ",ft.d=" + ft.day + ",ft.festival=" + ft.festival);
            gFestivalOfMonth.put(ft.day, ft.festival);
            gFestivalOfYear.put(ft.month, gFestivalOfMonth);
            return ft.festival;
        }
        return null;
    }

    class Factions {
        int month;
        int day;
        String festival;
    }

    //基督教
    class Christian extends Factions{}

    //东正教
    class Orthodox  extends Factions{}

    //计算基督教耶稣相关节日
    private Christian getChristianityComFestival(int year, Gregorian g){
        //Log.d("66", "getChristianityComFestival***start");
        Christian jd = new Christian();
        int[] monthAndDay = new int[2];
        String result = "";
        int[] easterFestival = getEasterFestival(year);//计算西派复活节日期
        for(int a = 2; a >= 0; a--){
            if(g.m == easterFestival[0] && g.d == easterFestival[1] - a){
                //Log.d("444", "111[  buildFestival---start1" + "," + g.m + "," + g.d);
                result = christianityFestival[2 - a];
                monthAndDay[0] = g.m;
                monthAndDay[1] = g.d;
            }
        }

        if(easterFestival[0] == APRIL && (easterFestival[1] == 1 || easterFestival[1] == 2)){
            if(easterFestival[1] == 1){
                if(g.d == 31 && g.m == MARCH){
                    result = christianityFestival[1];
                    monthAndDay[0] = g.m;
                    monthAndDay[1] = g.d;
                }else if(g.d == 30 && g.m == MARCH){
                    result = christianityFestival[0];
                    monthAndDay[0] = g.m;
                    monthAndDay[1] = g.d;
                }
            }else {
                if(g.d == 31 && g.m == MARCH){
                    result = christianityFestival[0];
                    monthAndDay[0] = g.m;
                    monthAndDay[1] = g.d;
                }
            }
        }

        if(g.m == easterFestival[0] && g.d == easterFestival[1] + 1 && easterFestival[1] < 31){
            //Log.d("444", "111[  buildFestival---start1" + "," + g.m + "," + g.d);
            result = christianityFestival[3];
            monthAndDay[0] = g.m;
            monthAndDay[1] = g.d;
        }else if(easterFestival[1] + 1 == 32 && g.m == easterFestival[0] + 1 && g.d == 1){
            //Log.d("444", "111[  buildFestival---start1" + "," + g.m + "," + g.d);
            result = christianityFestival[3];
            monthAndDay[0] = g.m;
            monthAndDay[1] = g.d;
        }

        jd.month = monthAndDay[0];
        jd.day = monthAndDay[1];
        jd.festival = result;
        return jd;
    }

    //计算东正教耶稣相关节日
    private Orthodox getOrthodoxFestival(int year, Gregorian g){
        //Log.d("66", "getOrthodoxFestival***start");
        Orthodox ort = new Orthodox();
        int[] monthAndDay = new int[2];
        String result = "";
        int[] orthodoxEaster = getOrthodoxEaster(year);//计算东正教复活节
        for(int a = 2; a >= 0; a--){
            if(g.m == orthodoxEaster[0] && g.d == orthodoxEaster[1] - a){
                //Log.d("444", "111[  buildFestival---start2" + "," + g.m + "," + g.d);
                result = orthodoxFestival[2 - a];
                monthAndDay[0] = g.m;
                monthAndDay[1] = g.d;
            }
        }

        if(orthodoxEaster[0] == 5 && (orthodoxEaster[1] == 1 || orthodoxEaster[1] == 2)){
            if(orthodoxEaster[1] == 1){
                if(g.d == 30 && g.m == APRIL){
                    result = orthodoxFestival[1];
                    monthAndDay[0] = g.m;
                    monthAndDay[1] = g.d;
                }else if(g.d == 29 && g.m == APRIL){
                    result = orthodoxFestival[0];
                    monthAndDay[0] = g.m;
                    monthAndDay[1] = g.d;
                }
            }else {
                if(g.d == 30 && g.m == APRIL){
                    result = orthodoxFestival[0];
                    monthAndDay[0] = g.m;
                    monthAndDay[1] = g.d;
                }
            }
        }

        if(g.m == orthodoxEaster[0] && g.d == orthodoxEaster[1] + 1 && orthodoxEaster[1] < 30){
            //Log.d("444", "111[  buildFestival---start1" + "," + g.m + "," + g.d);
            result = orthodoxFestival[3];
            monthAndDay[0] = g.m;
            monthAndDay[1] = g.d;
        }else if(orthodoxEaster[1] == 31 && g.m == orthodoxEaster[0] + 1 && g.d == 1){
            //Log.d("444", "111[  buildFestival---start1" + "," + g.m + "," + g.d);
            result = orthodoxFestival[3];
            monthAndDay[0] = g.m;
            monthAndDay[1] = g.d;
        }
        ort.month = monthAndDay[0];
        ort.day = monthAndDay[1];
        ort.festival = result;
        return ort;
    }

    public String obtainFestivalDateOfReligion(Religion religion, HashMap<Integer, HashMap<Integer, String>> religionFestivalDateCache,
                                                String[][] religion_data, int[][] religion_date, String religionStr){ //宗教
        HashMap<Integer, String> festivalOfReligion = religionFestivalDateCache.get(religion.m);
        if(festivalOfReligion != null && festivalOfReligion.size() != 0){
            String rFestival = festivalOfReligion.get(religion.d);
            if(rFestival != null){
                return rFestival;
            }
            if(RELIGION_GENERAL.equals(religionStr)){
                rFestival = getFestivalGregorian(religion.m, religion.d, religion_data, religion_date);
            }else if(RELIGION_ISLAMIC.equals(religionStr)){
                rFestival = getFestivalIslamic(religion.m, religion.d, religion_data, religion_date);
            }
            festivalOfReligion.put(religion.d, rFestival);
            return rFestival;
        }else {
            festivalOfReligion = new HashMap<>();
            String rFestival = null;
            if(RELIGION_GENERAL.equals(religionStr)){
                rFestival = getFestivalGregorian(religion.m, religion.d, religion_data, religion_date);
            }else if(RELIGION_ISLAMIC.equals(religionStr)){
                rFestival = getFestivalIslamic(religion.m, religion.d, religion_data, religion_date);
            }
            festivalOfReligion.put(religion.d, rFestival);
            religionFestivalDateCache.put(religion.m, festivalOfReligion);
            return rFestival;
        }
    }

    //显示general一般节日
    private String getFestivalGregorian(int month, int day, String[][] festival_data, int[][] festival_date) {
        //Log.d("66", "getFestivalGregorian***start");
        String tmp = "";
        int[] daysInMonth = festival_date[month - 1];
        for (int i = 0; i < daysInMonth.length; i++) {
            if (day == daysInMonth[i]) {
                tmp = festival_data[month - 1][i];
            }
        }
        return tmp;
    }

    private String getFestivalIslamic(int month, int day, String[][] islamic_data, int[][] islamic_date) {
        //Log.d("66", "getFestivalIslamic***start");
        String tmp = "";
        int[] daysInMonth = islamic_date[month - 1];
        for (int i = 0; i < daysInMonth.length; i++) {
            if (day == daysInMonth[i]) {
                tmp = islamic_data[month - 1][i];
            }
        }
        return tmp;
    }

    //计算父亲节
    private int getFatherDay(int y){
        Log.d("11", "getFatherDay***start");
        int day;
        c.set(y,6,1);
        int weekday = c.get(Calendar.DAY_OF_WEEK);

        if(weekday == 3){//星期日
            weekday = 7;
        }else if(weekday == 4){
            weekday = 1;
        }else if(weekday == 5){
            weekday = 2;
        }else if(weekday == 6){
            weekday = 3;
        }else if(weekday == 7){
            weekday = 4;
        }else if(weekday == 1){
            weekday = 5;
        }else if(weekday == 2){
            weekday = 6;
        }

        day = (7 - weekday) + 15;
        Log.d("6666", y + "/" + 6 + "/" + day);
        return day;
    }

    //计算母亲节
    private int getMotherDay(int y){
        Log.d("11", "getMotherDay***start");
        int day;
        c.set(y,5,1);
        int weekday = c.get(Calendar.DAY_OF_WEEK);

        if(weekday == 4){//星期日
            weekday = 7;
        }else if(weekday == 5){
            weekday = 1;
        }else if(weekday == 6){
            weekday = 2;
        }else if(weekday == 7){
            weekday = 3;
        }else if(weekday == 1){
            weekday = 4;
        }else if(weekday == 2){
            weekday = 5;
        }else if(weekday == 3){
            weekday = 6;
        }

        day = (7 - weekday) + 8;
        Log.d("6666", y + "/" + 5 + "/" + day);
        return day;
    }

    //计算农民日
    public int getFarmerDay(int y){
        Log.d("11", "getFarmerDay***start");
        int day;
        c.set(y,12,1);
        int weekday = c.get(Calendar.DAY_OF_WEEK);

        if(weekday == 4){//星期日
            weekday = 7;
        }else if(weekday == 5){
            weekday = 1;
        }else if(weekday == 6){
            weekday = 2;
        }else if(weekday == 7){
            weekday = 3;
        }else if(weekday == 1){
            weekday = 4;
        }else if(weekday == 2){
            weekday = 5;
        }else if(weekday == 3){
            weekday = 6;
        }

        day = (7 - weekday) + 1;
        Log.d("6666", y + "/" + 12 + "/" + day);
        return day;
    }

    //计算新教和天主教的复活节,返回几月几号
    private int[] getEasterFestival(int year){
        int day;
        int easterMonth;

        int N = year - 1900;
        int A = N - 19 * (N / 19);
        int Q = N / 4;
        int B = (7 * A + 1) / 19;
        int M = (11 * A + 4 - B) - 29 * ((11 * A + 4 - B) / 29);
        int W = N + Q + 31 - M - ((N + Q + 31 - M) / 7) * 7;

        int D = 25 - M - W;
        if(D == 0){
            day = 31;
            easterMonth = 3;
        }else if(D > 0){
            day = D;
            easterMonth = 4;
        }else{
            day = D + 31;
            easterMonth = 3;
        }
        return new int[]{easterMonth,day};
    }

    //计算东正教的复活节,返回几月几号
    private int[] getOrthodoxEaster(int y){
	//Log.d("66", "getOrthodoxEaster***start");
        int OrtDay;
        int OrtMonth;
        int a;

        int d = (y%19*19 + 15)%30;
        int e = (y%4*2+y%7*4-d+34)%7+d+127;
        int m = e/31;
        if(m > 4){
            a = e%31 + 2;//5
        }else {
            a = e%31 + 1;
        }

        if(a > 30){
            a = 1;
            m = 5;
        }
        OrtMonth = m;
        OrtDay = a;
        return new int[]{OrtMonth,OrtDay};
    }

    //计算耶稣飞升节
    public int[] getAscensionFestival(int year, int flag){
	//Log.d("11", "getAscensionFestival***start");
        int ascension_Day = 0;
        int ascension_Month = 0;
        int[] easterFestival = getEasterFestival(year);
        if(easterFestival[0] == APRIL){
            ascension_Month = easterFestival[0] + 1;
            if(flag == 0){
                ascension_Day = 9 + easterFestival[1];//耶稣升天节
            }else if(flag == 1){
                ascension_Day = 19 + easterFestival[1];//圣灵降临节
            }
        }else if(easterFestival[0] == MARCH) {
            ascension_Month = easterFestival[0] + 2;
            if(flag == 0){
                ascension_Day = easterFestival[1] - 22;//耶稣升天节
            }else if(flag == 1){
                ascension_Day = easterFestival[1] - 12;//圣灵降临节
            }
        }
        return new int[]{ascension_Month,ascension_Day};
    }

    public Islamic GTI(Gregorian g){//公历转回历
        IslamicChronology instance = IslamicChronology.getInstance();

        // setup date object for midday on May Day 2004 (ISO year 2004)
        int day = g.d;
        int month = g.m;
        int year = g.y;

        DateTime dtISO = new DateTime(year,month,day,0, 0, 0, 0);

        DateTime dtIslamic = dtISO.withChronology(instance);

        int yearIslamic = dtIslamic.getYear();
        int monthOfYear = dtIslamic.getMonthOfYear();
        int dayOfMonth = dtIslamic.getDayOfMonth();

        Islamic islamic = new Islamic();

        islamic.y = yearIslamic;
        islamic.m = monthOfYear;
        islamic.d = dayOfMonth;

        return islamic;
    }

    //获得节日字符串（包括重叠的）
    public String getStringFestival(String result, String festival){
        //Log.d("6666", "getStringFestival_1111"+"result:"+result + "festival"+ festival);
        if(!TextUtils.isEmpty(festival)){
            return result + "#" + festival;
        }else {
            return result;
        }
    }

    //获取资源初始化节日数据
    protected void getInitFestivalData(int resId, int[][] FESTIVAL_NUM, String[][] FESTIVAL_DATA){
        TypedArray monthArray = obtainTypedArray(resId);
        for(int i = 0; i < FESTIVAL_NUM.length; i++){
            for(int j = 0; j < FESTIVAL_NUM[i].length; j++){
                TypedArray festivalArray = getSlice(monthArray, i);
                FESTIVAL_DATA[i][j] = getString(festivalArray, j);
                if(FESTIVAL_DATA[i][j] != null){
                    Log.d("4444", "FESTIVAL_DATA[i][j]:"+FESTIVAL_DATA[i][j]+"i"+i+"j"+j);
                }
                festivalArray.recycle();
            }
        }
        monthArray.recycle();
    }

    protected TypedArray getSlice(TypedArray ta, long k) {
        int resId = ta.getResourceId((int)k, 0);
        return resourceStringUtil.obtainTypedArray(resId);
    }

    protected String getString(TypedArray ta, long k) {
        return ta.getString((int) k);
    }

    protected TypedArray obtainTypedArray(int resId){
        return resourceStringUtil.obtainTypedArray(resId);
    }

    protected static String getString(int resId){
        return resourceStringUtil.getString(resId);
    }

    protected static String[] getStringArray(int resId){
        return resourceStringUtil.getStringArray(resId);
    }
}
