/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.month;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract.Attendees;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.android.calendar.Event;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.festivalPicker.bizs.DPCManager;
import com.android.calendar.festivalPicker.entities.DPInfo;
import com.mediatek.calendar.extension.OPExtensionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

//给适配器设置布局格式：
// 主要包括：间隔线的绘制、背景色的绘制、日期数字的绘制、农历的绘制、事件标志的绘制、点击效果的绘制。

public class MonthWeekEventsView extends SimpleWeekView {

    private static final String TAG = "MonthView";

    private static final boolean DEBUG_LAYOUT = false;

    public static final String VIEW_PARAMS_ORIENTATION = "orientation";
    /// tag for animate the selected day @{
    public static final String VIEW_PARAMS_ANIMATE_SELECTED_DAY = "animate_selected_day";
    /// @}
    public static final String VIEW_PARAMS_ANIMATE_TODAY = "animate_today";

    /* NOTE: these are not constants, and may be multiplied by a scale factor */
    private static int TEXT_SIZE_MONTH_NUMBER = 32;
    private static int CIRCLE_SIZE_MONTH_NUMBER = 15;
    private static int TEXT_SIZE_EVENT = 12;
    private static int TEXT_SIZE_EVENT_TITLE = 14;
    private static int TEXT_SIZE_MORE_EVENTS = 12;
    private static int TEXT_SIZE_MONTH_NAME = 14;
    private static int TEXT_SIZE_WEEK_NUM = 12;

    private static int DNA_MARGIN = 4;
    private static int DNA_ALL_DAY_HEIGHT = 4;
    private static int DNA_MIN_SEGMENT_HEIGHT = 4;
    private static int DNA_WIDTH = 8;
    private static int DNA_ALL_DAY_WIDTH = 32;
    private static int DNA_SIDE_PADDING = 6;
    private static int CONFLICT_COLOR = Color.BLACK;
    private static int EVENT_TEXT_COLOR = Color.WHITE;

    private static int DEFAULT_EDGE_SPACING = 0;
    private static int SIDE_PADDING_MONTH_NUMBER = 4;
    private static int TOP_PADDING_MONTH_NUMBER = 4;
    private static int CIRCLE_PADDING_MONTH_NUMBER = 5;
    private static int TOP_PADDING_WEEK_NUMBER = 4;
    private static int SIDE_PADDING_WEEK_NUMBER = 20;
    private static int DAY_SEPARATOR_OUTER_WIDTH = 0;
    private static int DAY_SEPARATOR_INNER_WIDTH = 1;
    private static int DAY_SEPARATOR_VERTICAL_LENGTH = 53;
    private static int DAY_SEPARATOR_VERTICAL_LENGHT_PORTRAIT = 64;
    private static int MIN_WEEK_WIDTH = 50;

    private static int EVENT_X_OFFSET_LANDSCAPE = 38;
    private static int EVENT_Y_OFFSET_LANDSCAPE = 8;
    private static int EVENT_Y_OFFSET_PORTRAIT = 7;
    private static int EVENT_SQUARE_WIDTH = 10;
    private static int EVENT_SQUARE_BORDER = 2;
    private static int EVENT_LINE_PADDING = 2;
    private static int EVENT_RIGHT_PADDING = 4;
    private static int EVENT_BOTTOM_PADDING = 3;

    private static int TODAY_HIGHLIGHT_WIDTH = 2;

    private static int SPACING_WEEK_NUMBER = 24;
    private static boolean mInitialized = false;
    private static boolean mShowDetailsInMonth;

    protected Time mToday = new Time();
    /// M: for go to @{
    /// the selected day time
    protected Time mSelectedDayTime = new Time();
    /// the selected day index in the month by week view  所选天指数在月视图一列的自定义View里的位置
    protected int mSelectedDayIndex = -1;

    /// @}
    protected boolean mHasToday = false;
    protected int mTodayIndex = -1;
    protected int mOrientation = Configuration.ORIENTATION_LANDSCAPE;
    protected List<ArrayList<Event>> mEvents = null;
    protected ArrayList<Event> mUnsortedEvents = null;
    HashMap<Integer, Utils.DNAStrand> mDna = null;
    // This is for drawing the outlines around event chips and supports up to 10
    // events being drawn on each day. The code will expand this if necessary.
    protected FloatRef mEventOutlines = new FloatRef(10 * 4 * 4 * 7);


    protected static StringBuilder mStringBuilder = new StringBuilder(50);
    // TODO recreate formatter when locale changes
    protected static Formatter mFormatter = new Formatter(mStringBuilder, Locale.getDefault());

    protected Paint mMonthNamePaint;
    protected TextPaint mEventPaint;
    protected TextPaint mSolidBackgroundEventPaint;
    protected TextPaint mFramedEventPaint;
    protected TextPaint mDeclinedEventPaint;
    protected TextPaint mEventExtrasPaint;
    protected TextPaint mEventDeclinedExtrasPaint;
    protected Paint mWeekNumPaint;
    protected Paint mDNAAllDayPaint;
    protected Paint mDNATimePaint;
    protected Paint mEventSquarePaint;
    protected Paint festivalPaint;
    protected Paint weekPaint;

    protected Drawable mTodayDrawable;

    protected int mMonthNumHeight;
    protected int mMonthNumAscentHeight;
    protected int mEventHeight;
    protected int mEventAscentHeight;
    protected int mExtrasHeight;
    protected int mExtrasAscentHeight;
    protected int mExtrasDescent;
    protected int mWeekNumAscentHeight;

    protected int mMonthBGColor;
    protected int mMonthBGOtherColor;
    protected int mMonthBGTodayColor;
    protected int mMonthNumColor;
    protected int mMonthNumOtherColor;
    protected int mMonthNumTodayColor;
    protected int mWeekNumFocusColor;
    protected int mWeekNumNoFocusColor;
    protected int mMonthNameColor;
    protected int mMonthNameOtherColor;
    protected int mMonthEventColor;
    protected int mMonthDeclinedEventColor;
    protected int mMonthDeclinedExtrasColor;
    protected int mMonthEventExtraColor;
    protected int mMonthEventOtherColor;
    protected int mMonthEventExtraOtherColor;
    protected int mMonthWeekNumColor;
    protected int mMonthBusyBitsBgColor;
    protected int mMonthBusyBitsBusyTimeColor;
    protected int mMonthBusyBitsConflictTimeColor;
    private int mClickedDayIndex = -1;
    private int mClickedDayColor;
    private static final int mClickedAlpha = 128;

    protected int mEventChipOutlineColor = 0xFFFFFFFF;
    protected int mDaySeparatorInnerColor;
    protected int mTodayAnimateColor;

    private boolean mAnimateToday;
    private int mAnimateTodayAlpha = 0;
    private ObjectAnimator mTodayAnimator = null;

    /// M: for go to @{
    // animate color for selected day
    protected int mSelectedDayAnimateColor;
    // do we need to animate the selected day
    private boolean mAnimateSelectedDay;
    // animate selected day alpha
    private int mAnimateSelectedDayAlpha = 0;
    // animator for selected day
    private ObjectAnimator mSelectedDayAnimator = null;
    /// @}
    /// M: animator listener for selected day
    private final SelectedDayAnimatorListener mAnimatorListener = new SelectedDayAnimatorListener();

    /// M: lunar decoupling use
    private Context mContext;

    class TodayAnimatorListener extends AnimatorListenerAdapter { //跳转当天动画
        private volatile Animator mAnimator = null;
        private volatile boolean mFadingIn = false;

        @Override
        public void onAnimationEnd(Animator animation) {
            synchronized (this) {
                if (mAnimator != animation) {
                    animation.removeAllListeners();
                    animation.cancel();
                    return;
                }
                if (mFadingIn) {
                    if (mTodayAnimator != null) {
                        mTodayAnimator.removeAllListeners();
                        mTodayAnimator.cancel();
                    }
                    mTodayAnimator = ObjectAnimator.ofInt(MonthWeekEventsView.this,
                            "animateTodayAlpha", 255, 0);
                    mAnimator = mTodayAnimator;
                    mFadingIn = false;
                    mTodayAnimator.addListener(this);
                    mTodayAnimator.setDuration(600);
                    mTodayAnimator.start();
                } else {
                    mAnimateToday = false;
                    mAnimateTodayAlpha = 0;
                    mAnimator.removeAllListeners();
                    mAnimator = null;
                    mTodayAnimator = null;
                    invalidate();
                }
            }
        }

