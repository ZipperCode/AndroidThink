package com.think.accessibility.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.LruCache
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import com.think.accessibility.bean.AppInfo
import com.think.accessibility.bean.ViewInfo
import com.think.accessibility.room.DBHelper
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap

@SuppressLint("StaticFieldLeak")
object AccessibilityUtil {

    private val TAG: String = AccessibilityUtil::class.java.simpleName

    private const val SP_PKS_LIST_KEY = "name_list"


    var mAccessibilityService: AccessibilityService? = null

    /**
     * 保存需要过滤的包名
     */
    private val mNameList:MutableSet<String> = HashSet()


    /**
     * 是否执行绘制view
     */
    var mDrawViewBound: Boolean = false

    /**
     * 选中跳过的view
     */
    val mDumpViewInfo: MutableList<ViewInfo> = ArrayList()

    /**
     * 无障碍服务手机的view信息
     */
    var mCollectViewInfoList: MutableList<ViewInfo>? = null

    val mMainAppInfo:MutableList<AppInfo> = ArrayList()


    @SuppressLint("StaticFieldLeak")
    fun init(context: Context){
        pksInit(context)
        viewInfoInit(context)
    }

    private fun pksInit(context: Context){
        SpHelper.init(context)
        val pks = SpHelper.loadStringArray(SP_PKS_LIST_KEY)
        mNameList.clear()
        mNameList.addAll(pks)
    }


    fun addPks(pks: Collection<String>){
        mNameList.addAll(pks)
        SpHelper.saveStringArray(SP_PKS_LIST_KEY, mNameList)
    }

    fun addPks(pks: String){
        mNameList.add(pks)
        SpHelper.saveStringArray(SP_PKS_LIST_KEY, mNameList)
    }

    fun delPks(pks: String){
        mNameList.remove(pks)
        SpHelper.saveStringArray(SP_PKS_LIST_KEY, mNameList)
    }

    fun clearPks(){
        mNameList.clear()
        SpHelper.saveStringArray(SP_PKS_LIST_KEY, mNameList)
    }

    fun pksContains(pks: String): Boolean{
        return mNameList.contains(pks)
    }

    fun pksContainsAll(pks: List<String>): Boolean{
        return mNameList.containsAll(pks)
    }

