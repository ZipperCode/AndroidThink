package com.think.core.util.imageloader;

import com.think.core.util.security.MessageUtil;

import java.util.Objects;

public class Key {
    private String key;

    public Key(String url){
        this.key = MessageUtil.md5Crypt(url);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key1 = (Key) o;
        return key.equals(key1.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

}
