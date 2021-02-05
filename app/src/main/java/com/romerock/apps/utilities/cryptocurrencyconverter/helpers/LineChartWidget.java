package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.GraphModel;

import java.util.ArrayList;
import java.util.List;

public class LineChartWidget implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;
    private Context context;
    private List<GraphModel> listGraphModel;
    private double maxLimit;
    private double minLimit;
    private boolean isOnlyTime;
    private DialogsHelper dialogsHelper;
    private SharedPreferences sharedPreferences;
    private  Drawable drawable;

    public LineChartWidget(Context context, LineChart mChart, List<GraphModel> listGraphModel, double maxLimit, double minLimit, boolean isOnlyTime,
                           DialogsHelper dialogsHelper, SharedPreferences sharedPreferences) {
        this.mChart = mChart;
        this.context = context;
        this.listGraphModel = listGraphModel;
        this.maxLimit = maxLimit;
        this.minLimit = minLimit;
        this.isOnlyTime=isOnlyTime;
        this.dialogsHelper=dialogsHelper;
        this.sharedPreferences=sharedPreferences;
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setGridBackgroundColor(Color.BLACK);
        //  description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

    /*    MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mChart.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart*/

        mChart.setBorderColor(Color.BLACK);
        // x-axis limit line
        LimitLine llXAxis = new LimitLine(0f, "");
        llXAxis.setLineWidth(1f);
        llXAxis.enableDashedLine(0f, 0f, 0f);
        llXAxis.setTextSize(0f);

        XAxis xAxis = mChart.getXAxis();
        //   xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum((float) maxLimit);
        leftAxis.setAxisMinimum((float) minLimit);
        //leftAxis.setYOffset(20f);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getDescription().setEnabled(false);
        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.getXAxis().setDrawLabels(false);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawAxisLine(false);
        mChart.getAxisRight().setDrawAxisLine(false);
        mChart.getLegend().setEnabled(false);   // Hide the legend

        mChart.setDrawMarkers(true);
        MarkerView customMarkerView = new MarkerView(context, R.layout.market, isOnlyTime);
        mChart.setMarker(customMarkerView);



        // add data
        setData();
        mChart.animateXY(2500, 2500);
    }
    // TODO esta es la grafica a usar

    private void setData() {
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = 0; i < listGraphModel.size(); i++) {
            double val = listGraphModel.get(i).getValue();
            float indice;
            if(isOnlyTime){
                indice= Float.parseFloat(listGraphModel.get(i).getDate());
            }else{
                indice= (float) (Utilities.getDateToUnixTimeStamp(listGraphModel.get(i).getDate()));
            }
            values.add(new Entry(indice, (float) val));
        }
        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type*/
            set1 = new LineDataSet(values, null);
            set1.setDrawIcons(false);
            String themeSelected = sharedPreferences.getString(context.getString(R.string.preferences_theme_tittle), null);
            if (themeSelected.contains("Night")) {
                set1.setCircleColor(context.getResources().getColor(R.color.colorAccent));
                set1.setColor(context.getResources().getColor(R.color.lineColorGraphTheme1));
                set1.setHighLightColor(context.getResources().getColor(R.color.primaryTextTheme1));
                drawable = ContextCompat.getDrawable(context, R.drawable.fade_graph_theme1);

            } else {
                if (themeSelected.contains("Daylight")) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.fade_graph_theme2);
                    set1.setFillDrawable(drawable);
                    set1.setCircleColor(context.getResources().getColor(R.color.colorAccent_Daylight));
                    set1.setColor(context.getResources().getColor(R.color.lineColorGraphTheme2));
                    set1.setHighLightColor(context.getResources().getColor(R.color.primaryTextTheme2));
                }
            }
            set1.setFillDrawable(drawable);
            set1.setLineWidth(1f);
            set1.setDrawCircleHole(false);
            set1.setDrawCircles(false);
            set1.setValueTextSize(1f);
            set1.setDrawFilled(true);  // fill gradient
            set1.setFormSize(8f);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            data.notifyDataChanged();
            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
