package com.mitracking;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mitracking.db.DBHelper;
import com.mitracking.dialogs.CustomDialog;
import com.mitracking.dialogs.LoadDialog;
import com.mitracking.dialogs.UpdateDialog;
import com.mitracking.objs.LoginObj;
import com.mitracking.utils.GpsConfiguration;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Singleton extends Application {

    private static final boolean isQA = true;
    private static Singleton m_Instance;
    private static Context context;
    private static AppCompatActivity activity;
    private static FragmentManager fragmentManager;
    private static Fragment currentFragment;
    private static RelativeLayout toolbar;
    private static Button btn;

    //----------------------
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 10;

    private static final BlockingQueue<Runnable> sWorkQueue =
            new LinkedBlockingQueue<Runnable>(KEEP_ALIVE);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "TrackingUserTask #" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);
    //----------------------

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private static ImageLoaderConfiguration config;
    private static DisplayImageOptions defaultOptions;

    private static int currentapiVersion;

    private static File cache;

    private static String baseUrl = "",
            fileBaseUrl = "";

    private static LoadDialog load;
    private static CustomDialog customDialog;
    private static TextView action_txt;
    private static ImageView action_img, profile, info, menu_btn, addBtn;
    private static LoginObj loginObj;
    private static GpsConfiguration gps;
    private static SQLiteDatabase db;
    private static DBHelper dbh;
    private static UpdateDialog updateDialog;

    public Singleton() {
        super();
        m_Instance = this;
    }

    public static Singleton getInstance() {
        if(m_Instance == null) {
            synchronized(Singleton.class) {
                if(m_Instance == null) new Singleton();
            }
        }
        return m_Instance;
    }

    public void onCreate() {
        super.onCreate();
        context = this;

        if(isQA){

        } else {

        }

        currentapiVersion = Build.VERSION.SDK_INT;
        initBD();
        genCacheDataCarpet();
        initPreferences();
        initImageLoader(this);
        //initGPSConfig(this);
        /*if(!settings.getBoolean("moda", false)){
            dbh.initModalidad();
            savePreferences("moda", true);
        }*/
    }

    private void initBD(){
        if(dbh == null)
            dbh = new DBHelper(this, "Tracking", null, 10);
        if(db == null)
            db = dbh.getWritableDatabase();
        //dbh.insertColums();
    }

    public static SQLiteDatabase getDb(){
        return db;
    }

    public static DBHelper getBdh(){
        return dbh;
    }

    public static void genCacheDataCarpet(){
        if(cache == null)
            cache = new File(Environment.getExternalStorageDirectory(), "Trckg");
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }

    public static File getCacheCarpet(){
        return cache;
    }

    private void initPreferences(){
        if(settings == null)
            settings = getSharedPreferences("Trckg_prefs", Context.MODE_PRIVATE);
        if(editor == null)
            editor = settings.edit();
    }

    public static SharedPreferences getSettings(){
        return settings;
    }

    public static SharedPreferences.Editor getEditor(){
        return editor;
    }

    public static int dpTpPx(Context context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static ThreadPoolExecutor getsExecutor(){
        return sExecutor;
    }

    public static String getBaseUrl(){
        return baseUrl;
    }

    public static String getFileBaseUrl(){
        return fileBaseUrl;
    }

    private static void initImageLoader(Context context) {
        if(defaultOptions == null)
            defaultOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();

        config = new ImageLoaderConfiguration.Builder(context)
                //.imageDownloader(new SecureImageDownloader(context, 3000, 3000))
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(150 * 1024 * 1024) // 150 Mb
                .memoryCacheExtraOptions(480, 800)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .threadPoolSize(10)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getDefaultOptions(){
        return defaultOptions;
    }

    public static void loadImage(final String url, ImageView imageView, final ProgressBar load){
        ImageLoader.getInstance().displayImage(url, imageView, defaultOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Error de entrada o de salida";
                        break;
                    case DECODING_ERROR:
                        message = "La imagen no pudo ser decodificada";
                        break;
                    case NETWORK_DENIED:
                        message = "La descarga fue denegada";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Error desconocido";
                        break;
                }
                Log.i(url, message);
                load.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                load.setVisibility(View.GONE);
            }
        });
    }

    public static void clearImageCache(String url){
        MemoryCacheUtils.removeFromCache(url, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiskCache());
    }

    public static void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static LoadDialog showLoadDialog(FragmentManager manager){
        if(load == null){
            synchronized (Singleton.class){
                if(load == null){
                    load = LoadDialog.newInstance();
                    load.setCancelable(false);
                    try{
                        load.show(manager, "load dialog");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return load;
    }

    public static void dissmissLoad(){
        try {
            if(load != null) {
                try{
                    load.dismiss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                load = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void genToast(final Activity activity, final String msn){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "" + msn, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static CustomDialog showCustomDialog(FragmentManager manager, String title, String body, String action,
                                                int actionId){
        if(customDialog == null){
            synchronized (Singleton.class){
                if(customDialog == null){
                    customDialog = customDialog.newInstance(title, body, action, actionId);
                    customDialog.setCancelable(false);
                    try{
                        customDialog.show(manager, "custom dialog");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        customDialog = null;
                    }
                }
            }
        }
        return customDialog;
    }

    public static void dissmissCustom(){
        try {
            if(customDialog != null) {
                try{
                    customDialog.dismiss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                customDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CustomDialog getCustomDialog(){
        return customDialog;
    }

    public static int getCurrentApiVersion(){
        return currentapiVersion;
    }

    public static void savePreferences(String tag, String arg){
        editor.putString(tag, arg);
        editor.apply();
    }

    public static void savePreferences(String tag, int arg){
        editor.putInt(tag, arg);
        editor.apply();
    }

    public static void savePreferences(String tag, boolean arg){
        editor.putBoolean(tag, arg);
        editor.apply();
    }

    public static void savePreferences(String tag, long arg){
        editor.putLong(tag, arg);
        editor.apply();
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static void setCurrentActivity(AppCompatActivity arg){
        activity = arg;
    }

    public static AppCompatActivity getCurrentActivity(){
        return activity;
    }

    public static void setFragmentManager(FragmentManager arg){
        fragmentManager = arg;
    }

    public static FragmentManager getFragmentManager(){
        return fragmentManager;
    }

    public static void setCurrentFragment(Fragment arg){
        currentFragment = arg;
    }

    public static Fragment getCurrentFragment(){
        return currentFragment;
    }

    public static void setToolbar(RelativeLayout arg){
        toolbar = arg;
    }

    public static RelativeLayout getToolbar(){
        return toolbar;
    }

    public static void setActionTxt(TextView arg){
        action_txt = arg;
    }

    public static void setActionImg(ImageView arg){
        action_img = arg;
    }

    public static TextView getActionTxt(){
        return action_txt;
    }

    public static ImageView getActionImg(){
        return action_img;
    }

    public static void setProfileImg(ImageView arg){
        profile = arg;
    }

    public static ImageView getInfoImg(){
        return info;
    }

    public static void setInfoImg(ImageView arg){
        info = arg;
    }

    public static ImageView getAddImg(){
        return addBtn;
    }

    public static void setAddImg(ImageView arg){
        addBtn = arg;
    }

    public static ImageView getMenuImg(){
        return menu_btn;
    }

    public static void setMenuImg(ImageView arg){
        menu_btn = arg;
    }

    public static ImageView getProfileImg(){
        return profile;
    }

    public static void setLoginObj(LoginObj arg){
        if(loginObj == null)
            loginObj = arg;
    }

    public static LoginObj getLoginObj(){
        return loginObj;
    }

    public static void initGPSConfig(Context context){
        gps = new GpsConfiguration(context, false);
    }

    public static GpsConfiguration getGpsConfig(){
        return gps;
    }


    public static void setUpdateDialog(UpdateDialog arg){
        updateDialog = arg;
    }

    public static UpdateDialog getUpdateDialog(){
        return updateDialog;
    }

    public static void setBtn(Button arg){
        btn = arg;
    }

    public static Button getBtn(){
        return btn;
    }

    private static Timer timer;
    public static Timer getTimer() {
        if(timer == null) {
            synchronized(Singleton.class) {
                if(timer == null) new Timer();
            }
        }
        return timer;
    }

}
