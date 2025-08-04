package com.example.mystore.domain.modelClasses

data class UserDetailsModel(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val gender: String = "",
    val phone : String = "",
    val address: String = "",
    val profileImage: String = ""
)
