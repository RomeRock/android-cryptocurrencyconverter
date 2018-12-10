package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MarkerView extends com.github.mikephil.charting.components.MarkerView {

    private TextView txtDate;
    private TextView txtValue;
    private boolean isOnlyTime;

    public MarkerView(Context context, int layoutResource, boolean isOnlyTime) {
        super(context, layoutResource);
        this.isOnlyTime=isOnlyTime;
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtValue = (TextView) findViewById(R.id.txtValue);
    }


    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        txtDate.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(e.getY()))));
        if(!isOnlyTime)
            txtValue.setText(Utilities.getFormatedDate(Utilities.getDateFromUnixTimestamp((long) e.getX())));
        else{
            String[] spliter=Utilities.getFormatedDateOnlyTime(String.valueOf(e.getX())).split("T");
            txtValue.setText(spliter[1]);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
