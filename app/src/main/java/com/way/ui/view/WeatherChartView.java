package com.way.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.way.common.util.SystemUtils;
import com.way.common.util.TimeUtils;
import com.way.common.util.WeatherIconUtils;
import com.way.weather.plugin.bean.WeatherInfo;
import com.way.yahoo.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class WeatherChartView extends WeatherBaseView {

    LineChartView lineChartView;
    LinearLayout container;
    Handler mHandler;
    Context context;

    //////////////////////////
    ImageView icon1;
    ImageView icon2;
    ImageView icon3;
    ImageView icon4;
    ImageView icon5;
    ImageView icon6;

    ImageView footicon1;
    ImageView footicon2;
    ImageView footicon3;
    ImageView footicon4;
    ImageView footicon5;
    ImageView footicon6;


    //白天天气
    TextView w_txt_1, w_txt_2, w_txt_3, w_txt_4, w_txt_5, w_txt_6;

    //星期几
    TextView week_txt3, week_txt4, week_txt5, week_txt6;

    //晚上天气
    TextView foot_txt_1;
    TextView foot_txt_2;
    TextView foot_txt_3;
    TextView foot_txt_4;
    TextView foot_txt_5;
    TextView foot_txt_6;

    public WeatherChartView(Context c) {
        this(c, null);
    }

    public WeatherChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherChartView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        container = (LinearLayout) findViewById(R.id.container);

        icon1 = (ImageView) findViewById(R.id.c_icon_1);
        icon2 = (ImageView) findViewById(R.id.c_icon_2);
        icon3 = (ImageView) findViewById(R.id.c_icon_3);
        icon4 = (ImageView) findViewById(R.id.c_icon_4);
        icon5 = (ImageView) findViewById(R.id.c_icon_5);
        icon6 = (ImageView) findViewById(R.id.c_icon_6);

        footicon1 = (ImageView) findViewById(R.id.foot_icon_1);
        footicon2 = (ImageView) findViewById(R.id.foot_icon_2);
        footicon3 = (ImageView) findViewById(R.id.foot_icon_3);
        footicon4 = (ImageView) findViewById(R.id.foot_icon_4);
        footicon5 = (ImageView) findViewById(R.id.foot_icon_5);
        footicon6 = (ImageView) findViewById(R.id.foot_icon_6);

        w_txt_1 = (TextView) findViewById(R.id.w_txt_1);
        w_txt_2 = (TextView) findViewById(R.id.w_txt_2);
        w_txt_3 = (TextView) findViewById(R.id.w_txt_3);
        w_txt_4 = (TextView) findViewById(R.id.w_txt_4);
        w_txt_5 = (TextView) findViewById(R.id.w_txt_5);
        w_txt_6 = (TextView) findViewById(R.id.w_txt_6);

        foot_txt_1 = (TextView) findViewById(R.id.foot_txt_1);
        foot_txt_2 = (TextView) findViewById(R.id.foot_txt_2);
        foot_txt_3 = (TextView) findViewById(R.id.foot_txt_3);
        foot_txt_4 = (TextView) findViewById(R.id.foot_txt_4);
        foot_txt_5 = (TextView) findViewById(R.id.foot_txt_5);
        foot_txt_6 = (TextView) findViewById(R.id.foot_txt_6);


        week_txt3 = (TextView) findViewById(R.id.week_txt3);
        week_txt4 = (TextView) findViewById(R.id.week_txt4);
        week_txt5 = (TextView) findViewById(R.id.week_txt5);
        week_txt6 = (TextView) findViewById(R.id.week_txt6);

        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                lineChartView = new LineChartView(context);
                int displayHeight = SystemUtils.getDisplayHeight(context);
                int mHeight = displayHeight - getResources().getDimensionPixelSize(R.dimen.abs__action_bar_default_height);
                int displayWidth = SystemUtils.getDisplayWidth(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(displayWidth, mHeight / 4);

                container.addView(lineChartView, params);
            }
        };
        mHandler.sendEmptyMessage(0);
    }

