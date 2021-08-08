#!/usr/bin/env bash

adb logcat -c
adb logcat -v color,threadtime &

./gradlew connectedAndroidTest --stacktrace --info

