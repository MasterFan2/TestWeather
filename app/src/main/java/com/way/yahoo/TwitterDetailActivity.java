package com.way.yahoo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.way.common.util.T;
import com.way.net.HttpClient;
import com.way.net.bean.CommentLikeStatus;
import com.way.net.bean.Comments;
import com.way.net.bean.Resp;
import com.way.net.bean.TwitterDetailResp;
import com.way.net.bean.TwitterInfo;
import com.way.net.bean.TwitterLikeStatus;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.utils.HardwareUtil;
import com.way.utils.SystemBarTintManager;
import com.way.widget.MaterialRippleLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【new】
 */
public class TwitterDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    private DbUtils db;

    private ListView listView;
    private EditText contentEdit;
    private View statusBar;
    private MaterialRippleLayout sendBtn;

    private View headerView;
    private ViewHolder headHolder;

    private Context context;

    private MyAdapter adapter;

    private TwitterInfo info;
    private ArrayList<Comments> commentsList = new ArrayList<>();

    private boolean currentIsModify = false;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfHourMinute = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_detail);
        context = this;

        setSwipeBackEnable(false);

        db = DbUtils.create(context);
        db.configAllowTransaction(true);

        statusBar = findViewById(R.id.status_bar);
        setStatusBar();

        findViewById(R.id.header_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIsModify){
                    Intent intent = new Intent();
                    intent.putExtra("modify", "true");
                    setResult(13, intent);
                    currentIsModify = false;
                }
                finish();
            }
        });

        info = (TwitterInfo) getIntent().getSerializableExtra("info");

        if (info != null && info.getComments() != null && info.getComments().size() > 0) {
            commentsList = info.getComments();
            for (Comments comments: commentsList){
                try {
                    List<CommentLikeStatus> likeTempList = db.findAll(Selector.from(CommentLikeStatus.class).where("twitterId", "=", comments.getId()));
                    if(likeTempList != null && likeTempList.size() > 0){
                        comments.setIsLike(true);
                    }else {
                        comments.setIsLike(false);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }

        headerView = LayoutInflater.from(this).inflate(R.layout.item_comment, null);

        contentEdit = (EditText) findViewById(R.id.input_edit);
        sendBtn = (MaterialRippleLayout) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);



        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.addHeaderView(headerView);

        bindData();
    }

    public void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            statusBar.setVisibility(View.VISIBLE);
        }else {
            statusBar.setVisibility(View.GONE);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.title_blue);//通知栏所需颜色
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
     * 绑定数据
     */
    private void bindData() {
        if (info != null) {
            headHolder = new ViewHolder(headerView);
            headHolder.descTxt.setText(info.getContent());
            headHolder.commentNumTxt.setText(info.getCommentNum() + "");
            headHolder.goodNumTxt.setText(info.getSupportNum() + "");
            try {
                headHolder.timeTxt.setText(sdfHourMinute.format(sdf.parse(info.getDateCreated())));
            } catch (ParseException e) {
                headHolder.timeTxt.setText(info.getDateCreated());
                e.printStackTrace();
            }

            if (info.isLike())
                headHolder.goodImg.setImageResource(R.drawable.icon_like_highlighted);
            else headHolder.goodImg.setImageResource(R.drawable.icon_like);

            Picasso.with(context).load(info.getImgs()).into(headHolder.contentImg);
        }
    }

    @Override
    public void onClick(View v) {
        String content = contentEdit.getText().toString();

        if (TextUtils.isEmpty(content)) {
            T.showShort(context, "请输入评论内容！");
            return;
        }

        //对微博评论
        HttpClient.getInstance().commentTwitter(info.getId(), content, HardwareUtil.getDeviceUniqueCode(context), callback);
    }

    private Callback<Resp> callback = new Callback<Resp>() {
        @Override
        public void success(Resp resp, Response response) {

            if (resp.getCode() == 200) {
                T.showShort(context, "评论成功");
                HttpClient.getInstance().twitterDetail(info.getId(), twitterDetailRespCallback);
                currentIsModify = true;
                contentEdit.setText("");
            } else {
                T.showLong(context, resp.getMsg());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("评论失败");
        }
    };

    class ViewHolder {
        ImageView contentImg;
        TextView descTxt;
        TextView timeTxt;
        ImageView goodImg;
        TextView goodNumTxt;
        TextView commentNumTxt;

        public ViewHolder(View view) {
            contentImg = (ImageView) view.findViewById(R.id.content_img);
            timeTxt = (TextView) view.findViewById(R.id.time_txt);
            descTxt = (TextView) view.findViewById(R.id.desc_txt);
            goodImg = (ImageView) view.findViewById(R.id.good_img);
            goodNumTxt = (TextView) view.findViewById(R.id.good_number_txt);
            commentNumTxt = (TextView) view.findViewById(R.id.comment_number_txt);
        }
    }

    private Callback<Resp> twitterCommentSupportCallback = new Callback<Resp>() {
        @Override
        public void success(Resp resp, Response response) {
            if(resp.getCode() == 200){
                Comments comments =  commentsList.get(currentPosition);
                comments.setSupportNum(comments.getSupportNum() + 1);
                comments.setIsLike(true);
                try {
                    db.save(new CommentLikeStatus(comments.getId()));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                currentIsModify = true;

                HttpClient.getInstance().twitterDetail(info.getId(), twitterDetailRespCallback);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            System.out.println("ERROR");
        }
    };

    private Callback<TwitterDetailResp> twitterDetailRespCallback = new Callback<TwitterDetailResp>() {
        @Override
        public void success(TwitterDetailResp twitterDetailResp, Response response) {
            if(twitterDetailResp != null){
                info = twitterDetailResp.getResult();
                if (info != null && info.getComments() != null && info.getComments().size() > 0) {
                    commentsList = info.getComments();
                    for (Comments comments: commentsList){
                        try {
                            List<CommentLikeStatus> likeTempList = db.findAll(Selector.from(CommentLikeStatus.class).where("twitterId", "=", comments.getId()));
                            if(likeTempList != null && likeTempList.size() > 0){
                                comments.setIsLike(true);
                            }else {
                                comments.setIsLike(false);
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
                headHolder.commentNumTxt.setText(info.getCommentNum() + "");
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(currentIsModify){
            Intent intent = new Intent();
            intent.putExtra("modify", "true");
            setResult(13, intent);
            currentIsModify = false;
        }
        finish();
    }

    private int currentPosition = -1;
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commentsList == null ? 0 : commentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Comments comments = commentsList.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_subcomment, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.contentTxt.setText(comments.getContent());
            holder.goodNumTxt.setText(comments.getSupportNum() + "");

            if(comments.isLike())  holder.goodImg.setImageResource(R.drawable.icon_like_highlighted);
            else                   holder.goodImg.setImageResource(R.drawable.icon_like);

            holder.goodImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!comments.isLike()) {
                        currentPosition = position;
                        HttpClient.getInstance().twitterCommentSupport(comments.getId(), twitterCommentSupportCallback);
                    }else{
                        T.showLong(context, "您已经赞过啦.");
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView goodImg;
            TextView goodNumTxt;
            TextView contentTxt;

            public ViewHolder(View view) {
                goodImg = (ImageView) view.findViewById(R.id.subcomment_good_img);
                goodNumTxt = (TextView) view.findViewById(R.id.subcomment_good_num_txt);
                contentTxt = (TextView) view.findViewById(R.id.subcomment_content_txt);
            }
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
