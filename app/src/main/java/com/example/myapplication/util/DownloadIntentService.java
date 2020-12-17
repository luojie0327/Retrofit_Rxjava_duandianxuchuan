package com.example.myapplication.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.callback.DownloadCallBack;
import com.example.myapplication.http.RetrofitHttp;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import retrofit2.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadIntentService extends IntentService {


private String mDownloadFileName;
private Notification mNotification;
private NotificationManager mNotifyManager;
private String TAG = "luojie";
    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String downloadurl = intent.getExtras().getString("download_url");
        final int downloadId = intent.getExtras().getInt("download_id");

        mDownloadFileName = intent.getExtras().getString("download_file");
        final File file = new File(Constant.APP_ROOT_PATH + Constant.DOWNLOAD_DIR + mDownloadFileName);
        long range = 0;
        int progress = 0 ;
        if(file.exists()){
            range = SPDownloadUtil.getInstance().get(downloadurl,0);
            progress = (int)(range *100 / file.length());
            if(range == file.length()){
                //installApp(file);
                return;
            }
        }


        final RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.notify_download);
        remoteView.setProgressBar(R.id.pb_progress,100, progress,false);
        remoteView.setTextViewText(R.id.tv_progress,"已下载" + progress + "%");
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                .setContent(remoteView)
                .setTicker("正在下载")
                .setSmallIcon(R.mipmap.ic_launcher);
        mNotification  = builder.build();
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(downloadId,mNotification);




        RetrofitHttp.getInstance().downloadFile(range, downloadurl, mDownloadFileName, new DownloadCallBack() {
            @Override
            public void onProgress(int progress) {
                remoteView.setProgressBar(R.id.pb_progress,100,progress,false);
                remoteView.setTextViewText(R.id.tv_progress,"已下载" + progress + "%");
                mNotifyManager.notify(downloadId,mNotification);
            }

            @Override
            public void onCompleted() {
                Log.d("TAG","下载完成");
                mNotifyManager.cancel(downloadId);
                installApp(file);
            }

            @Override
            public void onError(String msg) {
                mNotifyManager.cancel(downloadId);
                Log.d("TAG","下载失败：" +msg);
            }
        });



    }


    private void installApp(File file){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
       Uri uri =  FileProvider.getUriForFile(MyApplication.getInstance(),MyApplication.getInstance().getPackageName() + ".fileprovider",file);
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        startActivity(intent);



    }

}
