package com.example.tanico.feature.seller.products

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tanico.core.AppContainer

/** Entry point of the seller "Produk" tab: switches between the list and the add/edit form. */
@Composable
fun SellerProductsScreen(modifier: Modifier = Modifier) {
    val listViewModel: ProductListViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ProductListViewModel(AppContainer.sellerProductRepository) }
        }
    )

    var formOpen by rememberSaveable { mutableStateOf(false) }
    var editingProductId by rememberSaveable { mutableStateOf<String?>(null) }
    // New key per form session so a finished form's ViewModel state is never reused.
    var formSession by rememberSaveable { mutableStateOf(0) }

    if (formOpen) {
        val formViewModel: ProductFormViewModel = viewModel(
            key = "product-form-$formSession",
            factory = viewModelFactory {
                initializer {
                    ProductFormViewModel(AppContainer.sellerProductRepository, editingProductId)
                }
            }
        )
        ProductFormScreen(
            viewModel = formViewModel,
            onClose = { formOpen = false },
            onSaved = {
                formOpen = false
                listViewModel.load()
            },
            modifier = modifier
        )
    } else {
        ProductListScreen(
            viewModel = listViewModel,
            onAddProduct = {
                editingProductId = null
                formSession += 1
                formOpen = true
            },
            onEditProduct = { product ->
                editingProductId = product.id
                formSession += 1
                formOpen = true
            },
            modifier = modifier
        )
    }
}
