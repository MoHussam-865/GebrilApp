package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

data class ItemsUseCases(
    val getItems: GetItemsUseCase,
    val getInvoiceItems: GetInvoiceItemsUseCase,
    val getItemByID: GetItemByIDUseCase,
    val deleteItems: DeleteItemsUseCase,
    val addItem: AddItemUseCase,
    val updateItem: UpdateItemUseCase,
    val copyFolderUseCases: CopyFolderUseCase,

    )
