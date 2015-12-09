package com.way.yahoo;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.way.yahoo.R;

public class CommentDetailActivity extends Activity {

    private ListView listView;
    private View headerView;
    private Context context;

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        context = this;

        headerView = LayoutInflater.from(this).inflate(R.layout.item_comment, null);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.addHeaderView(headerView);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_subcomment, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView goodImg;
            TextView  goodNumTxt;
            TextView  contentTxt;
            public ViewHolder(View view){
                goodImg = (ImageView) view.findViewById(R.id.subcomment_good_img);
                goodNumTxt= (TextView) view.findViewById(R.id.subcomment_good_num_txt);
                contentTxt= (TextView) view.findViewById(R.id.subcomment_content_txt);
            }
        }
    }
}
