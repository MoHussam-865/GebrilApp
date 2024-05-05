package com.android_a865.gebril_app.feature_settings.domain.use_cases

import android.util.Log
import com.android_a865.gebril_app.feature_settings.domain.models.DatabaseHolder
import com.android_a865.gebril_app.feature_settings.domain.repository.ImportExportRepository
import com.android_a865.gebril_app.utils.toObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

class ImportUseCase(
    private val repository: ImportExportRepository
) {

    suspend operator fun invoke(data0: String): String {


        try {

            val data = Gson().fromJson(data0, DatabaseHolder::class.java)

            //val data = data0.toObject<DatabaseHolder>()

            // invoices
            data.invoices.forEach {
                repository.insertInvoice(it)
            }
            // clients
            data.clients.forEach {
                repository.insertClient(it)
            }
            // items
            data.items.forEach {
                repository.insertItemEntity(it)
            }
            return "done"
        } catch (e: Exception) {
            Log.d("Importing Error", e.message.toString())
            return "Error"
        }
    }

}