package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;

/**
 * Created by Ebricko on 29/03/2018.
 */

public class ChartMarker extends  MarkerView {

    public ChartMarker(Context context, boolean isOnlyTime) {
        super(context, R.layout.layout_dot, isOnlyTime);
    }
}
