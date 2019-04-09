package com.example.shinelon.wanandroid.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.io.Serializable

class CommonDialogFragment: DialogFragment(){

    companion object {
        fun newInstance(title: String, message: String,listener: CommonDialogListener): CommonDialogFragment{
            val dialogFragment = CommonDialogFragment()
            val bundle = Bundle()
            bundle.putString("title",title)
            bundle.putString("message",message)
            bundle.putSerializable("listener",listener)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val title = arguments?.getString("title")
        val message = arguments?.getString("message")
        val listener = arguments?.getSerializable("listener") as CommonDialogListener
        val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK"){ _,_ ->
                    listener.onPositiveClick()
                }
                .setNegativeButton("CANCEL"){_,_ ->
                    listener.onNegativeClick()
                }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

}

/**
 * 托管该Fragment的Activity必须实现该接口
 */
interface CommonDialogListener: Serializable{
    val uuid: Long
    fun onPositiveClick()
    fun onNegativeClick()
}