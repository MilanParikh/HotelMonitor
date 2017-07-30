package com.milanparikh.hotelmonitor.Other;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Output;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.milanparikh.hotelmonitor.BuildConfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by milan on 6/20/2017.
 */

public class DownloadUpdate extends AsyncTask {
    File externalStorage;
    Context context;
    ProgressDialog progressDialog;

    public DownloadUpdate(Context context, ProgressDialog pDialog){
        this.context = context.getApplicationContext();
        progressDialog = pDialog;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        int count;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://www.openhouseguest.com/app-release.apk");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + urlConnection.getResponseCode()
                        + " " + urlConnection.getResponseMessage();
            }

            int fileSize = urlConnection.getContentLength();
            externalStorage = Environment.getExternalStorageDirectory();

            input = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(externalStorage+"/update.apk");

            byte data[] = new byte[4096];
            float total=0;

            while ((count = input.read(data)) != -1) {
                if (isCancelled()){
                    input.close();
                    return null;
                }

                total += count;
                if (fileSize > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileSize));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        progressDialog.dismiss();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/update.apk");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri updateURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE).setData(updateURI);
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(install);
        }else {
            Uri updateURI = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(updateURI, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress((int)values[0]);
    }
}
