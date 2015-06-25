package com.yoavst.quickapps.toggles


public data class ToggleItem(public val id: Int, public val name: String) {
    companion object {
        public val Wifi: Int = 0
        public val Data: Int = 1
        public val Brightness: Int = 2
        public val Sound: Int = 3
        public val Bluetooth: Int = 4
        public val Hotspot: Int = 5
    }
}