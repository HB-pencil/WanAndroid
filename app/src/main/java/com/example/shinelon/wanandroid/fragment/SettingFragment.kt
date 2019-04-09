package com.example.shinelon.wanandroid.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.design.widget.Snackbar
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.utils.toast
import java.io.File
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
            toast(activity,"暂未完善！")
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

        val file = getDiskCach()
        cache = DiskLruCache.open(file,1,1,1*1024*1024*10)//第三个参数一个key对应多少文件
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
        activity.externalCacheDir
        cache!!.delete()
        Snackbar.make(view,"清除缓存成功", Snackbar.LENGTH_SHORT).show()
        return true
    }
    fun updateCache(){
        val size = cache!!.size()/1024/1024F
        activity.externalCacheDirs
        cachePref = findPreference("clear")
        cachePref?.summary = "缓存大小为${size}M"
    }

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
    }
}