/*
 *  Copyright (C) 2015-present TzuTaLin
 */

package com.esand.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.esand.activity.utile.HTTPClient;
import com.esandinfo.livingdetection.EsLivingDetectionManager;
import com.esandinfo.livingdetection.bean.EsLivingDetectResult;
import com.esandinfo.livingdetection.biz.EsLivingDetectCallback;
import com.esandinfo.livingdetection.constants.EsLivingDetectErrorCode;
import com.esandinfo.livingdetection.util.AppExecutors;
import com.esandinfo.livingdetection.util.GsonUtil;
import com.esandinfo.livingdetection.util.MyLog;

import java.util.Map;


public class SearchFaceActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_face;
    private EsLivingDetectionManager manager;
    private HTTPClient client;
    private TextView tv_info;
    private TextView tvClean;
    private EditText et_db;
    private RadioButton btDistance;
    private RadioButton btBlink;
    private RadioButton btHeadShaking;
    private RadioButton btNodding;
    private RadioButton btMouthOpening;
    private int livingType = 1; // 活体类型
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // 初始化函数。建议提前加载 在application 提前调用, 提升性能
        EsLivingDetectionManager.Init();
        client = new HTTPClient(SearchFaceActivity.this);
        manager = new EsLivingDetectionManager(SearchFaceActivity.this);
        tv_info = (TextView) findViewById(R.id.tv_info);
        et_db = findViewById(R.id.et_db);
        tvClean = findViewById(R.id.ss_tv_clean);
        tvClean.setOnClickListener(this);
        btn_face = (Button) findViewById(R.id.btn_search);
        btn_face.setOnClickListener(this);

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
    }

    protected void startLivingDetect() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EsLivingDetectResult result = manager.verifyInit(livingType);
                if (EsLivingDetectErrorCode.ELD_SUCCESS == result.getCode()) {
                    String rsp = client.searchInit(result.getData());
                    showLog("认证初始化返回数据："+rsp);
                    Log.e("", "测试 rsp:" + rsp);
                    if (rsp != null){
                        Map map = GsonUtil.getAllJson().fromJson(rsp, Map.class);
                        token = (String)map.get("token");
                        //初始化成功过 访问服务端进行初始化
                        manager.startLivingDetect(token, new EsLivingDetectCallback() {
                            @Override
                            public void onFinish(final EsLivingDetectResult result) {
                                showLog("活体检测结束");
                                if (result.getCode() == EsLivingDetectErrorCode.ELD_SUCCESS) {
                                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String auth = client.searchAuth(result,et_db.getText().toString());
                                            showLog("auth："+auth);
                                            Log.e("", "测试 加密:" + auth);
                                        }
                                    });
                                } else {
                                    showLog(result.getMsg());
                                }
                            }
                        });
                    }
                } else {
                    showLog(result.getMsg());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (btn_face.getId() == view.getId()) {
            startLivingDetect();
        }
        if (tvClean.getId() == view.getId()) {
            tv_info.setText("");
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

    private void cs() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EsLivingDetectResult result = manager.verifyInit(livingType);
                JSONObject object = new JSONObject();
                object.put("initMsg",result.getData());
                object.put("appid","1231231");
                object.put("bizType","5");
                String rsp = HTTPClient.post("http://192.168.2.19:18090/livingdetection/facesearch/init", object.toJSONString());
                Map map = GsonUtil.getAllJson().fromJson(rsp, Map.class);
                token = (String)map.get("token");
                manager.startLivingDetect(token, new EsLivingDetectCallback() {
                    @Override
                    public void onFinish(final EsLivingDetectResult result) {
                        showLog("活体检测结束");
                        if (result.getCode() == EsLivingDetectErrorCode.ELD_SUCCESS) {
                            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject object = new JSONObject();
                                    object.put("token", result.getToken());
                                    object.put("verifyMsg", result.getData());
                                    object.put("dbName","esandinfo1");
                                    object.put("bizType","5");
                                    String rsp = HTTPClient.post("http://192.168.2.19:18090/livingdetection/facesearch/verify", object.toJSONString());
                                    MyLog.error(rsp);
                                }
                            });
                        } else {
                            showLog(result.getMsg());
                        }
                    }
                });
            }
        });
    }

    public void showLog(final String msg) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                String s = tv_info.getText().toString();
                tv_info.setText(s + "\n" + msg);
            }
        });
    }

}