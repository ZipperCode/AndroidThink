package com.think.gradle_plugin.ext


class FixExtension{
    /**
     * 生成的class 文件根目录，相对于Cls中的根路径
     */
    def classRootDir = "${Constants.FIX_ROOT_DIR}/classes"
    /**
     * 存放版本的根目录 如 fix/classes/1.0，不适用build下的相对目录
     */
    def versionClassRootDir = "${Constants.FIX_ROOT_DIR}/classes"
    /**
     * 存放合并版本的Class文件
     */
    def versionMargeRootDir = "${Constants.FIX_ROOT_DIR}/marge"
    /**
     * 当前的包版本号
     */
    def versionCode = "1.0"

    /**
     * 合并用到的旧版本
     */
    def margeOldVersion = "1.0"
    /**
     * 合并用到的新版本
     */
    def margeNewVersion = "1.0"

    boolean useFixTransform = true

    boolean  useInjectClassTransform = false;
    /**
     * 版本相同是否覆盖
     */
    boolean compareVersionCover = false
    /**
     * dx工具环境变量
     */
    def dxPath = ""

}