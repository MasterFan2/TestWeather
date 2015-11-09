package com.way.yahoo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.way.ui.swipeback.SwipeBackActivity;
import com.way.widget.recyclerviewdiviver.HorizontalDividerItemDecoration;

public class CommentsActivity extends SwipeBackActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(getResources().getColor(R.color.gray_400)).build());
        recyclerView.setAdapter(new CommentsAdapter());

        initData();
    }

    private void initData() {

    }

    public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.SimpleViewHolder> {

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(CommentsActivity.this).inflate(R.layout.item_comments, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View view) {
                super(view);
            }
        }
    }

}
