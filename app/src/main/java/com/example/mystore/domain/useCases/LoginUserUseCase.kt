package com.example.mystore.domain.useCases

import com.example.mystore.domain.modelClasses.UserCredentialsModel
import com.example.mystore.domain.repo.Repo
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repo: Repo
) {
    suspend operator fun invoke(user: UserCredentialsModel) = repo.loginUserWithEmailAndPassword(user)
}