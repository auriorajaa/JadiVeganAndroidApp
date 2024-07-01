package com.example.jadiveganapp.model;

public class ViewAllModel {

    private String UID;
    String RecipeImage;
    String RecipeTitle;
    String RecipeCategory;
    String RecipeServings;

    public ViewAllModel() {
    }

    public ViewAllModel(String UID, String recipeImage, String recipeTitle, String recipeCategory, String recipeServings) {
        this.UID = UID;
        RecipeImage = recipeImage;
        RecipeTitle = recipeTitle;
        RecipeCategory = recipeCategory;
        RecipeServings = recipeServings;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getRecipeImage() {
        return RecipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        RecipeImage = recipeImage;
    }

    public String getRecipeTitle() {
        return RecipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        RecipeTitle = recipeTitle;
    }

    public String getRecipeCategory() {
        return RecipeCategory;
    }

    public void setRecipeCategory(String recipeCategory) {
        RecipeCategory = recipeCategory;
    }

    public String getRecipeServings() {
        return RecipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        RecipeServings = recipeServings;
    }
}
