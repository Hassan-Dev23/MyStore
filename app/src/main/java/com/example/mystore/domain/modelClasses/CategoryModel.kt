package com.example.mystore.domain.modelClasses

data class CategoryModel(
    var id :String = "",
    val name : String = "",
    val imageUrl : String = "",
    val date : String = System.currentTimeMillis().toString()
)
