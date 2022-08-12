package com.think.core.exception;

import java.util.Objects;

/**
 * @author zhangzhipeng
 * @date 2022/8/12
 */
public class IgnoreExceptionBean {

    final String exceptionName;

    final String exceptionMsg;

    final boolean ignoreMsg;

    public IgnoreExceptionBean(String exceptionName, String exceptionMsg) {
        this.exceptionName = exceptionName;
        this.exceptionMsg = exceptionMsg;
        this.ignoreMsg = false;
    }

    public IgnoreExceptionBean(String exceptionName, String exceptionMsg, boolean ignoreMsg) {
        this.exceptionName = exceptionName;
        this.exceptionMsg = exceptionMsg;
        this.ignoreMsg = ignoreMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IgnoreExceptionBean that = (IgnoreExceptionBean) o;
        return ignoreMsg == that.ignoreMsg && exceptionName.equals(that.exceptionName) && exceptionMsg.equals(that.exceptionMsg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exceptionName, exceptionMsg, ignoreMsg);
    }
}
