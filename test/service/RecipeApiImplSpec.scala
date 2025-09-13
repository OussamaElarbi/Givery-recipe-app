package service

import constant.ApplicationConstants.*
import model.Recipe
import org.givery.recipe.model.*
import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import repository.RecipeRepository

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecipeApiImplSpec extends PlaySpec with BeforeAndAfterEach {

  private var mockRepository: RecipeRepository = _
  private var recipeApiImpl: RecipeApiImpl = _
  private val testDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
  private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  override def beforeEach(): Unit = {
    mockRepository = mock(classOf[RecipeRepository])
    recipeApiImpl = new RecipeApiImpl(mockRepository)
  }

  "RecipeApiImpl" should {

    "createRecipe" should {

      "successfully create a recipe with valid data" in {
        val request = CreateRecipeRequest(
          title = "Test Recipe",
          making_time = "30 minutes",
          serves = "4 people",
          ingredients = "flour, eggs, milk",
          cost = 500
        )

        val savedRecipe = new Recipe(
          id = 1L,
          title = "Test Recipe",
          makingTime = "30 minutes",
          serves = "4 people",
          ingredients = "flour, eggs, milk",
          cost = 500,
          createdAt = testDateTime,
          updatedAt = testDateTime
        )

        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        val result = recipeApiImpl.createRecipe(request)

        result.message mustBe Some(RecipeCreatedSuccessfully)
        result.recipe mustBe defined
        result.recipe.get must have size 1

        val responseRecipe = result.recipe.get.head
        responseRecipe.id mustBe Some(1L)
        responseRecipe.title mustBe Some("Test Recipe")
        responseRecipe.making_time mustBe Some("30 minutes")
        responseRecipe.serves mustBe Some("4 people")
        responseRecipe.ingredients mustBe Some("flour, eggs, milk")
        responseRecipe.cost mustBe Some(500)
        responseRecipe.created_at mustBe Some(testDateTime.format(fmt))
        responseRecipe.updated_at mustBe Some(testDateTime.format(fmt))

        verify(mockRepository).save(any[Recipe])
      }

      "throw IllegalArgumentException for negative cost" in {
        val request = CreateRecipeRequest(
          title = "Test Recipe",
          making_time = "30 minutes",
          serves = "4 people",
          ingredients = "flour, eggs, milk",
          cost = -100
        )

        an[IllegalArgumentException] must be thrownBy {
          recipeApiImpl.createRecipe(request)
        }

        verify(mockRepository, never()).save(any[Recipe])
      }

      "accept zero cost as valid" in {
        val request = CreateRecipeRequest(
          title = "Test Recipe",
          making_time = "30 minutes",
          serves = "4 people",
          ingredients = "flour, eggs, milk",
          cost = 0
        )

        val savedRecipe = new Recipe(
          id = 1L,
          title = "Test Recipe",
          makingTime = "30 minutes",
          serves = "4 people",
          ingredients = "flour, eggs, milk",
          cost = 0,
          createdAt = testDateTime,
          updatedAt = testDateTime
        )

        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        noException must be thrownBy {
          recipeApiImpl.createRecipe(request)
        }

        verify(mockRepository).save(any[Recipe])
      }
    }

    "listRecipes" should {

      "return empty list when no recipes exist" in {
        when(mockRepository.findAll()).thenReturn(List.empty)

        val result = recipeApiImpl.listRecipes()

        result.recipes mustBe Some(List.empty)
        verify(mockRepository).findAll()
      }

      "return list of recipes when recipes exist" in {
        val recipe1 = new Recipe(1L, "Recipe 1", "10 min", "2", "ingredient1", 100, testDateTime, testDateTime)
        val recipe2 = new Recipe(2L, "Recipe 2", "20 min", "4", "ingredient2", 200, testDateTime, testDateTime)

        when(mockRepository.findAll()).thenReturn(List(recipe1, recipe2))

        val result = recipeApiImpl.listRecipes()

        result.recipes mustBe defined
        result.recipes.get must have size 2

        val items = result.recipes.get
        items.head.id mustBe 1L
        items.head.title mustBe "Recipe 1"
        items.head.cost mustBe "100"

        items(1).id mustBe 2L
        items(1).title mustBe "Recipe 2"
        items(1).cost mustBe "200"

        verify(mockRepository).findAll()
      }

      "convert cost to string in recipe list items" in {
        val recipe = new Recipe(1L, "Recipe", "10 min", "2", "ingredients", 999, testDateTime, testDateTime)
        when(mockRepository.findAll()).thenReturn(List(recipe))

        val result = recipeApiImpl.listRecipes()

        result.recipes.get.head.cost mustBe "999"
      }
    }

    "getRecipeById" should {

      "return recipe when found" in {
        val recipe = new Recipe(1L, "Test Recipe", "30 min", "4", "ingredients", 500, testDateTime, testDateTime)
        when(mockRepository.findById(1L)).thenReturn(Some(recipe))

        val result = recipeApiImpl.getRecipeById(1L)

        result.message mustBe Some(RecipeDetailsById)
        result.recipe mustBe defined
        result.recipe.get must have size 1

        val item = result.recipe.get.head
        item.id mustBe 1L
        item.title mustBe "Test Recipe"
        item.cost mustBe "500"

        verify(mockRepository).findById(1L)
      }

      "throw NoSuchElementException when recipe not found" in {
        when(mockRepository.findById(999L)).thenReturn(None)

        a[NoSuchElementException] must be thrownBy {
          recipeApiImpl.getRecipeById(999L)
        }

        verify(mockRepository).findById(999L)
      }
    }

    "updateRecipe" should {

      "successfully update existing recipe" in {
        val existingRecipe =
          new Recipe(1L, "Old Title", "old time", "old serves", "old ingredients", 100, testDateTime, testDateTime)
        val request = UpdateRecipeRequest(
          title = "New Title",
          making_time = "new time",
          serves = "new serves",
          ingredients = "new ingredients",
          cost = 200
        )

        when(mockRepository.findById(1L)).thenReturn(Some(existingRecipe))
        when(mockRepository.update(any[Recipe])).thenReturn(existingRecipe)

        val result = recipeApiImpl.updateRecipe(1L, request)

        result.message mustBe Some(RecipeUpdatedSuccessfully)
        result.recipe mustBe defined
        result.recipe.get must have size 1

        val item = result.recipe.get.head
        item.title mustBe "New Title"
        item.making_time mustBe "new time"
        item.serves mustBe "new serves"
        item.ingredients mustBe "new ingredients"
        item.cost mustBe "200"

        existingRecipe.title mustBe "New Title"
        existingRecipe.makingTime mustBe "new time"
        existingRecipe.serves mustBe "new serves"
        existingRecipe.ingredients mustBe "new ingredients"
        existingRecipe.cost mustBe 200

        verify(mockRepository).findById(1L)
        verify(mockRepository).update(existingRecipe)
      }

      "throw NoSuchElementException when recipe to update not found" in {
        val request = UpdateRecipeRequest("title", "time", "serves", "ingredients", 100)
        when(mockRepository.findById(999L)).thenReturn(None)

        a[NoSuchElementException] must be thrownBy {
          recipeApiImpl.updateRecipe(999L, request)
        }

        verify(mockRepository).findById(999L)
        verify(mockRepository, never()).update(any[Recipe])
      }

      "throw IllegalArgumentException for negative cost in update" in {
        val request = UpdateRecipeRequest("title", "time", "serves", "ingredients", -50)

        an[IllegalArgumentException] must be thrownBy {
          recipeApiImpl.updateRecipe(1L, request)
        }

        verify(mockRepository, never()).findById(anyLong())
        verify(mockRepository, never()).update(any[Recipe])
      }

      "update recipe with zero cost" in {
        val existingRecipe = new Recipe(1L, "Title", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        val request = UpdateRecipeRequest("New Title", "new time", "new serves", "new ingredients", 0)

        when(mockRepository.findById(1L)).thenReturn(Some(existingRecipe))
        when(mockRepository.update(any[Recipe])).thenReturn(existingRecipe)

        val result = recipeApiImpl.updateRecipe(1L, request)

        result.recipe.get.head.cost mustBe "0"
        existingRecipe.cost mustBe 0

        verify(mockRepository).findById(1L)
        verify(mockRepository).update(existingRecipe)
      }

      "update updatedAt timestamp" in {
        val existingRecipe = new Recipe(1L, "Title", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        val request = UpdateRecipeRequest("New Title", "new time", "new serves", "new ingredients", 200)

        when(mockRepository.findById(1L)).thenReturn(Some(existingRecipe))
        when(mockRepository.update(any[Recipe])).thenReturn(existingRecipe)

        val beforeUpdate = LocalDateTime.now()
        recipeApiImpl.updateRecipe(1L, request)
        val afterUpdate = LocalDateTime.now()

        existingRecipe.updatedAt.isAfter(beforeUpdate) || existingRecipe.updatedAt.isEqual(beforeUpdate) mustBe true
        existingRecipe.updatedAt.isBefore(afterUpdate) || existingRecipe.updatedAt.isEqual(afterUpdate) mustBe true
      }
    }

    "deleteRecipe" should {

      "successfully delete existing recipe" in {
        val recipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        when(mockRepository.findById(1L)).thenReturn(Some(recipe))

        val result = recipeApiImpl.deleteRecipe(1L)

        result.message mustBe Some(RecipeDeletedSuccessfully)

        verify(mockRepository).findById(1L)
        verify(mockRepository).delete(1L)
      }

      "throw NoSuchElementException when recipe to delete not found" in {
        when(mockRepository.findById(999L)).thenReturn(None)

        a[NoSuchElementException] must be thrownBy {
          recipeApiImpl.deleteRecipe(999L)
        }

        verify(mockRepository).findById(999L)
        verify(mockRepository, never()).delete(anyLong())
      }
    }

    "private helper methods" should {

      "requireNonNegativeCost accept positive cost" in {
        val request = CreateRecipeRequest("title", "time", "serves", "ingredients", 100)
        val savedRecipe = new Recipe(1L, "title", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        noException must be thrownBy {
          recipeApiImpl.createRecipe(request)
        }
      }

      "requireNonNegativeCost accept zero cost" in {
        val request = CreateRecipeRequest("title", "time", "serves", "ingredients", 0)
        val savedRecipe = new Recipe(1L, "title", "time", "serves", "ingredients", 0, testDateTime, testDateTime)
        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        noException must be thrownBy {
          recipeApiImpl.createRecipe(request)
        }
      }

      "requireNonNegativeCost throw for negative cost" in {
        val request = CreateRecipeRequest("title", "time", "serves", "ingredients", -1)

        an[IllegalArgumentException] must be thrownBy {
          recipeApiImpl.createRecipe(request)
        }
      }

      "requireRecipe return recipe when found" in {
        val recipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        when(mockRepository.findById(1L)).thenReturn(Some(recipe))

        val result = recipeApiImpl.getRecipeById(1L)
        result.recipe mustBe defined
      }

      "requireRecipe throw when not found" in {
        when(mockRepository.findById(999L)).thenReturn(None)

        a[NoSuchElementException] must be thrownBy {
          recipeApiImpl.getRecipeById(999L)
        }
      }

      "toListItem convert Recipe to RecipeListItem with cost as string" in {
        val recipe = new Recipe(1L, "Recipe", "30 min", "4", "ingredients", 500, testDateTime, testDateTime)
        when(mockRepository.findAll()).thenReturn(List(recipe))

        val result = recipeApiImpl.listRecipes()
        val item = result.recipes.get.head

        item.id mustBe 1L
        item.title mustBe "Recipe"
        item.making_time mustBe "30 min"
        item.serves mustBe "4"
        item.ingredients mustBe "ingredients"
        item.cost mustBe "500"
      }

      "toListItem handle zero cost conversion" in {
        val recipe = new Recipe(1L, "Recipe", "30 min", "4", "ingredients", 0, testDateTime, testDateTime)
        when(mockRepository.findAll()).thenReturn(List(recipe))

        val result = recipeApiImpl.listRecipes()
        result.recipes.get.head.cost mustBe "0"
      }

      "toUpdateItem convert Recipe to UpdateRecipeItem with cost as string" in {
        val existingRecipe = new Recipe(1L, "Recipe", "30 min", "4", "ingredients", 750, testDateTime, testDateTime)
        val request = UpdateRecipeRequest("Updated Recipe", "45 min", "6", "new ingredients", 750)

        when(mockRepository.findById(1L)).thenReturn(Some(existingRecipe))
        when(mockRepository.update(any[Recipe])).thenReturn(existingRecipe)

        val result = recipeApiImpl.updateRecipe(1L, request)
        val item = result.recipe.get.head

        item.title mustBe "Updated Recipe"
        item.making_time mustBe "45 min"
        item.serves mustBe "6"
        item.ingredients mustBe "new ingredients"
        item.cost mustBe "750"
      }

      "format dates correctly in createRecipe response" in {
        val request = CreateRecipeRequest("Recipe", "30 min", "4", "ingredients", 100)
        val savedRecipe = new Recipe(1L, "Recipe", "30 min", "4", "ingredients", 100, testDateTime, testDateTime)

        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        val result = recipeApiImpl.createRecipe(request)
        val responseRecipe = result.recipe.get.head

        responseRecipe.created_at mustBe Some("2024-01-01 12:00:00")
        responseRecipe.updated_at mustBe Some("2024-01-01 12:00:00")
      }
    }

    "edge cases and boundary conditions" should {

      "handle empty strings in recipe fields" in {
        val request = CreateRecipeRequest("", "", "", "", 0)
        val savedRecipe = new Recipe(1L, "", "", "", "", 0, testDateTime, testDateTime)

        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        val result = recipeApiImpl.createRecipe(request)

        result.recipe.get.head.title mustBe Some("")
        result.recipe.get.head.making_time mustBe Some("")
        result.recipe.get.head.serves mustBe Some("")
        result.recipe.get.head.ingredients mustBe Some("")
      }

      "handle maximum integer cost" in {
        val maxCost = Int.MaxValue
        val request = CreateRecipeRequest("Recipe", "time", "serves", "ingredients", maxCost)
        val savedRecipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", maxCost, testDateTime, testDateTime)

        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        val result = recipeApiImpl.createRecipe(request)
        result.recipe.get.head.cost mustBe Some(maxCost)
      }

      "handle large recipe lists" in {
        val recipes = (1 to 100)
          .map(i =>
            new Recipe(i.toLong, s"Recipe $i", "time", "serves", "ingredients", i * 10, testDateTime, testDateTime)
          )
          .toList

        when(mockRepository.findAll()).thenReturn(recipes)

        val result = recipeApiImpl.listRecipes()
        result.recipes.get must have size 100
        result.recipes.get.head.id mustBe 1L
        result.recipes.get.last.id mustBe 100L
      }
    }

    "verify all repository interactions" should {

      "call save for create operations" in {
        val request = CreateRecipeRequest("Recipe", "time", "serves", "ingredients", 100)
        val savedRecipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        when(mockRepository.save(any[Recipe])).thenReturn(savedRecipe)

        recipeApiImpl.createRecipe(request)
        verify(mockRepository).save(any[Recipe])
      }

      "call findAll for list operations" in {
        when(mockRepository.findAll()).thenReturn(List.empty)

        recipeApiImpl.listRecipes()
        verify(mockRepository).findAll()
      }

      "call findById for get operations" in {
        val recipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        when(mockRepository.findById(1L)).thenReturn(Some(recipe))

        recipeApiImpl.getRecipeById(1L)
        verify(mockRepository).findById(1L)
      }

      "call findById and update for update operations" in {
        val recipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        val request = UpdateRecipeRequest("Updated", "new time", "new serves", "new ingredients", 200)
        when(mockRepository.findById(1L)).thenReturn(Some(recipe))
        when(mockRepository.update(any[Recipe])).thenReturn(recipe)

        recipeApiImpl.updateRecipe(1L, request)
        verify(mockRepository).findById(1L)
        verify(mockRepository).update(any[Recipe])
      }

      "call findById and delete for delete operations" in {
        val recipe = new Recipe(1L, "Recipe", "time", "serves", "ingredients", 100, testDateTime, testDateTime)
        when(mockRepository.findById(1L)).thenReturn(Some(recipe))

        recipeApiImpl.deleteRecipe(1L)
        verify(mockRepository).findById(1L)
        verify(mockRepository).delete(1L)
      }
    }
  }
}
