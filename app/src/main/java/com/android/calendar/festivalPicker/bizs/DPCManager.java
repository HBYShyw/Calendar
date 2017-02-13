package com.android.calendar.festivalPicker.bizs;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.R;
import com.android.calendar.festivalPicker.country.DPBurkinaCalendar;
import com.android.calendar.festivalPicker.country.DPCNCalendar;
import com.android.calendar.festivalPicker.country.DPCameroonCalendar;
import com.android.calendar.festivalPicker.country.DPCoteCalendar;
import com.android.calendar.festivalPicker.country.DPDefaultCalendar;
import com.android.calendar.festivalPicker.country.DPEgyptCalendar;
import com.android.calendar.festivalPicker.country.DPGhanaCalendar;
import com.android.calendar.festivalPicker.country.DPKenyaCalendar;
import com.android.calendar.festivalPicker.country.DPMalawiCalendar;
import com.android.calendar.festivalPicker.country.DPNigeriaCalendar;
import com.android.calendar.festivalPicker.country.DPRwandaCalendar;
import com.android.calendar.festivalPicker.country.DPTanzaniaCalendar;
import com.android.calendar.festivalPicker.country.DPUSCalendar;
import com.android.calendar.festivalPicker.entities.DPInfo;
import com.android.calendar.festivalPicker.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 月历管理器，日期管理器，包含一些常用的日期操作方法。
 * The manager of date picker.
 *
 * @author HYW 2015-12-26
 */
public final class DPCManager{
    private static final String TAG = "DPCManager";
    private static final HashMap<Integer, HashMap<Integer, DPInfo[][]>> DATE_CACHE = new HashMap<>();
    private final String EGYPT = "Egypt";
    private boolean initDataFlag;
    private String currentCountry;

    private static final HashMap<String, Set<String>> DECOR_CACHE_BG = new HashMap<>();
    private static final HashMap<String, Set<String>> DECOR_CACHE_TL = new HashMap<>();
    private static final HashMap<String, Set<String>> DECOR_CACHE_T = new HashMap<>();
    private static final HashMap<String, Set<String>> DECOR_CACHE_TR = new HashMap<>();
    private static final HashMap<String, Set<String>> DECOR_CACHE_L = new HashMap<>();
    private static final HashMap<String, Set<String>> DECOR_CACHE_R = new HashMap<>();
    private static DPCManager sManager;

    public DPCommonFestival c;

    private DPCManager() {
        Log.d("3333", "DPCManager()***start");
        initDataFlag = false;
        // 默认显示为中文日历
        /*获取系统语言：Locale.getDefault().getLanguage()
        获取应用语言：getResources().getConfiguration().locale.getLanguage()*/

        //获得开机向导中设置的国家,适配默认国家节日
        String default_country = PreferenceUtil.getString("default_country", EGYPT);

        String select_country = PreferenceUtil.getString("select_country", null);//手动选择的国家

        boolean system_default = PreferenceUtil.getBoolean("system_default", false);

        //获得的是系统当前国家 us,cn
//        String locale_country = Locale.getDefault().getCountry().toLowerCase();
        /*if (locale_country.equals("cn")) {
            initCalendar(new DPCNCalendar());
        } else{
            initCalendar(new DPUSCalendar());//us
        }*/
        Log.d("3333", "default_country:" + default_country+"current_Country:"+select_country+"system_default:"+system_default);

        if(system_default){
            if(default_country != null){
                switchObjectOfFestival(default_country);
                currentCountry = default_country;
            }
        } else {
            //判断手动选择与否
            if(select_country != null){
                switchObjectOfFestival(select_country);
                currentCountry = select_country;
            }/*else {
                //根据app语言适配
                switchObjectOfFestival(locale_country);
            }*/
        }
        //android.util.Log.d("333", "111[  DPCManager---1]" +"locale:" + locale+",Language: "+language);
    }

