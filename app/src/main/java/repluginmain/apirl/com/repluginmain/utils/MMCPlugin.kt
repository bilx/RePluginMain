package repluginmain.apirl.com.repluginmain.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import com.qihoo360.replugin.RePlugin

/**
 * Created by april on 2018/3/6.
 */
class MMCPlugin private constructor() {
    companion object {
        private val instance: MMCPlugin by lazy {
            MMCPlugin()
        }

    }

    private var installListener: InstallListener? = null

    /**
     * 打开插件
     *
     * @param context
     * @param pluginName
     * @param activityName
     * @param installListener
     */
    public fun openPlugin(context: Context, pluginName: String, activityName: String, installListener: InstallListener) {
        this.installListener = installListener
        if (RePlugin.isPluginInstalled(pluginName)) {//判断是否已经安装，安装了的话，就打开Activity，并且检查插件版本，需要更新的话就下载插件
            RePlugin.startActivity(context, RePlugin.createIntent(pluginName, activityName))

            installListener.onSuccess()

//            val info = RePlugin.getPluginInfo(pluginName)
////            if (info.version < 2) {
//            //版本号由你们接口获得，然后进行对比，插件版本低于接口的版本就下载更新
//            downPlugin(context, "http://插件地址", pluginName, activityName, true);
//            }
        } else {
            downPlugin(context, "http://插件地址", pluginName, activityName, false);
        }
    }

    /**
     * 安装插件
     *
     * @param context
     * @param pluginName
     * @param activityName
     */
    public fun installPlugin(context: Context, pluginName: String, activityName: String, isUpdate: Boolean) {
        val info = RePlugin.install(Environment.getExternalStorageDirectory().absolutePath + "/" + pluginName + ".apk");
        if (info != null) {
            if (isUpdate) {//判断，是否为更新，如果是更新就预加载，下次打开就是最新的插件，不是更新就开始安装
                RePlugin.preload(info);
            } else {
                Thread(Runnable {
                    @Override
                    fun run() {
                        RePlugin.startActivity(context, RePlugin.createIntent(info.getName(), activityName));
                        (context as Activity).runOnUiThread({
                            @Override
                            fun run() {
                                if (installListener != null) {
                                    installListener!!.onSuccess()
                                }
                            }
                        })
                    }
                }).start()
            }
        } else {
            if (installListener != null) {
                installListener!!.onFail("安装失败");
            }
        }
    }

    /**
     * 下载插件
     *
     * @param context
     * @param fileUrl
     * @param pluginName
     * @param activityName
     */
    public fun downPlugin(context: Context, fileUrl: String, pluginName: String, activityName: String, isUpdate: Boolean) {
        //获取文件存储权限
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
        }
//        //下载插件，里面的下载方法可以换成你们自己的，例如okhttp，xutils3等等下载都行，然后在回调中处理那几个方法就行
//        MMCHttpClient.getInstance(context).downLoadFile(fileUrl, Environment.getExternalStorageDirectory() + "/" + pluginName + ".apk", new MMCHttpClient . OnDownloadListener () {
//            @Override
//            public void onDownloadSuccess() {
//                installPlugin(context, pluginName, activityName, isUpdate);
//            }
//
//            @Override
//            public void onDownloading(int progress) {
//                if (installListener != null) {
//                    installListener.onInstalling(progress);
//                }
//            }
//
//            @Override
//            public void onDownloadFailed() {
//                if (installListener != null) {
//                    installListener.onFail("下载失败");
//                }
//            }
//        });
    }

    /**
     * 打开插件的Activity
     *
     * @param context
     * @param pluginName
     * @param activityName
     */
    fun openActivity(context: Context, pluginName: String, activityName: String) {
        RePlugin.startActivity(context, RePlugin.createIntent(pluginName, activityName))
    }

    /**
     * 打开插件的Activity 可带参数传递
     *
     * @param context
     * @param intent
     * @param pluginName
     * @param activityName
     */
    fun openActivity(context: Context, intent: Intent, pluginName: String, activityName: String) {
        intent.component = ComponentName(pluginName, activityName)
        RePlugin.startActivity(context, intent)
    }

    /**
     * 打开插件的Activity 带回调
     *
     * @param activity
     * @param intent
     * @param pluginName
     * @param activityName
     * @param requestCode
     */
    fun openActivityForResult(activity: Activity, intent: Intent, pluginName: String, activityName: String, requestCode: Int) {
        intent.component = ComponentName(pluginName, activityName);
        RePlugin.startActivityForResult(activity, intent, requestCode, null);
    }


    interface InstallListener {
        fun onInstalling(progress: Int)

        fun onFail(msg: String);

        fun onSuccess();
    }


}