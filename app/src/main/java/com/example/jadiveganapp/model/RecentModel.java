package com.example.jadiveganapp.model;

public class RecentModel {

    private String UID; // Add UID field
    private String RecipeTitle;
    private String RecipeCategory;
    private String RecipeServings;
    private String RecipeImage;

    public RecentModel() {
    }

    public RecentModel(String UID, String recipeTitle, String recipeCategory, String recipeServings, String recipeImage) {
        this.UID = UID;
        this.RecipeTitle = recipeTitle;
        this.RecipeCategory = recipeCategory;
        this.RecipeServings = recipeServings;
        this.RecipeImage = recipeImage;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getRecipeTitle() {
        return RecipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.RecipeTitle = recipeTitle;
    }

    public String getRecipeCategory() {
        return RecipeCategory;
    }

    public void setRecipeCategory(String recipeCategory) {
        this.RecipeCategory = recipeCategory;
    }

    public String getRecipeServings() {
        return RecipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.RecipeServings = recipeServings;
    }

    public String getRecipeImage() {
        return RecipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.RecipeImage = recipeImage;
    }
}
