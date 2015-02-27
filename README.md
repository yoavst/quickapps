# Quick Circle Apps

**Quick Circle Apps** is an Android application made by Yoav Sternberg.
The application provide modules for the G3 Quick Circle Case.

[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=com.yoavst.quickapps)

## How it works?
The app uses LG Quick Circle SDK and QSlide SDK.
The app was built using Kotlin, A Statically typed programming language targeting the JVM and JavaScript, which was developed by JetBrains.

* Torch - Enable/Disable camera flash.
* Music -Register `NotificationListenerService` that implements `RemoteController.OnClientUpdateListener`.
* Notifications - Register another `NotificationListenerService`.
* Calendar - Reading events data from `CalendarContract.Events`.
* Toggles - Each toggles use its permissions to change the state.
* Stopwatch - Uses `TimerTask` that run every 10 milliseconds to update the clock.
* Calculator - Evaluate the math string using EvalEx library.
* News - Use Feedly Cloud Api to receive the newest 20 articles from the user feed.
* Compass - Use Compass sensor.
* Dialer - call `ACTION_CALL` intent. The on-call screen is LG's one.
* Magic 8 ball - Magic...
* Recorder - use `MediaRecorder` to recorded audio.

License
-------

    Copyright 2014 Yoav Sternberg

    Quick Circle Apps is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Quick Circle Apps is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Quick Circle Apps. If not, see <http://www.gnu.org/licenses/>.

---