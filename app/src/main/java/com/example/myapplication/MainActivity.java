package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.DownloadIntentService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DOWNLOADAPK_ID = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvDownload = findViewById(R.id.tv_download);
        if(isServiceRunning(DownloadIntentService.class.getName())){
            Toast.makeText(MainActivity.this,"正在下载",Toast.LENGTH_SHORT).show();
        }
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2859174087,963187950&fm=23&gp=0.jpg";
                Intent intent = new Intent(MainActivity.this, DownloadIntentService.class);
                Bundle bundle = new Bundle();
                bundle.putString("download_url", downloadUrl);
                bundle.putInt("download_id", DOWNLOADAPK_ID);
                bundle.putString("download_file", "1.jpg");
                intent.putExtras(bundle);
                startService(intent);
            }
        });

    }

    private Boolean isServiceRunning(String className){

     boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
     List<ActivityManager.RunningServiceInfo> servicelist = activityManager.getRunningServices(Integer.MAX_VALUE);
        if(!(servicelist.size() > 0)){
            return  false;
        }

        for (int i = 0 ; i < servicelist.size() ; i++){
            if(servicelist.get(i).service.getClassName().equals(className)){
                isRunning = true;
                break;
            }
        }
        return isRunning;

    }


    }

