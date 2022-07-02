package com.jhkj.neakasa;


import android.app.Application;

import com.jhkj.neakasa.sp.SharedPreferencesHelper;
import com.umeng.commonsdk.UMConfigure;

/**
 * Created by wangfei on 2018/1/23.
 */

public class App extends Application {

    public static String AppKey = "618cd1e8e0f9bb492b5777ea";
    public static String AppMsgSecretKey = "8672ed25d047ca443f384ab64cff8bb8";
    public static String AppMasterSecretKey = "kro86tjdd4hqngdubirdksb5hbmdakn2";

    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferencesHelper=new SharedPreferencesHelper(this,"umeng");

        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(true);
        //友盟预初始化
        UMConfigure.preInit(getApplicationContext(),AppKey,"Umeng");
        /**
         * 打开app首次隐私协议授权，以及sdk初始化，判断逻辑请查看SplashTestActivity
         */
        //判断是否同意隐私协议，uminit为1时为已经同意，直接初始化umsdk
        if(sharedPreferencesHelper.getSharedPreference("uminit","").equals("1")){
            //友盟正式初始化
            UmInitConfig umInitConfig=new UmInitConfig();
            umInitConfig.UMinit(getApplicationContext());
        }


    }
}