        public void setAnimator(Animator animation) {
            mAnimator = animation;
        }

        public void setFadingIn(boolean fadingIn) {
            mFadingIn = fadingIn;
        }

    }

    /// M: the animator for the selected day @{
    class SelectedDayAnimatorListener extends AnimatorListenerAdapter {
        private volatile Animator mAnimator = null;
        private volatile boolean mFadingIn = false;

        @Override
        public void onAnimationEnd(Animator animation) {
            synchronized (this) {
                if (mAnimator != animation) {
                    animation.removeAllListeners();
                    animation.cancel();
                    return;
                }
                if (mFadingIn) {//由深到浅变化
                    if (mSelectedDayAnimator != null) {
                        mSelectedDayAnimator.removeAllListeners();
                        mSelectedDayAnimator.cancel();
                    }
                    mSelectedDayAnimator = ObjectAnimator.ofInt(MonthWeekEventsView.this,
                            "animateSelectedDayAlpha", 255, 0);//结束动画的时候由深到浅变化
                    mAnimator = mSelectedDayAnimator;
                    mFadingIn = false;
                    mSelectedDayAnimator.addListener(this);
                    mSelectedDayAnimator.setDuration(600);
                    mSelectedDayAnimator.start();
                } else {
                    mAnimateSelectedDay = false;
                    mAnimateSelectedDayAlpha = 0;
                    mAnimator.removeAllListeners();
                    mAnimator = null;
                    mSelectedDayAnimator = null;
                    invalidate();
                }
            }
        }

        public void setAnimator(Animator animation) {
            mAnimator = animation;
        }

        public void setFadingIn(boolean fadingIn) {
            mFadingIn = fadingIn;
        }
    }
    /// @}

    private int[] mDayXs;

    /**
     * This provides a reference to a float array which allows for easy size
     * checking and reallocation. Used for drawing lines.
     */
    private class FloatRef {
        float[] array;

        public FloatRef(int size) {
            array = new float[size];
        }

        public void ensureSize(int newSize) {
            if (newSize >= array.length) {
                // Add enough space for 7 more boxes to be drawn
                array = Arrays.copyOf(array, newSize + 16 * 7);
            }
        }
    }

    /**
     * Shows up as an error if we don't include this.
     */
    public MonthWeekEventsView(Context context) {
        super(context);
        mContext = context;
    }

    // Sets the list of events for this week. Takes a sorted list of arrays
    // divided up by day for generating the large month version and the full
    // arraylist sorted by start time to generate the dna version.
    public void setEvents(List<ArrayList<Event>> sortedEvents, ArrayList<Event> unsortedEvents) {
        setEvents(sortedEvents);
        // The MIN_WEEK_WIDTH is a hack to prevent the view from trying to
        // generate dna bits before its width has been fixed.
        createDna(unsortedEvents);
    }

    /**
     * Sets up the dna bits for the view. This will return early if the view
     * isn't in a state that will create a valid set of dna yet (such as the
     * views width not being set correctly yet).
     */
    public void createDna(ArrayList<Event> unsortedEvents) {
        if (unsortedEvents == null || mWidth <= MIN_WEEK_WIDTH || getContext() == null) {
            // Stash the list of events for use when this view is ready, or
            // just clear it if a null set has been passed to this view
            mUnsortedEvents = unsortedEvents;
            mDna = null;
            return;
        } else {
            // clear the cached set of events since we're ready to build it now
            mUnsortedEvents = null;
        }
        // Create the drawing coordinates for dna
        if (!mShowDetailsInMonth) {
            int numDays = mEvents.size();
            int effectiveWidth = mWidth - mPadding * 2;
            if (mShowWeekNum) {
                effectiveWidth -= SPACING_WEEK_NUMBER;
            }
            DNA_ALL_DAY_WIDTH = effectiveWidth / numDays - 2 * DNA_SIDE_PADDING;
            mDNAAllDayPaint.setStrokeWidth(DNA_ALL_DAY_WIDTH);
            mDayXs = new int[numDays];
            for (int day = 0; day < numDays; day++) {
                mDayXs[day] = computeDayLeftPosition(day) + DNA_WIDTH / 2 + DNA_SIDE_PADDING;

            }

            int top = DAY_SEPARATOR_INNER_WIDTH + DNA_MARGIN + DNA_ALL_DAY_HEIGHT + 1;
            int bottom = mHeight - DNA_MARGIN;
            mDna = Utils.createDNAStrands(mFirstJulianDay, unsortedEvents, top, bottom,
                    DNA_MIN_SEGMENT_HEIGHT, mDayXs, getContext());
        }
    }

