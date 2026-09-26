package com.example.alphabetlauncher.di

import android.content.Context
import com.example.alphabetlauncher.data.repository.AppRepositoryImpl
import com.example.alphabetlauncher.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepositoryImpl(context)
    }
}
