package com.android.calendar.festivalPicker.bizs;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class DPCalendar{
    protected final Calendar c = Calendar.getInstance();

    public final String FACTIONS_JD = "JD";
    public final String FACTIONS_ORTHODOX = "ORTHODOX";
    public final String RELIGION_GENERAL = "GENERAL";
    public final String RELIGION_ISLAMIC = "ISLAMIC";

    //公共节日
    public HashMap<Integer, HashMap<Integer, String[][]>> comFestivalDateCache = new HashMap<>();
    //公历节日
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalDateCache = new HashMap<>();
    //回历节日
    public HashMap<Integer, HashMap<Integer, String>> islamicFestivalDateCache = new HashMap<>();
    //基督节日
    public HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>> ChristianFestivalDateCache = new HashMap<>();
    //东正教节日
    public HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>> orthodoxFestivalDateCache = new HashMap<>();
    //Burkina
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheBurkina = new HashMap<>();
    //Cameroon
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheCameroon = new HashMap<>();
    //Cote
    public HashMap<Integer, HashMap<Integer, String>> islamicFestivalCacheCote = new HashMap<>();
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheCote = new HashMap<>();
    //Egypt
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheEgypt = new HashMap<>();
    //Ghana
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheGhana = new HashMap<>();
    //Kenya
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheKenya = new HashMap<>();
    //Malawi
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheMalawi = new HashMap<>();
    //Nigeria
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheNigeria = new HashMap<>();
    //Rwanda
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheRwanda = new HashMap<>();
    //Tanzania
    public HashMap<Integer, HashMap<Integer, String>> generalFestivalCacheTanzania = new HashMap<>();   

    //获取某年某月的节日数组
    public abstract String[][] buildMonthFestival(int year, int month);

    //获取某年某月的假期数组
    public abstract Set<String> buildMonthHoliday(int year, int month);

    //宗教
    public class Religion {
        public int d;
        public int m;
        public int y;
    }

    //gregorian
    public class Gregorian extends Religion{}

    //Islamic
    public class Islamic extends Religion{}

    //判断某年是否为闰年
    public boolean isLeapYear(int year){
        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
    }

    //判断给定日期是否为今天，选择的天与当前日期相同
    public boolean isToday(int year, int month, int day){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(year, month - 1, day);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) &&
                (c1.get(Calendar.MONTH) == (c2.get(Calendar.MONTH))) &&
                (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    //生成某年某月的公历天数数组
    public String[][] buildMonthG(int year, int month){
        c.clear();
        String tmp[][] = new String[6][7];
        c.set(year, month - 1, 1);
        int daysInMonth = 0;
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 ||
                month == 12){
            daysInMonth = 31;
        }else if(month == 4 || month == 6 || month == 9 || month == 11){
            daysInMonth = 30;
        }else if(month == 2){
            if(isLeapYear(year)){
                daysInMonth = 29;
            }else{
                daysInMonth = 28;
            }
        }
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        int day = 1;
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                tmp[i][j] = "";
                if(i == 0 && j >= dayOfWeek){
                    tmp[i][j] = "" + day;
                    day++;
                }else if(i > 0 && day <= daysInMonth){
                    tmp[i][j] = "" + day;
                    day++;
                }
            }
        }
        return tmp;
    }

    //生成某年某月的埃及周末日期集合
    public Set<String> buildIslamicMonthWeekend(int year, int month){
        Set<String> set = new HashSet<>();
        c.clear();
        c.set(year, month - 1, 1);
        do{
            int day = c.get(Calendar.DAY_OF_WEEK);
            if(day == Calendar.SATURDAY || day == Calendar.FRIDAY){
                set.add(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
            }
            c.add(Calendar.DAY_OF_YEAR, 1);
        }while(c.get(Calendar.MONTH) == month - 1);
        return set;
    }

    //生成某年某月的周末日期集合
    public Set<String> buildMonthWeekend(int year, int month){
        Set<String> set = new HashSet<>();
        c.clear();
        c.set(year, month - 1, 1);//设置这个月一号2016/1/1
        do{
            int day = c.get(Calendar.DAY_OF_WEEK);
            if(day == Calendar.SATURDAY || day == Calendar.SUNDAY){
                set.add(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
            }
            c.add(Calendar.DAY_OF_YEAR, 1);//加上一天
        }while(c.get(Calendar.MONTH) == month - 1);
        return set;
    }

    protected long GToNum(int year, int month, int day){
        month = (month + 9) % 12;
        year = year - month / 10;
        return 365 * year + year / 4 - year / 100 + year / 400 + (month * 306 + 5) / 10 + (day - 1);
    }

    protected int getBitInt(int data, int length, int shift){
        return (data & (((1 << length) - 1) << shift)) >> shift;
    }
}

