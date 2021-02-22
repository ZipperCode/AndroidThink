package com.think.demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 1、获取CameraManager对象
 * 2、获取相机信息
 * 3、初始化ImageReader ImageReader.newInstance()
 * 4、打开相机设备    cameraManager.openCamera
 * 5、创建Capture会话    cameraDevice.createCaptureSession()
 * 6、创建CaptureRequest cameraDevice.createCaptureRequest
 * 7、预览     captureSession.setRepeatingRequest
 * 8、拍照 capture.capture
 *
 */
class Camera2Activity : AppCompatActivity() {

    companion object {
        private const val TAG = "Camera2Activity"
        private val PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQ_CODE = 100
    }

    private var textureView: TextureView? = null

    private var cameraManager: CameraManager? = null

    // 相机设备类
    private var cameraDevice: CameraDevice? = null;

    // 相机信息
    private var characteristicsList: MutableList<CameraCharacteristics> = ArrayList();

    // 相机捕捉图像的设置请求
    private var captureRequest: CaptureRequest? = null;

    // 预览
    private var imageReader: ImageReader? = null;

    // 照相
    private var pictureImageReader: ImageReader? = null

    private var previewBuilder: CaptureRequest.Builder? = null
    private var pictureBuilder: CaptureRequest.Builder? = null

    // 预览的图片流
//    private var imageStreamReader: ImageReader? = null;
    // 相机打开后回调相机session
    private var cameraCaptureSession: CameraCaptureSession? = null;

    private var handler: Handler? = null

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            // Texture 可用时回调
            initCamera()
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            // Texture 销毁时回调
            return false
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            // Texture 大小改变时回调
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            // Texture 更新时回调
        }
    }

    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        Log.d(TAG, "onImageAvailableListener > $it")
    }

    private val onPictureImageAvailableListener = ImageReader.OnImageAvailableListener {
        Log.d(TAG, "onPictureImageAvailableListener > $it")
    }

    // openCamera 的回调函数 - 设备信息回调
    private val deviceStateCallback = object : CameraDevice.StateCallback() {

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "Camera onDisconnect")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.d(TAG, "Camera open error")
        }

        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "Camera onOpened")
            // 摄像头已经打开
            cameraDevice = camera;
            startPreview()
        }
    }

    /**
     * 摄像头管理回调，处理预览和拍照
     */
    private val sessionPreviewStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.d(TAG, "sessionPreviewStateCallback > onConfigureFailed")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            Log.d(TAG, "sessionPreviewStateCallback > onConfigured")
            cameraCaptureSession = session
            // 设置自动对焦
            previewBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            // 打开闪光灯
            previewBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            if (previewBuilder != null) {
                // 显示预览
                session.setRepeatingRequest(previewBuilder!!.build(), sessionCaptureCallback, handler)
            }
        }
    }


    private val sessionCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
//            cameraCaptureSession = session
            Log.d(TAG, "sessionCaptureCallback > onCaptureCompleted session: $session, request: $request, result: $result")
        }

        override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) {
            super.onCaptureProgressed(session, request, partialResult)
            Log.d(TAG, "sessionCaptureCallback > onCaptureProgressed session: $session, request: $request, partialResult: $partialResult")

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)

        textureView = findViewById(R.id.textureView);

        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        // 设置Texture 状态监听
        textureView!!.surfaceTextureListener = surfaceTextureListener;

        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_REQ_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQ_CODE)
            return
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
    }


    private fun initCamera() {
        // 获取相机服务
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
        // 查找相机
        for (id in cameraManager!!.cameraIdList) {
            val characteristics = cameraManager!!.getCameraCharacteristics(id);
            characteristicsList.add(characteristics);
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                continue
            }

            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            map.getOutputSizes(SurfaceTexture::class.java)
        }

        openCamera()

    }


    @SuppressLint("MissingPermission")
    private fun openCamera() {
        // 初始化ImageReader - 预览图
        imageReader = ImageReader.newInstance(textureView!!.width, textureView!!.height, ImageFormat.JPEG, 7);

        imageReader!!.setOnImageAvailableListener(onImageAvailableListener, handler)
        // 照片
        pictureImageReader = ImageReader.newInstance(textureView!!.width, textureView!!.height, ImageFormat.YUV_420_888, 2)
        pictureImageReader!!.setOnImageAvailableListener(onPictureImageAvailableListener, handler)

        cameraManager?.openCamera(CameraCharacteristics.LENS_FACING_FRONT.toString(), deviceStateCallback, handler)
    }

    private fun closeCamera() {
        cameraDevice?.close()
    }

    /**
     * 开启摄像头预览
     */
    private fun startPreview() {
        if (imageReader?.surface == null) {
            return
        }
        // 创建建造器
        previewBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        createCaptureSession()
    }


    private fun takePicture() {
        if (cameraDevice == null) {
            return
        }
        pictureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        // 讲ImageReader的surface作为目标
        pictureBuilder?.apply {
            addTarget(imageReader!!.surface)
            // 自动对焦
            set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            // 打开闪光灯
            set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            // 获取手机方向
            val rotation = windowManager.defaultDisplay.rotation
//            set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation)+rORIENTATIONS)
        }
        if (pictureBuilder != null) {
            cameraCaptureSession?.capture(pictureBuilder!!.build(), null, handler)
        }


    }

    private fun createCaptureSession() {
        Log.d(TAG, "createCaptureSession")
        closeCaptureSession()

        val viewSurface = Surface(textureView!!.surfaceTexture)
        // 添加预览的surface
        previewBuilder?.apply {
            addTarget(viewSurface)
        }
        // 创建captureSession
        cameraDevice?.createCaptureSession(arrayListOf(viewSurface, imageReader?.surface), sessionPreviewStateCallback, handler)
    }

    private fun closeCaptureSession() {
        cameraCaptureSession?.close()
        cameraCaptureSession = null
    }

    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size? {
        val list = ArrayList<Size>();
        val screenSize = Rect(0, width, 0, height);

        for (size in sizeMap) {
            val rect = Rect().apply {
                this.left = 0
                this.right = size.width
                this.top = 0
                this.bottom = size.height
            }
            if (screenSize.contains(rect)) {
                list.add(size);
            }
        }

        return if (list.isEmpty()) Size(width, height) else list.maxBy { it.width * it.height }
    }

    private fun hasCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }
}