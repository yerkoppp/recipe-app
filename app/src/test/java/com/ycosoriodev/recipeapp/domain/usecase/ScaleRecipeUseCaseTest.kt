package com.ycosoriodev.recipeapp.domain.usecase

import com.ycosoriodev.recipeapp.domain.model.Ingredient
import com.ycosoriodev.recipeapp.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class ScaleRecipeUseCaseTest {

    private val scaleRecipe = ScaleRecipeUseCase()

    @Test
    fun `scale recipe by 2 returns double amounts`() {
        val ingredient1 = Ingredient("Harina", 100.0, "g")
        val recipe = Recipe(
            title = "Pan",
            ingredients = listOf(ingredient1),
            portions = 2
        )

        val result = scaleRecipe(recipe, 2.0f)

        assertEquals(200.0, result.ingredients[0].amount, 0.01)
        assertEquals(4, result.portions)
    }

    @Test
    fun `scale recipe by half returns half amounts`() {
        val ingredient1 = Ingredient("Harina", 100.0, "g")
        val recipe = Recipe(
            title = "Pan",
            ingredients = listOf(ingredient1),
            portions = 4
        )

        val result = scaleRecipe(recipe, 0.5f)

        assertEquals(50.0, result.ingredients[0].amount, 0.01)
        assertEquals(2, result.portions)
    }
}
