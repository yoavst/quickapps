package com.yoavst.quickapps.torch

import android.content.Context

/**
 * Helper class for enable and disable the torch. The class uses implementation based on the current os.
 */
public object CameraManager {
    /**
     * The implementation for the camera.
     */
    private var manager: CameraManagerImpl? = null

    /**
     * Initialize the current implementation for the camera.
     */
    public fun invoke(context: Context) {
        if (manager == null) {
            // FIXME use camera2 in lollipop
            manager = KitkatCameraManagerImpl()
        }
    }

    /**
     * Returns true if the torch is enabled.
     * @return if the torch is enabled
     */
    public fun isTorchOn(): Boolean {
        return manager!!.torchOn
    }

    /**
     * Destroy all the allocated resources by the camera implementation.
     */
    public fun destroy() {
        manager?.destroy()
    }

    /**
     * Toggles the torch.
     * @return if the torch is enabled
     */
    public fun toggleTorch(): Boolean {
        return manager != null && manager!!.toggleTorch()
    }

    /**
     * Enables the torch.
     */
    public fun torch() {
        manager?.torch()
    }

    /**
     * Disables the torch.
     */
    public fun disableTorch() {
        manager?.disableTorch()
    }

    /**
     * Initialize the camera for quicker turning on.
     */
    public fun init() {
        manager?.init()
    }

    /**
     * Implementation for camera manager.
     */
    public abstract class CameraManagerImpl {
        /**
         * Initialize the camera for quicker turning on.
         */
        public abstract fun init()

        /**
         * Destroy all the allocated resources by the camera implementation.
         */
        public abstract fun destroy()

        /**
         * Toggles the torch.
         * @return if the torch is enabled
         */
        public abstract fun toggleTorch(): Boolean

        /**
         * Enables the torch.
         */
        public abstract fun torch()

        /**
         * Disables the torch.
         */
        public abstract fun disableTorch()

        /**
         *  true if the torch is enabled
         */
        public var torchOn: Boolean = false
    }
}