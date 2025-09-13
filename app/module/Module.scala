package module

import com.google.inject.AbstractModule
import org.givery.recipe.api.RecipeApi
import service.RecipeApiImpl

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[RecipeApi]).to(classOf[RecipeApiImpl])
  }
}
