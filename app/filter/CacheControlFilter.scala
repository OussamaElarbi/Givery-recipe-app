package filter

import org.apache.pekko.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CacheControlFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    nextFilter(rh).map { result =>
      if (rh.method == "GET" && rh.path.startsWith("/recipes")) {
        result.withHeaders("Cache-Control" -> "max-age=300")
      } else {
        result
      }
    }
  }
}
