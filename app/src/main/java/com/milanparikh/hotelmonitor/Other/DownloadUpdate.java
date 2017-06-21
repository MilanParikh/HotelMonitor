package com.milanparikh.hotelmonitor.Other;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Output;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by milan on 6/20/2017.
 */

public class DownloadUpdate extends AsyncTask {
    File externalStorage;
    Context context;

    public DownloadUpdate(Context context){
        this.context = context.getApplicationContext();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        int count;
        try {
            URL url = new URL("http://www.openhouseguest.com/app-release.apk");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();

            int fileSize = urlConnection.getContentLength();
            externalStorage = Environment.getExternalStorageDirectory();

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(externalStorage+"/update.apk");

            byte[] buffer = new byte[1024];
            float total=0;

            while ((count = input.read(buffer)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/fileSize));
                output.write(buffer, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/update.apk");
        Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }
}
