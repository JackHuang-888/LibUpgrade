package com.cy.lib.upgrade.model;

/**
 * Caiyuan Huang
 * <p>2016/10/27</p>
 * <p>更新信息</p>
 */
public class UpdateInfo {
    private boolean isForceInstall;//是否是强制更新
    private int versionCode;//版本号
    private String versionName;//版本名
    private String updateInfo;//更新信息
    private int updateSize;//更新大小
    private String updateTime;//更新时间
    private UpdateType updateType = UpdateType.TOTAL_UPDATE;//更新类型
    private TotalUpdateInfo totalUpdateInfo;//全量更新信息
    private IncrementalUpdateInfo incrementalUpdateInfo;//增量更新信息
    private InstallType installType = InstallType.NOTIFY_INSTALL;//安装方式


    /**
     * 更新方式(全量更新,增量更新)
     */
    public enum UpdateType {
        TOTAL_UPDATE, INCREMENTAL_UPDATE
    }

    /**
     * 安装方式(静默安,提示安装)
     */
    public enum InstallType {
        SILENT_INSTALL, NOTIFY_INSTALL
    }

    /**
     * 全量更新信息
     */
    public static class TotalUpdateInfo {
        private String apkUrl;//apk下载地址

        public String getApkUrl() {
            return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }
    }

    /**
     * 增量更新信息
     */
    public static class IncrementalUpdateInfo {
        private String fullApkMD5;//完整apk的MD5值
        private String patchUrl;//增量包的下载地址

        public String getFullApkMD5() {
            return fullApkMD5;
        }

        public void setFullApkMD5(String fullApkMD5) {
            this.fullApkMD5 = fullApkMD5;
        }

        public String getPatchUrl() {
            return patchUrl;
        }

        public void setPatchUrl(String patchUrl) {
            this.patchUrl = patchUrl;
        }
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public TotalUpdateInfo getTotalUpdateInfo() {
        return totalUpdateInfo;
    }

    public void setTotalUpdateInfo(TotalUpdateInfo totalUpdateInfo) {
        this.totalUpdateInfo = totalUpdateInfo;
    }

    public IncrementalUpdateInfo getIncrementalUpdateInfo() {
        return incrementalUpdateInfo;
    }

    public void setIncrementalUpdateInfo(IncrementalUpdateInfo incrementalUpdateInfo) {
        this.incrementalUpdateInfo = incrementalUpdateInfo;
    }

    public InstallType getInstallType() {
        return installType;
    }

    public void setInstallType(InstallType installType) {
        this.installType = installType;
    }

    public boolean isForceInstall() {
        return isForceInstall;
    }

    public void setIsForceInstall(boolean isForceInstall) {
        this.isForceInstall = isForceInstall;
    }

    public int getUpdateSize() {
        return updateSize;
    }

    public void setUpdateSize(int updateSize) {
        this.updateSize = updateSize;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
