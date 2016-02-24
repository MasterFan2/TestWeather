package com.way.yahoo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.way.utils.Conf;
import com.way.utils.Dbutils;
import com.way.utils.HardwareUtil;
import com.way.utils.NetworkUtil;
import com.way.utils.SystemBarTintManager;
import com.way.widget.WaitDialog;
import com.way.widget.component.MasterListView;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

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

    private final int NOTIFY_CHANGE = 0x01;//刷新显示
    private final int NOTIFY_PAGE = 0x02;//判断是否有下一页


    private PinnedSectionListView listView;
    private MyAdapter adapter;

    private ImageView leftImg, rightImg;
    private View statusBar;

    private Context context;//
    private List<TwitterInfo> displayList = new ArrayList<>();// main show data
    private List<TwitterInfo> sortList = new ArrayList<>();// only for sort
    private List<TwitterInfo> tempList = new ArrayList<>();//temp don't sort 存放第一次或每次加载更多的数据


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfMonthDay = new SimpleDateFormat("MM月dd日");
    private SimpleDateFormat sdfHourMinute = new SimpleDateFormat("HH:mm");

    private WaitDialog waitDialog;

    //-------------------------page----------------------
    private int pageSize = 30;
    private int pageIndex = 1;

    private Handler handler;

    private DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_twitter_list);
        context = this;

        db = x.getDb(Dbutils.getConfig());

        waitDialog = new WaitDialog.Builder(context).create();

        //init widget
        statusBar = findViewById(R.id.status_bar);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Conf.statusBar_height);
        statusBar.setLayoutParams(params);

        setStatusBar();

        listView = (PinnedSectionListView) findViewById(R.id.listView);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        listView.setOnRefreshListener(this, 0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(context, TwitterDetailActivity.class);
                TwitterInfo info = displayList.get(position - 1);
                intent.putExtra("info", info);
                startActivityForResult(intent, 505);
            }
        });

        leftImg = (ImageView) findViewById(R.id.header_left);
        leftImg.setOnClickListener(this);
        rightImg = (ImageView) findViewById(R.id.header_right);
        rightImg.setOnClickListener(this);

        //加载网络数据
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NOTIFY_CHANGE:
                        if (msg.arg1 >= pageSize) {
                            listView.setPullLoadEnable(true);
                        } else {
                            listView.setPullLoadEnable(false);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case NOTIFY_PAGE:

                        break;
                }
            }
        };


        if (NetworkUtil.hasConnection(context)) {
            waitDialog.show();
            HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
        } else {
            try {
                displayList = db.findAll(TwitterInfo.class);
                if (displayList != null && displayList.size() > 0) {
                    adapter.notifyDataSetChanged();
                } else {
                    NotConnection();
                    listView.setPullRefreshEnable(true);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
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

    //-----------------------------------------
    String tempDate = null;
    ///load more
    private Callback<TwitterResp> loadMoreCallback = new Callback<TwitterResp>() {
        @Override
        public void success(final TwitterResp resp, Response response) {
            if (resp != null && resp.getResult() != null && resp.getResult().size() > 0) {

                tempList.addAll(resp.getResult());
                sortList.clear();
                ///sort
                for (TwitterInfo info : tempList) {
                    String t = info.getDateCreated().split(" ")[0];

                    ///set is support
                    try {
                        List<TwitterLikeStatus> likeStatusList = db.findAll(TwitterLikeStatus.class);
                        if (likeStatusList != null && likeStatusList.size() > 0) {
                            List<TwitterLikeStatus> likeTempList = db.selector(TwitterLikeStatus.class).where("twitterId", "=", info.getId()).findAll();
                            if (likeTempList != null && likeTempList.size() > 0)
                                info.setIsLike(true);
                            else
                                info.setIsLike(false);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    /////////sort////////
                    if (TextUtils.isEmpty(tempDate) || !t.equals(tempDate)) {//time first

                        ///add section
                        sortList.add(new TwitterInfo(TwitterInfo.SECTION, info.getDateCreated()));
                        info.setType(TwitterInfo.ITEM);

                        ///add info
                        sortList.add(info);
                        tempDate = t;
                    } else if (t.equals(tempDate)) {
                        info.setType(TwitterInfo.ITEM);
                        sortList.add(info);//add info
                    }
                }

                tempDate = null;
                displayList = sortList;


                /// cache
                try {
                    List<TwitterInfo> localList = db.findAll(TwitterInfo.class);
                    if (localList != null && localList.size() > 0) {
                        db.dropTable(TwitterInfo.class);
                    }
                    for (TwitterInfo twitterInfo : displayList) {
                        db.save(twitterInfo);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                /// has next?
//                int size = resp.getResult() == null ? 0 : resp.getResult().size();
//                Message msg = handler.obtainMessage(NOTIFY_CHANGE, size, 0);
//                msg.sendToTarget();
                if (resp.getResult() != null && resp.getResult().size() >= pageSize) {
                    listView.setPullLoadEnable(true);
                } else {
                    listView.setPullLoadEnable(false);
                }
                adapter.notifyDataSetChanged();
            } else {
                listView.setPullLoadEnable(false);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            getDataError("获取数据错误, 请稍后再试...");
        }
    };


    ///first page or refresh
    private Callback<TwitterResp> callback = new Callback<TwitterResp>() {
        @Override
        public void success(TwitterResp resp, Response response) {
            tempList.clear();
            sortList.clear();
            if (waitDialog != null && waitDialog.isShowing()) waitDialog.dismiss();
            if (resp != null && resp.getResult() != null && resp.getResult().size() > 0) {

                ///sort
                tempList = resp.getResult();

                for (TwitterInfo info : tempList) {
                    String t = info.getDateCreated().split(" ")[0];

                    ///set is support
                    try {
                        List<TwitterLikeStatus> likeStatusList = db.findAll(TwitterLikeStatus.class);
                        if (likeStatusList != null && likeStatusList.size() > 0) {
                            List<TwitterLikeStatus> likeTempList = db.selector(TwitterLikeStatus.class).where("twitterId", "=", info.getId()).findAll();
                            if (likeTempList != null && likeTempList.size() > 0)
                                info.setIsLike(true);
                            else
                                info.setIsLike(false);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    /////////sort////////
                    if (TextUtils.isEmpty(tempDate) || !t.equals(tempDate)) {//time first

                        ///add section
                        sortList.add(new TwitterInfo(TwitterInfo.SECTION, info.getDateCreated()));
                        info.setType(TwitterInfo.ITEM);

                        ///add info
                        sortList.add(info);
                        tempDate = t;
                    } else if (t.equals(tempDate)) {
                        info.setType(TwitterInfo.ITEM);
                        sortList.add(info);//add info
                    }
                }

                ///has next page
                if (resp.getResult() != null && resp.getResult().size() >= pageSize) {
                    listView.setPullLoadEnable(true);
                } else {
                    listView.setPullLoadEnable(false);
                }

                tempDate = null;
                displayList = sortList;
                adapter.notifyDataSetChanged();
                listView.setPullRefreshEnable(true);
                listView.stopRefresh();

                //cache
                try {
                    List<TwitterInfo> localList = db.findAll(TwitterInfo.class);
                    if (localList != null && localList.size() > 0) {
                        db.dropTable(TwitterInfo.class);
                    }
                    for (TwitterInfo twitterInfo : displayList) {
                        db.save(twitterInfo);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

            } else {
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
            if (NetworkUtil.hasConnection(context)) {
                listView.startRefresh();
                pageIndex = 1;
                HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
            }
        } else if (requestCode == 505) {
            if (NetworkUtil.hasConnection(context)) {
                pageIndex = 1;
                HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
            }
        }
    }

    private Callback<Resp> twitterSupportCallback = new Callback<Resp>() {
        @Override
        public void success(Resp resp, Response response) {
            if (resp.getCode() == 200) {
                T.showShort(context, "赞成功!");
                if (currentPosition != -1) {
                    TwitterInfo info = displayList.get(currentPosition);
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
                    TwitterInfo info = displayList.get(currentPosition);
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
        if (NetworkUtil.hasConnection(context)) {
            pageIndex = 1;
            listView.startRefresh();
            listView.setPullRefreshEnable(false);
            HttpClient.getInstance().twitterList(pageIndex, pageSize, callback);
        } else {
            NotConnection();
        }

    }

    @Override
    public void onLoadMore(int id) {
        if (NetworkUtil.hasConnection(context)) {
            pageIndex++;
            HttpClient.getInstance().twitterList(pageIndex, pageSize, loadMoreCallback);
        } else {
            NotConnection();
        }

    }

    class MyAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return displayList.get(position).getType();
        }

        @Override
        public int getCount() {
            return displayList == null ? 0 : displayList.size();
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
            final TwitterInfo info = displayList.get(position);


            if (getItemViewType(position) == DatBean.SECTION) {//section
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

            } else if (getItemViewType(position) == DatBean.ITEM) {//item

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

                holder.supportLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPosition = position;
                        if (NetworkUtil.hasConnection(context)) {
                            if (info.isLike()) {
                                HttpClient.getInstance().twitterCancelSupport(info.getId(), twitterCancelSupportCallback);
                            } else {
                                HttpClient.getInstance().twitterSupport(info.getId(), twitterSupportCallback);
                            }
                        } else {
                            NotConnection();
                        }

                    }
                });

                //set support number
                holder.descTxt.setText(info.getContent());
                if (info.getSupportNum() <= 0) {
                    holder.goodNumTxt.setText("赞");
                } else {
                    holder.goodNumTxt.setText("" + info.getSupportNum());
                }

                if (info.getCommentNum() <= 0) {
                    holder.commentNumTxt.setText("评论");
                } else {
                    holder.commentNumTxt.setText("" + info.getCommentNum());
                }

                //set time
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
            LinearLayout supportLayout;
//            MaterialRippleLayout mrl;

            public ViewHolder(View view) {
                contentImg = (ImageView) view.findViewById(R.id.content_img);
                timeTxt = (TextView) view.findViewById(R.id.time_txt);
                descTxt = (TextView) view.findViewById(R.id.desc_txt);
                goodImg = (ImageView) view.findViewById(R.id.good_img);
                goodNumTxt = (TextView) view.findViewById(R.id.good_number_txt);
                commentNumTxt = (TextView) view.findViewById(R.id.comment_number_txt);
                supportLayout = (LinearLayout) view.findViewById(R.id.support_layout);
//                mrl = (MaterialRippleLayout) view.findViewById(R.id.comment_mrl);
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
