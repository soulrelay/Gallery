package com.soulrelay.gallery.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.soulrelay.gallery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 图片加载库的封装演示案例
 * Created by soulrelay on 2016/12/11 19:18
 */
public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btn_gallery)
    Button btnGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

    }

    @OnClick(R.id.btn_gallery)
    public void onClick() {
    }
}
