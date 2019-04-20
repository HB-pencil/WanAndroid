package com.example.shinelon.wanandroid.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.utils.getFoldSize
import com.example.shinelon.wanandroid.utils.toast
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.lang.Exception
import java.security.MessageDigest

class SettingFragment: PreferenceFragment(){
    private var cachePref: Preference? = null
    private var cache: DiskLruCache? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.setting_pre)
        findPreference("clear").setOnPreferenceClickListener {
            clearCache()
        }

        findPreference("check").setOnPreferenceClickListener {
            toast(activity,"暂不支持！")
            false
        }

        findPreference("about").setOnPreferenceClickListener{
            AlertDialog.Builder(activity)
                    .setTitle("关于本软件")
                    .setMessage(activity.resources.getString(R.string.about_details))
                    .setPositiveButton("好的我知道了"){
                        _,_ ->
                    }
                    .create()
                    .show()
            false
        }

        findPreference("open_source").setOnPreferenceClickListener {
            showSupportDialog()
            false
        }

        findPreference("suggest").setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SENDTO
            intent.type = "text/plain"
            //val array = arrayOf("hardblack@aliyun.com")
            //intent.putExtra(Intent.EXTRA_EMAIL,array)
            intent.data= Uri.parse("mailto:hardblack@aliyun.com")
            intent.putExtra(Intent.EXTRA_SUBJECT,"意见反馈")
            if(intent.resolveActivity(activity.packageManager)!=null) startActivity(intent)
            false
        }
        updateCache()
        //待补充,edit(key)后newOutputStream写入之后commit或者abort，最后flush
    }

    fun getDiskCach(): File {
        var cachPath = ""
        if(!Environment.isExternalStorageRemovable()|| Environment.MEDIA_MOUNTED== Environment.getExternalStorageState()){
            cachPath = activity.externalCacheDir.path
        }else{
            cachPath = activity.cacheDir.path
        }
        val file = File(cachPath)
        if (!file.exists()) file.mkdirs()
        return file
    }

    companion object {
        fun newInstance(): SettingFragment{
            val fragment = SettingFragment()
            return fragment
        }
    }

    fun clearCache():Boolean{
        launch {
            Glide.get(activity).clearDiskCache()
            Snackbar.make(view,"清除缓存成功", Snackbar.LENGTH_SHORT).show()
        }
        return false
    }
    fun updateCache(){
        val size = getFoldSize(File("${activity.cacheDir} /${InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR}"))
        cachePref = findPreference("clear")
    }

    private fun showSupportDialog() {
        val frameLayout = FrameLayout(activity)
        LayoutInflater.from(activity).inflate(R.layout.dialog_bottom,frameLayout,true)
        val dialog = AlertDialog.Builder(activity)
                .setTitle("本App由以下开源技术支持")
                .setView(frameLayout)
                .setPositiveButton(android.R.string.ok,{_,_ -> Unit})
                .create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    /**
    fun urlToMd5Key(url: String): String{
        val message = MessageDigest.getInstance("MD5")
        message.update(url.toByte())
        val key = byteToHexString(message.digest())
        return key
    }

    fun byteToHexString(orgin: ByteArray): String{
        val stringBuilder = StringBuilder()
        orgin.forEach {
            val str = Integer.toHexString(it.toInt() and 0xff)
            if (str.length==1) stringBuilder.append(0)
            stringBuilder.append(str)
        }
        return stringBuilder.toString()
    } **/
}