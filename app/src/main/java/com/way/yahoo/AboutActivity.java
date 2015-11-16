package com.way.yahoo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.way.ui.swipeback.SwipeBackActivity;

public class AboutActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
//		TextView tv = (TextView) findViewById(R.id.app_information);
//		Linkify.addLinks(tv, Linkify.ALL);
		((TextView) findViewById(R.id.city_title)).setText("关 于");
		findViewById(R.id.back_image).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
//		TextView version = (TextView) findViewById(R.id.subVersion);
//		try {
//			final PackageInfo packageInfo = getPackageManager().getPackageInfo(
//					getPackageName(), 0);
//			version.setText(packageInfo.versionName);
//		} catch (final NameNotFoundException e) {
//			version.setText("?");
//		}
//		final UpgradeManager upgradeMangeer = UpgradeManager.newInstance(this);
//		upgradeMangeer.askForNewVersionFlag(new CheckNewVersionListener() {
//
//			@Override
//			public void checkNewVersion(boolean result) {
//				if (result)
//					upgradeMangeer.askForNewVersion();
//			}
//		});
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
