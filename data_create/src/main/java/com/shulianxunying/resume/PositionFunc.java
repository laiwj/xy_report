package com.shulianxunying.resume;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/3/28 11:20.
 */
public class PositionFunc implements Serializable, Comparable<PositionFunc> {
    private String func;
    private String second_level;
    private String position;
    private HashSet<String> keywords = new HashSet<>();
    private HashSet<String> exclude = new HashSet<>();

    public PositionFunc(String func, String second_level, String position) {
        this.func = func;
        this.second_level = second_level;
        this.position = position;
    }
    public PositionFunc() {
        this.func = "unknown";
        this.second_level = "unknown";
        this.position = "unknown";
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getSecond_level() {
        return second_level;
    }

    public void setSecond_level(String second_level) {
        this.second_level = second_level;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public HashSet<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(HashSet<String> keywords) {
        this.keywords = keywords;
    }

    public HashSet<String> getExclude() {
        return exclude;
    }

    public void setExclude(HashSet<String> exclude) {
        this.exclude = exclude;
    }

    @Override
    public int compareTo(PositionFunc o) {
        int size = this.keywords.size() + this.getExclude().size();
        int size1 = o.getKeywords().size() + o.getExclude().size();
        if (size > size1)
            return -1;
        else if (size == size1)
            return 0;
        else
            return 1;
    }

    public boolean positionHit(String position_name) {
        for (String key : getKeywords()) {
            if (!position_name.contains(key)) {
                return false;
            }
        }
        for (String key : getExclude()) {
            if (position_name.contains(key)) {
                return false;
            }
        }
        return true;
    }
}
