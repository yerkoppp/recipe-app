package com.ycosoriodev.recipeapp.domain.di

import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository
import com.ycosoriodev.recipeapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideRecipeUseCases(repository: RecipeRepository): RecipeUseCases {
        return RecipeUseCases(
            getRecipes = GetRecipesUseCase(repository),
            getRecipeById = GetRecipeByIdUseCase(repository),
            addRecipe = AddRecipeUseCase(repository),
            deleteRecipe = DeleteRecipeUseCase(repository),
            scaleRecipe = ScaleRecipeUseCase()
        )
    }
}


