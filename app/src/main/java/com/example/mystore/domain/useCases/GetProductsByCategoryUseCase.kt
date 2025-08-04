package com.example.mystore.domain.useCases

import com.example.mystore.common.ResultState
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


class GetProductsByCategoryUseCase @Inject constructor(private val repo: Repo) {
    suspend operator fun invoke(category: String): Flow<ResultState<List<Product>>> {
        return repo.getProductsByCategory(category)
    }
}