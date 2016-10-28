# LibUpgrade
##一、概述
软件更新功能可以说是APP的标配。以前实现这个功能的时候，如果需求没有什么特别要求我会使用友盟更新库，有需求则自己一行一行代码重复撸。现在友盟更新挂了，所以我决定实现一个万能的更新库。主要功能如下：

 1. 支持增量更新、全量更新
 2. 支持静默安装、普通安装
 3. 支持UI定制
 4. 支持逻辑修改

##二、原理
更新功能主要分为下面几个步骤：

 1. 更新检测
 2. 更新检测后的UI提示
 3. 更新文件下载
 4. 文件下载时的UI提示
 5. 安装文件
 
把上面这5个功能分别独立出来，这样修改其中一个功能对其它功能就不会产生太大的影响，本库中上面对应步骤的接口定义如下：

 1. 更新检测（UpdateChecker）
 2. 更新检测后的UI提示（UpdateCheckUIHandler）
 3. 更新文件下载（Downloader）
 4. 文件下载时的UI提示（DownloadUIHandler）
 5. 安装文件（AppInstaller）
 
具体实现，请参照源码

##三、使用


###第一步，在Application.onCreate方法中进行初始化

```
    @Override
    public void onCreate() {
        super.onCreate();
        LibUpgradeInitializer.init(this);
    }
```

###第二步，配置更新库功能

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

###第三步，启用更新检查功能
```
 //此处的Context默认必须为Activity
 Updater.getInstance().check(this);
```

###第四步，自定义扩展功能


####1.增量更新

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


####2.全量更新


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


####3.强制更新

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
####4.普通安装模式

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

####5.静默安装模式

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

####6.修改更新时的提示UI

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

####7.修改文件下载时的UI

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