    private void initFestivalData(){
        if (c instanceof DPUSCalendar) {
            c.getInitFestivalData(R.array.America,DPUSCalendar.FESTIVAL_US_DATE,DPUSCalendar.FESTIVAL_US);
        } else if(c instanceof DPNigeriaCalendar){
            c.getInitFestivalData(R.array.Nigeria,DPNigeriaCalendar.FESTIVAL_NUM,DPNigeriaCalendar.FESTIVAL_NIGERIA);
            c.initCommonFestival();
        } else if(c instanceof DPKenyaCalendar){
            c.getInitFestivalData(R.array.Kenya,DPKenyaCalendar.FESTIVAL_NUM,DPKenyaCalendar.FESTIVAL_KENYA);
            c.initCommonFestival();
        } else if(c instanceof DPTanzaniaCalendar){
            c.getInitFestivalData(R.array.Tanzania,DPTanzaniaCalendar.FESTIVAL_NUM,DPTanzaniaCalendar.FESTIVAL_TANZANIA);
            c.initCommonFestival();
        } else if(c instanceof DPGhanaCalendar){
            c.getInitFestivalData(R.array.Ghana,DPGhanaCalendar.FESTIVAL_NUM,DPGhanaCalendar.FESTIVAL_GHANA);
            c.initCommonFestival();
        } else if(c instanceof DPEgyptCalendar){
            //Egypt
            c.getInitFestivalData(R.array.Egypt,DPEgyptCalendar.FESTIVAL_NUM,DPEgyptCalendar.FESTIVAL_EGYPT);
            c.getInitFestivalData(R.array.islamic, DPEgyptCalendar.ISLAMIC_COM_DATE, DPCommonFestival.ISLAMIC_COM);
            c.initOrthodoxFestival();
        } else if(c instanceof DPCameroonCalendar){
            c.getInitFestivalData(R.array.Cameroon,DPCameroonCalendar.FESTIVAL_NUM,DPCameroonCalendar.FESTIVAL_CAMEROON);
            c.initCommonFestival();
        } else if(c instanceof DPRwandaCalendar){
            c.getInitFestivalData(R.array.Rwanda,DPRwandaCalendar.FESTIVAL_NUM,DPRwandaCalendar.FESTIVAL_RWANDA);
            c.initCommonFestival();
        } else if(c instanceof DPCoteCalendar){
            c.getInitFestivalData(R.array.Cote,DPCoteCalendar.FESTIVAL_NUM,DPCoteCalendar.FESTIVAL_COTE);
            c.getInitFestivalData(R.array.Islamic_cote,DPCoteCalendar.ISLAMIC_COTE_DATE,DPCoteCalendar.ISLAMIC_COTE);
            c.initCommonFestival();
        } else if(c instanceof DPMalawiCalendar){
            c.getInitFestivalData(R.array.Malawi,DPMalawiCalendar.FESTIVAL_NUM,DPMalawiCalendar.FESTIVAL_MALAWI);
            c.initCommonFestival();
        } else if(c instanceof DPBurkinaCalendar){
            c.getInitFestivalData(R.array.Burkina,DPBurkinaCalendar.FESTIVAL_NUM,DPBurkinaCalendar.FESTIVAL_BURKINA);
            c.initCommonFestival();
        }
        initDataFlag = true;
    }
    /*for (int a = 0; a < 12; a++){
        for(int b = 0; b < 31; b++){
            String s = DPCommonFestival.ISLAMIC_COM[a][b];
            if(s != null){
                Log.d("4444", "initFestivalData_DPCommonFestival.ISLAMIC_COM[a][b]:"+s);
            }
        }
    }*/

    //选择国家节日对象
    private void switchObjectOfFestival(String country){
        Log.d("3333", "switchObjectOfFestival***start");
        switch(country){
            case "cn":
                initCalendar(new DPCNCalendar());
                break;
            case "us":
                initCalendar(new DPUSCalendar());
                break;
            case "Cote D'Ivoire":
                initCalendar(new DPCoteCalendar());
                break;
            case "Nigeria":
                initCalendar(new DPNigeriaCalendar());
                break;
            case "Rwanda":
                initCalendar(new DPRwandaCalendar());
                break;
            case "Tanzania":
                initCalendar(new DPTanzaniaCalendar());
                break;
            case "Ghana":
                initCalendar(new DPGhanaCalendar());
                break;
            case "Malawi":
                initCalendar(new DPMalawiCalendar());
                break;
            case "Kenya":
                initCalendar(new DPKenyaCalendar());
                break;
            case "Cameroon":
                initCalendar(new DPCameroonCalendar());
                break;
            case "Burkina Faso":
                initCalendar(new DPBurkinaCalendar());
                break;
            case "Egypt":
                initCalendar(new DPEgyptCalendar());
                break;
            case "Others":
                initCalendar(new DPDefaultCalendar());
                break;
            default:
                initCalendar(new DPDefaultCalendar());
                break;
        }
    }
    //监听到语言切换的时候，改变显示对象

