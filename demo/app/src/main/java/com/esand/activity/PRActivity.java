package com.esand.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.esand.activity.utile.AppValidationMgr;
import com.esand.activity.utile.HTTPClient;
import com.esand.activity.utile.SaveDataUtil;
import com.esandinfo.livingdetection.EsLivingDetectionManager;
import com.esandinfo.livingdetection.bean.EsLivingDetectResult;
import com.esandinfo.livingdetection.biz.EsLivingDetectCallback;
import com.esandinfo.livingdetection.constants.EsLivingDetectErrorCode;
import com.esandinfo.livingdetection.util.AppExecutors;
import com.esandinfo.livingdetection.util.GsonUtil;
import com.esandinfo.livingdetection.util.MyLog;

import java.util.Map;

public class PRActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private Button mBtnConfirm;
    private Button uploadBtn;
    private Button clearLogBtn;
    private EditText mEtCertNo;
    private EditText mEtCertName;
    private TextView mTvClean;
    private TextView mTvInfo;
    private String mCertName;
    private String mCertNo;
    private EsLivingDetectionManager manager;
    private Handler handler;
    private final int UPDATE_TEXT_VIEW = 0;
    private HTTPClient client;
    private RadioButton btDistance;
    private RadioButton btBlink;
    private RadioButton btHeadShaking;
    private RadioButton btNodding;
    private RadioButton btMouthOpening;
    int livingType = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr);
        initView();
        initData();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATE_TEXT_VIEW) {
                    mTvInfo.append(msg.obj + "\n");
                }
            }
        };
    }


    private void initView() {
        uploadBtn = findViewById(R.id.upload_log_btn);
        uploadBtn.setOnClickListener(this);
        clearLogBtn = findViewById(R.id.clear_log_btn);
        clearLogBtn.setOnClickListener(this);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        mEtCertName = findViewById(R.id.et_cert_name);
        mEtCertNo = findViewById(R.id.et_cert_no);
        mEtCertName.addTextChangedListener(this);
        mEtCertNo.addTextChangedListener(this);
        mEtCertName.setText(SaveDataUtil.getCertName(this));
        mEtCertNo.setText(SaveDataUtil.getCertNo(this));
        mTvInfo = findViewById(R.id.tv_info);
        mTvClean = findViewById(R.id.tv_clean);
        mTvClean.setOnClickListener(this);

        btDistance = (RadioButton)findViewById(R.id.btDistance);
        btDistance.setOnClickListener(this);
        btBlink = (RadioButton)findViewById(R.id.btBlink);
        btBlink.setOnClickListener(this);
        btHeadShaking = (RadioButton)findViewById(R.id.btHeadShaking);
        btHeadShaking.setOnClickListener(this);
        btNodding = (RadioButton)findViewById(R.id.btNodding);
        btNodding.setOnClickListener(this);
        btMouthOpening = (RadioButton)findViewById(R.id.btMouthOpening);
        btMouthOpening.setOnClickListener(this);
        client = new HTTPClient(PRActivity.this);
    }
    private void initData() {
        manager = new EsLivingDetectionManager(PRActivity.this);
        mCertName = mEtCertName.getText().toString();
        mCertNo = mEtCertNo.getText().toString();
        if (mCertName!=null && AppValidationMgr.isIDCard(mCertNo)) {
            mBtnConfirm.setEnabled(true);
            mBtnConfirm.setBackground(getResources().getDrawable(R.drawable.button_bg));
        } else {
            mBtnConfirm.setEnabled(false);
            mBtnConfirm.setBackground(getResources().getDrawable(R.drawable.button_unselect_bg));
        }
    }

    // 更新 UI 进程界面
    private void updateTextView(String messageStr) {
        Message message = Message.obtain();
        message.obj = messageStr;
        message.what = UPDATE_TEXT_VIEW;
        handler.sendMessage(message);
    }

    @Override
    public void onClick(final View view) {
        if (view.getId() == mBtnConfirm.getId()) {
            initData();
            SaveDataUtil.saveCertNo(PRActivity.this, mCertNo);
            SaveDataUtil.saveCertName(PRActivity.this, mCertName);
            //开始校验
            auth();
        }

        if(view.getId() == uploadBtn.getId()){
            final Context _this=this;
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    final String logId = MyLog.uploadLogFile();
                    Looper.prepare();
                    AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                    //    设置Title的内容
                    builder.setTitle("上传日志成功");
                    //    设置Content来显示一个信息
                    builder.setMessage("您的日志id为:"+logId);
                    builder.setPositiveButton("复制id", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            //剪切板Data对象
                            ClipData mClipData = ClipData.newPlainText("Simple test", logId);
                            //把clip对象放在剪贴板中
                            mClipboardManager.setPrimaryClip(mClipData);
                        }
                    });
                    //    设置一个PositiveButton
                    builder.setNegativeButton("确定", null);
                    builder.create();
                    builder.show();
                    Looper.loop();
                }
            });
        }

        if(view.getId() == clearLogBtn.getId()){
            MyLog.clearLog();
        }

        if (view.getId() == mTvClean.getId()) {
            mTvInfo.setText("");
        }
        if (btDistance.getId() == view.getId()) {
            livingType = 1;
        }

        if (btBlink.getId() == view.getId()) {
            livingType = 2;
        }

        if (btHeadShaking.getId() == view.getId()) {
            livingType = 3;
        }

        if (btNodding.getId() == view.getId()) {
            livingType = 4;
        }
        if (btMouthOpening.getId() == view.getId()) {
            livingType = 5;
        }
    }

    private void auth() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EsLivingDetectResult result = manager.verifyInit(livingType);
                if (EsLivingDetectErrorCode.ELD_SUCCESS == result.getCode()) {
                    String rsp = client.RPauthInit(result.getData(),mCertName,mCertNo);
                    updateTextView("认证初始化返回数据："+rsp);
                    Log.e("", "测试 rsp:" + rsp);
                    Map map = GsonUtil.getAllJson().fromJson(rsp, Map.class);
                    String token="";
                    if(map.get("token")!=null){
                        token=(String)map.get("token");
                    }
                    //初始化成功过 访问服务端进行初始化
                    manager.startLivingDetect(token, new EsLivingDetectCallback() {
                        @Override
                        public void onFinish(final EsLivingDetectResult result) {
                            updateTextView("活体检测结束");
                            if (result.getCode() == EsLivingDetectErrorCode.ELD_SUCCESS) {
                                AppExecutors.getInstance().networkIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        String auth = client.RPauth(result);
                                        updateTextView("auth："+auth);
                                        Log.e("", "测试 加密:" + auth);
                                    }
                                });
                            } else {
                                updateTextView(result.getMsg());
                            }
                        }
                    });
                } else {
                    updateTextView(result.getMsg());
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEtCertName!=null && AppValidationMgr.isIDCard(mEtCertNo.getText().toString())) {
            mBtnConfirm.setEnabled(true);
            mBtnConfirm.setBackground(getResources().getDrawable(R.drawable.button_bg));
        } else {
            mBtnConfirm.setEnabled(false);
            mBtnConfirm.setBackground(getResources().getDrawable(R.drawable.button_unselect_bg));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
