package com.esand.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.esand.activity.utile.HTTPClient;
import com.esandinfo.livingdetection.EsLivingDetectionManager;
import com.esandinfo.livingdetection.bean.EsLivingDetectResult;
import com.esandinfo.livingdetection.biz.EsLivingDetectCallback;
import com.esandinfo.livingdetection.constants.EsLivingDetectErrorCode;
import com.esandinfo.livingdetection.util.AppExecutors;
import com.esandinfo.livingdetection.util.GsonUtil;

import java.util.Map;

public class FaceCompareActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_face_compare;
    private TextView mTvClean;
    private TextView mTvInfo;
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
        setContentView(R.layout.activity_face_compare);
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
        btn_face_compare = findViewById(R.id.btn_face_compare);
        btn_face_compare.setOnClickListener(this);
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
        client = new HTTPClient(FaceCompareActivity.this);
    }
    private void initData() {
        manager = new EsLivingDetectionManager(FaceCompareActivity.this);
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
        if (view.getId() == btn_face_compare.getId()) {
            initData();
            //开始校验
            auth();
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
                    String rsp = client.faceCompareInit(result.getData());
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
                                        String auth = client.faceCompareVerify(result);
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