    public void setEvents(List<ArrayList<Event>> sortedEvents) {
        mEvents = sortedEvents;
        if (sortedEvents == null) {
            return;
        }
        if (sortedEvents.size() != mNumDays) {
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.wtf(TAG, "Events size must be same as days displayed: size="
                        + sortedEvents.size() + " days=" + mNumDays);
            }
            mEvents = null;
            return;
        }
    }

    protected void loadColors(Context context) {
        Resources res = context.getResources();
        mMonthWeekNumColor = res.getColor(R.color.month_week_num_color);
        mMonthNumColor = res.getColor(R.color.month_day_number); // 获得焦点的天
        mMonthNumOtherColor = res.getColor(R.color.month_day_number_other);//未获得焦点的天
        mMonthNumTodayColor = res.getColor(R.color.month_today_number); //今天颜色
        mWeekNumFocusColor = res.getColor(R.color.week_number_focus); //获得焦点的双休日
        mWeekNumNoFocusColor = res.getColor(R.color.week_number_no_focus); //未获得焦点的双休日
        mMonthNameColor = mMonthNumColor;
        mMonthNameOtherColor = mMonthNumOtherColor;
        mMonthEventColor = res.getColor(R.color.month_event_color);
        mMonthDeclinedEventColor = res.getColor(R.color.agenda_item_declined_color);
        mMonthDeclinedExtrasColor = res.getColor(R.color.agenda_item_where_declined_text_color);
        mMonthEventExtraColor = res.getColor(R.color.month_event_extra_color);
        mMonthEventOtherColor = res.getColor(R.color.month_event_other_color);
        mMonthEventExtraOtherColor = res.getColor(R.color.month_event_extra_other_color);
        mMonthBGTodayColor = res.getColor(R.color.month_today_bgcolor);//月视图当天背景颜色
        mMonthBGOtherColor = res.getColor(R.color.month_other_bgcolor);
        mMonthBGColor = res.getColor(R.color.month_bgcolor);
        //分割线颜色
        mDaySeparatorInnerColor = res.getColor(R.color.month_grid_lines);

        //TRANSSION BEGIN huangyongwen
        ///M:#Theme Manager#@{
        /*ICalendarThemeExt themeExt = ExtensionFactory.getCalendarTheme(getContext());
        if (themeExt.isThemeManagerEnable()) {
            int themeColor = themeExt.getThemeColor();
            mTodayAnimateColor = themeColor;
            mSelectedDayAnimateColor = themeColor;
            mClickedDayColor = themeColor;
        } else {
            mTodayAnimateColor = res.getColor(R.color.today_highlight_color);
            mSelectedDayAnimateColor = res.getColor(R.color.today_highlight_color);
            mClickedDayColor = res.getColor(R.color.day_clicked_background_color);
        }*/
        //TRANSSION END huangyongwen

        mTodayAnimateColor = res.getColor(R.color.today_highlight_color);
        mSelectedDayAnimateColor = res.getColor(R.color.today_highlight_color);
        mClickedDayColor = res.getColor(R.color.day_clicked_background_color);
        ///@}

        //TRANSSION BEGIN liaohuan
        if (mShowSelectDayColor) {
           mSelectedDayAnimateColor = res.getColor(R.color.select_highlight_color);
           }
        //TRANSSION END

        mTodayDrawable = res.getDrawable(R.drawable.today_blue_week_holo_light);
    }

    /**
     * Sets up the text and style properties for painting. Override this if you
     * want to use a different paint.
     */
    @Override
    protected void initView() {
        super.initView();

        if (!mInitialized) {
            Resources resources = getContext().getResources();
            mShowDetailsInMonth = Utils.getConfigBool(getContext(), R.bool.show_details_in_month);
            TEXT_SIZE_EVENT_TITLE = resources.getInteger(R.integer.text_size_event_title);
            TEXT_SIZE_MONTH_NUMBER = resources.getInteger(R.integer.text_size_month_number);
            /*int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.text_size_month_number);
            Log.d(TAG, "TEXT_SIZE_MONTH_NUMBER:" + TEXT_SIZE_MONTH_NUMBER + "dimensionPixelSize:" + dimensionPixelSize);*/
            SIDE_PADDING_MONTH_NUMBER = resources.getInteger(R.integer.month_day_number_margin);
            CONFLICT_COLOR = resources.getColor(R.color.month_dna_conflict_time_color);
            EVENT_TEXT_COLOR = resources.getColor(R.color.calendar_event_text_color);
            if (mScale != 1) {
                Log.d(TAG, "mScale:" + mScale);
                TOP_PADDING_MONTH_NUMBER *= mScale;
                CIRCLE_PADDING_MONTH_NUMBER *= mScale;
                TOP_PADDING_WEEK_NUMBER *= mScale;
                SIDE_PADDING_MONTH_NUMBER *= mScale;
                SIDE_PADDING_WEEK_NUMBER *= mScale;
                SPACING_WEEK_NUMBER *= mScale;
                TEXT_SIZE_MONTH_NUMBER *= mScale;
                CIRCLE_SIZE_MONTH_NUMBER *= mScale;
                TEXT_SIZE_EVENT *= mScale;
                TEXT_SIZE_EVENT_TITLE *= mScale;
                TEXT_SIZE_MORE_EVENTS *= mScale;
                TEXT_SIZE_MONTH_NAME *= mScale;
                TEXT_SIZE_WEEK_NUM *= mScale;
                DAY_SEPARATOR_OUTER_WIDTH *= mScale;
                DAY_SEPARATOR_INNER_WIDTH *= mScale;
                DAY_SEPARATOR_VERTICAL_LENGTH *= mScale;
                DAY_SEPARATOR_VERTICAL_LENGHT_PORTRAIT *= mScale;
                EVENT_X_OFFSET_LANDSCAPE *= mScale;
                EVENT_Y_OFFSET_LANDSCAPE *= mScale;
                EVENT_Y_OFFSET_PORTRAIT *= mScale;
                EVENT_SQUARE_WIDTH *= mScale;
                EVENT_SQUARE_BORDER *= mScale;
                EVENT_LINE_PADDING *= mScale;
                EVENT_BOTTOM_PADDING *= mScale;
                EVENT_RIGHT_PADDING *= mScale;
                DNA_MARGIN *= mScale;
                DNA_WIDTH *= mScale;
                DNA_ALL_DAY_HEIGHT *= mScale;
                DNA_MIN_SEGMENT_HEIGHT *= mScale;
                DNA_SIDE_PADDING *= mScale;
                DEFAULT_EDGE_SPACING *= mScale;
                DNA_ALL_DAY_WIDTH *= mScale;
                TODAY_HIGHLIGHT_WIDTH *= mScale;
            }
            if (!mShowDetailsInMonth) {
                TOP_PADDING_MONTH_NUMBER += DNA_ALL_DAY_HEIGHT + DNA_MARGIN;
            }
            mInitialized = true;
        }
        mPadding = DEFAULT_EDGE_SPACING;
        loadColors(getContext());
        // TODO modify paint properties depending on isMini

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setFakeBoldText(false);
        mMonthNumPaint.setAntiAlias(true);
        Log.d(TAG, "TEXT_SIZE_MONTH_NUMBER:" + TEXT_SIZE_MONTH_NUMBER);
        mMonthNumPaint.setTextSize(TEXT_SIZE_MONTH_NUMBER);
        mMonthNumPaint.setColor(mMonthNumColor);
        mMonthNumPaint.setStyle(Style.FILL);
        //huangyongwen@20161226 add for calendar new UI begin
        //mMonthNumPaint.setTextAlign(Align.RIGHT);
        mMonthNumPaint.setTextAlign(Align.CENTER);
        //huangyongwen@20161226 add for calendar new UI end
        mMonthNumPaint.setTypeface(Typeface.DEFAULT);

        mMonthNumAscentHeight = (int) (-mMonthNumPaint.ascent() + 0.5f);
        mMonthNumHeight = (int) (mMonthNumPaint.descent() - mMonthNumPaint.ascent() + 0.5f);

        mEventPaint = new TextPaint();
        mEventPaint.setFakeBoldText(true);
        mEventPaint.setAntiAlias(true);
        mEventPaint.setTextSize(TEXT_SIZE_EVENT_TITLE);
        mEventPaint.setColor(mMonthEventColor);

        mSolidBackgroundEventPaint = new TextPaint(mEventPaint);
        mSolidBackgroundEventPaint.setColor(EVENT_TEXT_COLOR);
        mFramedEventPaint = new TextPaint(mSolidBackgroundEventPaint);

        mDeclinedEventPaint = new TextPaint();
        mDeclinedEventPaint.setFakeBoldText(true);
        mDeclinedEventPaint.setAntiAlias(true);
        mDeclinedEventPaint.setTextSize(TEXT_SIZE_EVENT_TITLE);
        mDeclinedEventPaint.setColor(mMonthDeclinedEventColor);

        mEventAscentHeight = (int) (-mEventPaint.ascent() + 0.5f);
        mEventHeight = (int) (mEventPaint.descent() - mEventPaint.ascent() + 0.5f);

        mEventExtrasPaint = new TextPaint();
        mEventExtrasPaint.setFakeBoldText(false);
        mEventExtrasPaint.setAntiAlias(true);
        mEventExtrasPaint.setStrokeWidth(EVENT_SQUARE_BORDER);
        mEventExtrasPaint.setTextSize(TEXT_SIZE_EVENT);
        mEventExtrasPaint.setColor(mMonthEventExtraColor);
        mEventExtrasPaint.setStyle(Style.FILL);
        mEventExtrasPaint.setTextAlign(Align.LEFT);
        mExtrasHeight = (int)(mEventExtrasPaint.descent() - mEventExtrasPaint.ascent() + 0.5f);
        mExtrasAscentHeight = (int)(-mEventExtrasPaint.ascent() + 0.5f);
        mExtrasDescent = (int)(mEventExtrasPaint.descent() + 0.5f);

        mEventDeclinedExtrasPaint = new TextPaint();
        mEventDeclinedExtrasPaint.setFakeBoldText(false);
        mEventDeclinedExtrasPaint.setAntiAlias(true);
        mEventDeclinedExtrasPaint.setStrokeWidth(EVENT_SQUARE_BORDER);
        mEventDeclinedExtrasPaint.setTextSize(TEXT_SIZE_EVENT);
        mEventDeclinedExtrasPaint.setColor(mMonthDeclinedExtrasColor);
        mEventDeclinedExtrasPaint.setStyle(Style.FILL);
        mEventDeclinedExtrasPaint.setTextAlign(Align.LEFT);

        mWeekNumPaint = new Paint();
        mWeekNumPaint.setFakeBoldText(false);
        mWeekNumPaint.setAntiAlias(true);
        mWeekNumPaint.setTextSize(TEXT_SIZE_WEEK_NUM);
        mWeekNumPaint.setColor(mWeekNumColor);
        mWeekNumPaint.setStyle(Style.FILL);
        mWeekNumPaint.setTextAlign(Align.RIGHT);

        festivalPaint = new Paint();
        festivalPaint.setFakeBoldText(false);
        festivalPaint.setAntiAlias(true);
        festivalPaint.setTextSize(TEXT_SIZE_MONTH_NUMBER);
        festivalPaint.setStyle(Style.STROKE);
        festivalPaint.setTextAlign(Align.CENTER);
        festivalPaint.setTypeface(Typeface.DEFAULT);

        weekPaint = new Paint();
        weekPaint.setFakeBoldText(false);
        weekPaint.setAntiAlias(true);
        weekPaint.setTextSize(TEXT_SIZE_MONTH_NUMBER);
        weekPaint.setStyle(Style.FILL);
        weekPaint.setTextAlign(Align.CENTER);
        weekPaint.setTypeface(Typeface.SANS_SERIF);//DEFAULT

        mWeekNumAscentHeight = (int) (-mWeekNumPaint.ascent() + 0.5f);
        //ascent：是baseline之上至字符最高处的距离

        mDNAAllDayPaint = new Paint();
        mDNATimePaint = new Paint();
        mDNATimePaint.setColor(mMonthBusyBitsBusyTimeColor);
        mDNATimePaint.setStyle(Style.FILL_AND_STROKE);
        mDNATimePaint.setStrokeWidth(DNA_WIDTH);
        mDNATimePaint.setAntiAlias(false);
        mDNAAllDayPaint.setColor(mMonthBusyBitsConflictTimeColor);
        mDNAAllDayPaint.setStyle(Style.FILL_AND_STROKE);
        mDNAAllDayPaint.setStrokeWidth(DNA_ALL_DAY_WIDTH);
        mDNAAllDayPaint.setAntiAlias(false);

        mEventSquarePaint = new Paint();
        mEventSquarePaint.setStrokeWidth(EVENT_SQUARE_BORDER);
        mEventSquarePaint.setAntiAlias(false);

        if (DEBUG_LAYOUT) {
            Log.d("EXTRA", "mScale=" + mScale);
            Log.d("EXTRA", "mMonthNumPaint ascent=" + mMonthNumPaint.ascent()
                    + " descent=" + mMonthNumPaint.descent() + " int height=" + mMonthNumHeight);
            Log.d("EXTRA", "mEventPaint ascent=" + mEventPaint.ascent()
                    + " descent=" + mEventPaint.descent() + " int height=" + mEventHeight
                    + " int ascent=" + mEventAscentHeight);
            Log.d("EXTRA", "mEventExtrasPaint ascent=" + mEventExtrasPaint.ascent()
                    + " descent=" + mEventExtrasPaint.descent() + " int height=" + mExtrasHeight);
            Log.d("EXTRA", "mWeekNumPaint ascent=" + mWeekNumPaint.ascent()
                    + " descent=" + mWeekNumPaint.descent());
        }
    }

    @Override
    public void setWeekParams(HashMap<String, Integer> params, String tz) {
        super.setWeekParams(params, tz);

        if (params.containsKey(VIEW_PARAMS_ORIENTATION)) {
            mOrientation = params.get(VIEW_PARAMS_ORIENTATION);
        }

        updateToday(tz);
        mNumCells = mNumDays + 1;

        /// M: make it animate for selected day @{
        if (params.containsKey(VIEW_PARAMS_SELECTED_DAY)) {
            updateSelectedDayIndex(params.get(VIEW_PARAMS_SELECTED_DAY));
        }
        /// @}
        /// M: animate parameters  选中当天的动画参数，由浅到深变化
		//add mshowSelectDayColor by liaohuan-2016.11.17 transsion
        if (params.containsKey(VIEW_PARAMS_ANIMATE_SELECTED_DAY) && mHasSelectedDay && !mShowSelectDayColor) {
//        if (params.containsKey(VIEW_PARAMS_ANIMATE_SELECTED_DAY) && mHasSelectedDay) {
            //不执行动画了，什么时候需要动画再启动
            synchronized (mAnimatorListener) {
                if (mSelectedDayAnimator != null) {
                    mSelectedDayAnimator.removeAllListeners();
                    mSelectedDayAnimator.cancel();
                }
                mSelectedDayAnimator = ObjectAnimator.ofInt(this, "animateSelectedDayAlpha",
                        Math.max(mAnimateSelectedDayAlpha, 80), 255);
                mSelectedDayAnimator.setDuration(150);
                mAnimatorListener.setAnimator(mSelectedDayAnimator);
                mAnimatorListener.setFadingIn(true);
                mSelectedDayAnimator.addListener(mAnimatorListener);//设置监听
                //mAnimatorListener监听跳转动画的监听器
                //SelectedDayAnimatorListener内部类
                mAnimateSelectedDay = true;
                mSelectedDayAnimator.start();
            }
        }
    }

    /**
     *
     * @param tz
     */
    public boolean updateToday(String tz) {
        mToday.timezone = tz;
        mToday.setToNow();
        mToday.normalize(true);
        int julianToday = Time.getJulianDay(mToday.toMillis(false), mToday.gmtoff);
        if (julianToday >= mFirstJulianDay && julianToday < mFirstJulianDay + mNumDays) {
            mHasToday = true;
            mTodayIndex = julianToday - mFirstJulianDay; // 算出今天的坐标位置
        } else {
            mHasToday = false;
            mTodayIndex = -1;
        }
        return mHasToday;
    }

    /**
     * M: update the selected day index in our displayed view, for example, [0-6]
     * @param weekDay
     * @return the updated selected day's index
     */
    private int updateSelectedDayIndex(int weekDay) {
        if (weekDay < 0) {
            return -1;
        }
        int firstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mSelectedDayIndex = weekDay - firstDayOfWeek;
        // fix it if index < 0
        if (mSelectedDayIndex < 0) {
            mSelectedDayIndex += mNumDays;
        }
        return mSelectedDayIndex;
    }

    /// M: set animate alpha for selected day
    public void setAnimateSelectedDayAlpha(int alpha) {
        mAnimateSelectedDayAlpha = alpha;
        invalidate();
    }

    public void setAnimateTodayAlpha(int alpha) {
        mAnimateTodayAlpha = alpha;
        invalidate();
    }

    //绘制日历item
    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);//绘制背景
        drawWeekNums(canvas);//绘制天数
        drawDaySeparators(canvas);//间隔线绘制
        /// M: animate selected day   add mshowSelectDayColor by liaohuan-2016.11.17 transsion

        boolean todayNotShow;
        Log.d("2323", "onDraw_comming*****mTodayIndex:" + mTodayIndex);
        if(mSelectedDayIndex == mTodayIndex){
            todayNotShow = false;
        }else {
            todayNotShow = true;
        }
        if (mHasSelectedDay && todayNotShow && (mAnimateSelectedDay||mShowSelectDayColor)) { //不需要动画false||true  就是true 有选择的天
            drawSelectedDay(canvas); //绘制选中天的动画
        }
        drawClickSelectedDay(canvas);
        if (mShowDetailsInMonth) {//false
            drawEvents(canvas);
        } else {
            if (mDna == null && mUnsortedEvents != null) {
                createDna(mUnsortedEvents);
            }
            drawDNA(canvas);//事件绘制
        }
        drawClick(canvas);//点击效果绘制
    }

    protected void drawToday(Canvas canvas) {
        r.top = DAY_SEPARATOR_INNER_WIDTH + (TODAY_HIGHLIGHT_WIDTH / 2);
        r.bottom = mHeight - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH);
        r.left = computeDayLeftPosition(mTodayIndex) + (TODAY_HIGHLIGHT_WIDTH / 2);
        r.right = computeDayLeftPosition(mTodayIndex + 1)
                - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
        p.setColor(mTodayAnimateColor | (mAnimateTodayAlpha << 24));
        canvas.drawRect(r, p);
        p.setStyle(Style.FILL);
    }

    /// M: draw the animation for selected day @{ 绘制选中天的动画
    protected void drawSelectedDay(Canvas canvas) {
        r.top = DAY_SEPARATOR_INNER_WIDTH + (TODAY_HIGHLIGHT_WIDTH / 2);
        r.bottom = mHeight - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH);
        Log.d("2323", "drawSelectedDay_comming*****mSelectedDayIndex:" + mSelectedDayIndex);
        r.left = computeDayLeftPosition(mSelectedDayIndex) + (TODAY_HIGHLIGHT_WIDTH / 2);
        r.right = computeDayLeftPosition(mSelectedDayIndex + 1)
                - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
        p.setColor(mSelectedDayAnimateColor | (mAnimateSelectedDayAlpha << 24));
        canvas.drawRect(r, p);
        p.setStyle(Style.FILL);
    }
    /// @}

    protected void drawClickSelectedDay(Canvas canvas) {
        if(mClickSelectIndex != -1){
            int dayIndexFromLocation = getDayIndexFromLocation(mClickSelectIndex);
            r.top = DAY_SEPARATOR_INNER_WIDTH + (TODAY_HIGHLIGHT_WIDTH / 2);
            r.bottom = mHeight - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
            p.setStyle(Style.STROKE);
            p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH);
            Log.d("2323", "drawClickSelectedDay_comming*****dayIndexFromLocation:" + dayIndexFromLocation);
            r.left = computeDayLeftPosition(dayIndexFromLocation) + (TODAY_HIGHLIGHT_WIDTH / 2);
            r.right = computeDayLeftPosition(dayIndexFromLocation + 1)
                    - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
            p.setColor(mSelectedDayAnimateColor | (mAnimateSelectedDayAlpha << 24));
            canvas.drawRect(r, p);
            p.setStyle(Style.FILL);
        }
    }

    // TODO move into SimpleWeekView
    // Computes the x position for the left side of the given day
    //计算离左边的位置 x
    private int computeDayLeftPosition(int day) {
        int effectiveWidth = mWidth;
        int x = 0;
        int xOffset = 0;
        if (mShowWeekNum) {
            xOffset = SPACING_WEEK_NUMBER + mPadding;
            effectiveWidth -= xOffset;
        }
        x = day * effectiveWidth / mNumDays + xOffset;
        return x;
    }

    //AA--间隔线的绘制
    @Override
    protected void drawDaySeparators(Canvas canvas) {
        float lines[] = new float[8 * 4];
        int count = 6 * 4;
        int wkNumOffset = 0;
        int i = 0;
        if (mShowWeekNum) {
            // This adds the first line separating the week number
            //这增加了第一行分离星期数
            int xOffset = SPACING_WEEK_NUMBER + mPadding;
            count += 4;
            lines[i++] = xOffset;
            lines[i++] = 0;
            lines[i++] = xOffset;
            lines[i++] = mHeight;
            wkNumOffset++;
        }
        count += 4;
        lines[i++] = 0;
        lines[i++] = 0;
        lines[i++] = mWidth;
        lines[i++] = 0;
        int y0 = 0;
        int y1 = mHeight;

        while (i < count) {
            int x = computeDayLeftPosition(i / 4 - wkNumOffset);
            lines[i++] = x;
            lines[i++] = y0;
            lines[i++] = x;
            lines[i++] = y1;
        }
        p.setColor(mDaySeparatorInnerColor);
        p.setStrokeWidth(DAY_SEPARATOR_INNER_WIDTH);
        canvas.drawLines(lines, 0, count, p);//lines包括2个坐标，表示起始坐标和终点坐标
    }

    //AA--背景色的绘制
    @Override
    protected void drawBackground(Canvas canvas) {
        int i = 0;
        int offset = 0;
        r.top = DAY_SEPARATOR_INNER_WIDTH;//1
        r.bottom = mHeight;
        if (mShowWeekNum) {
            i++;
            offset++;
        }

        //huangyongwen@20161226 add for calendar new UI begin
        /*奇数月背景*/

//        if (!mOddMonth[i]) {
//            while (++i < mOddMonth.length && !mOddMonth[i])
//                ;
//            r.right = computeDayLeftPosition(i - offset);
//            r.left = 0;
//            p.setColor(mMonthBGOtherColor);
//            canvas.drawRect(r, p);
//            // compute left edge for i, set up r, draw
//        /*非奇数月但奇数月的前几天和获取焦点的月数的后几天位于同一行*/
//        } else if (!mOddMonth[(i = mOddMonth.length - 1)]) {
//            while (--i >= offset && !mOddMonth[i])
//                ;
//            i++;
//            // compute left edge for i, set up r, draw
//            r.right = mWidth;
//            r.left = computeDayLeftPosition(i - offset);
//            p.setColor(mMonthBGOtherColor);
//            canvas.drawRect(r, p);
//        }
        //huangyongwen@20161226 add for calendar new UI end

        //“今天”的背景，高亮显示
        /*if (mHasToday) {
            p.setColor(mMonthBGTodayColor);
            r.left = computeDayLeftPosition(mTodayIndex);
            r.right = computeDayLeftPosition(mTodayIndex + 1);
            canvas.drawRect(r, p);
        }*/

        /*if(mHasSelectedDay&&(mTodayIndex!=mSelectedDay)){
            Paint p1=new Paint();
            //p1=p;
            p1.setColor(getResources().getColor(R.color.event_center));
            p1.setStyle(Style.STROKE);
            p1.setStrokeWidth(3);
            r.left = computeDayLeftPosition(mSelectedDay)+1;
            r.right = computeDayLeftPosition(mSelectedDay + 1)-2;
            r.bottom-=2;
            canvas.drawRect(r, p1);
        }*/
    }

    // Draw the "clicked" color on the tapped day
    //AA--点击事件效果的绘制
    //当点击月视图某天时，会出现类似于selector的效果
    private void drawClick(Canvas canvas) {
        if (mClickedDayIndex != -1) {
            /*//HYW
            mClickSelectIndex = mClickedDayIndex;
            Log.d("2323", "drawClick_comming*****mClickSelectIndex:" + mClickSelectIndex);
            //hyw*/
            int alpha = p.getAlpha();
            p.setColor(mClickedDayColor);
            p.setAlpha(mClickedAlpha);
            r.left = computeDayLeftPosition(mClickedDayIndex);
            r.right = computeDayLeftPosition(mClickedDayIndex + 1);
            r.top = DAY_SEPARATOR_INNER_WIDTH;
            r.bottom = mHeight;
            canvas.drawRect(r, p);
            p.setAlpha(alpha);
            /*p.setStyle(Style.STROKE);
            p.setColor(mSelectedDayAnimateColor);
            canvas.drawRect(r, p);*/
        }
    }

    private DPCManager mManager = DPCManager.getInstance();
