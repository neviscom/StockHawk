package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * @author Nikita Simonov
 */

public class StocksLineActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_SYMBOL = "symbol";
    public static final int LINE_THICKNESS = 4;
    public static final float LINE_ALPHA = 0.5f;


    private String mSymbol;

    private LineChartView mLineChartView;
    private View mEmptyView;

    public static void launch(@NonNull Activity activity, @NonNull String symbol) {
        activity.startActivity(getStartIntent(activity, symbol));
    }

    @NonNull
    public static Intent getStartIntent(@NonNull Context context, @NonNull String symbol) {
        Intent intent = new Intent(context, StocksLineActivity.class);
        intent.putExtra(EXTRA_SYMBOL, symbol);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        mSymbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        setTitle(mSymbol);

        mEmptyView = findViewById(R.id.stock_empty_view);
        mLineChartView = (LineChartView) findViewById(R.id.linechart);
        if (mLineChartView != null) {
            initChart();
        }

        getLoaderManager().initLoader(R.id.stock_information_loader, Bundle.EMPTY, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{mSymbol},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && !data.isClosed() && data.moveToFirst()) {
            bindChart(data);
        } else {
            showEmptyView();
        }
        getLoaderManager().destroyLoader(R.id.stock_information_loader);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }

    private void initChart() {
        mLineChartView
                .setXLabels(AxisController.LabelPosition.NONE);
    }

    private void bindChart(@NonNull Cursor data) {
        LineSet lineSet = new LineSet();
        float maxBorderValue = Float.MIN_VALUE;
        float minBorderValue = Float.MAX_VALUE;

        do {
            String label = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
            float value = Float.parseFloat(label.replace(',', '.'));
            lineSet.addPoint(label, value);

            maxBorderValue = Math.max(maxBorderValue, value);
            minBorderValue = Math.min(minBorderValue, value);
        } while (data.moveToNext());

        lineSet.setColor(ContextCompat.getColor(this, R.color.material_green_700))
                .setThickness(LINE_THICKNESS)
                .setAlpha(LINE_ALPHA);

        mLineChartView
                .setAxisBorderValues(Math.round(minBorderValue), Math.round(maxBorderValue + 1))
                .addData(lineSet);

        if (lineSet.size() < 1) {
            showEmptyView();
        } else {
            showChart();
        }
    }

    private void showChart() {
        mEmptyView.setVisibility(View.GONE);
        mLineChartView.setVisibility(View.VISIBLE);
        mLineChartView.show();
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mLineChartView.setVisibility(View.GONE);
    }
}
