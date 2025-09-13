package filter

import play.api.http.HttpFilters
import play.filters.gzip.GzipFilter

import javax.inject.{Inject, Singleton}

@Singleton
class Filters @Inject() (
  gzipFilter: GzipFilter,
  cacheControlFilter: CacheControlFilter
) extends HttpFilters {

  override val filters = Seq(gzipFilter, cacheControlFilter)
}
