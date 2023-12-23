/*
 *  活体检测
 */

package com.esand.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.esand.activity.utile.HTTPClient;
import com.esandinfo.livingdetection.EsLivingDetectionManager;
import com.esandinfo.livingdetection.bean.EsCryptKeyType;
import com.esandinfo.livingdetection.bean.EsLDTInitConfig;
import com.esandinfo.livingdetection.bean.EsLivingDetectResult;
import com.esandinfo.livingdetection.biz.EsLivingDetectCallback;
import com.esandinfo.livingdetection.constants.EsLivingDetectErrorCode;
import com.esandinfo.livingdetection.util.AppExecutors;
import com.esandinfo.livingdetection.util.GsonUtil;
import com.esandinfo.livingdetection.util.MyLog;

import java.io.File;
import java.util.Map;

public class LivingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_face;
    private Button btn_getVideo;
    private EsLivingDetectionManager manager;
    private HTTPClient client;
    private TextView tv_info;
    private TextView tvClean;
    private CheckBox btDistance;
    private CheckBox btBlink;
    private CheckBox btHeadShaking;
    private CheckBox btNodding;
    private CheckBox btMouthOpening;
    private CheckBox btWeakDistance;
    private CheckBox btWeakHeadShaking;
    private CheckBox btWeakNodding;
    private CheckBox btWeakColors;
    private int livingType = 0; // 活体类型
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化函数。建议提前加载 在application 提前调用, 提升性能
        EsLivingDetectionManager.Init();
        EsLivingDetectionManager.LivingViewStyleInstance().setTextColor("#3322ff").setProgressBgColor("#ffaa11").setProgressStaGradient("#22dd11");
//        EsLivingDetectionManager.LivingViewStyleInstance().
//                setBackGroundColor("#112233").
//                setCircleBackWidth(10).
//                setProgressBgColor("#4422df").
//                setProgressStaGradient("#00ff00").
//                setProgressEndGradient("#ff00ff").setBackGroundColor("#ffaa22");
        client = new HTTPClient(LivingActivity.this);
        manager = new EsLivingDetectionManager(LivingActivity.this);
        tv_info = (TextView) findViewById(R.id.tv_info);
        tvClean = findViewById(R.id.tvClean);
        tvClean.setOnClickListener(this);
        btn_face = (Button) findViewById(R.id.btn_face);
        btn_face.setOnClickListener(this);
        btn_getVideo = findViewById(R.id.btn_getVideo);
        btn_getVideo.setOnClickListener(this);

        btDistance = (CheckBox)findViewById(R.id.btDistance);
        btDistance.setOnClickListener(this);
        btBlink = (CheckBox)findViewById(R.id.btBlink);
        btBlink.setOnClickListener(this);
        btHeadShaking = (CheckBox)findViewById(R.id.btHeadShaking);
        btHeadShaking.setOnClickListener(this);
        btNodding = (CheckBox)findViewById(R.id.btNodding);
        btNodding.setOnClickListener(this);
        btMouthOpening = (CheckBox)findViewById(R.id.btMouthOpening);
        btMouthOpening.setOnClickListener(this);
        btWeakColors = (CheckBox)findViewById(R.id.btWeakColors);
        btWeakColors.setOnClickListener(this);
//        btWeakDistance = (CheckBox)findViewById(R.id.btWeakDistance);
//        btWeakDistance.setOnClickListener(this);
//        btWeakHeadShaking = (CheckBox)findViewById(R.id.btWeakHeadShaking);
//        btWeakHeadShaking.setOnClickListener(this);
//        btWeakNodding = (CheckBox)findViewById(R.id.btWeakNodding);
//        btWeakNodding.setOnClickListener(this);
    }

    protected void startLivingDetect() {
        EsLivingDetectionManager.s_isOpenVideoRecorder = true; // 是否录制视频
        EsLivingDetectionManager.s_isAutoUploadVerifyMsg = false;
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                MyLog.debug("livingType->>>>>"+livingType);
                EsLDTInitConfig config = new EsLDTInitConfig(livingType);
                config.setKeyType(EsCryptKeyType.TEST);
                EsLivingDetectResult result = manager.verifyInit(config);
                if (EsLivingDetectErrorCode.ELD_SUCCESS == result.getCode()) {
                    JSONObject object = new JSONObject();
                    object.put("initMsg",result.getData());
                    String reqMsg = object.toJSONString();
                    String rsp = client.ldtInit(result.getData()); // 调用阿里云
                    showLog("认证初始化返回数据：" + rsp);
                    Log.e("", "测试 rsp:" + rsp);
                    if (rsp != null && rsp.length() > 1){
                        Map map = GsonUtil.getAllJson().fromJson(rsp, Map.class);
                        token = (String)map.get("token");
                        //初始化成功过 访问服务端进行初始化
                        manager.startLivingDetect(token, new EsLivingDetectCallback() {
                            @Override
                            public void onFinish(final EsLivingDetectResult result) {
                                File file = result.getVideo();
                                if(file != null){
                                    showLog("活体检测结束" + file.getAbsolutePath());
                                    file.deleteOnExit();
                                }

                                if (result.getCode() == EsLivingDetectErrorCode.ELD_SUCCESS) {
                                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            JSONObject object = new JSONObject();
                                            object.put("token", result.getToken());
                                            object.put("verifyMsg",result.getData());
                                            String reqMsg = object.toJSONString();
                                            // 阿里云线上环境
                                            String auth = client.ldtAuth(result);
                                            showLog("auth："+auth);
                                            Log.e("", "测试 加密:" + auth);
                                        }
                                    });

                                   if (result.getVideo() != null) {
                                       result.getVideo().delete();
                                   }
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
            livingType = 0;
            livingType = btDistance.isChecked()?livingType*10+1:livingType;
            livingType = btBlink.isChecked()?livingType*10+2:livingType;
            livingType = btHeadShaking.isChecked()?livingType*10+3:livingType;
            livingType = btNodding.isChecked()?livingType*10+4:livingType;
            livingType = btMouthOpening.isChecked()?livingType*10+5:livingType;
            livingType = btWeakColors.isChecked()?livingType*10+6:livingType;
//            livingType = btWeakDistance.isChecked()?livingType*10+7:livingType;
//            livingType = btWeakHeadShaking.isChecked()?livingType*10+8:livingType;
//            livingType = btWeakNodding.isChecked()?livingType*10+9:livingType;
            startLivingDetect();
//            cs();
        }
        if (btn_getVideo.getId() == view.getId()) {

            File ldtVideo = manager.getVideoFile(token);
            showLog("视频地址 ： "+ ldtVideo.getAbsolutePath());
        }

        if(R.id.tvClean == view.getId()){
            tv_info.setText("");
        }
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