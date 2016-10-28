LOCAL_PATH := $(call my-dir)
#----------------------------------------------------
#将bsdiff包装编译成动态库
JNI_BSDIFF_PATH :=$(LOCAL_PATH)
JNI_BSDIFF_CPP_FILE_LIST :=$(wildcard $(JNI_BSDIFF_PATH)/*.cpp)
include $(CLEAR_VARS)
LOCAL_MODULE := bsdiff_utils
LOCAL_C_INCLUDES := JNI_BSDIFF_PATH

LOCAL_SRC_FILES :=$(JNI_BSDIFF_CPP_FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_STATIC_LIBRARIES += bsdiff
include $(BUILD_SHARED_LIBRARY)
#----------------------------------------------------