package com.android_a865.gebril_app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.android_a865.gebril_app.data.MyRoomDatabase
import com.android_a865.gebril_app.data.MyRoomDatabase.Companion.DATABASE_NAME
import com.android_a865.gebril_app.data.domain.repo.CartRepo
import com.android_a865.gebril_app.data.domain.repo.InvoiceRepo
import com.android_a865.gebril_app.data.domain.repo.ItemRepo
import com.android_a865.gebril_app.data.domain.repo.PostRepo
import com.android_a865.gebril_app.data.repository.CartRepoIml
import com.android_a865.gebril_app.data.repository.InvoiceRepositoryImpl
import com.android_a865.gebril_app.data.repository.ItemRepoImpl
import com.android_a865.gebril_app.data.repository.PostRepoImpl
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.feature_settings.data.data_source.PreferencesManager
import com.android_a865.gebril_app.feature_settings.data.repository.SettingsRepoImpl
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepo
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
    fun provideContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRetrofitInstance(): ItemsApi = Retrofit.Builder()
            .baseUrl("http://192.168.1.12:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItemsApi::class.java)

    @Provides
    @Singleton
    fun provideCartRepository(db: MyRoomDatabase): CartRepo {
        return CartRepoIml(db.getCartDao(), db.getItemsDao())
    }

    @Provides
    @Singleton
    fun provideItemsRepository(db: MyRoomDatabase): ItemRepo {
        return ItemRepoImpl(db.getItemsDao())
    }

    @Provides
    @Singleton
    fun providePostsRepository(db: MyRoomDatabase): PostRepo {
        return PostRepoImpl(db.getPostsDao())
    }

    @Provides
    @Singleton
    fun provideInvoicesRepository(db: MyRoomDatabase): InvoiceRepo {
        return InvoiceRepositoryImpl(db.getInvoicesDao(), db.getItemsDao())
    }


    @Provides
    @Singleton
    fun provideSettingsRepository(preferencesManager: PreferencesManager): SettingsRepo {
        return SettingsRepoImpl(preferencesManager)
    }

}