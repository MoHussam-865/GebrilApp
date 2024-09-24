package com.android_a865.gebril_app.di

import android.app.Application
import androidx.room.Room
import com.android_a865.gebril_app.data.MyRoomDatabase
import com.android_a865.gebril_app.data.MyRoomDatabase.Companion.DATABASE_NAME
import com.android_a865.gebril_app.data.repository.InvoiceRepositoryImpl
import com.android_a865.gebril_app.data.repository.ItemsRepositoryImpl
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.domain.ItemsRepository
import com.android_a865.gebril_app.data.domain.PostsRepository
import com.android_a865.gebril_app.data.repository.PostRepositoryImpl
import com.android_a865.gebril_app.feature_settings.data.data_source.PreferencesManager
import com.android_a865.gebril_app.feature_settings.data.repository.SettingsRepositoryImpl
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        .baseUrl("http://192.168.1.12:8000")
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
    fun providePostsRepository(db: MyRoomDatabase): PostsRepository {
        return PostRepositoryImpl(db.getPostsDao())
    }

    @Provides
    @Singleton
    fun provideInvoicesRepository(db: MyRoomDatabase): InvoiceRepository {
        return InvoiceRepositoryImpl(db.getInvoicesDao(), db.getItemsDao())
    }


    @Provides
    @Singleton
    fun provideSettingsRepository(preferencesManager: PreferencesManager): SettingsRepository {
        return SettingsRepositoryImpl(preferencesManager)
    }

}