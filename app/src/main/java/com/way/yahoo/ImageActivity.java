package com.way.yahoo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.way.BitmapUtil;
import com.way.beans.BaseEntity;
import com.way.beans.GoodImageComment;
import com.way.beans.Image;
import com.way.beans.ImageResult;
import com.way.beans.UploadResp;
import com.way.common.util.SystemUtils;
import com.way.common.util.T;
import com.way.net.HttpClient;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.utils.Dbutils;
import com.way.widget.WaitDialog;
import com.way.widget.dialog.MTDialog;
import com.way.widget.dialog.OnClickListener;
import com.way.widget.dialog.ViewHolder;
import com.way.widget.indicator.AVLoadingIndicatorView;
import com.way.widget.recyclerviewdiviver.DividerGridItemDecoration;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import photopicker.PhotoPickerActivity;
import photopicker.utils.PhotoPickerIntent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImageActivity extends SwipeBackActivity implements OnClickListener {

    private String saveUrl;

    //网络数据
    private ImageResult dataList;

    private DbManager db;


    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private StaggeredGridLayoutManager gridLayoutManager;

    private ImageView cameraImg;

    public final static int REQUEST_CODE = 1;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    private MTDialog dialog;

    private WaitDialog waitDialog;

    private AVLoadingIndicatorView loadingIndicatorView;
    private LinearLayout footLayout;

    private ImageView contentImg;

    private boolean isGooding = false;//判断是否在处理赞       true:正在处理        false:空闲

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_image);

        db = x.getDb(Dbutils.getConfig());

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
        footLayout  = (LinearLayout) footView.findViewById(R.id.dialog_upload_foot_layout);
        dialog = new MTDialog.Builder(context)
                .setContentHolder(holder)
                .setCancelable(false)
                .setHeader(R.layout.dialog_upload_header_layout)
                .setFooter(footView)
                .setOnClickListener(this)
                .setGravity(MTDialog.Gravity.CENTER)
                .create();

        //network data
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList = null;
    }


    private Callback<ImageResult> cb = new Callback<ImageResult>() {
        @Override
        public void success(ImageResult imageResult, Response response) {
            if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
            dataList = imageResult;
            adapter.notifyDataSetChanged();
        }

        @Override
        public void failure(RetrofitError error) {
            if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
            T.showShort(context, "获取数据错误， 请稍后再试");
        }
    };

    /**
     * 获取数据
     */
    private void getData(){

        waitDialog.show();

        HttpClient.getInstance().getImages(0, 20, cb);
//        http = new HttpUtils();
//        http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/img/index?pageIndex=1&pageSize=20", new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
//                if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
//                dataList = new Gson().fromJson(responseInfo.result, ImageResult.class);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
//                T.showShort(context, "获取数据错误， 请稍后再试");
//            }
//        });
    }

    @Override
    public void onClick(MTDialog dialog, View view) {
        switch (view.getId()){
            case R.id.footer_upload_cancel_button:
                dialog.dismiss();
                break;
            case R.id.footer_upload_confirm_button:
                footLayout.setVisibility(View.INVISIBLE);
                loadingIndicatorView.setVisibility(View.VISIBLE);


                doUpload();
                break;
        }
    }


    /**
     * 上传图片
     */
    private String picUrl = "";
    private void doUpload() {
        new Thread(){
            @Override
            public void run() {
                uploadFile(new File(picUrl), "http://116.255.235.119:1280/weatherForecastServer/img/save");
            }
        }.start();

//
//        RequestParams params = new RequestParams();
//        params.addBodyParameter("uploadImg", new File(picUrl));
//        http.send(HttpRequest.HttpMethod.POST, "http://116.255.235.119:1280/weatherForecastServer/img/save", new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
//
//                T.showShort(context, "赞成功");
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                T.showShort(context, "操作失败， 请稍后再试");
//            }
//
//        });
    }

    //-----------------------------------------------
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    /**
     * Android上传文件到服务端
     *
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型


        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);


            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */


                sb.append("Content-Disposition: form-data; name=\"uploadImg\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                // if(res==200)
                // {
                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();

                UploadResp uploadResp = new Gson().fromJson(result, UploadResp.class);

                if(uploadResp != null && uploadResp.getCode() == 200) {
                    handler.sendEmptyMessage(0);
                }else {
                    handler.sendEmptyMessage(1);
                }
                Log.e(TAG, "result : " + result);
            }
        } catch (MalformedURLException e) {
            handler.sendEmptyMessage(1);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        }
        return result;
    }

    //-----------------------------------------------

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if(dialog != null && dialog.isShowing()) dialog.dismiss();
                    footLayout.setVisibility(View.VISIBLE);
                    loadingIndicatorView.setVisibility(View.INVISIBLE);
                    File file = new File(saveUrl);
                    if(file.exists()) file.delete();
                    T.showShort(context, "上传成功");
                    getData();
                    break;
                case 1:
                    footLayout.setVisibility(View.VISIBLE);
                    T.showShort(context, "上传失败， 请稍后再试");
                    break;
            }
        }
    };


    //------------------------
    private int currentPosition = -1;
    private Callback<BaseEntity> goodCallback = new Callback<BaseEntity>() {
        @Override
        public void success(BaseEntity baseEntity, Response response) {
            if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
            if(baseEntity.getCode() == 200) {
                T.showShort(context, "赞成功");
                if(saveTempGoodImageComment != null) {
                    try {
                        db.save(saveTempGoodImageComment);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    saveTempGoodImageComment = null;
                }

                if(updateTempGoodImageComment != null){
                    try {
                        db.update(updateTempGoodImageComment, "date");
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    updateTempGoodImageComment = null;
                }

                if(currentPosition != -1){
                    dataList.getResult().get(currentPosition).setSupportNum(dataList.getResult().get(currentPosition).getSupportNum() + 1);
                    adapter.notifyDataSetChanged();
                    currentPosition = -1;
                }
            }
            isGooding = false;
        }

        @Override
        public void failure(RetrofitError error) {
            if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
            isGooding = false;
            T.showShort(context, "赞失败，请稍后再试");
        }
    };

    private GoodImageComment updateTempGoodImageComment;
    private GoodImageComment saveTempGoodImageComment;
    private void doGood(int position) {
        Image image = dataList.getResult().get(position);

        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR)+"-" + calendar.get(Calendar.MONTH)+"-" + calendar.get(Calendar.DAY_OF_MONTH);
        List<GoodImageComment> localData = null;
        try {
            localData = db.selector(GoodImageComment.class).where("tag", "=", "comment").and("itemId", "=", image.getId()).findAll();
            if (localData == null || localData.size() <= 0) {//save

                saveTempGoodImageComment = new GoodImageComment("comment", image.getId(), today);

                if(isGooding == false) {
                    waitDialog.show();
                    isGooding = true;
                    currentPosition = position;

                    HttpClient.getInstance().goodImage(image.getId(), goodCallback);

                }else {
                    T.showLong(context, "请稍后， 正在处理中...");
                }
            } else {
                GoodImageComment goodImageComment = localData.get(0);
                if(goodImageComment.getDate().equals(today)) {//今天赞过
                    T.showShort(context, "您已经赞过啦！");
                }else {
                    updateTempGoodImageComment = new GoodImageComment("comment", image.getId(), today);

                    if(isGooding == false) {
                        waitDialog.show();
                        isGooding = true;
                        currentPosition = position;

                        HttpClient.getInstance().goodImage(image.getId(), goodCallback);
                    }else {
                        T.showLong(context, "请稍后， 正在处理中...");
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
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

                    doGood(position);

//                    Calendar calendar = Calendar.getInstance();
//                    String today = calendar.get(Calendar.YEAR)+"-" + calendar.get(Calendar.MONTH)+"-" + calendar.get(Calendar.DAY_OF_MONTH);
//                    List<GoodImageComment> localData = null;
//                    try {
//                        localData = db.findAll(Selector.from(GoodImageComment.class).where("tag", "=", "image").and("itemId", "=", image.getId()));
//                        if (localData == null || localData.size() <= 0) {//save
//
//                            GoodImageComment goodImageComment = new GoodImageComment("image", image.getId(), today);
//                            db.save(goodImageComment);
//
//                            if (isGooding == false) {
//                                isGooding = true;
//                                //赞
//                                http = new HttpUtils();
//                                http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/img/support?imgId=" + image.getId(), new RequestCallBack<String>() {
//                                    @Override
//                                    public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
//
//                                        //赞成功， 重新获取数据
//                                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                                        dataList.getResult().get(position).setSupportNum(dataList.getResult().get(position).getSupportNum() + 1);
//                                        T.showShort(context, "赞成功");
//                                        adapter.notifyDataSetChanged();
//                                        isGooding = false;
//                                    }
//
//                                    @Override
//                                    public void onFailure(HttpException e, String s) {
//                                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                                        T.showShort(context, "操作失败， 请稍后再试");
//                                        isGooding = false;
//                                    }
//                                });
//                            } else {
//                                T.showShort(context, "请稍后， 正在处理中...");
//                            }
//                        } else {
//                            GoodImageComment goodImageComment = localData.get(0);
//                            if(goodImageComment.getDate().equals(today)) {//今天赞过
//                                T.showShort(context, "您已经赞过啦！");
//                            }else {
//                                GoodImageComment tempGood = new GoodImageComment("image", image.getId(), today);
//                                db.update(tempGood, "date");
//
//                                if (isGooding == false) {
//                                    isGooding = true;
//                                    //赞
//                                    http = new HttpUtils();
//                                    http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/img/support?imgId=" + image.getId(), new RequestCallBack<String>() {
//                                        @Override
//                                        public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
//
//                                            //赞成功， 重新获取数据
//                                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                                            dataList.getResult().get(position).setSupportNum(dataList.getResult().get(position).getSupportNum() + 1);
//                                            T.showShort(context, "赞成功");
//                                            adapter.notifyDataSetChanged();
//                                            isGooding = false;
//                                        }
//
//                                        @Override
//                                        public void onFailure(HttpException e, String s) {
//                                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                                            T.showShort(context, "操作失败， 请稍后再试");
//                                            isGooding = false;
//                                        }
//                                    });
//                                } else {
//                                    T.showShort(context, "请稍后， 正在处理中...");
//                                }
//                            }
//                        }
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
                }
            });

            if(!image.getUrl().contains("http://")) {
                Picasso.with(context).load("http://116.255.235.119:1280/WeatherForecastServerUploadImg/" + image.getUrl()).placeholder(R.mipmap.img_default).into(holder.contentImg);
            }else {
                Picasso.with(context).load(image.getUrl()).placeholder(R.mipmap.img_default).into(holder.contentImg);
            }
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
                int width =  SystemUtils.getDisplayWidth(this);

                picUrl = selectedPhotos.get(0);

                Bitmap bmp = BitmapUtil.getimage(picUrl);

                File file = new File(saveUrl);
                if(file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Picasso.with(context).load(new File(saveUrl)).placeholder(R.mipmap.img_default).error(R.mipmap.img_default).into(contentImg);
                picUrl = saveUrl;

//                Picasso.with(context).load(selectedPhotos.get(0)).placeholder(R.mipmap.img_default).into(contentImg);
                //
                dialog.show();
            }
//            System.out.println(selectedPhotos.toString());
        }

    }


    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
