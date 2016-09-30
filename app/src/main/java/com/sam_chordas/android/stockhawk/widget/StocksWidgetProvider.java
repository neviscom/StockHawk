package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/**
 * @author Nikita Simonov
 */

public class StocksWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        StockWidgetUpdateService.start(context, appWidgetIds);
    }
}
