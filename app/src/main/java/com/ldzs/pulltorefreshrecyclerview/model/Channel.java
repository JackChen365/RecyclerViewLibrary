package com.ldzs.pulltorefreshrecyclerview.model;

/**
 * Created by cz on 16/1/27.
 */
public class Channel {
    public String name;
    public boolean use;

    public Channel() {
    }

    public Channel(String name, boolean use) {
        this.name = name;
        this.use = use;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel r = (Channel) o;
        return name.equals(r.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
