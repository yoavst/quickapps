package com.yoavst.quickapps.barcode.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.yoavst.quickapps.barcode.tools.camera.open.CameraManager;

import java.io.IOException;

/*
 * Copyright 2014 David Lázaro Esparcia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * QRCodeReaderView - Class which uses ZXING lib and let you easily integrate a QR decoder view.
 * Take some classes and made some modifications in the original ZXING - Barcode Scanner project.
 *
 * @author David Lzaro
 */
public class QRCodeReaderView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public interface OnQRCodeReadListener {

        void onQRCodeRead(Result result);

        void cameraNotFound();

        void QRCodeNotFoundOnCamImage();
    }

    private OnQRCodeReadListener mOnQRCodeReadListener;

    private static final String TAG = QRCodeReaderView.class.getName();

    private QRCodeReader mQRCodeReader;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;

    public QRCodeReaderView(Context context) {
        super(context);
        init();
    }

    public QRCodeReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnQRCodeReadListener(OnQRCodeReadListener onQRCodeReadListener) {
        mOnQRCodeReadListener = onQRCodeReadListener;
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    @SuppressWarnings("deprecation")
    private void init() {
        if (checkCameraHardware(getContext())) {
            mCameraManager = new CameraManager(getContext());

            mHolder = this.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  // Need to set this flag despite it's deprecated
        } else {
            Log.e(TAG, "Error: Camera not found");
            if (mOnQRCodeReadListener != null) {
                mOnQRCodeReadListener.cameraNotFound();
            }
        }
    }


    /****************************************************
     * SurfaceHolder.Callback,Camera.PreviewCallback
     ****************************************************/

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            // Indicate camera, our View dimensions
            mCameraManager.openDriver(holder, this.getWidth(), this.getHeight());
        } catch (IOException e) {
            Log.w(TAG, "Can not openDriver: " + e.getMessage());
            mCameraManager.closeDriver();
        }

        try {
            mQRCodeReader = new QRCodeReader();
            mCameraManager.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            mCameraManager.closeDriver();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mOnQRCodeReadListener = null;
        destroy();
    }

    public void destroy() {
        mCameraManager.getCamera().setPreviewCallback(null);
        mCameraManager.getCamera().stopPreview();
        mCameraManager.getCamera().release();
        mCameraManager.closeDriver();
    }

    private int frameCount = 0;
    private static final int SKIP_FRAMES = 3; // will detect 1/3 frames; minimum is 2

    // Called when camera take a frame
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        frameCount++;
        frameCount = frameCount % SKIP_FRAMES;
        if (frameCount != 0) {
            return;
        }
        PlanarYUVLuminanceSource source = mCameraManager.buildLuminanceSource(data, mPreviewWidth, mPreviewHeight);

        HybridBinarizer hybBin = new HybridBinarizer(source);
        BinaryBitmap bitmap = new BinaryBitmap(hybBin);

        try {
            Result result = mQRCodeReader.decode(bitmap);

            // Notify we found a QRCode
            if (mOnQRCodeReadListener != null) {
                // Transform resultPoints to View coordinates
                mOnQRCodeReadListener.onQRCodeRead(result);
            }

        } catch (ChecksumException | FormatException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            // Notify QR not found
            if (mOnQRCodeReadListener != null) {
                mOnQRCodeReadListener.QRCodeNotFoundOnCamImage();
            }
        } finally {
            mQRCodeReader.reset();
        }
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mPreviewWidth = mCameraManager.getPreviewSize().x;
        mPreviewHeight = mCameraManager.getPreviewSize().y;

        mCameraManager.stopPreview();
        mCameraManager.getCamera().setPreviewCallback(this);
        mCameraManager.getCamera().setDisplayOrientation(90); // Portrait mode

        mCameraManager.startPreview();
    }

    /**
     * Transform result to surfaceView coordinates
     * <p/>
     * This method is needed because coordinates are given in landscape camera coordinates.
     * Now is working but transform operations aren't very explained
     * <p/>
     * TODO re-write this method explaining each single value
     *
     * @return a new PointF array with transformed points
     */
    private PointF[] transformToViewCoordinates(ResultPoint[] resultPoints) {

        PointF[] transformedPoints = new PointF[resultPoints.length];
        int index = 0;
        float previewX = mCameraManager.getPreviewSize().x;
        float previewY = mCameraManager.getPreviewSize().y;
        float scaleX = this.getWidth() / previewY;
        float scaleY = this.getHeight() / previewX;

        for (ResultPoint point : resultPoints) {
            PointF tmppoint = new PointF((previewY - point.getY()) * scaleX, point.getX() * scaleY);
            transformedPoints[index] = tmppoint;
            index++;
        }
        return transformedPoints;

    }


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);


    }

}
