package com.ycosoriodev.recipeapp.data.di

import android.app.Application
import androidx.room.Room
import com.ycosoriodev.recipeapp.data.local.RecipeDatabase
import com.ycosoriodev.recipeapp.data.repository.RecipeRepositoryImpl
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRecipeDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            app,
            RecipeDatabase::class.java,
            RecipeDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(db: RecipeDatabase): RecipeRepository {
        return RecipeRepositoryImpl(db.recipeDao)
    }
}
