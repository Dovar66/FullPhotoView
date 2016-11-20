package com.dovar.fullphotoview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FullPhotoView custom = (FullPhotoView) findViewById(R.id.iv_custom);
        custom.setCustomView(R.layout.popup_custom, R.id.popup_custom_iv, this, R.id.popup_custom_tv);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "什么也没有", Toast.LENGTH_SHORT).show();
    }
}
