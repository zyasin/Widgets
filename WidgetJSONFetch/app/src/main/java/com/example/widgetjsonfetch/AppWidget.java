package com.example.widgetjsonfetch;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppWidget extends AppWidgetProvider {


    // Use your own JSON File URL here.
    private static final String FILE_URL = "https://zyasin.com/widgetdata.json";

    private Handler handler;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Iterate over each widget instance
        for (int appWidgetId : appWidgetIds) {
            // Create an instance of the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            // Set up the button click event
            views.setOnClickPendingIntent(R.id.widget_button, getPendingIntent(context));

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent getPendingIntent(Context context) {
        // Handle the button click event here
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction("FETCH_JSON");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null && intent.getAction() != null && intent.getAction().equals("FETCH_JSON")) {
            fetchJsonData(context);
        }
    }

    private void fetchJsonData(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(FILE_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        final String jsonData = response.toString();

                        Handler mainHandler = new Handler(context.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                displayToast(context, "Fetched JSON: " + jsonData);
                                Log.d("Fetched JSON", jsonData);
                            }
                        });
                    } else {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                displayToast(context, "Failed to fetch JSON. Response Code: " + responseCode);
                                Log.d("Failed to fetch", String.valueOf(responseCode));
                            }
                        });
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Handler mainHandler = new Handler(context.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            displayToast(context, "Exception while fetching JSON: " + e.getMessage());
                            Log.d("Exception while fetching", e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void displayToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
