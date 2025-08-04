package com.example.mystore.domain.useCases

import com.example.mystore.common.ResultState
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


class GetProductByIDUseCase @Inject constructor(private val repo: Repo) {
    suspend operator fun invoke(productId: String): Flow<ResultState<Product>> {
        return repo.getProductById(productId)
    }
}