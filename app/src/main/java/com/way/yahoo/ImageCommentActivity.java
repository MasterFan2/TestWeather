package com.way.yahoo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.net.bean.DatBean;
import com.way.yahoo.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import photopicker.widget.PinnedSectionListView;

public class ImageCommentActivity extends Activity {

    private PinnedSectionListView listView;

    private Context context;

    private ArrayList<DatBean> finalData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comment);

        context = this;

        listView = (PinnedSectionListView) findViewById(R.id.listView);

        finalData.add(new DatBean("2015-8-7", "", "", "", DatBean.SECTION));
        finalData.add(new DatBean("2015-8-7", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-8-7", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-8-7", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));

        finalData.add(new DatBean("2015-8-20", "", "", "", DatBean.SECTION));
        finalData.add(new DatBean("2015-8-20", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-8-20", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));

        finalData.add(new DatBean("2015-12-5", "", "", "", DatBean.SECTION));
        finalData.add(new DatBean("2015-12-5", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-5", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-5", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));

        finalData.add(new DatBean("2015-12-9", "", "", "", DatBean.SECTION));
        finalData.add(new DatBean("2015-12-9", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-9", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-9", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-9", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-9", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));
        finalData.add(new DatBean("2015-12-9", "张三", "20 世纪50年代，张大千游历世界，获得巨大的国际声誉，被西方艺坛赞为“东方之笔”。", "四川省", DatBean.ITEM));

        listView.setAdapter(new MyAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(context, CommentDetailActivity.class));
            }
        });
    }

    class MyAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return finalData.get(position).getType();
        }

        @Override
        public int getCount() {
            return finalData == null ? 0 : finalData.size();
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
            DatBean datBean = finalData.get(position);


            if(getItemViewType(position) == DatBean.SECTION){
                SectionViewHolder sectionViewHolder;
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_section, null);
                    sectionViewHolder = new SectionViewHolder(convertView);
                    convertView.setTag(sectionViewHolder);
                }else {
                    sectionViewHolder = (SectionViewHolder) convertView.getTag();
                }

                sectionViewHolder.nameTxt.setText(datBean.getDate());

            }else if(getItemViewType(position) == DatBean.ITEM){
                ViewHolder holder;
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //set data
                holder.descTxt.setText(datBean.getDesc());
            }
            return convertView;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == DatBean.SECTION;
        }

        class SectionViewHolder {
            TextView nameTxt;

            public SectionViewHolder(View view){
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

            public ViewHolder(View view){
                contentImg= (ImageView) view.findViewById(R.id.content_img);
                timeTxt = (TextView) view.findViewById(R.id.time_txt);
                descTxt = (TextView) view.findViewById(R.id.desc_txt);
                goodImg = (ImageView) view.findViewById(R.id.good_img);
                goodNumTxt = (TextView) view.findViewById(R.id.good_number_txt);
                commentNumTxt= (TextView) view.findViewById(R.id.comment_number_txt);
            }
        }
    }
}
