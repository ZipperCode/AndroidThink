package com.think.ui.view

import android.view.SurfaceHolder

/**
 *
 * @author  zhangzhipeng
 * @date    2022/10/10
 */
object SurfaceCallbackManager: SurfaceHolder.Callback {

    private var renderThread: RenderThread? = null

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (renderThread == null){
            renderThread = RenderThread()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }


    private class RenderThread: Thread("SurfaceThread"){
        override fun run() {
            while (!interrupted()){


            }
        }
    }
}