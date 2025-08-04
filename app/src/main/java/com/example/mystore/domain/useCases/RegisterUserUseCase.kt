package com.example.mystore.domain.useCases

import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.domain.repo.Repo
import com.google.firebase.firestore.auth.User
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val repo: Repo) {
    suspend operator fun invoke(user: UserDetailsModel) = repo.registerUserWithEmailAndPassword(user)

}