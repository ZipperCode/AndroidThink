package com.think.audioplayer

/**
 * 加载音频文件的方式
 */
enum class LoadAudioType {
    /**
     * 从raw资源目录下加载
     */
    CONTEXT,

    /**
     * 从文件中加载
     */
    PATH,

    /**
     * 文件描述符中加载
     */
    FILE_DESCRIPTOR,

    /**
     * Asset目录读取的文件描述符
     */
    ASSET_FILE_DESCRIPTOR
}