package com.mediatek.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//清理数据
public class ClearDataReceiver extends BroadcastReceiver {

    private static final String PACKAGE_NAME = "packageName";
    private static final String TAG = "ClearDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.v(TAG, "action = " + intent.getAction());
        String clearedPackage = intent.getStringExtra(PACKAGE_NAME);
        /// M: clearedPackage may be null
        if (clearedPackage != null && clearedPackage.equals(context.getPackageName())) {
            LogUtil.i(TAG, clearedPackage + ": Calendar App data was cleared. " +
                    "clear the unread messages");
            //给系统发送广播，unread，通知提醒了，但是还未查看的日程
            try{
                MTKUtils.writeUnreadReminders(context, 0);
            }catch(SecurityException e){
                e.printStackTrace();
                LogUtil.v(TAG, "WRITE_SETTINGS permission granted");
            }
        }
    }
}
