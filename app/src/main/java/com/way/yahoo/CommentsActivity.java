package com.way.yahoo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.umeng.analytics.MobclickAgent;
import com.way.beans.BaseEntity;
import com.way.beans.Comments;
import com.way.beans.CommentsResult;
import com.way.beans.GoodImageComment;
import com.way.common.util.T;
import com.way.net.HttpClient;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.widget.WaitDialog;

import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentsActivity extends SwipeBackActivity {

//    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private HttpUtils http;
    private ListView listview;
    private CommentsResult data ;
    private EditText editText;
    private TextView sendTxt;

    private WaitDialog dialog;

    private DbUtils db;

    private boolean isGooding = false;//判断是否在处理赞       true:正在处理        false:空闲

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        db = DbUtils.create(this);

        dialog = new WaitDialog.Builder(context).create();

        findViewById(R.id.image_header_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        editText = (EditText) findViewById(R.id.comments_editText);
        sendTxt  = (TextView) findViewById(R.id.comments_send_txt);
        sendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if(TextUtils.isEmpty(content)) {
                    T.showShort(context, "请输入内容");
                    return;
                }
                dialog.show();
                HttpClient.getInstance().sendComments(content, commentCallback);

            }
        });
        listview = (ListView) findViewById(R.id.listView);
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(getResources().getColor(R.color.gray_400)).build());
//        recyclerView.setAdapter(new CommentsAdapter());
        adapter = new CommentsAdapter();
        listview.setAdapter(adapter);

        HttpClient.getInstance().getComments(1, 30, cb);
    }

    private Callback<BaseEntity> commentCallback = new Callback<BaseEntity>() {
        @Override
        public void success(BaseEntity baseEntity, Response response) {
            if(dialog != null && dialog.isShowing()) dialog.dismiss();
            if(baseEntity.getCode() == 200) {
                editText.setText("");
                HttpClient.getInstance().getComments(1, 30, cb);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            if(dialog != null && dialog.isShowing()) dialog.dismiss();
            T.showShort(context, "评论失败");
        }
    };



    private Callback<CommentsResult> cb = new Callback<CommentsResult>() {
        @Override
        public void success(CommentsResult commentsResult, Response response) {
            if(commentsResult != null) {
                data = commentsResult;
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data = null;
    }



//    private void getData() {
//        //network data
//        dialog.show();
//        http = new HttpUtils();
//        http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/comment/index?pageIndex=1&pageSize=20", new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
//                if(dialog != null && dialog.isShowing()) dialog.dismiss();
//                data = new Gson().fromJson(responseInfo.result, CommentsResult.class);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                if(dialog != null && dialog.isShowing()) dialog.dismiss();
//                T.showShort(context, "获取数据错误， 请稍后再试");
//            }
//        });
//    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    //------------------------
    private int currentPosition = -1;
    private Callback<BaseEntity> goodCallback = new Callback<BaseEntity>() {
        @Override
        public void success(BaseEntity baseEntity, Response response) {
            if(dialog != null && dialog.isShowing()) dialog.dismiss();
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
                    data.getResult().get(currentPosition).setSupportNum(data.getResult().get(currentPosition).getSupportNum() + 1);
                    adapter.notifyDataSetChanged();
                    currentPosition = -1;
                }
            }
            isGooding = false;
        }

        @Override
        public void failure(RetrofitError error) {
            if(dialog != null && dialog.isShowing()) dialog.dismiss();
            isGooding = false;
            T.showShort(context, "赞失败，请稍后再试");
        }
    };

    private GoodImageComment updateTempGoodImageComment;
    private GoodImageComment saveTempGoodImageComment;
    private void doGood(int position) {
        Comments comments = data.getResult().get(position);

        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR)+"-" + calendar.get(Calendar.MONTH)+"-" + calendar.get(Calendar.DAY_OF_MONTH);
        List<GoodImageComment> localData = null;
        try {
            localData = db.findAll(Selector.from(GoodImageComment.class).where("tag", "=", "comment").and("itemId", "=", comments.getId()));
            if (localData == null || localData.size() <= 0) {//save

                saveTempGoodImageComment = new GoodImageComment("comment", comments.getId(), today);

                if(isGooding == false) {
                    dialog.show();
                    isGooding = true;
                    currentPosition = position;

                    HttpClient.getInstance().goodComments(comments.getId(), goodCallback);

                }else {
                    T.showLong(context, "请稍后， 正在处理中...");
                }
            } else {
                GoodImageComment goodImageComment = localData.get(0);
                if(goodImageComment.getDate().equals(today)) {//今天赞过
                    T.showShort(context, "您已经赞过啦！");
                }else {
                    updateTempGoodImageComment = new GoodImageComment("comment", comments.getId(), today);

                    if(isGooding == false) {
                        dialog.show();
                        isGooding = true;
                        currentPosition = position;

                        HttpClient.getInstance().goodComments(comments.getId(), goodCallback);
                    }else {
                        T.showLong(context, "请稍后， 正在处理中...");
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    class CommentsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data == null || data.getResult() == null ? 0 : data.getResult().size();
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
            SimpleViewHolder holder = null;
            if(holder == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false);
                holder = new SimpleViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (SimpleViewHolder) convertView.getTag();
            }

            //
            final Comments comments = data.getResult().get(position);
            holder.goodsNumTxt.setText(comments.getSupportNum() + "");
            holder.contentTxt.setText(comments.getContent());
            holder.goodsImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   doGood(position);
                }
            });

            return convertView;
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder {
            TextView goodsNumTxt;
            TextView contentTxt;
            ImageView goodsImg;

            public SimpleViewHolder(View view) {
                super(view);
                goodsNumTxt = (TextView) view.findViewById(R.id.item_comments_goodsNum_txt);
                contentTxt  = (TextView) view.findViewById(R.id.item_comments_content_txt);
                goodsImg    = (ImageView)  view.findViewById(R.id.item_comments_goods_img);
            }
        }
    }

//    public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.SimpleViewHolder> {
//
//        @Override
//        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            final View view = LayoutInflater.from(CommentsActivity.this).inflate(R.layout.item_comments, parent, false);
//            return new SimpleViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(SimpleViewHolder holder, int position) {
//            Comments comments = data.getResult().get(position);
//            holder.goodsNumTxt.setText(comments.getSupportNum() + "");
//            holder.contentTxt.setText(comments.getContent());
//            holder.goodsImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.show(context, "赞成功", Toast.LENGTH_SHORT);
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return data == null || data.getResult() == null ? 0 : data.getResult().size();
//        }
//
//        public class SimpleViewHolder extends RecyclerView.ViewHolder {
//            TextView goodsNumTxt;
//            TextView contentTxt;
//            ImageView goodsImg;
//
//            public SimpleViewHolder(View view) {
//                super(view);
//                goodsNumTxt = (TextView) view.findViewById(R.id.item_comments_goodsNum_txt);
//                contentTxt  = (TextView) view.findViewById(R.id.item_comments_content_txt);
//                goodsImg    = (ImageView)  view.findViewById(R.id.item_comments_goods_img);
//            }
//        }
//    }


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
