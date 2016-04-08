package com.example.androidwidget.androidwidget.Domain;

import java.util.ArrayList;

/**
 * 图片文件夹的数据模型
 */
public class ImageFolderInfo {
    private int id;
    private String name;
    private ArrayList<ImageInfo> images;

    public ImageFolderInfo() {
        this(0, null, new ArrayList<ImageInfo>());
    }

    public ImageFolderInfo(int id, String name) {
        this(id, name, new ArrayList<ImageInfo>());
    }

    private ImageFolderInfo(int id, String name, ArrayList<ImageInfo> images) {
        this.id = id;
        this.name = name;
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文件夹下图片地址列表
     */
    public void addImage(ImageInfo info) {
        images.add(info);
    }

    public String getPath(int location){
        return images.get(location).path;
    }

    public ImageInfo getById(int id){
        for(ImageInfo info:images){
            if(info.id==id){
                return info;
            }
        }
        return null;
    }

    public ImageInfo getByPath(String path){
        for(ImageInfo info:images){
            if(info.path.equalsIgnoreCase(path)){
                return info;
            }
        }
        return null;
    }

    public ImageInfo get(int location) {
        return images.get(location);
    }

    public int size() {
        return images.size();
    }

    public ArrayList<ImageInfo> list(){
        return images;
    }
}
