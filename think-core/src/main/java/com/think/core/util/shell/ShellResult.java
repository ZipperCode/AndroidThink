package com.think.core.util.shell;

public class ShellResult {
    /**
     * 结果码
     **/
    public int result;
    /**
     * 成功信息
     **/
    public String successMsg;
    /**
     * 错误信息
     **/
    public String errorMsg;

    public ShellResult(int result, String successMsg, String errorMsg) {
        this.result = result;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }
}
