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
import com.way.beans.Comments;
import com.way.beans.CommentsResult;
import com.way.beans.GoodImageComment;
import com.way.beans.ImageResult;
import com.way.common.util.T;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.widget.WaitDialog;
import com.way.widget.recyclerviewdiviver.HorizontalDividerItemDecoration;

import java.util.Calendar;
import java.util.List;

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
                RequestParams params = new RequestParams();
                params.addBodyParameter("content", content);
                http = new HttpUtils();
                http.send(HttpRequest.HttpMethod.POST, "http://116.255.235.119:1280/weatherForecastServer/comment/save", params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()

                        if(dialog != null && dialog.isShowing()) dialog.dismiss();
                        editText.setText("");
                        initData();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        if(dialog != null && dialog.isShowing()) dialog.dismiss();
                        T.showShort(context, "发送失败， 请稍后重试...");
                    }
                });

            }
        });
        listview = (ListView) findViewById(R.id.listView);
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(getResources().getColor(R.color.gray_400)).build());
//        recyclerView.setAdapter(new CommentsAdapter());
        adapter = new CommentsAdapter();
        listview.setAdapter(adapter);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data = null;
    }

    private void initData() {
        //network data
        dialog.show();
        http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, "http://116.255.235.119:1280/weatherForecastServer/comment/index?pageIndex=1&pageSize=20", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
                data = new Gson().fromJson(responseInfo.result, CommentsResult.class);
                if(dialog != null && dialog.isShowing()) dialog.dismiss();
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if(dialog != null && dialog.isShowing()) dialog.dismiss();
                T.showShort(context, "获取数据错误， 请稍后再试");
            }
        });
    }

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

                    Calendar calendar = Calendar.getInstance();
                    String today = calendar.get(Calendar.YEAR)+"-" + calendar.get(Calendar.MONTH)+"-" + calendar.get(Calendar.DAY_OF_MONTH);
                    List<GoodImageComment> localData = null;
                    try {
                        localData = db.findAll(Selector.from(GoodImageComment.class).where("tag", "=", "comment").and("itemId", "=", comments.getId()));
                        if (localData == null || localData.size() <= 0) {//save

                            GoodImageComment goodImageComment = new GoodImageComment("comment", comments.getId(), today);
                            db.save(goodImageComment);

                            if(isGooding == false) {
                                dialog.show();
                                isGooding = true;
                                String url = "http://116.255.235.119:1280/weatherForecastServer/comment/support?commentId=" + comments.getId();
                                //赞
                                http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
                                        if(dialog != null && dialog.isShowing()) dialog.dismiss();
                                        data.getResult().get(position).setSupportNum(data.getResult().get(position).getSupportNum() + 1);
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
                                T.showLong(context, "请稍后， 正在处理中...");
                            }
                        } else {
                            GoodImageComment goodImageComment = localData.get(0);
                            if(goodImageComment.getDate().equals(today)) {//今天赞过
                                T.showShort(context, "您已经赞过啦！");
                            }else {
                                GoodImageComment tempGood = new GoodImageComment("comment", comments.getId(), today);
                                db.update(tempGood, "date");

                                if(isGooding == false) {
                                    dialog.show();
                                    isGooding = true;
                                    String url = "http://116.255.235.119:1280/weatherForecastServer/comment/support?commentId=" + comments.getId();
                                    //赞
                                    http = new HttpUtils();
                                    http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(ResponseInfo<String> responseInfo) {//new TypeToken<List<Image>>() {}.getType()
                                            if(dialog != null && dialog.isShowing()) dialog.dismiss();
                                            data.getResult().get(position).setSupportNum(data.getResult().get(position).getSupportNum() + 1);
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
                                    T.showLong(context, "请稍后， 正在处理中...");
                                }
                            }
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
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

}