    //一些系统设置变化后回调此方法
    public void initAppLanguage(Context context) {
        if (context == null) {
            return;
        }
//        PreferenceUtil.commitString("select_country", null);
        initDataFlag = false;
        //清除缓存数据
        festivalCacheClear();

        android.util.Log.d("9999", "111[  initAppLanguage---start]" + "killAll"+DATE_CACHE);

        /*// 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);*/
    }

    //按返回键清除缓存
    public void clearDateCacheDayOfMonth(){
		android.util.Log.d("9999", "111[  initAppLanguage---start]" + "killAll"+DATE_CACHE);
        initDataFlag = false;
        festivalCacheClear();
    }

    private void festivalCacheClear(){
        DATE_CACHE.clear();
        c.comFestivalDateCache.clear();
        c.generalFestivalDateCache.clear();
        c.islamicFestivalDateCache.clear();
        c.ChristianFestivalDateCache.clear();
        c.orthodoxFestivalDateCache.clear();
        c.generalFestivalCacheBurkina.clear();
        c.generalFestivalCacheCameroon.clear();
        c.islamicFestivalCacheCote.clear();
        c.generalFestivalCacheCote.clear();
        c.generalFestivalCacheEgypt.clear();
        c.generalFestivalCacheGhana.clear();
        c.generalFestivalCacheKenya.clear();
        c.generalFestivalCacheMalawi.clear();
        c.generalFestivalCacheNigeria.clear();
        c.generalFestivalCacheRwanda.clear();
        c.generalFestivalCacheTanzania.clear();
    }

    /**
     * 获取月历管理器对象,单例
     * Get calendar manager
     *
     * @return 月历管理器
     */
    public static DPCManager getInstance() {
        if (sManager == null) {
            sManager = new DPCManager();
            synchronized(DPCManager.class){//避免多线程同步问题
                if(sManager == null){
                    sManager = new DPCManager();
                }
            }
        }
        return sManager;
    }

    /**
     * 初始化日历对象
     * <p/>
     * Initialization Calendar
     *
     * @param c ...
     */
    public void initCalendar(DPCommonFestival c) {
        this.c = c;
    }

    /**
     * 获取指定年月的日历对象数组
     *
     * @param year  公历年
     * @param month 公历月
     * @return 日历对象数组 该数组长度恒为 6x7 如果某个下标对应无数据则填充为null
     */
    //减少计算过程，存下以计算出的结果
    public DPInfo[][] obtainDPInfo(int year, int month) {
        HashMap<Integer, DPInfo[][]> dataOfYear = DATE_CACHE.get(year);
        //Log.d("9999", "mCManager.obtainDPInfo****obtainDPInfo_start"+"DATE_CACHE:"+DATE_CACHE);
        if (null != dataOfYear && dataOfYear.size() != 0) {
            DPInfo[][] dataOfMonth = dataOfYear.get(month);
            if (dataOfMonth != null) {
                Log.d("9999", "null != dataOfMonth****obtainDPInfo_start_111"+"coming");
                return dataOfMonth;
            }
            Log.d("9999", "null == dataOfMonth****obtainDPInfo_start_222"+"coming");
            dataOfMonth = buildDPInfo(year, month);
            dataOfYear.put(month, dataOfMonth);
            return dataOfMonth;
        }
        if (null == dataOfYear) dataOfYear = new HashMap<>();
        if(!initDataFlag){
            initFestivalData();
        }
        Log.d("9999", "null == dataOfYear****obtainDPInfo_start"+"coming");
        DPInfo[][] dataOfMonth = buildDPInfo(year, month);//得到这个月的日历数据
        dataOfYear.put((month), dataOfMonth);
        DATE_CACHE.put(year, dataOfYear);//存一年的数据
        return dataOfMonth;
    }

