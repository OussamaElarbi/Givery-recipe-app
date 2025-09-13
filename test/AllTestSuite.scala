import org.scalatest.Suites
import service.{RecipeApiImplIntegrationSpec, RecipeApiImplSpec}

class AllTestSuite
    extends Suites(
      new RecipeApiImplSpec,
      new RecipeApiImplIntegrationSpec
    )
