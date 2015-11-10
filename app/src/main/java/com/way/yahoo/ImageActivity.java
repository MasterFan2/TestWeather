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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.Picasso;
import com.way.beans.Image;
import com.way.beans.ImageResult;
import com.way.common.util.T;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.widget.WaitDialog;
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

    //网络数据
    private ImageResult dataList;
    private HttpUtils http ;


    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private StaggeredGridLayoutManager gridLayoutManager;

    private ImageView cameraImg;

    public final static int REQUEST_CODE = 1;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    private MTDialog dialog;

    private WaitDialog waitDialog;

    private AVLoadingIndicatorView loadingIndicatorView;

    private ImageView contentImg;

    private boolean isGooding = false;//判断是否在处理赞       true:正在处理        false:空闲

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        waitDialog = new WaitDialog.Builder(context).create();

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

        adapter = new ImageAdapter();
        recyclerView.setAdapter(adapter);

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
        contentImg = (ImageView) view.findViewById(R.id.dialog_upload_content_img);
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

        //network data
        http = new HttpUtils();

        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList = null;
    }

    /**
     * 获取数据
     */
    private void getData(){
        waitDialog.show();
        http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/img/index?pageIndex=1&pageSize=20", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
                if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
                dataList = new Gson().fromJson(responseInfo.result, ImageResult.class);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
                T.showShort(context, "获取数据错误， 请稍后再试");
            }
        });
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
        public void onBindViewHolder(SimpleViewHolder holder, final int position) {
            final Image image = dataList.getResult().get(position);
            holder.goodTxt.setText(image.getSupportNum() + "");
            holder.goodImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isGooding == false) {
                        isGooding = true;
                        //赞
                        http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/img/support?imgId=" + image.getId(), new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()

                                //赞成功， 重新获取数据
                                if(dialog != null && dialog.isShowing()) dialog.dismiss();
                                dataList.getResult().get(position).setSupportNum(dataList.getResult().get(position).getSupportNum() + 1);
                                T.showShort(context, "赞成功");
                                adapter.notifyDataSetChanged();
                                isGooding = false;
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                if(dialog != null && dialog.isShowing()) dialog.dismiss();
                                T.showShort(context, "操作失败， 请稍后再试");
                                isGooding = false;
                            }
                        });
                    }else {
                        T.showShort(context, "请稍后， 正在处理中...");
                    }

                }
            });
            Picasso.with(context).load(image.getUrl()).placeholder(R.mipmap.img_default).into(holder.contentImg);
        }

        @Override
        public int getItemCount() {
            return dataList == null || dataList.getResult() == null ? 0 : dataList.getResult().size();
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

            if(selectedPhotos != null && selectedPhotos.size() > 0){
                Picasso.with(context).load(selectedPhotos.get(0)).placeholder(R.mipmap.img_default).into( contentImg);
                dialog.show();
            }
//            System.out.println(selectedPhotos.toString());
        }

    }
}