    public DPInfo[][] buildDPInfo(int year, int month) {
        DPInfo[][] info = new DPInfo[6][7];
        String[][] strG = null;
        String[][] strF = null;
        Set<String> strHoliday = null;
        Set<String> strWeekend = null;
        if(c != null){
            Log.d("3333", "buildDPInfo****" + "COMING...");
            strG = c.buildMonthG(year, month);//生成某年某月的公历天数数组[0-31]

            //计算节日
            strF = c.buildMonthFestival(year, month);
//            strF = obtainMonthFestival(c,year, month);

            //获取某年某月的节日数组(抽象)，返回的是包含本年本月的所有节日的数组
            strHoliday = c.buildMonthHoliday(year, month);
            //获取某年某月的假期数组(抽象)

            if(currentCountry != null){
                if(EGYPT.equals(currentCountry)){
                    strWeekend = c.buildIslamicMonthWeekend(year, month);
                }else {
                    strWeekend = c.buildMonthWeekend(year, month);
                    //生成某年某月的周末日期集合
                }
            }
        }

        Set<String> decorBG = DECOR_CACHE_BG.get(year + ":" + month);//背景
        Set<String> decorTL = DECOR_CACHE_TL.get(year + ":" + month);//左上
        Set<String> decorT = DECOR_CACHE_T.get(year + ":" + month);
        Set<String> decorTR = DECOR_CACHE_TR.get(year + ":" + month);
        Set<String> decorL = DECOR_CACHE_L.get(year + ":" + month);
        Set<String> decorR = DECOR_CACHE_R.get(year + ":" + month);

        for (int i = 0; i < info.length; i++) {
            for (int j = 0; j < info[i].length; j++) {
                DPInfo tmp = new DPInfo();//日历数据实体对象
                if(strG != null){
                    Log.d("3333", "buildDPInfo****"+"strG[i][j]:"+strG[i][j]);
                }
                if(strF != null){
                    Log.d("3333", "buildDPInfo****" + "strF[i][j]:" + strF[i][j]);
                }
                if(strG != null && strF != null){
                    tmp.strG = strG[i][j];
                    if (c instanceof DPUSCalendar) {
                        tmp.strF = strF[i][j];//英语
                    } else if(c instanceof DPCNCalendar){
                        tmp.strF = strF[i][j];//中文,如果有节日将“F”设为空
                    } else if(c instanceof DPNigeriaCalendar){
                        tmp.strF = strF[i][j];//尼日利亚
                    } else if(c instanceof DPCoteCalendar){
                        tmp.strF = strF[i][j];//科特迪瓦
                    } else if(c instanceof DPRwandaCalendar){
                        tmp.strF = strF[i][j];//卢旺达
                    } else if(c instanceof DPTanzaniaCalendar){
                        tmp.strF = strF[i][j];//坦桑尼亚
                    } else if(c instanceof DPGhanaCalendar){
                        tmp.strF = strF[i][j];//加纳
                    } else if(c instanceof DPMalawiCalendar){
                        tmp.strF = strF[i][j];//马拉维
                    } else if(c instanceof DPKenyaCalendar){
                        tmp.strF = strF[i][j];//肯尼亚
                    } else if(c instanceof DPCameroonCalendar){
                        tmp.strF = strF[i][j];//喀麦隆
                    } else if(c instanceof DPBurkinaCalendar){
                        tmp.strF = strF[i][j];//布基纳法索
                    }else if(c instanceof DPEgyptCalendar){
                        tmp.strF = strF[i][j];//埃及
                    } else{
                        tmp.strF = strF[i][j];
                    }
                    Log.d("8888", "buildDPInfo****tmp.strF:"+tmp.strF);
                }

                if (strHoliday != null && !TextUtils.isEmpty(tmp.strG) && strHoliday.contains(tmp.strG)){//是否有包含这一天
                    tmp.isHoliday = true;
                }
                if (!TextUtils.isEmpty(tmp.strG)) {
                    tmp.isToday = c.isToday(year, month, Integer.valueOf(tmp.strG));
                }
                if (strWeekend != null && strWeekend.contains(tmp.strG)) {
                    tmp.isWeekend = true;
                }
                if (c instanceof DPCNCalendar) {
                    if (!TextUtils.isEmpty(tmp.strG)) {
                        tmp.isSolarTerms = ((DPCNCalendar) c).isSolarTerm(year, month, Integer.valueOf(tmp.strG));//节气
                    }
                    if (!TextUtils.isEmpty(strF[i][j]) && strF[i][j].endsWith("F")) {//是节日
                        tmp.isFestival = true;
                    }
                    if (!TextUtils.isEmpty(tmp.strG)) {
                        tmp.isDeferred = ((DPCNCalendar) c).isDeferred(year, month, Integer.valueOf(tmp.strG));//补休
                    }
                } else {
                    tmp.isFestival = !TextUtils.isEmpty(strF[i][j]);
                }
				
                if (null != decorBG && decorBG.contains(tmp.strG)) tmp.isDecorBG = true;
                if (null != decorTL && decorTL.contains(tmp.strG)) tmp.isDecorTL = true;
                if (null != decorT && decorT.contains(tmp.strG)) tmp.isDecorT = true;
                if (null != decorTR && decorTR.contains(tmp.strG)) tmp.isDecorTR = true;
                if (null != decorL && decorL.contains(tmp.strG)) tmp.isDecorL = true;
                if (null != decorR && decorR.contains(tmp.strG)) tmp.isDecorR = true;
                info[i][j] = tmp;
            }
        }
        return info;
    }
	
