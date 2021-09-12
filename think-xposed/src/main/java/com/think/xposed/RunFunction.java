package com.think.xposed;

@FunctionalInterface
public interface RunFunction {
    void execute() throws Throwable;
}