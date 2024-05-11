package com.android_a865.gebril_app.di

import android.app.Application
import androidx.room.Room
import com.android_a865.gebril_app.data.MyRoomDatabase
import com.android_a865.gebril_app.data.MyRoomDatabase.Companion.DATABASE_NAME
import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.data.repository.InvoiceRepositoryImpl
import com.android_a865.gebril_app.data.repository.ItemsRepositoryImpl
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.feature_main.domain.repository.InvoiceRepository
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import com.android_a865.gebril_app.feature_settings.data.data_source.PreferencesManager
import com.android_a865.gebril_app.feature_settings.data.repository.SettingsRepositoryImpl
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import com.google.gson.internal.GsonBuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(app: Application): MyRoomDatabase =
        Room.databaseBuilder(app, MyRoomDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun provideRetrofitInstance(): ItemsApi = Retrofit.Builder()
        .baseUrl("https://Example.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ItemsApi::class.java)

    @Provides
    @Singleton
    fun provideItemsRepository(db: MyRoomDatabase): ItemsRepository {
        return ItemsRepositoryImpl(db.getItemsDao())
    }

    @Provides
    @Singleton
    fun provideInvoicesRepository(db: MyRoomDatabase): InvoiceRepository {
        return InvoiceRepositoryImpl(db.getInvoicesDao())
    }


    @Provides
    @Singleton
    fun provideSettingsRepository(preferencesManager: PreferencesManager): SettingsRepository {
        return SettingsRepositoryImpl(preferencesManager)
    }

}