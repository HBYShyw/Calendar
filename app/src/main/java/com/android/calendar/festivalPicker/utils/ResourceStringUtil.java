package com.android.calendar.festivalPicker.utils;

import android.content.Context;
import android.content.res.TypedArray;
import com.android.calendar.festivalPicker.bizs.DPCManager;

/**
 * Created by yongwen.huang on 2016/12/21.
 */
public class ResourceStringUtil{

    private Context context;

    private static ResourceStringUtil resourceStringUtil;

    public static ResourceStringUtil getInstance() {
        if (resourceStringUtil == null) {
            resourceStringUtil = new ResourceStringUtil();
            synchronized(DPCManager.class){
                if(resourceStringUtil == null){
                    resourceStringUtil = new ResourceStringUtil();
                }
            }
        }
        return resourceStringUtil;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

    public String getString(int resId){
        return getContext().getResources().getString(resId);
    }

    public String[] getStringArray(int resId){
        return getContext().getResources().getStringArray(resId);
    }

    public TypedArray obtainTypedArray(int resId){
        return getContext().getResources().obtainTypedArray(resId);
    }
}
