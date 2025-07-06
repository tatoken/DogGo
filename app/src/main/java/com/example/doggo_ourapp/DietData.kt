package com.example.doggo_ourapp

data class DietData(
    var name: String? =null,
    var carbohydrates: String ? =null,
    var fats: String? =null,
    var proteins: String ? =null,
    var fibers: String? =null,
    var vitamins: String ? =null,
    var dietRecipe: MutableList<DietRecipeData> = mutableListOf(),
)
