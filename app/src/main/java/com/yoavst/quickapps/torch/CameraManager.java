package com.yoavst.quickapps.torch;

import android.content.Context;

/**
 * Created by Yoav.
 */
public class CameraManager {
	private static CameraManagerImpl manager;

	public static void init(Context context) {
		if (manager == null) {
			// FIXME use camera2 in lollipop
			manager = new KitkatCameraManagerImpl();
		}
	}

	public static boolean isTorchOn() {
		return CameraManagerImpl.torchOn;
	}

	public static void destroy() {
		if (manager != null) manager.destroy();
	}

	public static boolean toggleTorch() {
		return manager != null && manager.toggleTorch();
	}

	public static void torch() {
		if (manager != null) manager.torch();
	}

	public static void disableTorch() {
		if (manager != null) manager.disableTorch();
	}

	public static abstract class CameraManagerImpl {
		public static boolean torchOn = false;

		public abstract void init();

		public abstract void destroy();

		public abstract boolean toggleTorch();

		public abstract void torch();

		public abstract void disableTorch();
	}
}
