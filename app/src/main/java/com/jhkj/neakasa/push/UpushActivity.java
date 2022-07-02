package com.jhkj.neakasa.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jhkj.neakasa.BaseActivity;
import com.jhkj.neakasa.R;
import com.jhkj.neakasa.push.notification.DebugNotification;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushAliasCallback;
import com.umeng.message.api.UPushTagCallback;
import com.umeng.message.common.UmengMessageDeviceConfig;
import com.umeng.message.common.inter.ITagManager.Result;
import com.umeng.message.inapp.IUmengInAppMsgCloseCallback;
import com.umeng.message.inapp.InAppMessageManager;

import java.util.List;

public class UpushActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = UpushActivity.class.getName();
    private static final String TAG_REMAIN = "tag_remain";
    private static final String WEIGHTED_TAG_REMAIN = "weighted_tag_remain";

    private EditText inputTag;
    private EditText inputAlias;
    private EditText inputAliasType;

    private TextView tagRemain;

    private PushAgent mPushAgent;
    private Handler handler;

    private EditText customerTicker;
    private EditText customerTitle;
    private EditText customerBody;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("推送");
        setBackVisibility(false);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        initClickListener();
        handler = new Handler();

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();

        //展示插屏消息
        if (savedInstanceState == null) {
//            showCardMessage();
        }
    }

    private void initClickListener() {
        inputTag = findViewById(R.id.etx_input_tag);
        inputAlias = findViewById(R.id.etx_input_alias);
        inputAliasType = findViewById(R.id.etx_input_alias_type);
        tagRemain = findViewById(R.id.tv_tag_remain);
        customerTicker = findViewById(R.id.ticker_input);
        customerTitle = findViewById(R.id.title_input);
        customerBody = findViewById(R.id.body_input);
        findViewById(R.id.btn_add_tag).setOnClickListener(this);
        findViewById(R.id.btn_delete_tag).setOnClickListener(this);
        findViewById(R.id.btn_show_tag).setOnClickListener(this);
        findViewById(R.id.btn_add_alias).setOnClickListener(this);
        findViewById(R.id.btn_delete_alias).setOnClickListener(this);
        findViewById(R.id.btn_show_card_message).setOnClickListener(this);
        findViewById(R.id.btn_serialnet).setOnClickListener(this);
        findViewById(R.id.btn_device_check).setOnClickListener(this);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_upush;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_add_tag) {
            addTag();
        } else if (id == R.id.btn_delete_tag) {
            deleteTag();
        } else if (id == R.id.btn_show_tag) {
            showTag();
        } else if (id == R.id.btn_add_alias) {
            addAlias();
        } else if (id == R.id.btn_delete_alias) {
            deleteAlias();
        } else if (id == R.id.btn_set_alias) {
            setAlias();
        } else if (id == R.id.btn_show_card_message) {
            showCardMessage();
        } else if (id == R.id.btn_serialnet) {
            serialnet();
        } else if (id == R.id.btn_device_check) {
            deviceCheck();
        }
    }

    private void setAlias() {
        String alias = inputAlias.getText().toString();
        String aliasType = inputAliasType.getText().toString();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(this, "请输入alias", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(aliasType)) {
            Toast.makeText(this, "请输入alias type", Toast.LENGTH_SHORT).show();
            return;
        }
        mPushAgent.setAlias(alias, aliasType, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        inputAlias.setText("");
                        inputAliasType.setText("");
                    }
                });
            }
        });
    }

    private void deleteAlias() {
        String alias = inputAlias.getText().toString();
        String aliasType = inputAliasType.getText().toString();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(this, "请输入alias", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(aliasType)) {
            Toast.makeText(this, "请输入alias type", Toast.LENGTH_SHORT).show();
            return;
        }
        mPushAgent.deleteAlias(alias, aliasType, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        inputAlias.setText("");
                        inputAliasType.setText("");
                    }
                });
            }
        });
    }

    private void addAlias() {
        String alias = inputAlias.getText().toString();
        String aliasType = inputAliasType.getText().toString();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(this, "请先输入alias", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(aliasType)) {
            Toast.makeText(this, "请先输入alias type", Toast.LENGTH_SHORT).show();
            return;
        }
        mPushAgent.addAlias(alias, aliasType, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        inputAlias.setText("");
                        inputAliasType.setText("");
                    }
                });
            }
        });
    }

    private void showTag() {
        mPushAgent.getTagManager().getTags(new UPushTagCallback<List<String>>() {
            @Override
            public void onMessage(final boolean isSuccess, final List<String> result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess) {
                            StringBuilder info = new StringBuilder();
                            if (result != null) {
                                for (int i = 0; i < result.size(); i++) {
                                    String tag = result.get(i);
                                    info.append(tag);
                                    if (i != result.size() - 1) {
                                        info.append("、");
                                    }
                                }
                            }
                            PushDialogFragment.newInstance(1, 1, getString(R.string.push_get_tags),
                                    info.toString()).show(getFragmentManager(), "deleteTag");
                        } else {
                            PushDialogFragment.newInstance(1, 0, getString(R.string.push_get_tags),
                                    "").show(getFragmentManager(), "deleteTag");
                        }
                    }
                });
            }
        });
    }

    private void deleteTag() {
        final String tag = inputTag.getText().toString();
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(this, "请先输入tag", Toast.LENGTH_SHORT).show();
            return;
        }
        mPushAgent.getTagManager().deleteTags(new UPushTagCallback<Result>() {
            @Override
            public void onMessage(final boolean isSuccess, final Result result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        inputTag.setText("");
                        if (isSuccess) {
                            sharedPref.edit().putInt(TAG_REMAIN, result.remain).apply();
                            tagRemain.setText(String.valueOf(result.remain));
                            PushDialogFragment.newInstance(0, 1,
                                    getString(R.string.push_delete_success), tag).show(getFragmentManager(), "deleteTag");
                        } else {
                            PushDialogFragment.newInstance(0, 0,
                                    getString(R.string.push_delete_fail), tag).show(getFragmentManager(), "deleteTag");
                        }
                    }
                });
            }
        }, tag);
    }

    private void addTag() {
        final String tag = inputTag.getText().toString();
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(this, "请先输入tag", Toast.LENGTH_SHORT).show();
            return;
        }
        mPushAgent.getTagManager().addTags(new UPushTagCallback<Result>() {
            @Override
            public void onMessage(final boolean isSuccess, final Result result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        inputTag.setText("");
                        if (isSuccess) {
                            sharedPref.edit().putInt(TAG_REMAIN, result.remain).apply();
                            tagRemain.setText(String.valueOf(result.remain));
                            PushDialogFragment.newInstance(0, 1, getString(R.string.push_add_success), tag).show(
                                    getFragmentManager(), "addTag");
                        } else {
                            PushDialogFragment.newInstance(0, 0, getString(R.string.push_add_fail), tag).show(
                                    getFragmentManager(), "addTag");
                        }
                    }
                });
            }
        }, tag);
    }

    private void showCardMessage() {
        InAppMessageManager.getInstance(this).showCardMessage(this, "main",
                new IUmengInAppMsgCloseCallback() {
                    @Override
                    public void onClose() {
                        Log.i(TAG, "card message close");
                    }
        });
    }

    private void serialnet() {
        String tickerStr = customerTicker.getText().toString();
        String titleStr = customerTitle.getText().toString();
        String bodyStr = customerBody.getText().toString();
        if(TextUtils.isEmpty(tickerStr)){
            tickerStr = "Android unicast ticker";
        }
        if(TextUtils.isEmpty(titleStr)){
            titleStr = "Title";
        }
        if(TextUtils.isEmpty(bodyStr)){
            bodyStr = "Demo透传测试";
        }
        DebugNotification.transmission(tickerStr,titleStr,bodyStr,UpushActivity.this);
    }

    private void deviceCheck() {
        String push_switch = UmengMessageDeviceConfig.isNotificationEnabled(this);
        String status;
        switch (push_switch) {
            case "true":
                status = "是";
                break;
            case "false":
                status = "否";
                break;
            default:
                status = push_switch;
                break;
        }
        String cpu = UmengMessageDeviceConfig.getCPU();
        String osVersion = android.os.Build.VERSION.RELEASE;
        String info = getString(R.string.push_os_version) + osVersion + "\n" + getString(R.string.push_cpu_info) + cpu
                + "\n" + getString(R.string.push_system_notification_switch) + status;
        PushDialogFragment.newInstance(1, 0, getString(
                R.string.push_device_check), info).show(getFragmentManager(), "deviceCheck");
    }
}
