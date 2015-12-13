package com.way.widget.component;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtil {
	public static void put(Context context, String tag, Object ...objects){
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		for (int i = 0; i < objects.length; i++) {
			if(objects[i] instanceof Float){
				editor.putFloat(tag, Float.parseFloat(objects[i].toString()));
			}else if(objects[i] instanceof Integer){
				editor.putInt(tag, Integer.parseInt(objects[i].toString()));
			}else if(objects[i] instanceof Boolean){
				editor.putBoolean(tag, Boolean.parseBoolean(objects[i].toString()));
			}else if(objects[i] instanceof String){
				editor.putString(tag, objects[i].toString());
			}else if(objects[i] instanceof Long){
				editor.putLong(tag, Long.parseLong(objects[i].toString()));
			}
		}
		editor.commit(); //注意这句别忘了
	}
	
	public static String getString(Context context, String tag){
		//获取值
		return PreferenceManager.getDefaultSharedPreferences(context).getString(tag, null);
	}
	
	public static boolean getBoolean(Context context, String tag){
		//获取值
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(tag, false);
	}
}
