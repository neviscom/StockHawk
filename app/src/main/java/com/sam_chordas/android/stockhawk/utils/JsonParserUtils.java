package com.sam_chordas.android.stockhawk.utils;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 */
public final class JsonParserUtils {

    private static String LOG_TAG = JsonParserUtils.class.getSimpleName();

    public static boolean showPercent = true;

    @NonNull
    public static ArrayList<ContentProviderOperation> quoteJsonToContentVals(@NonNull String json) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray resultsArray;
        try {
            jsonObject = new JSONObject(json);
            if (jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count < 1) {
                    return batchOperations;
                } else if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results").getJSONObject("quote");
                    buildBatchOperation(jsonObject, batchOperations);
                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            buildBatchOperation(jsonObject, batchOperations);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    private static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    private static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuilder stringBuilder = new StringBuilder(change);
        stringBuilder.insert(0, weight);
        stringBuilder.append(ampersand);
        change = stringBuilder.toString();
        return change;
    }

    private static void buildBatchOperation(@NonNull JSONObject jsonObject,
                                            @NonNull ArrayList<ContentProviderOperation> operations) {
        ContentProviderOperation buildedOperation = buildBatchOperation(jsonObject);
        if (buildedOperation != null) {
            operations.add(buildedOperation);
        }
    }

    @Nullable
    private static ContentProviderOperation buildBatchOperation(@NonNull JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
            return null;
        }
        return builder.build();
    }
}
