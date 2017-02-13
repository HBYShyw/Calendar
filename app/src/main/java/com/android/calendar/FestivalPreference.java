package com.android.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.calendar.festivalPicker.utils.PreferenceUtil;

import java.util.Locale;

/**
 * 应用内国家切换列表
 * 每个Preference都是以键值对的形式保存下来的
 *
 * Created by yongwen.huang on 2016/11/3.
 */
public class FestivalPreference extends PreferenceFragment{
    private static final String TAG = "FestivalPreference";
    private ListPreference listPreference;
    private CheckBoxPreference checkBoxPreference;
    private CharSequence listSummary, checkBoxSummary;
    private String[] countryArray;
    private CharSequence[] entries;
    private String[] checkBoxSummaryCountry = new String[]{"America","Nigeria","Kenya",
            "Tanzania","Ghana","Egypt","Cameroon","Rwanda","Cote D'Ivoire","Malawi","Burkina Faso"};

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.festival_preferences);

        /*String system_language = Locale.getDefault().getLanguage();//en  获取系统语言国家对应语言 zh
        String locale = Locale.getDefault().getCountry().toLowerCase();//us   获得的是国家 cn 获取国家
        String local_language = getResources().getConfiguration().locale.getLanguage();//en  获取应用语言 zh

        Log.d("6666", "1111_ system_language:" + system_language);
        Log.d("6666", "1111_system locale:" + locale);
        Log.d("6666", "1111_ local_language:" + local_language);*/
        initView();
        initEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(view != null){
            view.setBackgroundColor(getResources().getColor(R.color.background_color));
        }
        return view;
    }

    private void initView(){
        checkBoxPreference = (CheckBoxPreference) findPreference("follow_system");
        listPreference = (ListPreference) findPreference("Countries");
        //获得列表默认提示语
        //获取ListPreference中的实体内容
        entries = listPreference.getEntries();
        countryArray = getResources().getStringArray(R.array.countries);
        listSummary = listPreference.getSummary();
        checkBoxSummary = checkBoxPreference.getSummary();

        //初始化设置ListPreference默认选中值
        /*String value = PreferenceUtil.getString("country", null);
        if(value != null){
            listPreference.setValue(value);
        }*/

        //当手动没有选中国家时
        String select_country = PreferenceUtil.getString("select_country", null);
        if(select_country == null){
            checkBoxPreference.setChecked(true);
            listPreference.setEnabled(false);
        }

        if(checkBoxPreference.isChecked()) {
            //跟随系统默认设置的国家，手动匹配设置为空
            PreferenceUtil.commitString("select_country", null);
            PreferenceUtil.commitBoolean("system_default", true);//系统默认为true

            String default_country = PreferenceUtil.getString("default_country", null);
            for(int i = 0; i < checkBoxSummaryCountry.length; i++){
                if(checkBoxSummaryCountry[i].equals(default_country)){
                    checkBoxPreference.setSummary(checkBoxSummary + "("+entries[i]+")");
                }
            }

            listPreference.setEnabled(false);
            listPreference.setSummary(listSummary);
        }else {
            PreferenceUtil.commitBoolean("system_default", false);
            listPreference.setEnabled(true);
            /*String value = PreferenceUtil.getString("country", (String) summary);
            listPreference.setSummary(value);*/
            int index = PreferenceUtil.getInt("index", -1);
            if(index != -1){
                listPreference.setSummary(entries[index]);//有国家选中，更改标题
            }
        }
    }

    private void initEvent(){
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue){

                //获取ListPreference中的实体内容的下标值
                int index = listPreference.findIndexOfValue((String)newValue);
                android.util.Log.d("555", "FestivalPreference[initEvent]" + ","+entries.length+","+index+","+Locale.getDefault());
                listPreference.setValue((String) entries[index]);
                PreferenceUtil.commitInt("index", index);
                PreferenceUtil.commitString("country",entries[index].toString());
                PreferenceUtil.commitBoolean("system_default", false);

                if(entries[index].equals(countryArray[0])){
                    switchCountry("cn");
                }else if(entries[index].equals(countryArray[1])){
                    switchCountry("us");
                }else if(entries[index].equals(countryArray[2])){
                    switchCountry("Nigeria");
                }else if(entries[index].equals(countryArray[3])){
                    switchCountry("Kenya");
                }else if(entries[index].equals(countryArray[4])){
                    switchCountry("Tanzania");
                }else if(entries[index].equals(countryArray[5])){
                    switchCountry("Ghana");
                }else if(entries[index].equals(countryArray[6])){
                    switchCountry("Egypt");
                }else if(entries[index].equals(countryArray[7])){
                    switchCountry("Cameroon");
                }else if(entries[index].equals(countryArray[8])){
                    switchCountry("Rwanda");
                }else if(entries[index].equals(countryArray[9])){
                    switchCountry("Cote D'Ivoire");
                }else if(entries[index].equals(countryArray[10])){
                    switchCountry("Malawi");
                }else if(entries[index].equals(countryArray[11])){
                    switchCountry("Burkina Faso");
                }else{
                    switchCountry("Others");
                }

                //选择国家后，重启主页
                Intent intent = new Intent(getActivity(), AllInOneActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
                // 杀掉进程
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                return true;
            }
        });
    }

    protected void switchCountry(String country) {
        //保存选择的类型
        PreferenceUtil.commitString("select_country", country);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
        if("follow_system".equals(preference.getKey())){
            if(checkBoxPreference.isChecked()){//跟随系统默认，监听系统语言切换
                listPreference.setSummary(listSummary);
                PreferenceUtil.commitString("select_country", null);
                PreferenceUtil.commitBoolean("system_default", true);//系统默认为true

                //选择国家后，重启主页
                Intent intent = new Intent(getActivity(), AllInOneActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
                // 杀掉进程
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }else{
                /*String value = PreferenceUtil.getString("select", (String) summary);
                listPreference.setSummary(value);*/
                checkBoxPreference.setSummary(checkBoxSummary);

                listPreference.setValue(null);
                listPreference.setEnabled(true);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
