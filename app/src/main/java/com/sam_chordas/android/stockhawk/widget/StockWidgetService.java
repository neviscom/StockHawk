package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.StocksLineActivity;

/**
 * @author Nikita Simonov
 */

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewsFactory();
    }

    class StockRemoteViewsFactory implements RemoteViewsFactory {

        @Nullable
        private Cursor mData;

        @Override
        public void onCreate() {
            // do nothing
        }

        @Override
        public void onDataSetChanged() {
            if (mData != null) {
                mData.close();
            }

            final long idToken = Binder.clearCallingIdentity();

            mData = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE},
                    QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"},
                    null);

            Binder.restoreCallingIdentity(idToken);
        }

        @Override
        public void onDestroy() {
            if (mData != null) {
                mData.close();
                mData = null;
            }
        }

        @Override
        public int getCount() {
            return mData != null ? mData.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.li_widget);
            if (mData == null || i == AdapterView.INVALID_POSITION || !mData.moveToPosition(i)) {
                return null;
            }
            bindViewData(views, mData);
            setOnClickListener(views, mData);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.li_widget);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            if (mData != null && mData.moveToPosition(i)) {
                return Long.parseLong(mData.getString(mData.getColumnIndex(QuoteColumns._ID)));
            }
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private void bindViewData(final @NonNull RemoteViews views, final @NonNull Cursor data) {
            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            String price = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));

            views.setTextViewText(R.id.tv_stock_symbol, symbol);
            views.setTextViewText(R.id.tv_bid_price, price);
        }

        private void setOnClickListener(final @NonNull RemoteViews views, final @NonNull Cursor data) {
            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            views.setOnClickFillInIntent(R.id.li_widget,
                    StocksLineActivity.getStartIntent(getApplicationContext(), symbol));
        }
    }
}
