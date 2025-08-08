package com.example.mystore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystore.common.ResultState
import com.example.mystore.domain.modelClasses.CartModel
import com.example.mystore.domain.modelClasses.CategoryModel
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.domain.modelClasses.UserCredentialsModel
import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.domain.modelClasses.WishListModel
import com.example.mystore.domain.useCases.CartUseCase
import com.example.mystore.domain.useCases.GetAllCategoriesUseCase
import com.example.mystore.domain.useCases.GetAllProductsUseCase
import com.example.mystore.domain.useCases.GetHomeCategoriesUseCase
import com.example.mystore.domain.useCases.GetProductByIDUseCase
import com.example.mystore.domain.useCases.GetProductsByCategoryUseCase
import com.example.mystore.domain.useCases.LoginUserUseCase
import com.example.mystore.domain.useCases.RegisterUserUseCase
import com.example.mystore.domain.useCases.UserDetailsUseCase
import com.example.mystore.domain.useCases.WishListUseCase
import com.example.mystore.domain.useCases.ProfileManagementUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val userDetailsUseCase: UserDetailsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val getProductByIdUseCase: GetProductByIDUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getHomeCategoriesUseCase: GetHomeCategoriesUseCase,
    private val wishListUseCase: WishListUseCase,
    private val cartUseCase: CartUseCase,
    private val profileManagementUseCase: ProfileManagementUseCase,
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    //UI States
    private val _signUpState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val signUpState = _signUpState.asStateFlow()
    private val _loginUserState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val loginUserState = _loginUserState.asStateFlow()
    private val _userDetailsState = MutableStateFlow<UIState<UserDetailsModel>>(UIState.Empty)
    val userDetailsState = _userDetailsState.asStateFlow()
    private val _getAllCategoriesState =
        MutableStateFlow<UIState<List<CategoryModel>>>(UIState.Empty)
    val categoriesState = _getAllCategoriesState.asStateFlow()
    private val _getAllProductsState = MutableStateFlow<UIState<List<Product>>>(UIState.Empty)
    val getAllProductsState = _getAllProductsState.asStateFlow()
    private val _getProductsByCategoryState =
        MutableStateFlow<UIState<List<Product>>>(UIState.Empty)
    val getProductsByCategoryState = _getProductsByCategoryState.asStateFlow()
    private val _getProductByIdState = MutableStateFlow<UIState<Product>>(UIState.Empty)
    val getProductByIdState = _getProductByIdState.asStateFlow()

    // Wishlist States
    private val _getWishListState = MutableStateFlow<UIState<List<WishListModel>>>(UIState.Empty)
    val getWishListState = _getWishListState.asStateFlow()

    private val _addToWishListState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val addToWishListState = _addToWishListState.asStateFlow()
    private val _removeFromWishListState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val removeFromWishListState = _removeFromWishListState.asStateFlow()

    // Cart States
    private val _getCartListState = MutableStateFlow<UIState<List<CartModel>>>(UIState.Empty)
    val getCartListState = _getCartListState.asStateFlow()
    private val _addToCartState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val addToCartState = _addToCartState.asStateFlow()
    private val _removeFromCartState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val removeFromCartState = _removeFromCartState.asStateFlow()

    // Profile Management States
    private val _updateProfileState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val updateProfileState = _updateProfileState.asStateFlow()

    private val _uploadImageState = MutableStateFlow<UIState<String>>(UIState.Empty)
    val uploadImageState = _uploadImageState.asStateFlow()

    private val _homeScreenState =
        MutableStateFlow<CombineUState<List<CategoryModel>, List<Product>>>(
            CombineUState.Empty
        )
    val homeScreenState = _homeScreenState.asStateFlow()


    init {

        firebaseAuth.addAuthStateListener {
            _isLoggedIn.value = it.currentUser != null
            if (_isLoggedIn.value) {
                loadHomeData()
            } else {
                _homeScreenState.value = CombineUState.Empty
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    //    Functions For Ui States
    fun registerUser(user: UserDetailsModel) {
        viewModelScope.launch {
            registerUserUseCase.invoke(user).collect {
                when (it) {
                    is ResultState.Error -> {
                        _signUpState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _signUpState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _signUpState.value = UIState.Success(it.data as String)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun loginUser(user: UserCredentialsModel) {
        viewModelScope.launch {
            loginUserUseCase.invoke(user).collect {
                when (it) {
                    is ResultState.Error -> {
                        _loginUserState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _loginUserState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _loginUserState.value = UIState.Success(it.data as String)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun userDetails() {
        viewModelScope.launch {
            userDetailsUseCase.invoke().collect {
                when (it) {
                    is ResultState.Error -> {
                        _userDetailsState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _userDetailsState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _userDetailsState.value = UIState.Success(it.data as UserDetailsModel)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }
    fun loadHomeData() {
        viewModelScope.launch {
            combine(
                getHomeCategoriesUseCase.invoke(),
                getAllProductsUseCase.invoke()
            ) { categories, products ->
                when {
                    categories is ResultState.Success<*> && products is ResultState.Success<*> -> {
                        CombineUState.Success(
                            categories.data as List<CategoryModel>,
                            products.data as List<Product>
                        )
                    }

                    categories is ResultState.Error -> {
                        CombineUState.Error("Category Error " + categories.message)
                    }

                    products is ResultState.Error -> {
                        CombineUState.Error("Products Error " + products.message)
                    }

                    categories is ResultState.Loading || products is ResultState.Loading -> {
                        CombineUState.Loading
                    }

                    else -> {
                        CombineUState.Empty
                    }
                }
            }.collect { state ->
                _homeScreenState.value = state
            }
        }
    }

    fun getAllCategories() {
        viewModelScope.launch {
            getAllCategoriesUseCase.invoke().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllCategoriesState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _getAllCategoriesState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _getAllCategoriesState.value =
                            UIState.Success(it.data as List<CategoryModel>)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }


    fun getAllProducts() {
        viewModelScope.launch {
            getAllProductsUseCase.invoke().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllProductsState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _getAllProductsState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _getAllProductsState.value = UIState.Success(it.data as List<Product>)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun getProductsByCategory(category: String) {
        viewModelScope.launch {
            getProductsByCategoryUseCase.invoke(category).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getProductsByCategoryState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _getProductsByCategoryState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _getProductsByCategoryState.value =
                            UIState.Success(it.data as List<Product>)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun getProductById(productId: String) {
        viewModelScope.launch {
            getProductByIdUseCase.invoke(productId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getProductByIdState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _getProductByIdState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _getProductByIdState.value = UIState.Success(it.data as Product)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun getWishlistItems() {
        viewModelScope.launch {
            wishListUseCase.getWishList(
                firebaseAuth.currentUser!!.uid
            ).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getWishListState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _getWishListState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _getWishListState.value = UIState.Success(it.data as List<WishListModel>)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun removeFromWishList(wishListId: String) {
        viewModelScope.launch {
            wishListUseCase.removeFromWishList(wishListId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _removeFromWishListState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _removeFromWishListState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _removeFromWishListState.value = UIState.Success(it.data as String)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun addToWishList(wishListProduct: WishListModel) {
        viewModelScope.launch {
            wishListUseCase.addToWishList(wishListProduct).collect {
                when (it) {
                    is ResultState.Error -> {
                        _addToWishListState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _addToWishListState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _addToWishListState.value = UIState.Success(it.data as String)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun getCartItems() {
        viewModelScope.launch {
            cartUseCase.getCart(
                firebaseAuth.currentUser!!.uid
            ).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getCartListState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _getCartListState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _getCartListState.value = UIState.Success(it.data as List<CartModel>)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }
    fun addToCart(cartProduct: CartModel) {
        viewModelScope.launch {
            cartUseCase.addToCart(cartProduct).collect {
                when (it) {
                    is ResultState.Error -> {
                        _addToCartState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _addToCartState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _addToCartState.value = UIState.Success(it.data as String)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun removeFromCart(cartId: String) {
        viewModelScope.launch {
            cartUseCase.removeFromCart(cartId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _removeFromCartState.value = UIState.Error(it.message)
                    }

                    is ResultState.Loading -> {
                        _removeFromCartState.value = UIState.Loading
                    }

                    is ResultState.Success<*> -> {
                        _removeFromCartState.value = UIState.Success(it.data as String)
                    }

                    is ResultState.Empty -> {}
                }
            }
        }
    }

    fun updateUserProfile(userDetails: UserDetailsModel) {
        viewModelScope.launch {
            profileManagementUseCase.updateProfile(userDetails).collect {
                when (it) {
                    is ResultState.Loading -> _updateProfileState.value = UIState.Loading
                    is ResultState.Success<*> -> _updateProfileState.value = UIState.Success(it.data as String)
                    is ResultState.Error -> _updateProfileState.value = UIState.Error(it.message)
                    is ResultState.Empty -> _updateProfileState.value = UIState.Empty
                }
            }
        }
    }

    fun uploadProfileImage(imageUri: String) {
        viewModelScope.launch {
            profileManagementUseCase.uploadProfileImage(imageUri).collect {
                when (it) {
                    is ResultState.Loading -> _uploadImageState.value = UIState.Loading
                    is ResultState.Success<*> -> _uploadImageState.value = UIState.Success(it.data as String)
                    is ResultState.Error -> _uploadImageState.value = UIState.Error(it.message)
                    is ResultState.Empty -> _uploadImageState.value = UIState.Empty
                }
            }
        }
    }
}

sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String) : UIState<Nothing>()
    object Empty : UIState<Nothing>()
}

sealed class CombineUState<out T1, out T2> {
    object Loading : CombineUState<Nothing, Nothing>()
    data class Success<T1, T2>(val data: T1, val data2: T2) : CombineUState<T1, T2>()
    data class Error(val message: String) : CombineUState<Nothing, Nothing>()
    object Empty : CombineUState<Nothing, Nothing>()
}
