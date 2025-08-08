package com.example.mystore.domain.dI

import com.example.mystore.data.repoImpl.RepoImpl
import com.example.mystore.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideRepo(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore, firebaseStorage: FirebaseStorage): Repo {
        return RepoImpl(firebaseAuth, firebaseFirestore)

    }
}