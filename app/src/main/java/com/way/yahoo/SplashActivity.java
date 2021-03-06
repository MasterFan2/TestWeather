package com.way.yahoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends Activity {
	private static final int ACTIVITY_TIMEOUT_GOTO_NEXT = 0;
	private static final int ACTIVITY_TIME = 2500;
	private Window mWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//不显示系统的标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN );
//
//		mWindow = getWindow();
//		WindowManager.LayoutParams params = mWindow.getAttributes();
//		params.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//		mWindow.setAttributes(params);

		setContentView(R.layout.splash_activity_layout);
		mHandler.sendEmptyMessageDelayed(ACTIVITY_TIMEOUT_GOTO_NEXT, ACTIVITY_TIME);


	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(ACTIVITY_TIMEOUT_GOTO_NEXT);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ACTIVITY_TIMEOUT_GOTO_NEXT:
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};
}
