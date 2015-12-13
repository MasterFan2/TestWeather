package com.way.yahoo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.way.common.util.T;
import com.way.net.HttpClient;
import com.way.net.bean.DatBean;
import com.way.net.bean.Resp;
import com.way.net.bean.TwitterInfo;
import com.way.net.bean.TwitterLikeStatus;
import com.way.net.bean.TwitterResp;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.utils.HardwareUtil;
import com.way.utils.SystemBarTintManager;
import com.way.widget.MaterialRippleLayout;
import com.way.widget.WaitDialog;
import com.way.widget.component.MasterListView;
import com.way.widget.component.MasterListViewHeader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import photopicker.widget.PinnedSectionListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【new】
 */
public class TwitterListActivity extends SwipeBackActivity implements View.OnClickListener, MasterListView.OnRefreshListener {

    private DbUtils db;

    private PinnedSectionListView listView;
    private MyAdapter adapter;

    private ImageView leftImg, rightImg;
    private View statusBar;

    private Context context;
    private ArrayList<DatBean> finalData = new ArrayList<>();
    private ArrayList<TwitterInfo> finalInfoList = new ArrayList<>();
    private ArrayList<TwitterInfo> tempList = new ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfMonthDay = new SimpleDateFormat("MM月dd日");
    private SimpleDateFormat sdfHourMinute = new SimpleDateFormat("HH:mm");

    private WaitDialog waitDialog;

