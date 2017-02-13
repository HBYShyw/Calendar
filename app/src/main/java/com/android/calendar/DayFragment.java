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

package com.android.calendar;

import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.android.calendar.festivalPicker.bizs.DPCManager;
import com.android.calendar.festivalPicker.entities.DPInfo;
import com.mediatek.calendar.LogUtil;
import com.mediatek.calendar.PDebug;
//AA--日视图：DayFragment的布局采用了自定义布局DayView，而填充该布局文件时用到了ViewSwitcher
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * This is the base class for Day and Week Activities.
 */
public class DayFragment extends Fragment implements CalendarController.EventHandler, ViewFactory {
    /**
     * The view id used for all the views we create. It's OK to have all child
     * views have the same ID. This ID is used to pick which view receives
     * focus when a view hierarchy is saved / restore
     */
    ///M:@{
    private static final String TAG = "DayFragment";
    ///@}
    private static final int VIEW_ID = 1;

    protected static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";

    protected ProgressBar mProgressBar;
    protected ViewSwitcher mViewSwitcher;
    protected Animation mInAnimationForward;
    protected Animation mOutAnimationForward;
    protected Animation mInAnimationBackward;
    protected Animation mOutAnimationBackward;
    EventLoader mEventLoader;

    Time mSelectedDay = new Time();

    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            if (!DayFragment.this.isAdded()) {
                return;
            }
            String tz = Utils.getTimeZone(getActivity(), mTZUpdater);
            mSelectedDay.timezone = tz;
            mSelectedDay.normalize(true);
        }
    };

    private int mNumDays;
    private TextView festivalName_1, festivalName_2, festivalName_3;
    private DPCManager mCManager = DPCManager.getInstance();
    private LinearLayout label_parent_1;
    private RelativeLayout label_parent_2, label_parent_3;

    public DayFragment() {
        mSelectedDay.setToNow();
    }

    public DayFragment(long timeMillis, int numOfDays) {
        mNumDays = numOfDays;
        if (timeMillis == 0) {
            mSelectedDay.setToNow();
        } else {
            mSelectedDay.set(timeMillis);
        }
    }

    /**
     * M: pass the context in to get original displayed time in our calendar
     * @param context
     * @param timeMillis
     * @param numOfDays
     */
    public DayFragment(Context context, long timeMillis, int numOfDays) {
        mNumDays = numOfDays;
        mSelectedDay = Utils.getValidTimeInCalendar(context, timeMillis);
        //hahaha
        Log.d("666", TAG+"111[  DayFragment---start" +",mSelectedDay: " + mSelectedDay);
    }

    @Override
    public void onCreate(Bundle icicle) {
        PDebug.EndAndStart("AllInOneActivity.onCreate->DayFragment.onCreate", "DayFragment.onCreate");

        PDebug.Start("DayFragment.onCreate.superOnCreate");
        super.onCreate(icicle);
        PDebug.End("DayFragment.onCreate.superOnCreate");
        Log.d("8888", TAG+"+onCreate****onCreate_start");
        Context context = getActivity();

        PDebug.Start("DayFragment.onCreate.loadAnimations");
        mInAnimationForward = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        mOutAnimationForward = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        mInAnimationBackward = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        mOutAnimationBackward = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
        PDebug.End("DayFragment.onCreate.loadAnimations");

        mEventLoader = new EventLoader(context);
        PDebug.EndAndStart("DayFragment.onCreate", "DayFragment.onCreate->DayFragment.onCreateView");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        PDebug.EndAndStart("DayFragment.onCreate->DayFragment.onCreateView", "DayFragment.onCreateView");
        Log.d("8888", TAG+"+onCreateView****onCreateView_start");
        PDebug.Start("DayFragment.onCreateView.inflateViewSwitcher");
        View v;
        if(mNumDays == 1){
            v = inflater.inflate(R.layout.day_fragment, null);
            LinearLayout rootView = (LinearLayout) v.findViewById(R.id.dayView_fragment);
            FrameLayout viewGroup = (FrameLayout) inflater.inflate(R.layout.day_activity, null);
            //        FrameLayout viewParent = (FrameLayout) viewGroup.findViewById(R.id.dayView_activity);
            ViewSwitcher viewSwitcher = (ViewSwitcher) viewGroup.findViewById(R.id.switcher);
            /*FrameLayout customView = (FrameLayout) inflater.inflate(
                R.layout.day_activity, null).findViewById(R.id.switcher);*/
            //        rootView.addView(viewGroup);
            viewGroup.removeView(viewSwitcher);
            rootView.addView(viewSwitcher);
            mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.switcher);
            label_parent_1 = (LinearLayout) rootView.findViewById(R.id.festival_label_parent_1);
            label_parent_2 = (RelativeLayout) rootView.findViewById(R.id.festival_label_parent_2);
            label_parent_3 = (RelativeLayout) rootView.findViewById(R.id.festival_label_parent_3);
            festivalName_1 = (TextView) label_parent_1.findViewById(R.id.festival_label);
            festivalName_2 = (TextView) label_parent_2.findViewById(R.id.festival_label_tv);
            festivalName_3 = (TextView) label_parent_3.findViewById(R.id.festival_label_tv);
            if(mSelectedDay != null){
                setFestivalByTime(mSelectedDay.year, mSelectedDay.month+1, mSelectedDay.monthDay);
                Log.d("666", TAG+"111[  setFestivalByTime---start" +",mSelectedDay.monthDay: " + mSelectedDay.monthDay);
            }
        }else {
            v = inflater.inflate(R.layout.day_activity, null);
            mViewSwitcher = (ViewSwitcher) v.findViewById(R.id.switcher);
        }

        PDebug.End("DayFragment.onCreateView.inflateViewSwitcher");

        mViewSwitcher.setFactory(this);//AA--通过实现ViewFactory

        PDebug.Start("DayFragment.onCreateView.updateViewSwitcher");
        mViewSwitcher.getCurrentView().requestFocus();
        ((DayView) mViewSwitcher.getCurrentView()).updateTitle();
        PDebug.End("DayFragment.onCreateView.updateViewSwitcher");

        PDebug.EndAndStart("DayFragment.onCreateView", "DayFragment.onCreateView->AllInOneActivity.onResume");

        return v;
    }

    //ViewSwitch是一个视图切换组件，可以把多个视图重叠在一起，
    // 而每次只显示一个视图，而给ViewSwitch而创建要显示的View时，
    // 有两种方式：
    // 1、既可以在xml文件中添加，
    // 2、也可以通过实现ViewFactory，重写makeView()添加。

    /*重写makeView()*/
    public View makeView() {
        PDebug.Start("DayFragment.makeView");

        mTZUpdater.run();
        DayView view = new DayView(getActivity(), CalendarController
                .getInstance(getActivity()), mViewSwitcher, mEventLoader, mNumDays);
        view.setId(VIEW_ID);
        view.setLayoutParams(new ViewSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        view.setSelected(mSelectedDay, false, false);

        PDebug.End("DayFragment.makeView");
        return view;
    }

    @Override
    public void onResume() {
        PDebug.EndAndStart("AllInOneActivity.onResume->DayFragment.onResume", "DayFragment.onResume");

        super.onResume();
        mEventLoader.startBackgroundThread();
        mTZUpdater.run();
        eventsChanged();
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.handleOnResume();
        view.restartCurrentTimeUpdates();

        view = (DayView) mViewSwitcher.getNextView();
        view.handleOnResume();
        view.restartCurrentTimeUpdates();

        PDebug.End("DayFragment.onResume");
        PDebug.Start("DayFragment.onResume->DayView.onSizeChanged");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        long time = getSelectedTimeInMillis();
        if (time != -1) {
            outState.putLong(BUNDLE_KEY_RESTORE_TIME, time);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.cleanup();
        view = (DayView) mViewSwitcher.getNextView();
        view.cleanup();
        mEventLoader.stopBackgroundThread();

        // Stop events cross-fade animation
        view.stopEventsAnimation();
        ((DayView) mViewSwitcher.getNextView()).stopEventsAnimation();
    }

    void startProgressSpinner() {
        // start the progress spinner
        mProgressBar.setVisibility(View.VISIBLE);
    }

    void stopProgressSpinner() {
        // stop the progress spinner
        mProgressBar.setVisibility(View.GONE);
    }

    private void goTo(Time goToTime, boolean ignoreTime, boolean animateToday) {
        if(mNumDays == 1){
            setFestivalByTime(goToTime.year, goToTime.month+1, goToTime.monthDay);
            Log.d("666", TAG+"222[  setFestivalByTime---start" +",goToTime.monthDay: " + goToTime.monthDay);
        }

        //hahaha
        Log.d("666", TAG+"111[  goTo---start" +",goToTime: " + goToTime);
        if (mViewSwitcher == null) {
            // The view hasn't been set yet. Just save the time and use it later.
            mSelectedDay.set(goToTime);
            return;
        }

        DayView currentView = (DayView) mViewSwitcher.getCurrentView();
        ///M:@{
        if (currentView == null) {
            LogUtil.e(TAG, "getCurrentView() return null,return");
            return;
        }
        currentView.selectionFocusShow(false);
        ///@}
        // How does goTo time compared to what's already displaying?
        int diff = currentView.compareToVisibleTimeRange(goToTime);

        if (diff == 0) {
            // In visible range. No need to switch view
            currentView.setSelected(goToTime, ignoreTime, animateToday);
        } else {
            // Figure out which way to animate
            if (diff > 0) {
                mViewSwitcher.setInAnimation(mInAnimationForward);
                mViewSwitcher.setOutAnimation(mOutAnimationForward);
            } else {
                mViewSwitcher.setInAnimation(mInAnimationBackward);
                mViewSwitcher.setOutAnimation(mOutAnimationBackward);
            }

            DayView next = (DayView) mViewSwitcher.getNextView();
           ///M:@{
            next.selectionFocusShow(false);
            ///@}
            if (ignoreTime) {
                next.setFirstVisibleHour(currentView.getFirstVisibleHour());
            }

            next.setSelected(goToTime, ignoreTime, animateToday);
            next.reloadEvents();
            mViewSwitcher.showNext();
            next.requestFocus();
            next.updateTitle();
            next.restartCurrentTimeUpdates();
        }
    }

    /**
     * Returns the selected time in milliseconds. The milliseconds are measured
     * in UTC milliseconds from the epoch and uniquely specifies any selectable
     * time.
     *
     * @return the selected time in milliseconds
     */
    public long getSelectedTimeInMillis() {
        if (mViewSwitcher == null) {
            return -1;
        }
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        if (view == null) {
            return -1;
        }
        return view.getSelectedTimeInMillis();
    }

    public void eventsChanged() {
        PDebug.Start("DayFragment.eventsChanged");

        if (mViewSwitcher == null) {
            return;
        }
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.clearCachedEvents();
        view.reloadEvents();

        view = (DayView) mViewSwitcher.getNextView();
        view.clearCachedEvents();

        PDebug.End("DayFragment.eventsChanged");
    }

    Event getSelectedEvent() {
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        return view.getSelectedEvent();
    }

    boolean isEventSelected() {
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        return view.isEventSelected();
    }

    Event getNewEvent() {
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        return view.getNewEvent();
    }

    public DayView getNextView() {
        return (DayView) mViewSwitcher.getNextView();
    }

    public long getSupportedEventTypes() {
        return EventType.GO_TO | EventType.EVENTS_CHANGED;
    }

    private void setFestivalByTime(int year, int month, int day){
        Log.d("2222", TAG+"111[  setFestivalByTime---start" +",year: " + year + ",month:"+ month + ",day:" + day);
        DPInfo[][] tmp = mCManager.obtainDPInfo(year, month);
            /*当月份与日期匹配的时候，绘制出节日*/
        //遍历根据年月算出的数组，如果与当天匹配，则做当天的绘制处理
        for (int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                /*int day = Integer.parseInt(tmp[i][j].strG);
                int monthDay = mSelectedDay.monthDay;*/
                String sMonthDay = String.valueOf(day);
                if(sMonthDay.equals(tmp[i][j].strG)){//当前天与二维数组天数匹配
                    if(!tmp[i][j].strF.equals("")){
                        if(label_parent_1 != null){
                            label_parent_1.setVisibility(View.VISIBLE);
                        }
                        Log.d("2222", "tmp[i][j]_1111:"+tmp[i][j].strF);
                        if(tmp[i][j].strF.contains("#")){
                            Log.d("6666", "tmp[i][j]_2222:"+tmp[i][j].strF);
                            String[] festivalStr = tmp[i][j].strF.split("#");
                            if(festivalStr.length == 2){
                                if(label_parent_2 != null && label_parent_3 != null){
                                    label_parent_2.setVisibility(View.VISIBLE);
                                    label_parent_3.setVisibility(View.GONE);
                                    festivalName_1.setText(festivalStr[0]);
                                    festivalName_2.setText(festivalStr[1]);
                                    Log.d("6666", "tmp[i][j]_3333:"+festivalStr[0]+","+festivalStr[1]);
                                }
                            }else if(festivalStr.length == 3){
                                if(label_parent_2 != null && label_parent_3 != null){
                                    label_parent_2.setVisibility(View.VISIBLE);
                                    label_parent_3.setVisibility(View.VISIBLE);
                                    festivalName_1.setText(festivalStr[0]);
                                    festivalName_2.setText(festivalStr[1]);
                                    festivalName_3.setText(festivalStr[2]);
                                }
                            }
                        }else {
                            if(label_parent_1 != null && label_parent_2 != null && label_parent_3 != null){
                                label_parent_2.setVisibility(View.GONE);
                                label_parent_3.setVisibility(View.GONE);
                                festivalName_1.setText(tmp[i][j].strF);
                            }
                        }
                    }else {
                        if(label_parent_1 != null && label_parent_2 != null && label_parent_3 != null){
                            label_parent_1.setVisibility(View.GONE);
                            label_parent_2.setVisibility(View.GONE);
                            label_parent_3.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    public void handleEvent(EventInfo msg) {
        if (msg.eventType == EventType.GO_TO) {
            Log.d("666", TAG+"111[  DayFragment handleEvent---start" +",msg.selectedTime: " + msg.selectedTime);
// TODO support a range of time
// TODO support event_id
// TODO support select message

            Log.d("666", TAG+"111[  handleEvent---start" +",msg.selectedTime: " + msg.selectedTime);
            Log.d("666", TAG+"111[  handleEvent---start" +",msg.selectedTime: " + msg.selectedTime.format2445());
            Log.d("666", TAG+"111[  handleEvent---start" +",msg.startTime: " + msg.startTime);
            Log.d("666", TAG+"111[  handleEvent---start" +",msg.endTime: " + msg.endTime);


            goTo(msg.selectedTime, (msg.extraLong & CalendarController.EXTRA_GOTO_DATE) != 0,
                    (msg.extraLong & CalendarController.EXTRA_GOTO_TODAY) != 0);
        } else if (msg.eventType == EventType.EVENTS_CHANGED) {
            eventsChanged();
        }
    }
}
