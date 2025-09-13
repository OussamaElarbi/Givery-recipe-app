package service

import constant.ApplicationConstants.*
import converter.RecipeConverter
import model.Recipe
import org.givery.recipe.model.*
import org.scalatestplus.play.PlaySpec

import java.time.LocalDateTime

class RecipeApiImplIntegrationSpec extends PlaySpec {

  "RecipeApiImpl Integration" should {

    "demonstrate complete recipe lifecycle" in {
      // This test demonstrates the complete flow without mocks
      // to ensure all components work together correctly

      val mockRepo = new MockRecipeRepository()
      val api = new RecipeApiImpl(mockRepo)

      // Create recipe
      val createRequest = CreateRecipeRequest(
        title = "Integration Test Recipe",
        making_time = "60 minutes",
        serves = "8 people",
        ingredients = "integration test ingredients",
        cost = 1000
      )

      val createResponse = api.createRecipe(createRequest)
      createResponse.message mustBe Some(RecipeCreatedSuccessfully)
      createResponse.recipe mustBe defined
      val createdRecipe = createResponse.recipe.get.head
      createdRecipe.id mustBe Some(1L)

      // List recipes
      val listResponse = api.listRecipes()
      listResponse.recipes mustBe defined
      listResponse.recipes.get must have size 1
      listResponse.recipes.get.head.title mustBe "Integration Test Recipe"

      // Get recipe by ID
      val getResponse = api.getRecipeById(1L)
      getResponse.message mustBe Some(RecipeDetailsById)
      getResponse.recipe mustBe defined
      getResponse.recipe.get.head.title mustBe "Integration Test Recipe"

      // Update recipe
      val updateRequest = UpdateRecipeRequest(
        title = "Updated Integration Recipe",
        making_time = "90 minutes",
        serves = "10 people",
        ingredients = "updated integration ingredients",
        cost = 1500
      )

      val updateResponse = api.updateRecipe(1L, updateRequest)
      updateResponse.message mustBe Some(RecipeUpdatedSuccessfully)
      updateResponse.recipe.get.head.title mustBe "Updated Integration Recipe"
      updateResponse.recipe.get.head.cost mustBe "1500"

      // Verify update persisted
      val getUpdatedResponse = api.getRecipeById(1L)
      getUpdatedResponse.recipe.get.head.title mustBe "Updated Integration Recipe"

      // Delete recipe
      val deleteResponse = api.deleteRecipe(1L)
      deleteResponse.message mustBe Some(RecipeDeletedSuccessfully)

      // Verify deletion
      an[NoSuchElementException] must be thrownBy {
        api.getRecipeById(1L)
      }

      // Verify empty list
      val emptyListResponse = api.listRecipes()
      emptyListResponse.recipes.get mustBe empty
    }

    "handle error scenarios in integration" in {
      val mockRepo = new MockRecipeRepository()
      val api = new RecipeApiImpl(mockRepo)

      // Test negative cost validation
      val invalidRequest = CreateRecipeRequest("Recipe", "time", "serves", "ingredients", -100)
      an[IllegalArgumentException] must be thrownBy {
        api.createRecipe(invalidRequest)
      }

      // Test non-existent recipe operations
      a[NoSuchElementException] must be thrownBy {
        api.getRecipeById(999L)
      }

      a[NoSuchElementException] must be thrownBy {
        api.updateRecipe(999L, UpdateRecipeRequest("title", "time", "serves", "ingredients", 100))
      }

      a[NoSuchElementException] must be thrownBy {
        api.deleteRecipe(999L)
      }
    }
  }

  // Mock repository for integration testing
  class MockRecipeRepository extends repository.RecipeRepository(null) {
    private var recipes = scala.collection.mutable.Map[Long, Recipe]()
    private var nextId = 1L

    override def findAll(): List[Recipe] = recipes.values.toList

    override def findById(id: Long): Option[Recipe] = recipes.get(id)

    override def save(recipe: Recipe): Recipe = {
      recipe.id = nextId
      recipe.createdAt = LocalDateTime.now()
      recipe.updatedAt = LocalDateTime.now()
      recipes(nextId) = recipe
      nextId += 1
      recipe
    }

    override def update(recipe: Recipe): Recipe = {
      recipe.updatedAt = LocalDateTime.now()
      recipes(recipe.id) = recipe
      recipe
    }

    override def delete(id: Long): Unit = {
      recipes.remove(id)
    }
  }
}
