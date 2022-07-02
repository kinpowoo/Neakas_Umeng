package com.jhkj.neakasa.push;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jhkj.neakasa.BaseActivity;
import com.jhkj.neakasa.R;
import com.jhkj.neakasa.UmInitConfig;
import com.jhkj.neakasa.sp.SharedPreferencesHelper;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;
import com.umeng.message.inapp.InAppMessageManager;
import com.umeng.message.inapp.UmengSplashMessageActivity;

public class SplashTestActivity extends BaseActivity {
    View inflate;
    Dialog dialog;
    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesHelper = new SharedPreferencesHelper(this, "umeng");

        /*** sp中uminit为1已经同意隐私协议*/
        if (sharedPreferencesHelper.getSharedPreference("uminit", "").equals("1")) {

            /***
             *  此时初始化在appcation中进行，这里不做任何友盟初始化操作，直接跳转activity
             *
             */
            //推送全屏消息设置（非初始化操作，这是推送全屏消息设置）
            InAppMessageManager mInAppMessageManager = InAppMessageManager.getInstance(SplashTestActivity.this);
            mInAppMessageManager.setInAppMsgDebugMode(true);
            //跳转homeactivity
            mInAppMessageManager.setMainActivityPath("com.jhkj.neakasa.push.UpushActivity");

            //推送平台多维度推送决策必须调用方法(需要同意隐私协议之后初始化完成调用)
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED){
                String[] mPermissionList = new String[]{
                        Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this,mPermissionList, 123);
            }else{
                goToMainPage();
            }
        } else {
            /*** 隐私协议授权弹窗*/
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED){
                String[] mPermissionList = new String[]{
                        Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this,mPermissionList, 123);
            }else{
                dialog();
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.layout_splash;
    }

    public void goToMainPage(){
        /*** 友盟sdk正式初始化*/
        UmInitConfig umInitConfig = new UmInitConfig();
        umInitConfig.UMinit(getApplicationContext());

        //推送平台多维度推送决策必须调用方法(需要同意隐私协议之后初始化完成调用)
        PushAgent.getInstance(SplashTestActivity.this).onAppStart();

        //跳转到HomeActivity
        Intent intent = new Intent(SplashTestActivity.this, UpushActivity.class);
        startActivity(intent);
        finish();

    }

    @SuppressLint("ResourceType")
    public void dialog() {
        dialog = new Dialog(this, R.style.dialog);
        inflate = LayoutInflater.from(SplashTestActivity.this).inflate(R.layout.diaologlayout, null);
        TextView succsebtn = (TextView) inflate.findViewById(R.id.succsebtn);
        TextView canclebtn = (TextView) inflate.findViewById(R.id.caclebtn);

        succsebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*** uminit为1时代表已经同意隐私协议，sp记录当前状态*/
                sharedPreferencesHelper.put("uminit", "1");
                UMConfigure.submitPolicyGrantResult(getApplicationContext(), true);
                //关闭弹窗
                dialog.dismiss();
                goToMainPage();
            }
        });

        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                UMConfigure.submitPolicyGrantResult(getApplicationContext(), false);
                //不同意隐私协议，退出app
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });

        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);

        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123 && permissions[0].equals(Manifest.permission.READ_PHONE_STATE)
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            /*** sp中uminit为1已经同意隐私协议*/
            if (sharedPreferencesHelper.getSharedPreference("uminit", "").equals("1")){
                goToMainPage();
            }else{
                dialog();
            }
        }else{
            String[] mPermissionList = new String[]{
                    Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(this,mPermissionList, 123);
        }
    }

}