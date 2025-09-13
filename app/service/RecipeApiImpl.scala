package service

import converter.RecipeConverter
import jakarta.inject.Inject
import org.givery.recipe.api.RecipeApi
import org.givery.recipe.model.{
  CreateRecipeRequest,
  CreateRecipeResponse,
  DeleteRecipeResponse,
  GetRecipeResponse,
  ListRecipesResponse,
  RecipeListItem,
  UpdateRecipeItem,
  UpdateRecipeRequest,
  UpdateRecipeResponse,
  Recipe as RecipeDTO
}
import repository.RecipeRepository
import model.Recipe

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, format}

class RecipeApiImpl @Inject() (recipeRepository: RecipeRepository) extends RecipeApi {

  /** Create a new recipe.
    *   - Validates cost >= 0
    *   - Persists entity
    *   - Returns created recipe payload as per spec
    */
  override def createRecipe(createRecipeRequest: CreateRecipeRequest): CreateRecipeResponse = {
    requireNonNegativeCost(createRecipeRequest.cost)

    val entity = RecipeConverter.fromRequest(createRecipeRequest)
    val saved = recipeRepository.save(entity)

    val responseRecipe = RecipeDTO(
      id = Some(saved.id),
      title = Some(saved.title),
      making_time = Some(saved.makingTime),
      serves = Some(saved.serves),
      ingredients = Some(saved.ingredients),
      cost = Some(saved.cost),
      created_at = Some(saved.createdAt.format(fmt)),
      updated_at = Some(saved.updatedAt.format(fmt))
    )

    CreateRecipeResponse(
      message = Some("Recipe successfully created!"),
      recipe = Some(List(responseRecipe))
    )
  }

  /** List all recipes. Returns array of RecipeListItem (cost as string) as required by the spec.
    */
  override def listRecipes(): ListRecipesResponse = {
    val items: List[RecipeListItem] = recipeRepository.findAll().map(toListItem)
    ListRecipesResponse(recipes = Some(items))
  }

  /** Get a single recipe by ID. Throws NoSuchElementException if not found to be translated to 404 by the error
    * handler.
    */
  override def getRecipeById(id: Long): GetRecipeResponse = {
    val item = toListItem(requireRecipe(id))
    GetRecipeResponse(message = Some("Recipe details by id"), recipe = Some(List(item)))
  }

  /** Update a recipe by ID.
    *   - Validates cost >= 0
    *   - Updates mutable entity fields and persists
    *   - Returns UpdateRecipeResponse with cost as string
    */
  override def updateRecipe(id: Long, updateRecipeRequest: UpdateRecipeRequest): UpdateRecipeResponse = {
    requireNonNegativeCost(updateRecipeRequest.cost)

    val recipe = requireRecipe(id)
    recipe.title = updateRecipeRequest.title
    recipe.makingTime = updateRecipeRequest.making_time
    recipe.serves = updateRecipeRequest.serves
    recipe.ingredients = updateRecipeRequest.ingredients
    recipe.cost = updateRecipeRequest.cost
    recipe.updatedAt = LocalDateTime.now()

    val updated = recipeRepository.update(recipe)
    val item = toUpdateItem(updated)

    UpdateRecipeResponse(
      message = Some("Recipe successfully updated!"),
      recipe = Some(List(item))
    )
  }

  /** Delete a recipe by ID. Throws NoSuchElementException if not found to be translated to 404.
    */
  override def deleteRecipe(id: Long): DeleteRecipeResponse = {
    requireRecipe(id) // ensure it exists or raise 404
    recipeRepository.delete(id)
    DeleteRecipeResponse(message = Some("Recipe successfully removed!"))
  }

  // Common date formatter for created_at/updated_at fields
  private val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  // Validate non-negative cost
  private def requireNonNegativeCost(cost: Int): Unit =
    if (cost < 0) throw new IllegalArgumentException("Cost must be >= 0")

  // Fetch a recipe or throw to signal 404 via the error handler
  private def requireRecipe(id: Long): Recipe =
    recipeRepository.findById(id).getOrElse(throw new NoSuchElementException())

  // Map entity to list item (used by list and get)
  private def toListItem(r: Recipe): RecipeListItem =
    RecipeListItem(
      id = r.id,
      title = r.title,
      making_time = r.makingTime,
      serves = r.serves,
      ingredients = r.ingredients,
      cost = r.cost.toString
    )

  // Map entity to update response item
  private def toUpdateItem(r: Recipe): UpdateRecipeItem =
    UpdateRecipeItem(
      title = r.title,
      making_time = r.makingTime,
      serves = r.serves,
      ingredients = r.ingredients,
      cost = r.cost.toString
    )

}
