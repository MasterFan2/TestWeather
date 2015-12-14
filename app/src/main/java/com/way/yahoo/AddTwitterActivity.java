package com.way.yahoo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.way.BitmapUtil;
import com.way.common.util.SystemUtils;
import com.way.common.util.T;
import com.way.net.HttpClient;
import com.way.net.bean.Resp;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.utils.Conf;
import com.way.utils.HardwareUtil;
import com.way.utils.SystemBarTintManager;
import com.way.widget.WaitDialog;
import com.way.widget.dialog.MTDialog;
import com.way.widget.dialog.OnClickListener;
import com.way.widget.dialog.ViewHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import photopicker.PhotoPickerActivity;
import photopicker.utils.PhotoPickerIntent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * 【new】
 */
public class AddTwitterActivity extends SwipeBackActivity implements View.OnClickListener, OnClickListener {

    private final int INPUT_TEXT_LENGHT = 140;//输入字数限制

    private ImageView addImg;
    private EditText inputEdit;
    private TextView contentLenTxt;
//    private View statusBar;

    private MTDialog dialog;
    private WaitDialog waitDialog;

    private Button sendBtn;
    public final static int REQUEST_CODE = 1;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    //上传图片
    private String picUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_twitter);

//        statusBar = findViewById(R.id.status_bar);
//        setStatusBar();

        waitDialog = new WaitDialog.Builder(context).create();

        //init dailog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_twitter, null);
        ViewHolder holder = new ViewHolder(view);

        dialog = new MTDialog.Builder(context)
                .setContentHolder(holder)
                .setFooter(R.layout.dialog_footer_twitter)
                .setCancelable(false)
                .setOnClickListener(this)
                .setGravity(MTDialog.Gravity.CENTER)
                .create();

        //
        addImg = (ImageView) findViewById(R.id.add_img);
        addImg.setOnClickListener(this);

        findViewById(R.id.header_left).setOnClickListener(this);
        contentLenTxt = (TextView) findViewById(R.id.content_number_txt);
        inputEdit = (EditText) findViewById(R.id.input_edit);
        inputEdit.addTextChangedListener(new TextWatcher() {

            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = inputEdit.getSelectionStart();
                editEnd = inputEdit.getSelectionEnd();
                contentLenTxt.setText(s.length() + "/" + INPUT_TEXT_LENGHT);
                contentLenTxt.setTextColor(getResources().getColor(R.color.gray_800));
                if (temp.length() > INPUT_TEXT_LENGHT) {
                    T.showShort(context, "你输入的字数已经超过了限制！");
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    inputEdit.setText(s);
                    contentLenTxt.setTextColor(getResources().getColor(R.color.red));
                    inputEdit.setSelection(tempSelection);
                }
            }
        });
        sendBtn = (Button) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
    }

    public void setStatusBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            statusBar.setVisibility(View.VISIBLE);
//        }else {
//            statusBar.setVisibility(View.GONE);
//        }
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintResource(R.color.title_blue);//通知栏所需颜色
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * processing choose iamge callback
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }

            if (selectedPhotos != null && selectedPhotos.size() > 0) {
                int width = SystemUtils.getDisplayWidth(this);

                picUrl = selectedPhotos.get(0);

                Bitmap bmp = BitmapUtil.getimage(picUrl);

                File file = new File(BitmapUtil.saveUrl);
                if (file.exists()) file.delete();
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

                Picasso.with(context).load(new File(BitmapUtil.saveUrl)).placeholder(R.mipmap.img_default).error(R.mipmap.img_default).into(addImg);
                picUrl = BitmapUtil.saveUrl;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!TextUtils.isEmpty(picUrl)){
            File file = new File(BitmapUtil.saveUrl);
            if(file.exists() || !TextUtils.isEmpty(inputEdit.getText().toString())) {
                dialog.show();
                return;
            }
        }
        finish();
    }

    /**
     * 发送数据
     */
    private void send() {
        String content = inputEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            T.showShort(context, "请输入内容");
            return;
        }

        if (TextUtils.isEmpty(picUrl)) {
            T.showShort(context, "请选择上传的图片");
            return;
        }

        //
        TypedFile typedFile = new TypedFile("image/jpg", new File(BitmapUtil.saveUrl));

        //
        waitDialog.show();
        HttpClient.getInstance().sendImageAndComments(typedFile, Conf.CITY_ID, content, HardwareUtil.getDeviceUniqueCode(context), callback);
    }

    private Callback<Resp> callback = new Callback<Resp>() {
        @Override
        public void success(Resp resp, Response response) {
            if (resp.getCode() == 200) {
                T.showLong(context, "发布成功");
                if(waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            } else {
                T.showLong(context, resp.getMsg());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("上传失败");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left:
                if(!TextUtils.isEmpty(picUrl)){
                    File file = new File(BitmapUtil.saveUrl);
                    if(file.exists() || !TextUtils.isEmpty(inputEdit.getText().toString())) {
                        dialog.show();
                        return;
                    }
                }
                finish();
                break;
            case R.id.send_btn:
                send();
                break;
            case R.id.add_img:
                PhotoPickerIntent intent = new PhotoPickerIntent(context);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onClick(MTDialog dialog, View view) {
        switch (view.getId()){
            case R.id.cancel_layout:
                dialog.dismiss();
                break;
            case R.id.confirm_layout:
                finish();
                break;
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
