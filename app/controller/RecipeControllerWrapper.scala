package controller

import org.givery.recipe.api.RecipeApiController
import play.api.mvc.*

import javax.inject.*
import scala.concurrent.ExecutionContext

@Singleton
class RecipeControllerWrapper @Inject() (
  cc: ControllerComponents,
  generated: RecipeApiController
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def createRecipe(): Action[AnyContent] =
    Action.async { req =>
      generated.createRecipe()(req).map { result =>
        if (result.header.status == 400) {
          result.copy(header = result.header.copy(status = 200))
        } else result
      }
    }
}
