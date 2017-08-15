package com.shulianxunying.position;

/**
 * Created by SuChang on 2017/6/5 10:13.
 */
public class PositionPathNode {
    String position_name;
    int count;

    public PositionPathNode(String position_name, int count) {
        this.position_name = position_name;
        this.count = count;
    }

    public PositionPathNode() {
    }

    public String getPosition_name() {
        return position_name;
    }

    public void setPosition_name(String position_name) {
        this.position_name = position_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
