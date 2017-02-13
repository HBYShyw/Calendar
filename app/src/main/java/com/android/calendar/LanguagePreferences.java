package com.android.calendar;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.calendar.festivalPicker.utils.PreferenceUtil;

import java.util.Locale;

/**
 * Created by yongwen.huang on 2016/12/12.
 */
public class LanguagePreferences extends PreferenceFragment{
    private static final String TAG = "LanguagePreferences";
    private ListPreference listPreference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.language_preferences);

        initView();
        initEvent();
    }

    private void initView(){
        listPreference = (ListPreference) findPreference("language");
        listPreference.setSummary(listPreference.getValue());
    }

    private void initEvent(){
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue){
                //获取ListPreference中的实体内容
                CharSequence[] entries = listPreference.getEntries();
                //获取ListPreference中的实体内容的下标值
                int index = listPreference.findIndexOfValue((String)newValue);
                android.util.Log.d("6666", "FestivalPreference[initEvent]" + ","+entries.length+","+index+","+Locale.getDefault());
                listPreference.setValue((String) entries[index]);

//                PreferenceUtil.commitString("language",entries[index].toString());

                String entry = (String) entries[index];
                switch(entry){
                    case "跟随系统":
                        switchLanguage("en");
                        break;
                    case "简体中文":
                        switchLanguage("cn");
                        break;
                    case "繁体中文（台湾）":
                        switchLanguage("tw");
                        break;
                    case "繁体中文（香港）":
                        switchLanguage("hk");
                        break;
                    case "English":
                        switchLanguage("en");
                        break;
                    default:
                        //跟随系统
                        switchLanguage("en");
                        break;
                }
                return true;
            }
        });
    }

    protected void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        String locale = Locale.getDefault().getCountry().toLowerCase();
        String system_language = Locale.getDefault().getLanguage();
        String local_language = config.locale.getLanguage();//en  获取应用语言 zh
        android.util.Log.d("6666", "FestivalPreference[language-1]" + "系统语言:"+locale+",应用语言:"+local_language+"system_language:"+system_language);


        Log.d("6666", "1111_ system_language:" + system_language);
        Log.d("6666", "1111_ local_language:" + local_language);

        // 应用用户选择语言

        if (language.equals("cn")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if(language.equals("tw")){
            config.locale = Locale.TAIWAN;
        }else if(language.equals("hk")){
            config.locale = Locale.TAIWAN;
        }else if(language.equals("en")){
            config.locale = Locale.ENGLISH;

        }else {
            config.locale = new Locale("es", "Malawi", "");
        }
        resources.updateConfiguration(config, dm);

        android.util.Log.d("6666", "FestivalPreference[language-1]" + "系统语言:"+locale+",应用语言:"+local_language+"system_language:"+system_language);

        /*获取系统语言：Locale.getDefault().getLanguage()
        获取应用语言：getResources().getConfiguration().locale.getLanguage()*/

        //保存设置语言的类型
        PreferenceUtil.commitString("language", language);
    }
}
