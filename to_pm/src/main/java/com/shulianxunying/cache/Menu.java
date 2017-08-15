package com.shulianxunying.cache;

/**
 * Created by Suchang on 2016/8/11 13:10.
 */
public class Menu {

    String name;
    String href;
    String iconClass;

    public Menu() {
    }

    public Menu(String name, String href, String iconClass) {
        this.name = name;
        this.href = href;
        this.iconClass = iconClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
}
