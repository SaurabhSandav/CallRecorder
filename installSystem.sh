#!/bin/bash

# CHANGE THESE FOR YOUR APP
app_package="com.redridgeapps.callrecorder"
dir_app_name="CallRecorder"
MAIN_ACTIVITY="MainActivity"

ADB="/opt/Android/Sdk/platform-tools/adb" # how you execute adb
ADB_SH="$ADB shell su -c"

path_sysapp="/system/priv-app" # assuming the app is priviledged
apk_host="./app/build/outputs/apk/debug/app-debug.apk"
apk_name=$dir_app_name".apk"
apk_target_dir="$path_sysapp/$dir_app_name"
apk_target_sys="$apk_target_dir/$apk_name"
sdcard_tmp="/sdcard/tmp"

# Delete previous APK
rm -f $apk_host

# Compile the APK: you can adapt this for production build, flavors, etc.
./gradlew assembleDebug || exit -1 # exit on failure

# Install APK: using adb root
$ADB_SH "mount -o rw,remount /"
$ADB_SH "mkdir -p $sdcard_tmp" 2> /dev/null
$ADB_SH "mkdir -p $apk_target_dir" 2> /dev/null
$ADB push $apk_host /sdcard/tmp/$apk_name 2> /dev/null
$ADB_SH "mv $sdcard_tmp/$apk_name $apk_target_sys"
$ADB_SH "rmdir $sdcard_tmp" 2> /dev/null

# Give permissions
$ADB_SH "chmod 755 $apk_target_dir"
$ADB_SH "chmod 644 $apk_target_sys"

########################################
# Push permissions file
########################################

permissions_file_name="privapp-permissions-com.redridgeapps.callrecorder.xml"
permissions_file="./$permissions_file_name"
permissions_target_dir="/system/etc/permissions"

#$ADB_SH "rm /etc/permissions/privapp-permissions-redridgeapps.xml"
$ADB push $permissions_file /sdcard/tmp/$permissions_file_name 2> /dev/null
$ADB_SH "mv $sdcard_tmp/$permissions_file_name $permissions_target_dir"
$ADB_SH "chmod 644 $permissions_target_dir/$permissions_file_name"

########################################

#Unmount system
$ADB_SH "mount -o remount,ro /"

# Stop the app
$ADB shell "am force-stop $app_package"

# Re execute the app
$ADB shell "am start -n \"$app_package/$app_package.$MAIN_ACTIVITY\" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"
