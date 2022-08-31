package com.jrrobo.juniorroboapp.di

import com.jrrobo.juniorroboapp.network.EndPoints
import com.jrrobo.juniorroboapp.network.JuniorRoboApi
import com.jrrobo.juniorroboapp.utility.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    /**
     * Provides the Retrofit Client Api reference for network calls
     * With added Scalars and GSON Converter factories
     */
    @Singleton
    @Provides
    fun provideCurrencyApi(): JuniorRoboApi = Retrofit.Builder()
        .baseUrl(EndPoints.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JuniorRoboApi::class.java)


    /**
     * Provides the Dispatchers for mocked testing of coroutines
     */
    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}