package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.StocksLineActivity;

/**
 * @author Nikita Simonov
 */

public class StockWidgetUpdateService extends IntentService {

    public static void start(final @NonNull Context context, final @NonNull int[] appWidgetsIds) {
        Intent intent = new Intent(context, StockWidgetUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetsIds);
        context.startService(intent);
    }

    public StockWidgetUpdateService() {
        this(StockWidgetUpdateService.class.getName());
    }

    public StockWidgetUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockWidgetUpdateService.class.getSimpleName(), "onHandleIntent");
        int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        Bundle args = new Bundle();
        StockTaskService stockTaskService = new StockTaskService(this);
        stockTaskService.onRunTask(new TaskParams("init", args));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_layout);

            setTemplateIntent(remoteViews);
            bindViews(remoteViews);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void setTemplateIntent(@NonNull RemoteViews remoteViews) {
        Intent stockIntent = new Intent(this, StocksLineActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, stockIntent, 0);
        remoteViews.setPendingIntentTemplate(R.id.lv_stocks_list, pendingIntent);
    }

    private void bindViews(@NonNull RemoteViews remoteViews) {
        Intent remoteViewIntent = new Intent(this, StockWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.lv_stocks_list, remoteViewIntent);
        remoteViews.setEmptyView(R.id.lv_stocks_list, R.id.empty);
    }
}
