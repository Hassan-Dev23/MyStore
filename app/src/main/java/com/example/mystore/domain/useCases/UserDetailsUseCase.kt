package com.example.mystore.domain.useCases

import com.example.mystore.domain.repo.Repo
import javax.inject.Inject

class UserDetailsUseCase @Inject constructor(private val repo : Repo) {
    suspend operator fun invoke() = repo.userDetails()
}