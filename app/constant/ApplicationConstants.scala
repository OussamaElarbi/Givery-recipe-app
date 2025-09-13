package constant

/** Centralized application-wide constants. Use these to avoid hardcoding strings across services/controllers/handlers.
  */
object ApplicationConstants {
  // Messages - success
  val RecipeCreatedSuccessfully = "Recipe successfully created!"
  val RecipeUpdatedSuccessfully = "Recipe successfully updated!"
  val RecipeDeletedSuccessfully = "Recipe successfully removed!"

  // Messages - info
  val RecipeDetailsById = "Recipe details by id"

  // Messages - error
  val RecipeCreationFailed = "Recipe creation failed!"
  val RecipeNotFound = "No recipe found"
  val ResourceNotFound = "Resource not found"
  val Conflict = "Conflict"
  val InternalServerError = "Internal server error"
  val BadRequest = "Bad request"

  // Validation messages
  val CostMustBeNonNegative = "Cost must be >= 0"

  // Formatting
  val DefaultDateTimePattern = "yyyy-MM-dd HH:mm:ss"
}
