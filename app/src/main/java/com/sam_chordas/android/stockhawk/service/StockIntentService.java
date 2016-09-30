package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

    public static final String EXTRA_SYMBOL = "symbol";
    public static final String EXTRA_TAG = "tag";

    public static final String INIT = "init";
    public static final String ADD = "add";
    public static final String PERIODIC = "periodic";

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    public static void start(@NonNull Context context, @NonNull @Action String action) {
        start(context, action, null);
    }

    public static void start(@NonNull Context context, @NonNull @Action String action, @Nullable String symbol) {
        Intent intent = new Intent(context, StockIntentService.class);
        intent.putExtra(EXTRA_TAG, action);
        if (symbol != null) {
            intent.putExtra(EXTRA_SYMBOL, symbol);
        }
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra(EXTRA_TAG).equals(ADD)) {
            args.putString(EXTRA_SYMBOL, intent.getStringExtra(EXTRA_SYMBOL));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(EXTRA_TAG), args));
    }

    @StringDef({INIT, ADD})
    private @interface Action {}
}
