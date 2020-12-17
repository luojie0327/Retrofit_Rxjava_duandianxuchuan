package com.example.myapplication.http;

import android.content.Context;

import com.example.myapplication.callback.DownloadCallBack;
import com.example.myapplication.util.Constant;
import com.example.myapplication.util.SPDownloadUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHttp {


    private static RetrofitHttp sIsntance;
    public static String baseUrl = ApiService.BASE_URL;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static ApiService apiService;

    public static RetrofitHttp getInstance() {

        if (sIsntance == null) {
            synchronized (RetrofitHttp.class) {
                if (sIsntance == null) {
                    sIsntance = new RetrofitHttp();
                }
            }

        }

        return sIsntance;
    }


    private RetrofitHttp() {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(ApiService.class);

    }

    public void downloadFile(final long range, final String url, final String fileName, final DownloadCallBack downloadCallBack) {

        File file = new File(Constant.APP_ROOT_PATH + Constant.DOWNLOAD_DIR, fileName);
        String totalLength = "-";
        if (file.exists()) {
            totalLength = totalLength + file.length();
        }

        apiService.executeDownload("bytes=" + Long.toString(range) + totalLength, url)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        RandomAccessFile randomAccessFile = null;
                        InputStream inputStream = null;

                        try {
                            String filepath = Constant.APP_ROOT_PATH + Constant.APP_ROOT_PATH;
                            File source = new File(filepath, fileName);

                            File desfile = new File(filepath);
                            if (!desfile.exists()) {
                                desfile.mkdirs();
                            }
                            int len = 0;
                            byte[] buf = new byte[1024];

                            inputStream = responseBody.byteStream();
                            randomAccessFile = new RandomAccessFile(source, "rwd");
                            long responseLength = responseBody.contentLength();
                            if (range == 0) {
                                randomAccessFile.setLength(responseLength);
                            }
                            randomAccessFile.seek(range);
                            int progress = 0;
                            int lastProgress = 0;
                            long total = range;

                            while ((len = inputStream.read(buf)) != -1) {
                                randomAccessFile.write(buf, 0, len);
                                total = total + len;
                                lastProgress = progress;
                                SPDownloadUtil.getInstance().save(url,total);
                                progress = (int) (total * 100 / randomAccessFile.length());
                                if (progress > 0 && progress != lastProgress) {
                                    downloadCallBack.onProgress(progress);
                                }
                            }
                            downloadCallBack.onCompleted();

                        } catch (Exception e) {
                            e.printStackTrace();
                            downloadCallBack.onError(e.getMessage().toString());

                        } finally {
                            try {
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        downloadCallBack.onError(e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
