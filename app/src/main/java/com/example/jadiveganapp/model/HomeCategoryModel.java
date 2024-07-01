package com.example.jadiveganapp.model;

public class HomeCategoryModel {

    String Name;
    String Image;

    public HomeCategoryModel() {


    }

    public HomeCategoryModel(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