//	public void setWeatherInfo(Forecast weatherInfo.getForecast()) {
//		if (weatherInfo.getForecast() == null || weatherInfo.getForecast().getType(1) < 0)
//			return;
//		// 天气图标
//
//		// 星期
//		weekDay1.setText("今天");// 从今天开始
//		weekDay2.setText(TimeUtils.getWeek(1, TimeUtils.XING_QI));
//		weekDay3.setText(TimeUtils.getWeek(2, TimeUtils.XING_QI));
//		weekDay4.setText(TimeUtils.getWeek(3, TimeUtils.XING_QI));
//		weekDay5.setText(TimeUtils.getWeek(4, TimeUtils.XING_QI));
//		// 最高温
//		highTempDay1.setText(weatherInfo.getForecast().getTmpHigh(1) + "°");
//		highTempDay2.setText(weatherInfo.getForecast().getTmpHigh(2) + "°");
//		highTempDay3.setText(weatherInfo.getForecast().getTmpHigh(3) + "°");
//		highTempDay4.setText(weatherInfo.getForecast().getTmpHigh(4) + "°");
//		highTempDay5.setText(weatherInfo.getForecast().getTmpHigh(5) + "°");
//		// 最低温
//		lowTempDay1.setText(weatherInfo.getForecast().getTmpLow(1) + "°");
//		lowTempDay2.setText(weatherInfo.getForecast().getTmpLow(2) + "°");
//		lowTempDay3.setText(weatherInfo.getForecast().getTmpLow(3) + "°");
//		lowTempDay4.setText(weatherInfo.getForecast().getTmpLow(4) + "°");
//		lowTempDay5.setText(weatherInfo.getForecast().getTmpLow(5) + "°");
//
//		weatherInfo.getForecast()FootView.setText("");
//	}

    @Override
    public void setWeatherInfo(WeatherInfo weatherInfo) {
        int numValues = 6;

        ////////////////////icon
        icon1.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(0)));
        icon2.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(1)));
        icon3.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(2)));
        icon4.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(3)));
        icon5.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(4)));
        icon6.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(5)));


        w_txt_1.setText(weatherInfo.getForecast().getWeatherNamesFrom(0));
        w_txt_2.setText(weatherInfo.getForecast().getWeatherNamesFrom(1));
        w_txt_3.setText(weatherInfo.getForecast().getWeatherNamesFrom(2));
        w_txt_4.setText(weatherInfo.getForecast().getWeatherNamesFrom(3));
        w_txt_5.setText(weatherInfo.getForecast().getWeatherNamesFrom(4));
        w_txt_6.setText(weatherInfo.getForecast().getWeatherNamesFrom(5));

        foot_txt_1.setText(weatherInfo.getForecast().getWeatherNamesTo(0));
        foot_txt_2.setText(weatherInfo.getForecast().getWeatherNamesTo(1));
        foot_txt_3.setText(weatherInfo.getForecast().getWeatherNamesTo(2));
        foot_txt_4.setText(weatherInfo.getForecast().getWeatherNamesTo(3));
        foot_txt_5.setText(weatherInfo.getForecast().getWeatherNamesTo(4));
        foot_txt_6.setText(weatherInfo.getForecast().getWeatherNamesTo(5));

        footicon1.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(0)));
        footicon2.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(1)));
        footicon3.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(2)));
        footicon4.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(3)));
        footicon5.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(4)));
        footicon6.setImageResource(WeatherIconUtils.getWeatherIcon(weatherInfo.getForecast().getType(5)));

        week_txt3.setText(TimeUtils.getWeek(1, TimeUtils.ZHOU));
        week_txt4.setText(TimeUtils.getWeek(2, TimeUtils.ZHOU));
        week_txt5.setText(TimeUtils.getWeek(3, TimeUtils.ZHOU));
        week_txt6.setText(TimeUtils.getWeek(4, TimeUtils.ZHOU));

        int min = 999;
        int max = 0;

        List<Integer> lowList = new ArrayList<>();
        List<Integer> hightList = new ArrayList<>();
        List<Line> lines = new ArrayList<>();

        for (int i = 0; i < numValues; i++) {
            int tempMin = weatherInfo.getForecast().getTmpLow(i);
            min = Math.min(min, tempMin);
            lowList.add(tempMin);

            int tempMax = weatherInfo.getForecast().getTmpHigh(i);
            max = Math.max(max, tempMax);
            hightList.add(tempMax);
        }

        //low
        List<PointValue> valuesLows = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; i++) {
            valuesLows.add(new PointValue(i, lowList.get(i)));
        }
        Line lowLine = new Line(valuesLows);
        lowLine.setShape(ValueShape.CIRCLE);
        lowLine.setColor(getResources().getColor(R.color.gray_400)).setCubic(false);//setCubic:true 设置为平滑线
        lowLine.setHasLabels(true);//显示点的数字
        lowLine.setIsUp(false);

        //hight
        List<PointValue> valuesHighs = new ArrayList<>();
        for (int i = 0; i < numValues; i++) {
            valuesHighs.add(new PointValue(i, hightList.get(i)));
        }
        Line highLine = new Line(valuesHighs);
        highLine.setShape(ValueShape.CIRCLE);
        highLine.setColor(getResources().getColor(android.R.color.white)).setCubic(false);//setCubic:true 设置为平滑线
        highLine.setHasLabels(true);//显示点的数字
        highLine.setIsUp(true);


        ////////////////////////
        lines.add(lowLine);
        lines.add(highLine);


        ////////////////////////
        LineChartData lineData = new LineChartData(lines);

        lineData.setAxisXBottom(null);
        lineData.setAxisYLeft(null);
        lineData.setAxisYRight(null);

        lineChartView.setLineChartData(lineData);

        lineChartView.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, max + 3, numValues, min - 3);//最高，最低，列数

        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);

        lineChartView.setZoomEnabled(false);

        lineChartView.startDataAnimation(500);
    }
}
