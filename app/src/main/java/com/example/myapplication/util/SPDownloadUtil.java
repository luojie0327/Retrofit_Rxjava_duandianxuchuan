package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplication.MyApplication;

public class SPDownloadUtil {


    private static SharedPreferences mSharedPreferences;
    private static SPDownloadUtil instance;

    public static SPDownloadUtil getInstance(){
        if(mSharedPreferences == null || instance == null){

            synchronized (SPDownloadUtil.class){
                instance = new SPDownloadUtil();
                mSharedPreferences = MyApplication.getInstance().getSharedPreferences
                        (
                                MyApplication.getInstance().getPackageName() + ".downloadSp", Context.MODE_PRIVATE
                        );
            }

        }

        return instance;
    }

    /**
     *
     * 清空数据
     * @return true 清楚成功
     */

    public boolean clear(){
        return mSharedPreferences.edit().clear().commit();
    }

    /**
     * 保存数据
     *
     * @param key    键
     * @param value  保存的值
     * @return
     */
    public boolean save(String key,long value){
         return mSharedPreferences.edit().putLong(key,value).commit();
    }


    /**
     * 获取保存的数据
     *
     * @param key   键
     * @param defValue  默认值
     * @return value
     */
    public long get(String key,long defValue){
        return mSharedPreferences.getLong(key,defValue);
    }




}