//    private DPDecor mDecor = new DPDecor();

    @Override
    protected void drawWeekNums(Canvas canvas) {
        int y;
        int i = 0;
        int offset = -1;
        int todayIndex = mTodayIndex;
        int x = 0;
        int numCount = mNumDays;
        if (mShowWeekNum) {
            x = SIDE_PADDING_WEEK_NUMBER + mPadding;//20+
            y = mWeekNumAscentHeight + TOP_PADDING_WEEK_NUMBER;
            canvas.drawText(mDayNumbers[0], x, y, mWeekNumPaint);
            numCount++;
            i++;
            todayIndex++;
            offset++;
        }
        // 绘制最前面一行显示周数

        //transsion begin,IB-02645-huangyongwen,fix 2016-10-20
        // Get the julian monday used to show the lunar info.让这周一显示儒略历信息
        int julianMonday = Utils.getJulianMondayFromWeeksSinceEpoch(mWeek);
        Time time = new Time(mTimeZone);
//        time.setJulianDay(julianMonday);
        Utils.setJulianDayInGeneral(time, julianMonday);

        if (time.weekDay != mWeekStart) {
            int diff = time.weekDay - mWeekStart;
            if (diff < 0) {
                diff += 7;
            }
            time.monthDay -= diff;
            time.normalize(true);
        }
        mFirstJulianDay = Time.getJulianDay(time.toMillis(true), time.gmtoff);

        boolean flag;
        int month;
        int CurrentYear = time.year;
        int mOriginYear = time.year;
        int mCurrentMonth = time.month + 1;

        //transsion end,IB-02645-huangyongwen,fix 2016-10-20
        Log.d("1111", "drawWeekNums 111111****"+ mOriginYear + "/" + mCurrentMonth + "/" + mDayNumbers[i]);


        int month_day_wight = computeDayLeftPosition(2)-computeDayLeftPosition(1);
        y = mMonthNumAscentHeight + TOP_PADDING_MONTH_NUMBER + TOP_PADDING_MONTH_NUMBER/4;

        boolean isBold = false;
        boolean isFocusMonth = mFocusDay[i];
        mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor : mMonthNumOtherColor);

        for (; i < numCount; i++) {  //**********************7**********************//
            /***************************************************************************************/
            //如果循环一周并到了一个月的倒数第一天，那么下次循环绘制这个月的最后一天，月份会加一
            //计算年月
            month = mCurrentMonth;

            SharedPreferences prefs = GeneralPreferences.getSharedPreferences(getContext());
            //获得显示周数参数
            flag = prefs.getBoolean(GeneralPreferences.KEY_SHOW_WEEK_NUM, GeneralPreferences.DEFAULT_SHOW_WEEK_NUM);
            Log.d("5555", "getMonthOfDay mFirstMonth****"+ flag);

            mCurrentMonth = getMonthOfDay(mDayNumbers, time, i, mCurrentMonth, flag);

            //获得当前日期对应的年
            if(month == 12 && mCurrentMonth < month){//过了一年
                CurrentYear = mOriginYear + 1;
            }

            //获得，年月日后，进行匹配
            //生成某年某月的公历天数数组
            //根据年月确认
            //优化，存值
            DPInfo[][] tmp = mManager.obtainDPInfo(CurrentYear, mCurrentMonth);


            Log.d("2222", "getMonthOfDay mFirstMonth****"+ mFirstMonth + "1");
            Log.d("1111", "drawWeekNums 222222****"+ CurrentYear + "/" + mCurrentMonth + "/" + mDayNumbers[i]);
            Log.d("1111", time.year+","+ time.month +","+ time.monthDay+","+ time.yearDay+ ","+ time.format2445());
            //canvas.drawText(CurrentYear+","+mCurrentMonth, x , y+60, mMonthNumPaint);//年 月
            //canvas.drawText(mCurrentMonth +","+ mDayNumbers[i], x , y+60, mMonthNumPaint);//月 日

            Log.d("2222", "mCManager.obtainDPInfo****CurrentYear:"+ CurrentYear +"/mCurrentMonth:"+ mCurrentMonth);
            /*判断是否是“今天”*/
            if (mHasToday && todayIndex == i) {
                /*mMonthNumPaint.setColor(mMonthNumTodayColor);
                mMonthNumPaint.setFakeBoldText(isBold = true);//是今天，就设置为粗体
*/
                if (i + 1 <= numCount) {
                    // Make sure the color will be set back on the next
                    // iteration
                    isFocusMonth = !mFocusDay[i];  /*重点改动*///今天则为焦点月
                }
                /*判断是否是获取了焦点的月，获取焦点的月字体会变深*/
            } else if (mFocusDay[i] != isFocusMonth) {
                mMonthNumPaint.setFakeBoldText(false);
                isFocusMonth = mFocusDay[i];
//                mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor : mMonthNumOtherColor); // true为焦点月字体颜色
            }
//            x = computeDayLeftPosition(i - offset) - (SIDE_PADDING_MONTH_NUMBER);
            x = computeDayLeftPosition(i-offset)-month_day_wight / 2 + TOP_PADDING_MONTH_NUMBER/2;
            Log.d("66666", "TOP_PADDING_MONTH_NUMBER:" + TOP_PADDING_MONTH_NUMBER);
//            canvas.drawText(mDayNumbers[i], x, y, mMonthNumPaint);
            int circleY = y - CIRCLE_PADDING_MONTH_NUMBER;

            //canvas.drawText(mCurrentMonth +","+ mDayNumbers[i], x , y+60, mMonthNumPaint);

            /*当月份与日期匹配的时候，绘制出节日*/
            //遍历根据年月算出的数组，如果与当天匹配，则做当天的绘制处理
            for (int a = 0; a < 6; a++) {
                for (int b = 0; b < 7; b++) {
                    Log.d("2222","\n+tmp[a][b].strG"+tmp[a][b].strG);
                    if(tmp != null && mDayNumbers[i].equals(tmp[a][b].strG)){  //当前天与二维数组这一天匹配
                        /*canvas.drawText(mCurrentMonth+"/"+mDayNumbers[i],x , y+60, mMonthNumPaint);//月日
                        canvas.drawText(CurrentYear+"",x , y+35, mMonthNumPaint);//年*/
                        Log.d("2222", "mDayNumbers[i]****"+ mDayNumbers[i]);

                        if(tmp[a][b].isWeekend){ //今天为周末
                            if (mHasToday && todayIndex == i) {
                                weekPaint.setColor(!isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                canvas.drawCircle(x, circleY, CIRCLE_SIZE_MONTH_NUMBER, weekPaint);
                                weekPaint.setColor(mMonthNumTodayColor);
                                weekPaint.setFakeBoldText(isBold = true);
                                canvas.drawText(mDayNumbers[i] , x, y, weekPaint);
                            }else {
                                weekPaint.setFakeBoldText(isBold = false);
                                weekPaint.setColor(isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                canvas.drawText(mDayNumbers[i] , x, y, weekPaint);
                            }
                        }else {
                            if(mHasToday && todayIndex == i) {
                                mMonthNumPaint.setColor(!isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                canvas.drawCircle(x, circleY, CIRCLE_SIZE_MONTH_NUMBER, mMonthNumPaint);
                                mMonthNumPaint.setColor(mMonthNumTodayColor);
                                mMonthNumPaint.setFakeBoldText(isBold = true);//是今天，就设置为粗体
                                canvas.drawText(mDayNumbers[i], x, y, mMonthNumPaint);
                            }else {
                                mMonthNumPaint.setFakeBoldText(isBold = false);
                                mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor : mMonthNumOtherColor); // true为焦点月字体颜色
                                canvas.drawText(mDayNumbers[i], x, y, mMonthNumPaint);
                            }
                        }

                        if(!tmp[a][b].strF.equals("")){ //匹配这一天为节日
                            if(mHasToday && todayIndex == i) {
                                festivalPaint.setStrokeWidth((float) 1.0);
                                festivalPaint.setColor(!isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                canvas.drawCircle(x, circleY, CIRCLE_SIZE_MONTH_NUMBER, festivalPaint);
                            }else{
                                festivalPaint.setStrokeWidth((float) 2.0);
                                festivalPaint.setColor(isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                canvas.drawCircle(x, circleY, CIRCLE_SIZE_MONTH_NUMBER, festivalPaint);
                                Log.d("2222", "onDrawFestival****start");
                                canvas.drawText(tmp[a][b].strF, x, y + 65, festivalPaint);
    //                          canvas.drawPoint(x,y,festivalPaint);
    //                          RectF outerRect = new RectF(x-42, y-25, x+10, y+5); //矩形范围
    //                          canvas.drawRoundRect(outerRect, 15, 15, festivalPaint); // 画圆角矩形
    //                          canvas.drawText(mDayNumbers[i], x, y, festivalPaint);
                                /*if (mHasToday && todayIndex == i) {
                                    festivalPaint.setFakeBoldText(isBold = true);
                                }*/
                            }
                        }
						if(tmp[a][b].isHoliday){
                            Log.d("888", "festival[  drawWeekNums---start" +",tmp[a][b].isHoliday:" + tmp[a][b].isHoliday
							+"mCurrentMonth:"+mCurrentMonth+"tmp[a][b].strG:"+tmp[a][b].strG);
                            if(mHasToday && todayIndex == i) {
                                festivalPaint.setColor(!isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                festivalPaint.setStrokeWidth((float) 2.0);
                                canvas.drawCircle(x, circleY, CIRCLE_SIZE_MONTH_NUMBER, festivalPaint);
                            }else {
                                festivalPaint.setStrokeWidth((float) 2.0);
                                festivalPaint.setColor(isFocusMonth ? mWeekNumFocusColor : mWeekNumNoFocusColor);
                                canvas.drawCircle(x, circleY, CIRCLE_SIZE_MONTH_NUMBER, festivalPaint);
                                //                            canvas.drawText("", x, y, mMonthNumPaint);
                                //                            canvas.drawText(mDayNumbers[i], x, y, festivalPaint);
                            }
                        }
                    }
                }
            }
        }

        //mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor : mMonthNumOtherColor);
        //canvas.drawText(tmp[a][b].strF,x ,y+100, mMonthNumPaint);
            /***************************************************************************************************/
            ///M: do the extension here @{
            OPExtensionFactory.getLunarExtension(mContext).drawLunarString(mContext, canvas, mMonthNumPaint,
                    x, y, getDayFromLocation(x));
            ///@}
        if (isBold) {
            mMonthNumPaint.setFakeBoldText(isBold = false);//设置字体为粗体
            weekPaint.setFakeBoldText(isBold = false);
        }
    }

    /*private DPInfo[][] obtainDataOfMonth(int year, int month){
        HashMap<Integer, DPInfo[][]> dataOfYear = DATE_CACHE.get(year);
        if (null != dataOfYear && dataOfYear.size() != 0){
            DPInfo[][] dataOfMonth = dataOfYear.get(month);
            if (dataOfMonth != null) {
                Log.d("8888", "oldMonth == month && OldYear == year****obtainDataOfMonth_start"+"year:"+year+"month:"+month);
                return dataOfMonth;
            }
            dataOfMonth = mManager.obtainDPInfo(year, month);
            dataOfYear.put(month, dataOfMonth);
            return dataOfMonth;
        }else if(dataOfYear == null){
                Log.d("8888", "dataOfYear != null****obtainDataOfMonth_start"+"year:"+year+"month:"+month);
                dataOfYear = new HashMap<>();
                DPInfo[][] dataOfMonth = mManager.obtainDPInfo(year, month);
                dataOfYear.put((month), dataOfMonth);
                DATE_CACHE.put(year, dataOfYear);
                return dataOfMonth;
            }
        return null;
    }*/

    private int getMonthOfDay(String[] mDayNumbers, Time time, int i, int mCurrentMonth, boolean flag){
        if(0< i && i < 7 && !flag && Integer.parseInt(mDayNumbers[i]) < Integer.parseInt(mDayNumbers[i-1])){
            mCurrentMonth = time.month + 2;
            if(mCurrentMonth == 13){
                mCurrentMonth = 1;
            }
        }else if(1< i && i < 8 && flag && Integer.parseInt(mDayNumbers[i]) < Integer.parseInt(mDayNumbers[i-1])){
            mCurrentMonth = time.month + 2;
            if(mCurrentMonth == 13){
                mCurrentMonth = 1;
            }
        }
        //执行一次，绘制一列格子的日期，一次循环绘制一个星期
        return mCurrentMonth;
    }

    protected void drawEvents(Canvas canvas) {
        if (mEvents == null) {
            return;
        }

        int day = -1;
        for (ArrayList<Event> eventDay : mEvents) {
            day++;
            if (eventDay == null || eventDay.size() == 0) {
                continue;
            }
            int ySquare;
            int xSquare = computeDayLeftPosition(day) + SIDE_PADDING_MONTH_NUMBER + 1;
            int rightEdge = computeDayLeftPosition(day + 1);

            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                ySquare = EVENT_Y_OFFSET_PORTRAIT + mMonthNumHeight + TOP_PADDING_MONTH_NUMBER;
                rightEdge -= SIDE_PADDING_MONTH_NUMBER + 1;
            } else {
                ySquare = EVENT_Y_OFFSET_LANDSCAPE;
                rightEdge -= EVENT_X_OFFSET_LANDSCAPE;
            }

            // Determine if everything will fit when time ranges are shown.
            boolean showTimes = true;
            Iterator<Event> iter = eventDay.iterator();
            int yTest = ySquare;
            while (iter.hasNext()) {
                Event event = iter.next();
                int newY = drawEvent(canvas, event, xSquare, yTest, rightEdge, iter.hasNext(),
                        showTimes, /*doDraw*/ false);
                if (newY == yTest) {
                    showTimes = false;
                    break;
                }
                yTest = newY;
            }

            int eventCount = 0;
            iter = eventDay.iterator();
            while (iter.hasNext()) {
                Event event = iter.next();
                int newY = drawEvent(canvas, event, xSquare, ySquare, rightEdge, iter.hasNext(),
                        showTimes, /*doDraw*/ true);
                if (newY == ySquare) {
                    break;
                }
                eventCount++;
                ySquare = newY;
            }

            int remaining = eventDay.size() - eventCount;
            if (remaining > 0) {
                drawMoreEvents(canvas, remaining, xSquare);
            }
        }
    }

    protected int addChipOutline(FloatRef lines, int count, int x, int y) {
        lines.ensureSize(count + 16);
        // top of box
        lines.array[count++] = x;
        lines.array[count++] = y;
        lines.array[count++] = x + EVENT_SQUARE_WIDTH;
        lines.array[count++] = y;
        // right side of box
        lines.array[count++] = x + EVENT_SQUARE_WIDTH;
        lines.array[count++] = y;
        lines.array[count++] = x + EVENT_SQUARE_WIDTH;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH;
        // left side of box
        lines.array[count++] = x;
        lines.array[count++] = y;
        lines.array[count++] = x;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH + 1;
        // bottom of box
        lines.array[count++] = x;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH;
        lines.array[count++] = x + EVENT_SQUARE_WIDTH + 1;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH;

        return count;
    }

    /**
     * Attempts to draw the given event. Returns the y for the next event or the
     * original y if the event will not fit. An event is considered to not fit
     * if the event and its extras won't fit or if there are more events and the
     * more events line would not fit after drawing this event.
     *
     * @param canvas the canvas to draw on
     * @param event the event to draw
     * @param x the top left corner for this event's color chip
     * @param y the top left corner for this event's color chip
     * @param rightEdge the rightmost point we're allowed to draw on (exclusive)
     * @param moreEvents indicates whether additional events will follow this one
     * @param showTimes if set, a second line with a time range will be displayed for non-all-day
     *   events
     * @param doDraw if set, do the actual drawing; otherwise this just computes the height
     *   and returns
     * @return the y for the next event or the original y if it won't fit
     */
    protected int drawEvent(Canvas canvas, Event event, int x, int y, int rightEdge,
            boolean moreEvents, boolean showTimes, boolean doDraw) {
        /*
         * Vertical layout:
         *   (top of box)
         * a. EVENT_Y_OFFSET_LANDSCAPE or portrait equivalent
         * b. Event title: mEventHeight for a normal event, + 2xBORDER_SPACE for all-day event
         * c. [optional] Time range (mExtrasHeight)
         * d. EVENT_LINE_PADDING
         *
         * Repeat (b,c,d) as needed and space allows.  If we have more events than fit, we need
         * to leave room for something like "+2" at the bottom:
         *
         * e. "+ more" line (mExtrasHeight)
         *
         * f. EVENT_BOTTOM_PADDING (overlaps EVENT_LINE_PADDING)
         *   (bottom of box)
         */
        final int BORDER_SPACE = EVENT_SQUARE_BORDER + 1;       // want a 1-pixel gap inside border
        final int STROKE_WIDTH_ADJ = EVENT_SQUARE_BORDER / 2;   // adjust bounds for stroke width
        boolean allDay = event.allDay;
        int eventRequiredSpace = mEventHeight;
        if (allDay) {
            // Add a few pixels for the box we draw around all-day events.
            eventRequiredSpace += BORDER_SPACE * 2;
        } else if (showTimes) {
            // Need room for the "1pm - 2pm" line.
            eventRequiredSpace += mExtrasHeight;
        }
        int reservedSpace = EVENT_BOTTOM_PADDING;   // leave a bit of room at the bottom
        if (moreEvents) {
            // More events follow.  Leave a bit of space between events.
            eventRequiredSpace += EVENT_LINE_PADDING;

            // Make sure we have room for the "+ more" line.  (The "+ more" line is expected
            // to be <= the height of an event line, so we won't show "+1" when we could be
            // showing the event.)
            reservedSpace += mExtrasHeight;
        }

        if (y + eventRequiredSpace + reservedSpace > mHeight) {
            // Not enough space, return original y
            return y;
        } else if (!doDraw) {
            return y + eventRequiredSpace;
        }

        boolean isDeclined = event.selfAttendeeStatus == Attendees.ATTENDEE_STATUS_DECLINED;
        int color = event.color;
        if (isDeclined) {
            color = Utils.getDeclinedColorFromColor(color);
        }

        int textX, textY, textRightEdge;

        if (allDay) {
            // We shift the render offset "inward", because drawRect with a stroke width greater
            // than 1 draws outside the specified bounds.  (We don't adjust the left edge, since
            // we want to match the existing appearance of the "event square".)
            r.left = x;
            r.right = rightEdge - STROKE_WIDTH_ADJ;
            r.top = y + STROKE_WIDTH_ADJ;
            r.bottom = y + mEventHeight + BORDER_SPACE * 2 - STROKE_WIDTH_ADJ;
            textX = x + BORDER_SPACE;
            textY = y + mEventAscentHeight + BORDER_SPACE;
            textRightEdge = rightEdge - BORDER_SPACE;
        } else {
            r.left = x;
            r.right = x + EVENT_SQUARE_WIDTH;
            r.bottom = y + mEventAscentHeight;
            r.top = r.bottom - EVENT_SQUARE_WIDTH;
            textX = x + EVENT_SQUARE_WIDTH + EVENT_RIGHT_PADDING;
            textY = y + mEventAscentHeight;
            textRightEdge = rightEdge;
        }

        Style boxStyle = Style.STROKE;
        boolean solidBackground = false;
        if (event.selfAttendeeStatus != Attendees.ATTENDEE_STATUS_INVITED) {
            boxStyle = Style.FILL_AND_STROKE;
            if (allDay) {
                solidBackground = true;
            }
        }
        mEventSquarePaint.setStyle(boxStyle);
        mEventSquarePaint.setColor(color);
        canvas.drawRect(r, mEventSquarePaint);

        float avail = textRightEdge - textX;
        CharSequence text = TextUtils.ellipsize(
                event.title, mEventPaint, avail, TextUtils.TruncateAt.END);
        Paint textPaint;
        if (solidBackground) {
            // Text color needs to contrast with solid background.
            textPaint = mSolidBackgroundEventPaint;
        } else if (isDeclined) {
            // Use "declined event" color.
            textPaint = mDeclinedEventPaint;
        } else if (allDay) {
            // Text inside frame is same color as frame.
            mFramedEventPaint.setColor(color);
            textPaint = mFramedEventPaint;
        } else {
            // Use generic event text color.
            textPaint = mEventPaint;
        }
        canvas.drawText(text.toString(), textX, textY, textPaint);
        y += mEventHeight;
        if (allDay) {
            y += BORDER_SPACE * 2;
        }

        if (showTimes && !allDay) {
            // show start/end time, e.g. "1pm - 2pm"
            textY = y + mExtrasAscentHeight;
            mStringBuilder.setLength(0);
            text = DateUtils.formatDateRange(getContext(), mFormatter, event.startMillis,
                    event.endMillis, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_ALL,
                    Utils.getTimeZone(getContext(), null)).toString();
            text = TextUtils.ellipsize(text, mEventExtrasPaint, avail, TextUtils.TruncateAt.END);
            canvas.drawText(text.toString(), textX, textY, isDeclined ? mEventDeclinedExtrasPaint
                    : mEventExtrasPaint);
            y += mExtrasHeight;
        }

        y += EVENT_LINE_PADDING;

        return y;
    }

    protected void drawMoreEvents (Canvas canvas, int remainingEvents, int x) {
        int y = mHeight - (mExtrasDescent + EVENT_BOTTOM_PADDING);
        String text = getContext().getResources().getQuantityString(
                R.plurals.month_more_events, remainingEvents);
        mEventExtrasPaint.setAntiAlias(true);
        mEventExtrasPaint.setFakeBoldText(true);
        canvas.drawText(String.format(text, remainingEvents), x, y, mEventExtrasPaint);
        mEventExtrasPaint.setFakeBoldText(false);
    }

    /**
     * Draws a line showing busy times in each day of week The method draws
     * non-conflicting times in the event color and times with conflicting
     * events in the dna conflict color defined in colors.
     *
     *绘制一条线，忙的时候显示在每周工作日的方法绘制非冲突的时代，
     *在事件颜色，时间与 dna 中的冲突事件冲突中的颜色定义颜色。
     * @param canvas
     */
    //AA--绘制事件
    protected void drawDNA(Canvas canvas) {
        // Draw event and conflict times 绘制事件和时间冲突
        if (mDna != null) {
            for (Utils.DNAStrand strand : mDna.values()) {
                if (strand.color == CONFLICT_COLOR || strand.points == null
                        || strand.points.length == 0) {
                    continue;
                }
                mDNATimePaint.setColor(strand.color);
                canvas.drawLines(strand.points, mDNATimePaint);
            }
            // Draw black last to make sure it's on top
            Utils.DNAStrand strand = mDna.get(CONFLICT_COLOR);
            if (strand != null && strand.points != null && strand.points.length != 0) {
                mDNATimePaint.setColor(strand.color);
                canvas.drawLines(strand.points, mDNATimePaint);
            }
            if (mDayXs == null) {
                return;
            }
            int numDays = mDayXs.length;
            int xOffset = (DNA_ALL_DAY_WIDTH - DNA_WIDTH) / 2;
            if (strand != null && strand.allDays != null && strand.allDays.length == numDays) {
                for (int i = 0; i < numDays; i++) {
                    // this adds at most 7 draws. We could sort it by color and
                    // build an array instead but this is easier.
                    if (strand.allDays[i] != 0) {
                        mDNAAllDayPaint.setColor(strand.allDays[i]);
                        canvas.drawLine(mDayXs[i] + xOffset, DNA_MARGIN, mDayXs[i] + xOffset,
                                DNA_MARGIN + DNA_ALL_DAY_HEIGHT, mDNAAllDayPaint);
                    }
                }
            }
        }
    }

    @Override
    protected void updateSelectionPositions() {
        if (mHasSelectedDay) {
            int selectedPosition = mSelectedDay - mWeekStart;
            if (selectedPosition < 0) {
                selectedPosition += 7;
            }
            int effectiveWidth = mWidth - mPadding * 2;
            effectiveWidth -= SPACING_WEEK_NUMBER;
            mSelectedLeft = selectedPosition * effectiveWidth / mNumDays + mPadding;
            mSelectedRight = (selectedPosition + 1) * effectiveWidth / mNumDays + mPadding;
            mSelectedLeft += SPACING_WEEK_NUMBER;
            mSelectedRight += SPACING_WEEK_NUMBER;
        }
    }

    //根据当前点击位置确定点击的是在哪个坐标
    public int getDayIndexFromLocation(float x) {
        int dayStart = mShowWeekNum ? SPACING_WEEK_NUMBER + mPadding : mPadding;
        if (x < dayStart || x > mWidth - mPadding) {
            return -1;
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        return ((int) ((x - dayStart) * mNumDays / (mWidth - dayStart - mPadding)));
    }

    //AA--
    // 根据当前点击位置确定点击的是哪天的坐标
    // 来获取当前日期
    @Override
    public Time getDayFromLocation(float x) {
        int dayPosition = getDayIndexFromLocation(x);//(0-6)
        if (dayPosition == -1) {
            return null;
        }
        int day = mFirstJulianDay + dayPosition;

        Time time = new Time(mTimeZone);
        Log.d("333", "111[  getDayFromLocation---start" +"," +dayPosition+", day:"+ day +",time:"+ time);
        /// M: Delete a google walk around, which was used to correct the error
        /// at the first day of 1970 caused by google original framework.
        /// The framework error has been corrected,so the walk around is no longer needed.
        /// @{
            /*
             *        if (mWeek == 0) {
             *             // This week is weird...
             *             if (day < Time.EPOCH_JULIAN_DAY) {
             *                 day++;
             *            } else if (day == Time.EPOCH_JULIAN_DAY) {
             *                 time.set(1, 0, 1970);
             *                 time.normalize(true);
             *                return time;
             *            }
             *        }
             */
        ///@}
        /// M: if it's before or on the epoch Julian day, Time.setJulianDay() can't work correctly,
        // however, we could compute the time by ourself @{
        Utils.setJulianDayInGeneral(time, day);
        // @}

        return time;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Context context = getContext();
        // only send accessibility events if accessibility and exploration are
        // on.
        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Service.ACCESSIBILITY_SERVICE);
        if (!am.isEnabled() || !am.isTouchExplorationEnabled()) {
            return super.onHoverEvent(event);
        }
        if (event.getAction() != MotionEvent.ACTION_HOVER_EXIT) {
            Time hover = getDayFromLocation(event.getX());
            if (hover != null
                    && (mLastHoverTime == null || Time.compare(hover, mLastHoverTime) != 0)) {
                Long millis = hover.toMillis(true);
                String date = Utils.formatDateRange(context, millis, millis,
                        DateUtils.FORMAT_SHOW_DATE);
                AccessibilityEvent accessEvent = AccessibilityEvent
                        .obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
                accessEvent.getText().add(date);
                if (mShowDetailsInMonth && mEvents != null) {
                    int dayStart = SPACING_WEEK_NUMBER + mPadding;
                    int dayPosition = (int) ((event.getX() - dayStart) * mNumDays / (mWidth
                            - dayStart - mPadding));
                    ArrayList<Event> events = mEvents.get(dayPosition);
                    List<CharSequence> text = accessEvent.getText();
                    for (Event e : events) {
                        text.add(e.getTitleAndLocation() + ". ");
                        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
                        if (!e.allDay) {
                            flags |= DateUtils.FORMAT_SHOW_TIME;
                            if (DateFormat.is24HourFormat(context)) {
                                flags |= DateUtils.FORMAT_24HOUR;
                            }
                        } else {
                            flags |= DateUtils.FORMAT_UTC;
                        }
                        text.add(Utils.formatDateRange(context, e.startMillis, e.endMillis,
                                flags) + ". ");
                    }
                }
                sendAccessibilityEventUnchecked(accessEvent);
                mLastHoverTime = hover;
            }
        }
        return true;
    }

    public void setClickedDay(float xLocation) {
        mClickedDayIndex = getDayIndexFromLocation(xLocation);
        Log.d("333", "111[  setClickedDay---start" + "mClickedDayIndex:"+ mClickedDayIndex );
        invalidate();
    }
    public void clearClickedDay() {
        mClickedDayIndex = -1;
        invalidate();
    }
}
