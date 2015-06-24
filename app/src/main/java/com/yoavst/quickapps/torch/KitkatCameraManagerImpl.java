package com.yoavst.quickapps.torch;

import android.hardware.Camera;

/**
 * Implementation for camera manager using deprecated Kitkat and below APIs.
 */
public class KitkatCameraManagerImpl extends CameraManager.CameraManagerImpl {
	private Camera mCamera;
	private Camera.Parameters mParameters;

	@Override
	public void init() {
		if (mCamera == null) {
			mCamera = Camera.open();
			mParameters = mCamera.getParameters();
		}
	}

	@Override
	public void destroy() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
			System.gc();
		}
	}

	@Override
	public boolean toggleTorch() {
		init();
		boolean flashOnBefore = mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH);
		if (flashOnBefore) {
			disableTorch();
			return false;
		} else {
			torch();
			return true;
		}
	}

	@Override
	public void torch() {
		if (mCamera != null && mParameters != null) {
			if (!mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
				mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(mParameters);
				mCamera.startPreview();
				setTorchOn(true);
			}
		}
	}

	@Override
	public void disableTorch() {
		if (mCamera != null && mParameters != null) {
			mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(mParameters);
			mCamera.stopPreview();
            setTorchOn(false);
		}
	}
}
