package com.think.study.live

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.think.study.R


class LiveCycleDialogFragment : DialogFragment() {

    override fun onAttachFragment(childFragment: Fragment) {
        Log.i(TAG,"onAttachFragment")
        super.onAttachFragment(childFragment)
    }


    // 启动过程 onAttach -> onCreate -> onCreateDialog -> onCreateView -> (Activity)onCreateView ->
    // onViewCreated -> onActivityCreated ->  onViewStateRestored ->  onStart -> (Activity) onResume
    // -> onResume -> (Activity)onAttachedToWindow


    override fun onAttach(context: Context) {
        Log.i(TAG,"onAttach")
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.i(TAG,"onCreateDialog")
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG,"onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_cycle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG,"onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG,"onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.i(TAG,"onViewStateRestored")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.i(TAG,"onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.i(TAG,"onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.i(TAG,"onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG,"onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.i(TAG,"onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.i(TAG,"onDestroy")
        super.onDestroy()
    }

    /**
     * 点击之后 onDismiss-> onPause -> onStop ->onDestroyView -> onDestroy ->onDetach
     */
    override fun onDismiss(dialog: DialogInterface) {
        Log.i(TAG,"onDismiss")
        super.onDismiss(dialog)
    }


    override fun onDetach() {
        Log.i(TAG,"onDetach")
        super.onDetach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.i(TAG,"onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun setInitialSavedState(state: SavedState?) {
        Log.i(TAG,"setInitialSavedState")
        super.setInitialSavedState(state)
    }

    companion object {
        val TAG = LiveCycleDialogFragment::class.java.simpleName
    }
}
