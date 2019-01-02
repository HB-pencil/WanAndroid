package com.example.shinelon.wanandroid.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

class CommonDialogFragment: DialogFragment(){

    companion object {
        fun newInstance(title: String, message: String,listener: CommonDialogListener): CommonDialogFragment{
            val dialogFragment = CommonDialogFragment()
            val bundle = Bundle()
            bundle.putString("title",title)
            bundle.putString("message",message)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val title = arguments.getString("title")
        val message = arguments.getString("message")
        val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK"){ _,_ ->
                    val listener = activity as CommonDialogListener
                    listener.onPositiveClick()
                }
                .setNegativeButton("CANCEL"){_,_ ->
                    val listener = activity as CommonDialogListener
                    listener.onNegativeClick()
                }
        return builder.create()
    }

}

/**
 * 托管该Fragment的Activity必须实现该接口
 */
interface CommonDialogListener{
    fun onPositiveClick()
    fun onNegativeClick()
}