    //-------------------------page----------------------
    private int pageSize = 30;
    private int pageIndex = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_twitter_list);
        context = this;

        db = DbUtils.create(context);
        db.configAllowTransaction(true);


        waitDialog = new WaitDialog.Builder(context).create();

        //init widget
        statusBar = findViewById(R.id.status_bar);
        setStatusBar();
        listView = (PinnedSectionListView) findViewById(R.id.listView);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        listView.setOnRefreshListener(this, 0);

        leftImg = (ImageView) findViewById(R.id.header_left);
        leftImg.setOnClickListener(this);
        rightImg = (ImageView) findViewById(R.id.header_right);
        rightImg.setOnClickListener(this);

        //加载网络数据
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        waitDialog.show();
        HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
    }

    public void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            statusBar.setVisibility(View.VISIBLE);
        } else {
            statusBar.setVisibility(View.GONE);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.red);//通知栏所需颜色
        tintManager.setTintColor(R.color.green);
        tintManager.setNavigationBarTintColor(R.color.yellow);
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

    //-----------------------------------------
    String tempDate = null;
    private Callback<TwitterResp> loadMoreCallback = new Callback<TwitterResp>() {
        @Override
        public void success(TwitterResp resp, Response response) {
            tempList.clear();
            if (resp != null) {

                for (TwitterInfo info : resp.getResult()) {
                    String t = info.getDateCreated().split(" ")[0];
                    if (TextUtils.isEmpty(tempDate) || !t.equals(tempDate)) {//第一次
                        tempList.add(new TwitterInfo(TwitterInfo.SECTION, info.getDateCreated()));
                        info.setType(TwitterInfo.ITEM);
                        tempList.add(info);
                        tempDate = t;
                    } else if (t.equals(tempDate)) {
                        info.setType(TwitterInfo.ITEM);
                        tempList.add(info);
                    }
                }

                if(resp.getResult() != null && resp.getResult().size() >= pageSize){
                    listView.setPullLoadEnable(true);
                }else{
                    listView.setPullLoadEnable(false);
                }

                try {
                    List<TwitterLikeStatus> likeStatusList = db.findAll(TwitterLikeStatus.class);
                    if (likeStatusList != null && likeStatusList.size() > 0) {
                        for (TwitterInfo info : tempList) {
                            List<TwitterLikeStatus> likeTempList = db.findAll(Selector.from(TwitterLikeStatus.class).where("twitterId", "=", info.getId()));
                            if (likeTempList != null && likeTempList.size() > 0)
                                info.setIsLike(true);
                            else info.setIsLike(false);
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                finalInfoList.addAll(tempList);
                adapter.notifyDataSetChanged();
            }else{
                listView.setPullLoadEnable(false);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("获取数据错误, 请稍后再试...");
        }
    };

    private Callback<TwitterResp> callback = new Callback<TwitterResp>() {
        @Override
        public void success(TwitterResp resp, Response response) {
            tempList.clear();
            if (waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
            if (resp != null) {
                for (TwitterInfo info : resp.getResult()) {
                    String t = info.getDateCreated().split(" ")[0];
                    if (TextUtils.isEmpty(tempDate) || !t.equals(tempDate)) {//第一次
                        tempList.add(new TwitterInfo(TwitterInfo.SECTION, info.getDateCreated()));
                        info.setType(TwitterInfo.ITEM);
                        tempList.add(info);
                        tempDate = t;
                    } else if (t.equals(tempDate)) {
                        info.setType(TwitterInfo.ITEM);
                        tempList.add(info);
                    }
                }
                if (resp.getResult() != null && resp.getResult().size() >= pageSize) {
                    listView.setPullLoadEnable(true);
                } else {
                    listView.setPullLoadEnable(false);
                }

                try {
                    List<TwitterLikeStatus> likeStatusList = db.findAll(TwitterLikeStatus.class);
                    if (likeStatusList != null && likeStatusList.size() > 0) {
                        for (TwitterInfo info : tempList) {
                            List<TwitterLikeStatus> likeTempList = db.findAll(Selector.from(TwitterLikeStatus.class).where("twitterId", "=", info.getId()));
                            if (likeTempList != null && likeTempList.size() > 0)
                                info.setIsLike(true);
                            else info.setIsLike(false);
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                finalInfoList = tempList;
                adapter.notifyDataSetChanged();
                listView.setPullRefreshEnable(true);
                listView.stopRefresh();
            }else{
                listView.setPullLoadEnable(false);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("获取数据错误, 请稍后再试...");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                Intent intent = new Intent(TwitterListActivity.this, AddTwitterActivity.class);
                startActivityForResult(intent, 502);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 502) {
            listView.startRefresh();
            pageIndex = 1;
            HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
        } else if (requestCode == 505) {
            pageIndex = 1;
            HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
        }
    }

    private Callback<Resp> twitterSupportCallback = new Callback<Resp>() {
        @Override
        public void success(Resp resp, Response response) {
            if (resp.getCode() == 200) {
                T.showShort(context, "赞成功!");
                if (currentPosition != -1) {
                    TwitterInfo info = finalInfoList.get(currentPosition);
                    info.setSupportNum(info.getSupportNum() + 1);
                    info.setIsLike(true);
                    try {
                        db.save(new TwitterLikeStatus(info.getId(), HardwareUtil.getDeviceUniqueCode(context)));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                T.showShort(context, resp.getMsg());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("赞失败!");
        }
    };

    private Callback<Resp> twitterCancelSupportCallback = new Callback<Resp>() {
        @Override
        public void success(Resp resp, Response response) {
            if (resp.getCode() == 200) {
                T.showShort(context, "取消成功!");
                if (currentPosition != -1) {
                    TwitterInfo info = finalInfoList.get(currentPosition);
                    info.setSupportNum(info.getSupportNum() - 1);
                    info.setIsLike(false);
                    try {
                        db.delete(TwitterLikeStatus.class, WhereBuilder.b("twitterId", "=", info.getId()));//save(new TwitterLikeStatus(info.getId(), HardwareUtil.getDeviceUniqueCode(context)));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                T.showShort(context, resp.getMsg());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("取消失败!");
        }
    };


    //--------------------------------------------------
    private int currentPosition = -1;

    @Override
    public void onRefresh(int id) {
        pageIndex = 1;
        listView.startRefresh();
        listView.setPullRefreshEnable(false);
        HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
    }

    @Override
    public void onLoadMore(int id) {
        pageIndex++;
        HttpClient.getInstance().twitterList(pageIndex, pageSize, loadMoreCallback);
    }

    class MyAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return finalInfoList.get(position).getType();
        }

        @Override
        public int getCount() {
            return finalInfoList == null ? 0 : finalInfoList.size();
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
            final TwitterInfo info = finalInfoList.get(position);


            if (getItemViewType(position) == DatBean.SECTION) {
                SectionViewHolder sectionViewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_section, null);
                    sectionViewHolder = new SectionViewHolder(convertView);
                    convertView.setTag(sectionViewHolder);
                } else {
                    sectionViewHolder = (SectionViewHolder) convertView.getTag();
                }
                try {
                    sectionViewHolder.nameTxt.setText(sdfMonthDay.format(sdf.parse(info.getDateCreated())));
                } catch (ParseException e) {
                    sectionViewHolder.nameTxt.setText(info.getDateCreated());
                    e.printStackTrace();
                }

            } else if (getItemViewType(position) == DatBean.ITEM) {

                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (info.isLike()) {
                    holder.goodImg.setImageResource(R.drawable.icon_like_highlighted);
                } else {
                    holder.goodImg.setImageResource(R.drawable.icon_like);
                }

                holder.mrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TwitterDetailActivity.class);
                        TwitterInfo info = finalInfoList.get(position);
                        intent.putExtra("info", info);
                        startActivityForResult(intent, 505);
                    }
                });

                holder.goodImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPosition = position;
                        if (info.isLike()) {
                            HttpClient.getInstance().twitterCancelSupport(info.getId(), twitterCancelSupportCallback);
                        } else {
                            HttpClient.getInstance().twitterSupport(info.getId(), twitterSupportCallback);
                        }
                    }
                });

                //set data
                holder.descTxt.setText(info.getContent());
                holder.goodNumTxt.setText("" + info.getSupportNum());
                holder.commentNumTxt.setText("" + info.getCommentNum());
                try {
                    holder.timeTxt.setText(sdfHourMinute.format(sdf.parse(info.getDateCreated())));
                } catch (ParseException e) {
                    holder.timeTxt.setText(info.getDateCreated());
                    e.printStackTrace();
                }
                Picasso.with(context).load(info.getImgs()).placeholder(R.mipmap.img_default).into(holder.contentImg);
            }
            return convertView;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == DatBean.SECTION;
        }

        class SectionViewHolder {
            TextView nameTxt;

            public SectionViewHolder(View view) {
                nameTxt = (TextView) view.findViewById(R.id.section_txt);
            }
        }

        class ViewHolder {
            ImageView contentImg;
            TextView descTxt;
            TextView timeTxt;
            ImageView goodImg;
            TextView goodNumTxt;
            TextView commentNumTxt;
            MaterialRippleLayout mrl;

            public ViewHolder(View view) {
                contentImg = (ImageView) view.findViewById(R.id.content_img);
                timeTxt = (TextView) view.findViewById(R.id.time_txt);
                descTxt = (TextView) view.findViewById(R.id.desc_txt);
                goodImg = (ImageView) view.findViewById(R.id.good_img);
                goodNumTxt = (TextView) view.findViewById(R.id.good_number_txt);
                commentNumTxt = (TextView) view.findViewById(R.id.comment_number_txt);
                mrl = (MaterialRippleLayout) view.findViewById(R.id.comment_mrl);
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
