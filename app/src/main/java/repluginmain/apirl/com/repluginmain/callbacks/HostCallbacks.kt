package repluginmain.apirl.com.repluginmain.callbacks

import android.content.Context
import android.content.Intent
import android.util.Log
import com.qihoo360.replugin.RePluginCallbacks
import repluginmain.apirl.com.repluginmain.MainApplication

/**
 * Created by april on 2018/2/27.
 */
class HostCallbacks(context: Context?) : RePluginCallbacks(context){

    override fun onPluginNotExistsForActivity(context: Context?, plugin: String?, intent: Intent?, process: Int): Boolean {
        //跳转的插件activity不存在回调，可以处理下载逻辑
        Log.d(MainApplication.TAG, "onPluginNotExistsForActivity: Start download... p=$plugin; i=$intent")

        return super.onPluginNotExistsForActivity(context, plugin, intent, process)
    }
}