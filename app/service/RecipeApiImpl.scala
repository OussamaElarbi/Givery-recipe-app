package service

import constant.ApplicationConstants.{
  RecipeCreatedSuccessfully,
  RecipeDeletedSuccessfully,
  RecipeDetailsById,
  RecipeUpdatedSuccessfully
}
import converter.RecipeConverter
import jakarta.inject.Inject
import model.Recipe
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
import play.api.Logging
import repository.RecipeRepository

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, format}

class RecipeApiImpl @Inject() (recipeRepository: RecipeRepository) extends RecipeApi with Logging {

  /** Create a new recipe.
    *   - Validates cost >= 0
    *   - Persists entity
    *   - Returns created recipe payload as per spec
    */
  override def createRecipe(createRecipeRequest: CreateRecipeRequest): CreateRecipeResponse = {
    logger.debug(s"[createRecipe] Received request: $createRecipeRequest")
    requireNonNegativeCost(createRecipeRequest.cost)

    val entity = RecipeConverter.fromRequest(createRecipeRequest)
    val saved = recipeRepository.save(entity)

    logger.info(s"[createRecipe] Recipe saved with id=${saved.id}")

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
      message = Some(RecipeCreatedSuccessfully),
      recipe = Some(List(responseRecipe))
    )
  }

  /** List all recipes. Returns array of RecipeListItem (cost as string) as required by the spec.
    */
  override def listRecipes(): ListRecipesResponse = {
    logger.debug("[listRecipes] Fetching all recipes")

    val items: List[RecipeListItem] = recipeRepository.findAll().map(toListItem)

    logger.info(s"[listRecipes] Fetched ${items.size} recipes")
    ListRecipesResponse(recipes = Some(items))
  }

  /** Get a single recipe by ID. Throws NoSuchElementException if not found to be translated to 404 by the error
    * handler.
    */
  override def getRecipeById(id: Long): GetRecipeResponse = {
    logger.debug(s"[getRecipeById] Fetching recipe with id=$id")

    val item = toListItem(requireRecipe(id))
    logger.info(s"[getRecipeById] Recipe found: id=${item.id}")

    GetRecipeResponse(message = Some(RecipeDetailsById), recipe = Some(List(item)))
  }

  /** Update a recipe by ID.
    *   - Validates cost >= 0
    *   - Updates mutable entity fields and persists
    *   - Returns UpdateRecipeResponse with cost as string
    */
  override def updateRecipe(id: Long, updateRecipeRequest: UpdateRecipeRequest): UpdateRecipeResponse = {
    logger.debug(s"[updateRecipe] Updating recipe id=$id with $updateRecipeRequest")

    requireNonNegativeCost(updateRecipeRequest.cost)

    val recipe = requireRecipe(id)
    recipe.title = updateRecipeRequest.title
    recipe.makingTime = updateRecipeRequest.making_time
    recipe.serves = updateRecipeRequest.serves
    recipe.ingredients = updateRecipeRequest.ingredients
    recipe.cost = updateRecipeRequest.cost
    recipe.updatedAt = LocalDateTime.now()

    val updated = recipeRepository.update(recipe)
    logger.info(s"[updateRecipe] Recipe updated successfully: id=${updated.id}")

    val item = toUpdateItem(updated)

    UpdateRecipeResponse(
      message = Some(RecipeUpdatedSuccessfully),
      recipe = Some(List(item))
    )
  }

  /** Delete a recipe by ID. Throws NoSuchElementException if not found to be translated to 404.
    */
  override def deleteRecipe(id: Long): DeleteRecipeResponse = {
    logger.debug(s"[deleteRecipe] Deleting recipe id=$id")

    requireRecipe(id)
    recipeRepository.delete(id)

    logger.info(s"[deleteRecipe] Recipe deleted successfully: id=$id")
    DeleteRecipeResponse(message = Some(RecipeDeletedSuccessfully))
  }

  // Common date formatter for created_at/updated_at fields
  private val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  // Validate non-negative cost
  private def requireNonNegativeCost(cost: Int): Unit =
    if (cost < 0) throw new IllegalArgumentException("Cost must be >= 0")

  // Fetch a recipe or throw 404 via the error handler
  private def requireRecipe(id: Long): Recipe = {
    logger.warn(s"[requireRecipe] Recipe not found: id=$id")
    recipeRepository.findById(id).getOrElse(throw new NoSuchElementException())
  }

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
