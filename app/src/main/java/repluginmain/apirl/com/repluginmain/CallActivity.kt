package repluginmain.apirl.com.repluginmain

import android.app.Activity
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.qihoo360.replugin.RePlugin
import kotlinx.android.synthetic.main.activity_call.*

/**
 * Created by april on 2018/3/1.
 */
class CallActivity : Activity() {


    var clz: Class<*>? = null
    var view: Any? = null


    val pluginName = "plugin_u3d_2" //插件别名

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        initPlayer()
    }

    private fun initPlayer() {

        val cl = RePlugin.fetchClassLoader(pluginName)
        if (cl == null) {
            Toast.makeText(this, "Not install $pluginName", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            clz = cl.loadClass("com.jiayiworld.myviews.MyUnityPlayer")

            val constructor = clz!!.getConstructor(ContextWrapper::class.java)
            view = constructor.newInstance(RePlugin.fetchContext(pluginName))

            Log.d("", "clz = $clz , view is  View = ${view is View}")

            Toast.makeText(this, "clz = $view , clz is  View = ${view is View}", Toast.LENGTH_SHORT).show()

            mUnityLayout.addView(view as View)
            Log.d("", "加入控件到布局")
        } catch (e: Exception) {
            // 有可能Demo2根本没有这个类，也有可能没有相应方法（通常出现在"插件版本升级"的情况）
            Toast.makeText(this, "异常", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        val met = clz!!.getMethod("resume")
        met.invoke(view)
    }

    // Quit Unity
    override fun onDestroy() {
        Log.d("", "模型界面退出")
        try {
            val met = clz!!.getMethod("quit")
            met.invoke(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()

    }

    // Pause Unity
    override fun onPause() {

        val met = clz!!.getMethod("pause")
        met.invoke(view)

        super.onPause()
    }


    // This ensures the layout will be correct.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val met = clz!!.getMethod("configurationChanged", Configuration::class.java)
        met.invoke(view, newConfig)

    }

    // Notify Unity of the focus change.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        val met = clz!!.getMethod("windowFocusChanged", Boolean::class.java)
        met.invoke(view, hasFocus)

    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_MULTIPLE) {
            val met = clz!!.getMethod("injectEvent", InputEvent::class.java)
            met.invoke(view, event) as Boolean
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {

        try {
            val met = clz!!.getMethod("injectEvent", InputEvent::class.java)
            return met.invoke(view, event) as Boolean
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val met = clz!!.getMethod("quit")
            met.invoke(view)
            finish()
            return true
        }

        val met = clz!!.getMethod("injectEvent", InputEvent::class.java)
        return met.invoke(view, event) as Boolean
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val met = clz!!.getMethod("injectEvent", InputEvent::class.java)
        return met.invoke(view, event) as Boolean
    }

    /*API12*/
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        val met = clz!!.getMethod("injectEvent", InputEvent::class.java)
        return met.invoke(view, event) as Boolean
    }
}