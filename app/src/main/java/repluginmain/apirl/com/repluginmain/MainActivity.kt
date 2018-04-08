package repluginmain.apirl.com.repluginmain

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.qihoo360.replugin.RePlugin
import kotlinx.android.synthetic.main.activity_main.*
import repluginmain.apirl.com.repluginmain.utils.FileUtil

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(MainApplication.TAG, "宿主启动")
        setListeners()

    }

    val pluginName = "plugin_u3d_2" //插件别名
    val u3dPluginName = "plugin_u3d" //u3d插件别名

    private fun setListeners() {

        //安装插件
        mBtnInstall.setOnClickListener {

            installPlugin("Project_test-debug.apk")

        }

        //打开插件activity
        mBtnOpenPlugin.setOnClickListener({
            val intent = RePlugin.createIntent(pluginName, "com.jiayiworld.utest.MainActivity")
            intent.putExtra("param1", "宿主的请求")
            val result = RePlugin.startActivity(this, intent)

            Toast.makeText(this, "打开插件：$result", Toast.LENGTH_SHORT).show()
        })



        mBtnCheckPluginInstall.setOnClickListener({
            val result = RePlugin.isPluginInstalled(pluginName)
            Toast.makeText(this, "插件$pluginName  已安装：$result", Toast.LENGTH_SHORT).show()
        })

        mBtnUnInstallPlugin.setOnClickListener({
            val result = RePlugin.uninstall(pluginName)
            Toast.makeText(this, "插件$pluginName ,卸载：$result", Toast.LENGTH_SHORT).show()
        })




        mBtnPluginUsed.setOnClickListener({

            startActivity(Intent(this, CallActivity::class.java))
//

        })


//        mBtnInstallU3d.setOnClickListener({
//            installPlugin("app-debug.apk")
//        })
//
//        mBtnOpenU3d.setOnClickListener({
//            val intent = RePlugin.createIntent(u3dPluginName, "mall.april.com.u3dtest.MainActivity")
//            intent.putExtra("param1", "宿主的请求")
//            val result = RePlugin.startActivity(this, intent)
//            Log.d(MainApplication.TAG, "打开插件：$result")
//        })
    }

    private fun installPlugin(pluginApkName: String) {
        val pluginPath = FileUtil.getRootPath().absolutePath + "/plugin/" + pluginApkName

        Log.d(MainApplication.TAG, "插件apk目录:$pluginPath")

        //安装插件
        val pluginInfo = RePlugin.install(pluginPath)
        if (pluginInfo != null) {
            //预加载插件
            RePlugin.preload(pluginInfo)
            Toast.makeText(this, "插件安装成功，alias=${pluginInfo.alias} version = ${pluginInfo.version}", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "插件安装失败", Toast.LENGTH_SHORT).show()

        }
    }


}


