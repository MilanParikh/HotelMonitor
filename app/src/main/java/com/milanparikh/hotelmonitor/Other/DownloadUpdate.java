package com.milanparikh.hotelmonitor.Other;

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
    }
}