	/*private String[][] obtainMonthFestival(DPCalendar c, int year, int month){
        Log.d("1111", "obtainMonthFestival_start111"+"year"+year+",month"+month);
        HashMap<Integer, String[][]> dataFestivalOfYear = festivalDateCache.get(year);
        if(null != dataFestivalOfYear && dataFestivalOfYear.size() != 0){
            String[][] strings = festivalDateCache.get(2016).get(1);
            for (int a = 0; a < 6; a++){
                for(int b = 0; b < 7; b++){
                    Log.d("1111", "obtainMonthFestival_strings"+strings[a][b]);
                }
            }
            String[][] dataFestivalOfMonth = dataFestivalOfYear.get(month);
            if (dataFestivalOfMonth != null) {
                for (int a = 0; a < 6; a++){
                    for(int b = 0; b < 7; b++){
                        Log.d("1111", "obtainMonthFestival_dataFestivalOfMonth"+dataFestivalOfMonth[a][b]);
                    }
                }
                Log.d("1111", "null != dataFestivalOfYear****obtainMonthFestival_start"+"coming");
                return dataFestivalOfMonth;
            }
            Log.d("1111", "null != dataFestivalOfYear****obtainMonthFestival_start111"+"coming");
            dataFestivalOfMonth = c.buildMonthFestival(year, month);
            dataFestivalOfYear.put(month, dataFestivalOfMonth);
            return dataFestivalOfMonth;
        }else if(dataFestivalOfYear == null){
            Log.d("1111", "null == dataFestivalOfYear****obtainMonthFestival_start"+"coming");
            dataFestivalOfYear = new HashMap<>();
            String[][] dataFestivalOfMonth = c.buildMonthFestival(year, month);
            dataFestivalOfYear.put(month, dataFestivalOfMonth);
            festivalDateCache.put(year, dataFestivalOfYear);
            return dataFestivalOfMonth;
        }
        return null;
    }*/

    private void setDecor(List<String> date, HashMap<String, Set<String>> cache) {
        for (String str : date) {
            int index = str.lastIndexOf("-");
            String key = str.substring(0, index).replace("-", ":");
            Set<String> days = cache.get(key);
            if (null == days) {
                days = new HashSet<>();
            }
            days.add(str.substring(index + 1, str.length()));
            cache.put(key, days);
        }
    }

    /**
     * 设置有背景标识物的日期
     * <p/>
     * Set date which has decor of background
     *
     * @param date 日期列表 List of date
     */
    public void setDecorBG(List<String> date) {//设置有背景标识物的日期
        setDecor(date, DECOR_CACHE_BG);
    }

    /**
     * 设置左上角有标识物的日期
     * <p/>
     * Set date which has decor on Top left
     *
     * @param date 日期列表 List of date
     */
    public void setDecorTL(List<String> date) {//设置左上角有标识物的日期
        setDecor(date, DECOR_CACHE_TL);
    }

    /**
     * 设置顶部有标识物的日期
     * <p/>
     * Set date which has decor on Top
     *
     * @param date 日期列表 List of date
     */
    public void setDecorT(List<String> date) {//设置顶部有标识物的日期
        setDecor(date, DECOR_CACHE_T);
    }

    /**
     * 设置右上角有标识物的日期
     * <p/>
     * Set date which has decor on Top right
     *
     * @param date 日期列表 List of date
     */
    public void setDecorTR(List<String> date) {//设置右上角有标识物的日期
        setDecor(date, DECOR_CACHE_TR);
    }

    /**
     * 设置左边有标识物的日期
     * <p/>
     * Set date which has decor on left
     *
     * @param date 日期列表 List of date
     */
    public void setDecorL(List<String> date) {//设置左边有标识物的日期
        setDecor(date, DECOR_CACHE_L);
    }

    /**
     * 设置右上角有标识物的日期
     * <p/>
     * Set date which has decor on right
     *
     * @param date 日期列表 List of date
     */
    public void setDecorR(List<String> date) {//设置右上角有标识物的日期
        setDecor(date, DECOR_CACHE_R);
    }
}
