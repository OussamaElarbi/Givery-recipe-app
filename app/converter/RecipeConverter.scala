package converter

import model.Recipe
import org.givery.recipe.model.{CreateRecipeRequest, CreateRecipeResponse}

import java.time.LocalDateTime

object RecipeConverter {
  def fromRequest(req: CreateRecipeRequest): Recipe = {
    val recipe = new Recipe()
    recipe.title = req.title
    recipe.makingTime = req.making_time
    recipe.serves = req.serves
    recipe.ingredients = req.ingredients
    recipe.cost = req.cost
    recipe.createdAt = LocalDateTime.now()
    recipe.updatedAt = LocalDateTime.now()
    recipe
  }
}
