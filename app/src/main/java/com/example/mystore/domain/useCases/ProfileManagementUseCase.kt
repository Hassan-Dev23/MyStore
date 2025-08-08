package com.example.mystore.domain.useCases

import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.domain.repo.Repo
import javax.inject.Inject

class ProfileManagementUseCase @Inject constructor(private val repo: Repo) {
    suspend fun uploadProfileImage(imageUri: String) = repo.uploadProfileImage(imageUri)
    suspend fun updateProfile(userDetails: UserDetailsModel) = repo.updateUserProfile(userDetails)
}
