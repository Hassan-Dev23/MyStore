package com.example.mystore.data.repoImpl

import com.example.mystore.common.CART_PATH
import com.example.mystore.common.CATEGORY_PATH
import com.example.mystore.common.PRODUCT_PATH
import com.example.mystore.common.ResultState
import com.example.mystore.common.USER_COLLECTION
import com.example.mystore.common.WISHLIST_PATH
import com.example.mystore.domain.modelClasses.CartModel
import com.example.mystore.domain.modelClasses.CategoryModel
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.domain.modelClasses.UserCredentialsModel
import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.domain.modelClasses.WishListModel
import com.example.mystore.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class RepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : Repo {
    override suspend fun registerUserWithEmailAndPassword(user: UserDetailsModel): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            try {
                firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnSuccessListener {

                        trySend(ResultState.Success("Your Profile Created Successfully"))
                        firebaseFirestore.collection(USER_COLLECTION).document(it.user!!.uid)
                            .set(user).addOnSuccessListener {
                                trySend(ResultState.Success("Congratulation! You have created your account Successfully."))

                            }

                    }.addOnFailureListener {
                        trySend(
                            ResultState.Error(
                                "Error Message : ${it.message.toString()}" + "\n" +
                                        "Error Cause : ${it.cause.toString()}" + "\n" +
                                        "Error StackTrace : ${it.stackTrace}"
                            )
                        )
                    }
            } catch (e: Exception) {
                trySend(
                    ResultState.Error(
                        "Error Message : ${e.message.toString()}" + "\n" +
                                "Error Cause : ${e.cause.toString()}" + "\n" +
                                "Error StackTrace : ${e.stackTrace}"
                    )
                )
            }
            awaitClose {
                close()
            }

        }


    override suspend fun loginUserWithEmailAndPassword(user: UserCredentialsModel): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            try {
                firebaseAuth.signInWithEmailAndPassword(user.e, user.p)
                    .addOnSuccessListener {

                        trySend(ResultState.Success("Congratulations!! You Signed In Successfully."))


                    }.addOnFailureListener {
                        trySend(
                            ResultState.Error(
                                "Error Message : ${it.message.toString()}" + "\n" +
                                        "Error Cause : ${it.cause.toString()}" + "\n" +
                                        "Error StackTrace : ${it.stackTrace}"
                            )
                        )
                    }
            } catch (e: Exception) {
                trySend(
                    ResultState.Error(
                        "Error Message : ${e.message.toString()}" + "\n" +
                                "Error Cause : ${e.cause.toString()}" + "\n" +
                                "Error StackTrace : ${e.stackTrace}"
                    )
                )
            }
            awaitClose {
                close()
            }


        }

    override suspend fun getAllCategories(): Flow<ResultState<List<CategoryModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(CATEGORY_PATH).get().addOnSuccessListener {
                val categories = it.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(CategoryModel::class.java)?.apply {
                        id = documentSnapshot.id
                    }
                }

                trySend(ResultState.Success(categories))
            }.addOnFailureListener {
                trySend(
                    ResultState.Error(
                        "Error Message : ${it.message.toString()}" + "\n" +
                                "Error Cause : ${it.cause.toString()}" + "\n" +
                                "Error StackTrace : ${it.stackTrace}"
                    )
                )
            }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun getAllProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(PRODUCT_PATH).get().addOnSuccessListener { it ->
                val products = it.documents.mapNotNull {
                    it.toObject(Product::class.java).apply {
                        this?.id = it.id
                    }
                }
                trySend(ResultState.Success(products))
            }.addOnFailureListener {
                trySend(
                    ResultState.Error(
                        "Error Message : ${it.message.toString()}" + "\n" +
                                "Error Cause : ${it.cause.toString()}" + "\n" +
                                "Error StackTrace : ${it.stackTrace}"
                    )
                )
            }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun getProductsByCategory(category: String): Flow<ResultState<List<Product>>> =
        callbackFlow {
            trySend(ResultState.Loading)

            val listenerRegistration = try {
                firebaseFirestore.collection(PRODUCT_PATH)
                    .whereEqualTo("category", category)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            trySend(
                                ResultState.Error(
                                    "Error Message: ${exception.message}\n" +
                                            "Cause: ${exception.cause}\n" +
                                            "StackTrace: ${exception.stackTraceToString()}"
                                )
                            )
                            return@addSnapshotListener
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            val productList = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(Product::class.java)?.copy(id = doc.id)
                            }
                            trySend(ResultState.Success(productList))
                        } else {
                            trySend(ResultState.Success(emptyList()))
                        }
                    }
            } catch (e: Exception) {
                trySend(
                    ResultState.Error(
                        "Error Message : ${e.message}\n" +
                                "Cause : ${e.cause}\n" +
                                "StackTrace : ${e.stackTraceToString()}"
                    )
                )
                null
            }

            awaitClose {
                listenerRegistration?.remove() // ✅ Always safely remove the listener
            }
        }


    override suspend fun getProductById(productId: String): Flow<ResultState<Product>> = flow {
        emit(ResultState.Loading)

        try {
            val snapshot = firebaseFirestore
                .collection(PRODUCT_PATH)
                .document(productId)
                .get()
                .await()

            val product = snapshot.toObject(Product::class.java)

            if (product != null) {
                emit(ResultState.Success(product))
            } else {
                emit(ResultState.Error("Product not found"))
            }
        } catch (e: Exception) {
            emit(
                ResultState.Error(
                    "Error Message: ${e.message}\n" +
                            "Cause: ${e.cause}\n" +
                            "StackTrace: ${e.stackTraceToString()}"
                )
            )
        }
    }


    override suspend fun getHomeCategories(): Flow<ResultState<List<CategoryModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(CATEGORY_PATH)
                .limit(4)
                .get().addOnSuccessListener {
                val categories = it.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(CategoryModel::class.java)?.apply {
                        id = documentSnapshot.id
                    }
                }
                trySend(ResultState.Success(categories))
            }.addOnFailureListener {
                trySend(
                    ResultState.Error(
                        "Error Message : ${it.message.toString()}" + "\n" +
                                "Error Cause : ${it.cause.toString()}" + "\n" +
                                "Error StackTrace : ${it.stackTrace}"
                    )
                )
            }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun addProductToCart(cartProduct: CartModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(CART_PATH).add(cartProduct)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product added to cart successfully."))
                }.addOnFailureListener {
                    trySend(
                        ResultState.Error(
                            "Error Message : ${it.message.toString()}" + "\n" +
                                    "Error Cause : ${it.cause.toString()}" + "\n" +
                                    "Error StackTrace : ${it.stackTrace}"
                        )
                    )
                }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun getCartProducts(userId: String): Flow<ResultState<List<CartModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(CART_PATH)
                .whereEqualTo("userId", userId)
                .get().addOnSuccessListener { snapshot ->
                    val cartProducts = snapshot.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(CartModel::class.java)?.apply {
                            id = documentSnapshot.id
                        }
                    }
                    trySend(ResultState.Success(cartProducts))
                }.addOnFailureListener {
                    trySend(
                        ResultState.Error(
                            "Error Message : ${it.message.toString()}" + "\n" +
                                    "Error Cause : ${it.cause.toString()}" + "\n" +
                                    "Error StackTrace : ${it.stackTrace}"
                        )
                    )
                }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }

    }

    override suspend fun removeProductFromCart(cartId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(CART_PATH).document(cartId).delete()
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product removed from cart successfully."))
                }.addOnFailureListener {
                    trySend(
                        ResultState.Error(
                            "Error Message : ${it.message.toString()}" + "\n" +
                                    "Error Cause : ${it.cause.toString()}" + "\n" +
                                    "Error StackTrace : ${it.stackTrace}"
                        )
                    )
                }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun addProductToWishlist(wishListProduct: WishListModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(WISHLIST_PATH).add(wishListProduct)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product added to wishlist successfully."))
                }.addOnFailureListener {
                    trySend(
                        ResultState.Error(
                            "Error Message : ${it.message.toString()}" + "\n" +
                                    "Error Cause : ${it.cause.toString()}" + "\n" +
                                    "Error StackTrace : ${it.stackTrace}"
                        )
                    )
                }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun getWishlistProducts(userId: String): Flow<ResultState<List<WishListModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(WISHLIST_PATH)
                .whereEqualTo("userId", userId)
                .get().addOnSuccessListener { snapshot ->
                    val wishListProducts = snapshot.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(WishListModel::class.java)?.apply {
                            id = documentSnapshot.id
                        }
                    }
                    trySend(ResultState.Success(wishListProducts))
                }.addOnFailureListener {
                    trySend(
                        ResultState.Error(
                            "Error Message : ${it.message.toString()}" + "\n" +
                                    "Error Cause : ${it.cause.toString()}" + "\n" +
                                    "Error StackTrace : ${it.stackTrace}"
                        )
                    )
                }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }

    override suspend fun removeProductFromWishlist(wishListId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseFirestore.collection(WISHLIST_PATH).document(wishListId).delete()
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product removed from wishlist successfully."))
                }.addOnFailureListener {
                    trySend(
                        ResultState.Error(
                            "Error Message : ${it.message.toString()}" + "\n" +
                                    "Error Cause : ${it.cause.toString()}" + "\n" +
                                    "Error StackTrace : ${it.stackTrace}"
                        )
                    )
                }
        } catch (e: Exception) {
            trySend(
                ResultState.Error(
                    "Error Message : ${e.message.toString()}" + "\n" +
                            "Error Cause : ${e.cause.toString()}" + "\n" +
                            "Error StackTrace : ${e.stackTrace}"
                )
            )
        }
        awaitClose {
            close()
        }
    }


}