package com.example.androidwidget.androidwidget.Domain;

import java.io.Serializable;
public class ImageInfo implements Serializable {
    public final int id;
    public final String path;

    public ImageInfo(int id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public int hashCode() {

        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (! (o instanceof  ImageInfo)) {
            return false;
        }

        if (((ImageInfo) o).id < 0 && this.path.equals(((ImageInfo) o).path)) {
            return true;
        }

        if (((ImageInfo) o).id == this.id && this.path.equals(((ImageInfo) o).path)) {
            return true;
        }

        return (false);
    }
}
