package com.way.yahoo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.ui.swipeback.SwipeBackActivity;
import com.way.widget.dialog.MTDialog;
import com.way.widget.dialog.OnClickListener;
import com.way.widget.dialog.ViewHolder;
import com.way.widget.indicator.AVLoadingIndicatorView;
import com.way.widget.recyclerviewdiviver.DividerGridItemDecoration;
import com.way.widget.recyclerviewdiviver.HorizontalDividerItemDecoration;
import com.way.widget.recyclerviewdiviver.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import photopicker.PhotoPickerActivity;
import photopicker.utils.PhotoPickerIntent;

public class ImageActivity extends SwipeBackActivity implements OnClickListener {

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager gridLayoutManager;

    private ImageView cameraImg;

    public final static int REQUEST_CODE = 1;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    private MTDialog dialog;

    private AVLoadingIndicatorView loadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        findViewById(R.id.image_header_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cameraImg = (ImageView) findViewById(R.id.image_camera_img);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        DividerGridItemDecoration dividerGridItemDecoration = new DividerGridItemDecoration(context);
        recyclerView.addItemDecoration(dividerGridItemDecoration);
//        recyclerView.addItemDecoration(new VerticalDividerItemDecoration.Builder(context).color(getResources().getColor(R.color.gray_400)).build());
        recyclerView.setAdapter(new ImageAdapter());

        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intent = new PhotoPickerIntent(context);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //init dailog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_upload_layout, null);
        ViewHolder holder = new ViewHolder(view);

        View footView = LayoutInflater.from(context).inflate(R.layout.dialog_upload_foot_layout, null);
        loadingIndicatorView = (AVLoadingIndicatorView) footView.findViewById(R.id.dialog_upload_loadingView);
        dialog = new MTDialog.Builder(context)
                .setContentHolder(holder)
                .setCancelable(false)
                .setHeader(R.layout.dialog_upload_header_layout)
                .setFooter(footView)
                .setOnClickListener(this)
                .setGravity(MTDialog.Gravity.CENTER)
                .create();
    }

    @Override
    public void onClick(MTDialog dialog, View view) {
        switch (view.getId()){
            case R.id.footer_upload_cancel_button:
                dialog.dismiss();
                break;
            case R.id.footer_upload_confirm_button:
                loadingIndicatorView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.SimpleViewHolder> {

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(ImageActivity.this).inflate(R.layout.item_image_layout, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder {
            ImageView contentImg;
            ImageView goodImg;
            TextView  goodTxt;
            public SimpleViewHolder(View view) {
                super(view);
                contentImg = (ImageView) view.findViewById(R.id.item_content_img);
                goodImg    = (ImageView) view.findViewById(R.id.item_good_img);
                goodTxt    = (TextView) view.findViewById(R.id.item_good_txt);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
//            System.out.println(selectedPhotos.toString());
        }
        dialog.show();
    }
}
