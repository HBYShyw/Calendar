package com.android.calendar.festivalPicker.entities;

/**
 * 日历数据实体
 * 封装日历绘制时需要的数据
 * 
 * Entity of calendar
 *
 * @author HYW 2015-12-25
 */
public class DPInfo {
    public String strG, strF;
    public boolean isHoliday;
    public boolean isToday, isWeekend;
    public boolean isSolarTerms, isFestival, isDeferred;
    public boolean isDecorBG;
    public boolean isDecorTL, isDecorT, isDecorTR, isDecorL, isDecorR;
}