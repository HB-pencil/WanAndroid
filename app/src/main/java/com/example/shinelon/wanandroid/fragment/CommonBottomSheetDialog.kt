package com.example.shinelon.wanandroid.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.shinelon.wanandroid.R
import kotlinx.android.synthetic.main.dialog_bottom.view.*

class CommonBottomSheetDialog: BottomSheetDialogFragment(){

    companion object {
        fun newInstance(title: String, message: String,listener: CommonDialogListener): CommonBottomSheetDialog{
            val dialogFragment = CommonBottomSheetDialog()
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
        val dialog = BottomSheetDialog(context)
        val frameLayout = FrameLayout(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom,frameLayout,true)
        dialog.setContentView(frameLayout)
        val viewGroup = frameLayout.parent as ViewGroup
        viewGroup.setBackgroundColor(Color.TRANSPARENT)
        //TODO 完善内容
        val listener = activity as CommonBottomSheetDialogListener
        view.dialog_cancel.setOnClickListener {
            if (dialog.isShowing) dialog.dismiss()
            listener.onNegativeClick()
        }
        view.dialog_confirm.setOnClickListener {
            listener.onPositiveClick()
        }
        return dialog
    }

}

/**
 * 托管该Fragment的Activity必须实现该接口
 */
interface CommonBottomSheetDialogListener{
    fun onPositiveClick()
    fun onNegativeClick()
}