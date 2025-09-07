package org.givery.recipe
package loader

import play.api.*
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}

class CustomApplicationLoader extends GuiceApplicationLoader() {
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    // Debug what Render is passing
    val envSecret = sys.env.get("PLAY_SECRET")
    println(s"=== DEBUG: PLAY_SECRET from env = [${envSecret.getOrElse("NOT SET")}] ===")

    super.builder(context)
  }
}
