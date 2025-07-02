package com.example.doggo_ourapp

data class DietData(
    var name: String?,
    var carbohydrates: String?,
    var fats: String?,
    var proteins: String?,
    var fibers: String?,
    var vitamins: String?,
    var DietRecipe: MutableList<DietRecipeData>? = mutableListOf(),
)