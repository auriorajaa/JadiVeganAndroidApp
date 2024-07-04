package com.example.jadiveganapp.model;

public class FavoriteRecipeModel {
    private String recipeUid;
    private String recipeTitle;
    private String recipeImage;
    private String recipeCategory;
    private String recipeServings;

    public FavoriteRecipeModel() {
        // Diperlukan oleh Firebase
    }

    public FavoriteRecipeModel(String recipeUid, String recipeTitle, String recipeImage, String recipeCategory, String recipeServings) {
        this.recipeUid = recipeUid;
        this.recipeTitle = recipeTitle;
        this.recipeImage = recipeImage;
        this.recipeCategory = recipeCategory;
        this.recipeServings = recipeServings;
    }

    public String getRecipeUid() {
        return recipeUid;
    }

    public void setRecipeUid(String recipeUid) {
        this.recipeUid = recipeUid;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(String recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.recipeServings = recipeServings;
    }
}