    /**
     * 从数据库中查找所有保存的view信息
     */
    private fun viewInfoInit(context: Context){
        val db = DBHelper.openViewInfoDatabase(context.applicationContext)
        mDumpViewInfo.clear()
        try {
            mDumpViewInfo.addAll(db.getViewInfoDao().getAll())
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun addViewInfo(viewInfo: ViewInfo){
        val dao = DBHelper.getViewInfoDao()
        try{
            dao.insert(viewInfo)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun deleteViewInfo(viewInfo: ViewInfo){
        if(Looper.getMainLooper() != Looper.myLooper()){
            Log.e(TAG,"[addViewInfo] 非主线程")
        }
        val dao = DBHelper.getViewInfoDao()
        try{
            dao.delete(viewInfo)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun viewInfoListIds(packageName: String): List<String>{
        return mDumpViewInfo
                .filter { (it.packageName == packageName) and !TextUtils.isEmpty(it.viewId) }
                .map { it.viewId!! }.distinct()

    }

    fun packageViewInfoList(packageName:String) : List<ViewInfo>{
        if(TextUtils.isEmpty(packageName)){
            return emptyList()
        }
        val dao = DBHelper.getViewInfoDao()
        try {
            val a =  dao.queryByPackageName(packageName)
            return a
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    /**
     * 进行手势滑动操作，需要Android N以上版本
     * @param service       无障碍服务
     * @param path          滑动路径
     * @param startTime     持续时间，手势到笔画的时间（毫秒）
     * @param duration      path路径走过的时间（毫秒）
     */

    @RequiresApi(Build.VERSION_CODES.N)
    fun gestureScroll(
            service: AccessibilityService?,
            path: Path,
            @IntRange(from = 0) startTime: Long = 10,
            @IntRange(from = 0) duration: Long = 10,
            callback: AccessibilityService.GestureResultCallback?
    ) {
        if (service == null) {
            return
        }
        Log.d(TAG, "[gestureScroll]")
        service.dispatchGesture(
                GestureDescription.Builder()
                        .addStroke(
                                GestureDescription.StrokeDescription(path, startTime, duration)).build(),
                callback,
                null
        )
    }

    /**
     * 手势点击 需要Android N以上版本
     * @param service       无障碍服务
     * @param point         触摸点，在屏幕中的位置
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun gestureClick(service: AccessibilityService?, point: Point) {
        Log.d(TAG, "[gestureClick] point = $point")
        val pointPth = Path().apply {
            moveTo(point.x.toFloat(), point.y.toFloat())
            lineTo(point.x.toFloat(), point.y.toFloat())
        }
        gestureClick(service, pointPth)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun gestureClick(service: AccessibilityService?, pointPth: Path) {
        Log.d(TAG, "[gestureClick] pointPth = $pointPth")
        gestureScroll(service, pointPth, callback = object : AccessibilityService.GestureResultCallback() {})

    }


    fun gestureClick(service: AccessibilityService?, nodeInfo: AccessibilityNodeInfo?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (nodeInfo == null) {
                return
            }
            val rect = Rect()
            nodeInfo.getBoundsInScreen(rect)
            val pointPth = getRandomPath(rect)
            Log.d(TAG, "[gestureClick] pointPth = $pointPth")
            gestureScroll(service, pointPth, callback = object : AccessibilityService.GestureResultCallback() {})
        }
    }

    /**
     * 点击
     * @param nodeInfo      待点击的节点
     */
    fun click(nodeInfo: AccessibilityNodeInfo): Boolean {
        return performAction(nodeInfo, AccessibilityNodeInfo.ACTION_CLICK)
    }

    /**
     * 深入点击，当前控件不可点击，就查找可点击的父控件
     */
    fun deepClick(nodeInfo: AccessibilityNodeInfo): Boolean {
        var isClick = nodeInfo.isClickable
        try {
            if (!isClick) {
                var canClick = nodeInfo
                do {
                    canClick = findClickableView(canClick)
                    isClick = click(canClick)
                } while (!isClick)
            } else {
                click(nodeInfo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mAccessibilityService?.let {
                    val rect = Rect()
                    nodeInfo.getBoundsInScreen(rect)
                    gestureClick(it, getRandomPath(rect))
                }
            }
        }
        return isClick
    }

    /**
     * 长按
     * @param nodeInfo      待长按的节点
     */
    fun longClick(nodeInfo: AccessibilityNodeInfo): Boolean {
        return performAction(nodeInfo, AccessibilityNodeInfo.ACTION_LONG_CLICK)
    }

    /**
     * 双击，默认双击相差50ms
     * @param nodeInfo      待双击的节点
     */
    fun doubleClick(nodeInfo: AccessibilityNodeInfo, milliseconds: Long = 50L): Boolean {
        return if (click(nodeInfo)) {
            TimeUnit.MILLISECONDS.sleep(milliseconds)
            click(nodeInfo)
        } else {
            false
        }
    }

    fun goHome() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }

    /**
     * 执行其他的操作
     * @param nodeInfo      待操作的节点
     * @param action        执行的操作
     */
    fun performAction(nodeInfo: AccessibilityNodeInfo, action: Int): Boolean {
        Log.d(TAG, "[performAction] action = $action")
        try {
            return nodeInfo.performAction(action)
        } finally {
            nodeInfo.recycle()
        }
    }

    fun performGlobalAction(action: Int): Boolean {
        Log.d(TAG, "[performGlobalAction] action = $action")
        return mAccessibilityService?.performGlobalAction(action) ?: false
    }

    /**
     * 通过id查找对应的节点信息
     * @param rootNodeInfo  根节点
     * @param id            查找的id
     */
    fun findNodeById(
            rootNodeInfo: AccessibilityNodeInfo?,
            id: String
    ): AccessibilityNodeInfo? {
        Log.d(TAG, "[findNodeById] id = $id")
        if (TextUtils.isEmpty(id) or (rootNodeInfo == null)) {
            return null
        }
        val list = rootNodeInfo!!.findAccessibilityNodeInfosByViewId(id)
        return list?.distinct()?.firstOrNull()
    }

    fun findNodeListById(
            rootNodeInfo: AccessibilityNodeInfo?,
            id: String
    ): List<AccessibilityNodeInfo> {
        Log.d(TAG, "[findNodeById] id = $id")
        if (TextUtils.isEmpty(id) or (rootNodeInfo == null)) {
            return emptyList()
        }
        val list = rootNodeInfo!!.findAccessibilityNodeInfosByViewId(id)
        return list ?: emptyList()
    }

    /**
     * 通过文本查找节点
     * @param rootNodeInfo  根节点
     * @param text          文本
     */
    fun findNodeByText(
            rootNodeInfo: AccessibilityNodeInfo?,
            text: String
    ): AccessibilityNodeInfo? {
        Log.d(TAG, "[findNodeById] text = $text")
        if ((rootNodeInfo == null) or TextUtils.isEmpty(text)) {
            return null
        }

        val nodeList = rootNodeInfo!!.findAccessibilityNodeInfosByText(text)
        return nodeList?.distinct()?.firstOrNull()
    }

    fun findNodeListByText(
            rootNodeInfo: AccessibilityNodeInfo?,
            text: String
    ): List<AccessibilityNodeInfo> {
        Log.d(TAG, "[findNodeById] text = $text")
        if ((rootNodeInfo == null) or TextUtils.isEmpty(text)) {
            return emptyList()
        }

        val nodeList = rootNodeInfo!!.findAccessibilityNodeInfosByText(text)
        return nodeList ?: emptyList()
    }

    fun findNode(
            rootNodeInfo: AccessibilityNodeInfo?,
            id: String,
            text: String,
            contentDescription: String = ""
    ): AccessibilityNodeInfo? {
        Log.d(TAG, "[findNode] id = $id text = $text contentDescription = $contentDescription")
        var findNode: AccessibilityNodeInfo? = null
        val idNodeList = rootNodeInfo?.findAccessibilityNodeInfosByViewId(id)
        // 先通过id进行查找， 在进行文本匹配或者内容匹配
        if (idNodeList != null) {
            var find = false
            for (node in idNodeList) {
                try {
                    if (!find and ((text == node.text) or (node.text.contains(text)))) {
                        findNode = node
                        find = true
                    } else if (!find and (node.contentDescription == contentDescription)
                            or (node.contentDescription?.contains(contentDescription) == true)) {
                        findNode = node
                        find = true
                    } else {
                        node.recycle()
                    }
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "当前组件 text == null")
                }

            }
        }
        return findNode
    }

    /**
     * 通过边界查找节点
     *
     */
    fun findByBound(
            rootNodeInfo: AccessibilityNodeInfo?,
            destBound: Rect,
            deep: Int = 0,
            maxDeep: Int = 50
    ): AccessibilityNodeInfo? {
        if (rootNodeInfo == null) {
            return null
        }
        if (deep > maxDeep) {
            return null
        }
        val deepParam = deep + 1
        for (index in 0..rootNodeInfo.childCount) {
            val child = rootNodeInfo.getChild(index)
            if (child != null) {
                val childRect = Rect()
                child.getBoundsInScreen(childRect)
                if (childRect.contains(destBound)) {
                    val findByBound = findByBound(child, destBound, deepParam)
                    if (findByBound != null) {
                        return findByBound
                    }
                }
            }
        }
        return null
    }


    fun findClickableView(childNode: AccessibilityNodeInfo): AccessibilityNodeInfo {
        Log.d(TAG, "[findClickableView] child-clickable = ${childNode.isClickable}")
        return if (!childNode.isClickable) {
            findClickableView(childNode.parent)
        } else {
            childNode
        }
    }

    fun findWebViewNode(rootNodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        return findWebViewNode(rootNodeInfo, "android.webkit.WebView")
    }

    fun findWebViewNode(rootNodeInfo: AccessibilityNodeInfo?, webViewClassPattern: String): AccessibilityNodeInfo? {
        if (rootNodeInfo == null) {
            return null
        }
        var webViewNode: AccessibilityNodeInfo? = null
        for (i in 0 until rootNodeInfo.childCount) {
            val child = rootNodeInfo.getChild(i)
            if(child == null){
                continue
            }

            if (child.className.contains(webViewClassPattern, ignoreCase = true)) {
                webViewNode = child
                Log.d(TAG, "找到webView")
                break
            }
            if (child.childCount > 0) {
                webViewNode = findWebViewNode(child)
            }
        }
        return webViewNode
    }

    fun findWebViewContent(webViewNodeInfo: AccessibilityNodeInfo?, contentDescription: String): AccessibilityNodeInfo? {
        if (webViewNodeInfo == null)
            return null
        var findNode: AccessibilityNodeInfo? = null
        for (i in 0 until webViewNodeInfo.childCount) {
            val child = webViewNodeInfo.getChild(i)
            try {
                val content = child.text?.toString() ?: ""
                if (!TextUtils.isEmpty(content) and (content.contains(contentDescription))) {
                    findNode = child
                    break
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            if (findNode != null) {
                return findNode
            }
            if (child.childCount > 0) {
                findNode = findWebViewContent(child, contentDescription)
            }
        }
        return findNode
    }

    fun findWebViewContentList(
            webViewNodeInfo: AccessibilityNodeInfo?,
            contentDescription: String,
            list: MutableList<AccessibilityNodeInfo> = ArrayList()
    ) {
        if (webViewNodeInfo == null)
            return
        for (i in 0 until webViewNodeInfo.childCount) {
            val child = webViewNodeInfo.getChild(i)
            try {
                val content = child.contentDescription?.toString() ?: ""
                if (!TextUtils.isEmpty(content) and content.contains(contentDescription)) {
                    list.add(child)
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            if (child.childCount > 0) {
                findWebViewContentList(child, contentDescription, list)
            }
        }
    }

    fun collectViewInfo(viewNodeInfo: AccessibilityNodeInfo?, viewInfoList: MutableList<ViewInfo>){
        if (viewNodeInfo == null){
            return
        }
        val screenRect = Rect()
        viewNodeInfo.getBoundsInScreen(screenRect)
        viewNodeInfo.refresh()
        viewInfoList.add(ViewInfo(0, viewNodeInfo.packageName.toString(), "", viewNodeInfo.viewIdResourceName, screenRect))
        for (i in 0 until viewNodeInfo.childCount) {
            val child = viewNodeInfo.getChild(i)
            collectViewInfo(child, viewInfoList)
        }

    }

    fun getRandomPath(rect: Rect): Path {
        Log.d(TAG, "[getRandomPath] rect = $rect")
        val path = Path()
        val point = Point(getRandomPoint(rect.left, rect.right), getRandomPoint(rect.top, rect.bottom))
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        Log.d(TAG, rect.toString())
        Log.d(TAG, point.toString())
        return path
    }

    private fun getRandomPoint(start: Int, end: Int): Int {
        return (Math.random() * (end - start) + start).toInt()
    }
}