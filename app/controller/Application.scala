package controller

import org.givery.recipe.api.RecipeApiController
import play.api.mvc.*

import javax.inject.*
import scala.concurrent.ExecutionContext

@Singleton
class Application @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok("API is up and running.")
  }
}
