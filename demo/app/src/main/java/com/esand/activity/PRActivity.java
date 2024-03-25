package com.esand.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.esand.activity.util.AppValidationMgr;
import com.esand.activity.util.HTTPClient;
import com.esand.activity.util.SaveDataUtil;
import com.esandinfo.livingdetection.EsLivingDetectionManager;
import com.esandinfo.livingdetection.bean.EsLDTInitConfig;
import com.esandinfo.livingdetection.bean.EsLivingDetectResult;
import com.esandinfo.livingdetection.biz.EsLivingDetectCallback;
import com.esandinfo.livingdetection.constants.EsLivingDetectErrorCode;
import com.esandinfo.livingdetection.util.AppExecutors;
import com.esandinfo.livingdetection.util.GsonUtil;

import java.util.Map;

/**
 * 实名认证 DEMO
 */
public class PRActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private Button mBtnConfirm;
    private EditText mEtCertNo;
    private EditText mEtCertName;
    private TextView mTvInfo;
    private String mCertName;
    private String mCertNo;
    private EsLivingDetectionManager manager;
    private Handler handler;
    private final int UPDATE_TEXT_VIEW = 0;
    private HTTPClient client;
    private CheckBox btDistance;
    private CheckBox btBlink;
    private CheckBox btHeadShaking;
    private CheckBox btNodding;
    private CheckBox btMouthOpening;
    private CheckBox cbWithOCR;
    private CheckBox cbOCRFirst;
    private CheckBox cbIncFront;
    private CheckBox cbMode;
    private int livingType = 0; // 活体类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        mEtCertName = findViewById(R.id.et_cert_name);
        mEtCertNo = findViewById(R.id.et_cert_no);
        mEtCertName.addTextChangedListener(this);
        mEtCertNo.addTextChangedListener(this);
        mEtCertName.setText(SaveDataUtil.getCertName(this));
        mEtCertNo.setText(SaveDataUtil.getCertNo(this));
        mTvInfo = findViewById(R.id.tv_info);

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
        cbWithOCR = (CheckBox)findViewById(R.id.cbWithOCR);
        cbWithOCR.setOnClickListener(this);
        cbOCRFirst = (CheckBox)findViewById(R.id.cbOCRFirst);
        cbOCRFirst.setOnClickListener(this);
        cbIncFront = (CheckBox)findViewById(R.id.cbIncFront);
        cbIncFront.setOnClickListener(this);
        cbMode = (CheckBox)findViewById(R.id.cbMode);
        cbMode.setOnClickListener(this);
        client = new HTTPClient(PRActivity.this);
    }

    private void initData() {
        manager = new EsLivingDetectionManager(PRActivity.this);
        updateComfirmButtonStatus();
    }

    /**
     * 更新按钮状态
     */
    private void updateComfirmButtonStatus() {
        mCertName = mEtCertName.getText().toString();
        mCertNo = mEtCertNo.getText().toString();
        if (mCertName!=null && AppValidationMgr.isIDCard(mCertNo) || (cbMode!=null && cbMode.isChecked())) {
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
            livingType = 0;
            livingType = btDistance.isChecked()?livingType*10+1:livingType;
            livingType = btBlink.isChecked()?livingType*10+2:livingType;
            livingType = btHeadShaking.isChecked()?livingType*10+3:livingType;
            livingType = btNodding.isChecked()?livingType*10+4:livingType;
            livingType = btMouthOpening.isChecked()?livingType*10+5:livingType;
            //开始校验
            auth();
        }

        updateComfirmButtonStatus();
    }

    private void auth() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                // 认证配置
                EsLDTInitConfig config = new EsLDTInitConfig(livingType);
                //config.setLivingType(livingType);
                config.withOCR = cbWithOCR.isChecked();// 是否包含身份证OCR
                config.ocrFirst = cbOCRFirst.isChecked(); // OCR 优先
                config.ocrIncFront = cbIncFront.isChecked(); // 是否包括身份证背面
                if (cbMode.isChecked()) {
                    /**
                     * 业务模式
                     *     0 ： 仅活体
                     *     1 ： 全流程，除活体外还支持其他交互页面 (当前仅支持实名认证交互界面)
                     */
                    config.mode = 1;
                }

                EsLivingDetectResult result = manager.verifyInit(config);
                if (EsLivingDetectErrorCode.ELD_SUCCESS == result.getCode()) {
                    String rsp = client.idInit(result.getData(),mCertName,mCertNo);
                    updateTextView("认证初始化返回数据："+rsp);
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
                                        String auth = client.idAuth(result);
                                        updateTextView("实名认证结果："+auth);
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
        updateComfirmButtonStatus();
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
