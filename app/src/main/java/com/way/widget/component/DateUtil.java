package com.way.widget.component;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String currentTime() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String t = sdf.format(date);
		return t;
	}
	
	/**
	 * 获取两个时间之间的时间差
	 * @param start  yyyy-MM-dd HH:mm:ss
	 * @param end  yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getIntervalOfTwoTime(String start,String end){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long time = null;
		try {
			Date start_time = sdf.parse(start);
			Date end_time = sdf.parse(end);
			time = (end_time.getTime() - start_time.getTime());
			
			time = time/60000;//60000=1min
			if(time <= 0){
				return "刚刚";
			}else if(time > 0 && time < 60){
				return String.valueOf(time) + "分钟前";
			}else{
				String msg = "";
				if(time/(60*24) < 1440 && start.contains(getToday())){
					msg = "今天  " + start.substring(10, 16);
				}else{
					msg = start.substring(0, 10);
				}
				return msg;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    /**
     *  推送消息 列表 时间 显示
     * @param time 时间格式：2014-06-10 10:05:27
     * @return 如果是 当天时间为 今天，显示为：12:34， <br/>如果是昨天 显示为：昨天 12:34
     *  <br/>如果是同一年 显示 12-12 12:34 <br/>如果不是同一年 显示 2014-06-10 10:05
     */
    public static String getChangeTime(String time){

        if(TextUtils.isEmpty(time)){
            return "";
        }

        Date date = null;
        try
        {
            SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            date = format.parse(time);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return "";
        }

        long timeStamp = date.getTime();

        Date currentTime = new Date();
        long currentTimeStamp = currentTime.getTime();
        long seconds = (currentTimeStamp - timeStamp)/1000;

        long minutes = Math.abs(seconds/60);
        long hours = Math.abs(minutes/60);
        long days = Math.abs(hours/24);

        if ( seconds <= 15 )
        {
            return "刚刚";
        }
        else if ( seconds < 60 )
        {
            return seconds+"秒前";
        }
        else if ( seconds < 120 )
        {
            return"1分钟前";
        }
        else if ( minutes < 60 )
        {
            return minutes+"分钟前";
        }
        else if ( minutes < 120 )
        {
            return "1小时前";
        }
        else if ( hours < 24 )
        {
            return hours +"小时前";
        }
        else if ( hours < 24 * 2 )
        {
            return "1天前";
        }
        else if ( days < 30 )
        {
            return days+"天前" ;
        }

        time = time.substring(0,16);

        String timestr = time.substring(0, 4) + time.substring(5, 7)
                + time.substring(8, 10);
        String strdata = new SimpleDateFormat("yyyyMMdd").format(new Date());

        int timeint = Integer.parseInt(timestr);
        int newint = Integer.parseInt(strdata);

        String strdata2 = new SimpleDateFormat("yyyy").format(new Date());
        int timeint2 = Integer.parseInt(time.substring(0, 4));
        int newint2= Integer.parseInt(strdata2);
        if(timeint2 == newint2){// 判断日期是同一年
            return time.substring(5, 16);
        }else{
            return time.substring(0, 16);
        }

    }
	
	/**
	 * 获取今天时间 格式YY-MM-DD  eg:2010-04-19
	 * @return
	 */
	public static String getToday(){
		String str = "";
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		str = dateformat.format(new Date());
		return str;
	}
}
