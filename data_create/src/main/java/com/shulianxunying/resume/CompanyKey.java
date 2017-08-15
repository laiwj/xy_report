package com.shulianxunying.resume;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/5/9 15:18.
 */
public class CompanyKey implements Serializable {
    String keyword;
    HashSet<String> industry = new HashSet<>();
    HashSet<String> keys = new HashSet<>();

    public CompanyKey() {
    }

    public CompanyKey(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public HashSet<String> getIndustry() {
        return industry;
    }

    public void setIndustry(HashSet<String> industry) {
        this.industry = industry;
    }

    public HashSet<String> getKeys() {
        return keys;
    }

    public void setKeys(HashSet<String> keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "CompanyKey{" +
                "keyword='" + keyword + '\'' +
                ", industry=" + industry +
                ", keys=" + keys +
                '}';
    }
}
