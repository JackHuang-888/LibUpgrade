> 转载请标明出处
> 本文出自[HCY的微博](http://my.csdn.net/Huang_Cai_Yuan)

###一、概述
软件更新功能可以说是APP的标配。以前实现这个功能的时候，自己一行一行代码重复撸，浪费时间。所以我决定实现一个万能的可复用的更新库。让它支持增量更新、全量更新、静默安装、普通方式安装、可以自定义UI。下面就来介绍一下我实现这个库的主要技术点：增量更新、静默安装及如何封装。
###二、软件增量更新处理流程

#### (1)服务端处理流程

 1.验证请求的合法性。
 2.如果请求不合法(比如请求是模拟的，非客户端发出的)，则拒绝服务。
 3.如果请求合法，获取versionCode等信息，根据versionCode判断软件是否更新。
 4.如果不需要更新，则返回对应信息。
 5.如果需要更新，获取与versionCode对应的客户端文件的MD5，判断该MD5值是否在历史版本文件的MD5列表中，如果在说明支持增量更新。
 6.如果不支持增量更新，则返回完整apk文件的下载链接。
 7.如果支持增量更新，判断对应的patch文件是否存在。
 8.如果对应的patch文件不存在，调用脚本程序生成对应的patch文件，并返回该patch文件的下载链接。
 9.如果对应的patch文件存在，则返回该patch文件的下载链接。
 
 
#### (2)客户端处理流程

 1.收集apk的基本信息，向服务端发送更新请求。
 2.如果没有更新，则做对应的提示操作。
 3.如果有更新，判断是否是增量更新还是全量更新。
 4.如果是全量更新，则下载对应的apk文件，展示相应的UI，安装apk即可。
 5.如果是增量更新，则下载对应的patch文件，展示相应的UI，然后提取客户端的apk文件到指定目录并与patch文件合并成一个新的apk文件，判断新合成的apk文件是否与从服务端获取的完整的apk文件MD5的值一致，若一致说明合成成功，安装新合成的apk文件即可，若不一致说明合成失败，进行安装失败的提示。
 
###三、增量更新的实现
通过上面的处理流程分析，我们发现实现增量更新的难点主要在于patch文件的生成、新apk文件的合成这两个部分。这里借助开源的bsdiff来实现这两部分的功能。
#### (1)下载二进制差分、合并工具
增量更新的实现用到第三方库[bsdiff](http://www.daemonology.net/bsdiff/)，该库依赖[bzip2](http://www.bzip.org/downloads.html)。
![bsdiff官网截图](http://img.blog.csdn.net/20161102140819136)
bsdiff目前支持Linux、Windows，同时也有Python版本的源码。
#### (2)服务端patch文件的生成
服务端可以根据需要，选择对应的版本进行patch文件的生成,比如Windows版本的生成方式如下:
![Windows 32位版本文件结构](http://img.blog.csdn.net/20161102150942804)

同时按住Shift+右键，选择“在此处打开命令窗口”，执行命令 bsdiff old.apk new.apk patch.patch即可生成patch包，至于脚本怎么执行这些命令，请读者自行发挥。

#### (3)客户端新apk的合成实现
点击(1)中图片所示的"here"链接，下载linux版本的源代码，同时下载bzip2的源代码，文件目录结构如下:
![bsdiff及bzip2的代码目录结构](http://img.blog.csdn.net/20161102153434485)
接着将bsdiff.c、bspatch.c文件中的main方法改成diff、patch
然后编写jni代码,调用bsdiff和bspatch的diff、patch方法
```
#include "jni_bsdiff.h"

#ifdef __cplusplus
extern "C" {
#endif

//定义方法宏，用于拼接方法名
#define JNI_METHOD(METHOD_NAME) \
  Java_com_cy_lib_upgrade_bsdiff_BsDiff_##METHOD_NAME

extern int diff(int argc, char *argv[]);
extern int patch(int argc, char *argv[]);

JNIEXPORT jint JNICALL JNI_METHOD(diff)(JNIEnv *env, jobject object,
                                        jstring old_path, jstring new_path, jstring patch_path) {
    int argc = 4;
    char *argv[argc];
    argv[0] = (char *) "bsdiff";
    argv[1] = (char *) (env)->GetStringUTFChars(old_path, 0);
    argv[2] = (char *) (env)->GetStringUTFChars(new_path, 0);
    argv[3] = (char *) (env)->GetStringUTFChars(patch_path, 0);
    bool isCrash = false;
    int ret;
    try {
        ret = diff(argc, argv);
    }
    catch (...) {
        isCrash = true;
    }
    (env)->ReleaseStringUTFChars(old_path, argv[1]);
    (env)->ReleaseStringUTFChars(new_path, argv[2]);
    (env)->ReleaseStringUTFChars(patch_path, argv[3]);
    return isCrash ? -1 : ret;
}

JNIEXPORT jint JNICALL JNI_METHOD(patch)(JNIEnv *env, jobject object,
                                         jstring old_path, jstring new_path, jstring patch_path) {
    int argc = 4;
    char *argv[argc];
    argv[0] = (char *) "bspatch";
    argv[1] = (char *) (env)->GetStringUTFChars(old_path, 0);
    argv[2] = (char *) (env)->GetStringUTFChars(new_path, 0);
    argv[3] = (char *) (env)->GetStringUTFChars(patch_path, 0);
    bool isCrash = false;
    int ret;
    try {
        ret = patch(argc, argv);
    }
    catch (...) {
        isCrash = true;
    }
    (env)->ReleaseStringUTFChars(old_path, argv[1]);
    (env)->ReleaseStringUTFChars(new_path, argv[2]);
    (env)->ReleaseStringUTFChars(patch_path, argv[3]);
    return isCrash ? -1 : ret;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    // Get jclass with env->FindClass.
    // Register methods with env->RegisterNatives.
    return JNI_VERSION_1_6;
}


#ifdef __cplusplus
}
#endif
```
接下来，在外层的Android.mk文件中编写makefile脚本(gradle里面编译jni我不熟，哈哈哈，还是makefile用着习惯),将bsdiff、bzip2编译成静态库，同时引入子目录的Android.mk文件。

```
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

```
在jni_bsdiff目录下面的Android.mk文件中编写生成我们要用的动态库的脚本如下

```
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
```
再接下来，在build.gradle里面编写编译脚本即可

```
    task ndkBuild(type: Exec, description: 'Compile JNI source via NDK') {
        def ndkDir = project.plugins.getPlugin('com.android.library').sdkHandler.ndkFolder
        print "ndkDir=" + ndkDir + "\n"
        commandLine "$ndkDir\\ndk-build.cmd",
                'NDK_PROJECT_PATH=build/intermediates/ndk',
                'NDK_LIBS_OUT=libs',
                'APP_BUILD_SCRIPT=jni/Android.mk',
                'NDK_APPLICATION_MK=jni/Application.mk'
    }

    tasks.withType(JavaCompile) {
        compileTask -> compileTask.dependsOn ndkBuild
    }
```
如果不出意外我们的libbsdiff_utils.so就可以生成了。然后我们编写java层的调用代码
```
public class BsDiff {

    static {
        try {
            System.loadLibrary("bsdiff_utils");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static native int diff(String oldPath, String newPath, String patchPath);

    public static native int patch(String oldPath, String newPath, String patchPath);
}
```
新apk文件的合成我们要用到的是patch方法，它的参数oldPath表示当前apk的文件路径，newPath表示合成后的apk文件路径，patchPath则为下载的增量包的路径。oldPath的取值，比较稳妥的做法是把当前安装的apk文件拷贝到一个可读可写的目录，防止bspatch对已安装的apk文件产生破坏。附上获取当前apk文件的路径的代码：

```
    /**
     * 获取已安装apk的路径
     *
     * @param context apk的上下文
     * @return apk文件路径
     */
    public static String getApkPath(Context context) {
        if (context != null) {
            ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
            return applicationInfo.sourceDir;
        }
        return "";
    }
```
###四、静默安装实现
静默安装这里采用pm install命令实现，因此应用需要获取到Root权限才能执行成功。

```
/**
     * 静默安装
     *
     * @param apkFilePath apk文件路径
     * @return true表示安装成功，否则返回false
     */
    public static boolean silentInstall(String apkFilePath) {
        boolean isInstallOk = false;
        if (isSupportSilentInstall()) {
            DataOutputStream dataOutputStream = null;
            BufferedReader bufferedReader = null;
            try {
                Process process = Runtime.getRuntime().exec("su");
                dataOutputStream = new DataOutputStream(process.getOutputStream());
                String command = "pm install -r " + apkFilePath + "\n";
                dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                dataOutputStream.flush();
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.flush();
                process.waitFor();
                bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder msg = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    msg.append(line);
                }
                if (msg.toString().contains("Success")) {
                    isInstallOk = true;
                }
            } catch (Exception e) {
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return isInstallOk;
    }
```

###五、封装

为了打造一个可复用的软件更新库，这里根据软件更新的流程抽象了五个接口，流程与接口的对应关系如下：
 1. 更新检测（UpdateChecker）
 2. 更新检测后的UI提示（UpdateCheckUIHandler）
 3. 更新文件下载（Downloader）
 4. 文件下载时的UI提示（DownloadUIHandler）
 5. 安装文件（AppInstaller）
 如果使用者发现哪一步不符合自己的需求，只要实现这个步骤的接口并注入到全局配置中即可，从而实现“万能”的软件更新功能。
 具体实现，请参照源码：[https://github.com/Money888/LibUpgrade.git](https://github.com/Money888/LibUpgrade.git)

####(1)更新库的使用
第一步，在Application.onCreate方法中进行初始化
```
    @Override
    public void onCreate() {
        super.onCreate();
        LibUpgradeInitializer.init(this);
    }
```
第二步，配置更新库功能

```
      final UpdaterConfiguration config = new UpdaterConfiguration();
        config.updateChecker(new UpdateChecker() {
            @Override
            public void check(UpdateCheckCallback callback) {
                //此处模拟更新信息获取,信息获取后需要将UpdateInfo设置到配置信息中，然后要调用相应的回调方法才能使整个流程完整执行
                UpdateInfo updateInfo = new UpdateInfo();
                updateInfo.setVersionCode(2);
                updateInfo.setVersionName("v1.2");
                updateInfo.setUpdateTime("2016/10/28");
                updateInfo.setUpdateSize(1024);
                updateInfo.setUpdateInfo("更新日志:\n1.新增万能更新库，实现更新功能只要几行代码。");
                //使用全量更新信息
                updateInfo.setUpdateType(UpdateInfo.UpdateType.TOTAL_UPDATE);
                UpdateInfo.TotalUpdateInfo totalUpdateInfo = new UpdateInfo.TotalUpdateInfo();
                totalUpdateInfo.setApkUrl("http://wap.apk.anzhi.com/data2/apk/201609/05/f06abcb0e2cba4c8ce2301c4b437a492_72932500.ap");
                updateInfo.setTotalUpdateInfo(totalUpdateInfo);
                if (updateInfo != null) {
                    //设置更新信息，这样各模块就可以通过config.getUpdateInfo()共享这个数据了,注意这个方法一定要调用且要在UpdateCheckCallback.onCheckSuccess之前调用
                    config.updateInfo(updateInfo);
                    callback.onCheckSuccess();
                } else {
                    callback.onCheckFail("");
                }
            }
        });
        Updater.getInstance().init(config);
```
 第三步，启用更新检查功能
```
 //此处的Context默认必须为Activity
 Updater.getInstance().check(this);
```

####(2)自定义功能扩展使用

1.增量更新

```
      config.updateChecker(new UpdateChecker() {
            @Override
            public void check(UpdateCheckCallback callback) {
                UpdateInfo updateInfo = new UpdateInfo();
                //....
                //设置增量更新信息,设置完整的apk的MD5及增量包下载地址(此处的增量包需要由bsdiff生成)
                updateInfo.setUpdateType(UpdateInfo.UpdateType.INCREMENTAL_UPDATE);
                UpdateInfo.IncrementalUpdateInfo incrementalUpdateInfo = new UpdateInfo.IncrementalUpdateInfo();
                incrementalUpdateInfo.setFullApkMD5("e7eec01baac70f8a3688570439b9b467");
                incrementalUpdateInfo.setPatchUrl("http://bmob-cdn-4990.b0.upaiyun.com/2016/10/28/aa0bc17f40a91b0b80915a49b40c0174.patch");
                updateInfo.setIncrementalUpdateInfo(incrementalUpdateInfo);
                //.......
            }
        });
```


2.全量更新


```
        config.updateChecker(new UpdateChecker() {
            @Override
            public void check(UpdateCheckCallback callback) {
                UpdateInfo updateInfo = new UpdateInfo();
                //....
                //设置全量更新信息
                updateInfo.setUpdateType(UpdateInfo.UpdateType.TOTAL_UPDATE);
                UpdateInfo.TotalUpdateInfo totalUpdateInfo = new UpdateInfo.TotalUpdateInfo();
                totalUpdateInfo.setApkUrl("http://wap.apk.anzhi.com/data2/apk/201609/05/f06abcb0e2cba4c8ce2301c4b437a492_72932500.apk");
                updateInfo.setTotalUpdateInfo(totalUpdateInfo);
                //.......
            }
        });
```


3.强制更新

```
        config.updateChecker(new UpdateChecker() {
            @Override
            public void check(UpdateCheckCallback callback) {
                UpdateInfo updateInfo = new UpdateInfo();
                //....
                //设置强制更新
                updateInfo.setIsForceInstall(true);
                //.......
            }
        });
```
4.普通安装模式

```
        config.updateChecker(new UpdateChecker() {
            @Override
            public void check(UpdateCheckCallback callback) {
                UpdateInfo updateInfo = new UpdateInfo();
                //....
                //设置普通模式的安装
                updateInfo.setInstallType(UpdateInfo.InstallType.NOTIFY_INSTALL);
                //.......
            }
        });
```

5.静默安装模式

```
      config.updateChecker(new UpdateChecker() {
            @Override
            public void check(UpdateCheckCallback callback) {
                UpdateInfo updateInfo = new UpdateInfo();
                //....
                //设置静默安装模式,设置此模式前必须确保手机对本应用授予了Root权限
                updateInfo.setInstallType(UpdateInfo.InstallType.SILENT_INSTALL);
                //.......
            }
        });
```

6.修改更新时的提示UI

```
        //处理UI时，在必要的时机需要调用config.getDownloader()的相关方法，才能保证流程正确执行
        config.updateUIHandler(new UpdateCheckUIHandler() {
            @Override
            public void setContext(Context context) {
                //此处的context为Updater.getInstance().check(Context context)方法传入的context
            }

            @Override
            public void hasUpdate() {
                //有更新时的UI展示
            }

            @Override
            public void noUpdate() {
                //没有更新时的UI展示
            }

            @Override
            public void checkError(String error) {
                //更新检查失败时的UI展示
            }
        });
```

7.修改文件下载时的UI

```
        config.downloadUIHandler(new DownloadUIHandler() {
            @Override
            public void setContext(Context context) {
                //此处的context为Updater.getInstance().check(Context context)方法传入的context
            }

            @Override
            public void downloadStart() {
                //开始下载时的UI展示
            }

            @Override
            public void downloadProgress(int progress, int total) {
               //下载进度的展示
            }

            @Override
            public void downloadComplete(String path) {
              //下载完成时的处理，此处应通过config.getUpdateInfo()获取更信息，然后再通过相应的安装器进行安装
            }

            @Override
            public void downloadError(String error) {
               //下载失败时的UI提示
            }

            @Override
            public void downloadCancel() {
              //下载取消时的UI提示
            }
        });
```

