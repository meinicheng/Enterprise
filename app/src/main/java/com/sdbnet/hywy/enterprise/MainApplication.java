package com.sdbnet.hywy.enterprise;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sdbnet.hywy.enterprise.ui.SplashActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.ToastUtil;


import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static final boolean DEVELOPER_MODE = true;


    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        PreferencesUtil.initialize(this);

        initImageLoader(this);

        uncaughtException();
        initStrictMode();
        initDebug();

        initSDFile();


    }

    public static List<String> mAccounts = new ArrayList<String>();

    private void initDebug() {
        if (!mAccounts.contains("18802691909"))
            mAccounts.add("18802691909");
        ToastUtil.openShow();
        LogUtil.openLog();

    }

    private void initStrictMode() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build());
        }
    }

    /**
     * 捕获全局异常
     */
    private void uncaughtException() {
        if (DEVELOPER_MODE) {
            return;
        }
        CrashHandler.getInstance().init(this);
//        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                ex.printStackTrace();
//                Intent intent = new Intent(getApplicationContext(),
//                        SplashActivity.class);
//                PendingIntent restartIntent = PendingIntent.getActivity(
//                        getApplicationContext(), 0, intent,
//                        Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                // 立即重启应用
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis(),
//                        restartIntent);
//                // 退出程序
//                ActivityStackManager.getStackManager().popAllActivitys();
//            }
//        });
    }

    private void initSDFile() {
        File file = new File(Constants.SDBNET.BASE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File picFile = new File(Constants.SDBNET.PATH_PHOTO);
        if (!picFile.exists()) {
            picFile.mkdirs();
        }
        File formatsFile = new File(Constants.SDBNET.SDPATH_FORMATS);
        if (!formatsFile.exists()) {
            formatsFile.mkdirs();
        }
        File crashFile = new File(Constants.SDBNET.SDPATH_CRASH);
        if (!crashFile.exists()) {
            crashFile.mkdirs();
        }

    }


    protected void showLongToast(int pResId) {
        showLongToast(getString(pResId));
    }

    protected void showLongToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    private DisplayImageOptions options;

    private void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                        // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_photo)
                .showImageForEmptyUri(R.drawable.no_photo)
                .showImageOnFail(R.drawable.no_photo).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

    }

    public DisplayImageOptions getImgOptions() {
        return options;
    }

}
