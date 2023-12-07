/*
 *  Copyright (C) 2015-present TzuTaLin
 */

package com.esand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class IndexActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_sr;
    private Button btn_ht;
    private Button btn_ss;
    private Button btn_face_compare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
    }

    private void initView() {
        btn_sr = findViewById(R.id.btn_sr);
        btn_ht = findViewById(R.id.btn_ht);
        btn_ss = findViewById(R.id.btn_ss);
        btn_face_compare = findViewById(R.id.btn_face_compare);
        btn_sr.setOnClickListener(this);
        btn_ht.setOnClickListener(this);
        btn_ss.setOnClickListener(this);
        btn_face_compare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_sr:
                //跳转到实人认证
                intent = new Intent(IndexActivity.this, PRActivity.class);
                break;
            case R.id.btn_ht:
                intent = new Intent(IndexActivity.this, LivingActivity.class);
                break;
            case R.id.btn_ss:
                intent = new Intent(IndexActivity.this, SearchFaceActivity.class);
                break;
            case R.id.btn_face_compare:
                intent = new Intent(IndexActivity.this, FaceCompareActivity.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
        startActivity(intent);

    }

}