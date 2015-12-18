package com.way.yahoo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.widget.TouchImageView;

public class BigPicActivity extends SwipeBackActivity {

    private TouchImageView img;

    private String url ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pic);

        findViewById(R.id.header_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img = (TouchImageView) findViewById(R.id.content_img);

        url = getIntent().getStringExtra("url");

        if(!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).into(img);
        }else{
            Picasso.with(context).load(R.mipmap.img_default).into(img);
        }

    }
}
