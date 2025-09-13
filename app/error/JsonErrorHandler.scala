package error

import jakarta.inject.Inject
import play.api.http.HttpErrorHandler
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.{Environment, Logging, Mode}
import constant.ApplicationConstants._

import scala.concurrent.{ExecutionContext, Future}

class JsonErrorHandler @Inject() (env: Environment)(using ec: ExecutionContext) extends HttpErrorHandler with Logging {

  private val requiredFields = "title, making_time, serves, ingredients, cost"

  /** Handle 4xx client errors producing a JSON body with optional 'required' for createRecipe. */
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val (msg, required) =
      if (isCreateRecipe(request) && statusCode == 400)
        (RecipeCreationFailed, Some(requiredFields))
      else
        (if (message == null || message.trim.isEmpty) defaultClientMessage(statusCode) else message, None)

    Future.successful(Results.Status(statusCode)(payload(msg, required)))
  }

  /** Handle 5xx server errors and common exceptions, mapping to appropriate status and JSON payload. */
  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
    val (status, msg, required) = ex match {
      // Treat common bad input/validation cases as 400
      case _: IllegalArgumentException             => asClientOrCreate(request, 400)
      case _: play.api.libs.json.JsResultException => asClientOrCreate(request, 400, "Malformed JSON")
      // Not found-like cases
      case _: jakarta.persistence.EntityNotFoundException => (404, RecipeNotFound, None)
      case _: NoSuchElementException                      => (404, RecipeNotFound, None)
      // Conflict/constraint
      case _: org.hibernate.exception.ConstraintViolationException =>
        if (isCreateRecipe(request)) (409, RecipeCreationFailed, Some(requiredFields))
        else (409, Conflict, None)
      case _: java.sql.SQLIntegrityConstraintViolationException =>
        if (isCreateRecipe(request)) (409, RecipeCreationFailed, Some(requiredFields))
        else (409, Conflict, None)
      // Fallback
      case _ =>
        if (isCreateRecipe(request)) (500, RecipeCreationFailed, Some(requiredFields))
        else (500, InternalServerError, None)
    }

    // Log errors with stacktrace in all modes; responses stay JSON-only
    logger.error(s"Unhandled exception at ${request.method} ${request.uri}: ${ex.getMessage}", ex)

    val body = payload(msg, required)
    Future.successful(Results.Status(status)(body))
  }

  /** Utility to produce client-like responses or create-specific responses including required fields. */
  private def asClientOrCreate(
    request: RequestHeader,
    status: Int,
    fallbackMessage: String = BadRequest
  ): (Int, String, Option[String]) = {
    if (isCreateRecipe(request)) (status, RecipeCreationFailed, Some(requiredFields))
    else (status, fallbackMessage, None)
  }

  /** Build the JSON error payload with message and optional required field list. */
  private def payload(message: String, required: Option[String]): JsObject = {
    Json.obj("message" -> message) ++ required.fold(Json.obj())(r => Json.obj("required" -> r))
  }

  /** Return true when the request targets POST /recipes (create endpoint). */
  private def isCreateRecipe(request: RequestHeader): Boolean =
    request.method.equalsIgnoreCase("POST") && request.path == "/recipes"

  /** Provide default text for common client status codes. */
  private def defaultClientMessage(status: Int): String =
    status match {
      case 400 => BadRequest
      case 404 => ResourceNotFound
      case 409 => Conflict
      case _   => s"Client error ($status)"
    }
}
