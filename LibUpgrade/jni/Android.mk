LOCAL_PATH := $(call my-dir)
#定义子目录下面的makefile文件列表
SUB_MK_FILES := $(call all-subdir-makefiles)

#----------------------------------------------------
#将bzip2编译成静态库
BZIP2_PATH :=$(LOCAL_PATH)/bzip2
BZIP2_C_FILE_LIST :=$(wildcard $(BZIP2_PATH)/*.c)
include $(CLEAR_VARS)
LOCAL_MODULE := bzip2
LOCAL_C_INCLUDES := BZIP2_PATH
LOCAL_SRC_FILES :=$(BZIP2_C_FILE_LIST:$(LOCAL_PATH)/%=%)
include $(BUILD_STATIC_LIBRARY)
#----------------------------------------------------

#----------------------------------------------------
#将bsdiff编译成静态库
BSDIFF_PATH :=$(LOCAL_PATH)/bsdiff
BSDIFF_C_FILE_LIST :=$(wildcard $(BSDIFF_PATH)/*.c)
include $(CLEAR_VARS)
LOCAL_MODULE := bsdiff
LOCAL_STATIC_LIBRARIES += bzip2
LOCAL_C_INCLUDES := BSDIFF_PATH
LOCAL_SRC_FILES :=$(BSDIFF_C_FILE_LIST:$(LOCAL_PATH)/%=%)
include $(BUILD_STATIC_LIBRARY)
#----------------------------------------------------

#编译子目录下的make file文件
include $(SUB_MK_FILES)
