package model

import jakarta.persistence.*

import java.time.LocalDateTime
import scala.annotation.meta.field

@Entity
@Table(name = "recipes")
class Recipe(
  @(Id @field)
  @(GeneratedValue @field)(strategy = GenerationType.IDENTITY)
  var id: Long = 0L,
  @(Column @field)(name = "title", nullable = false, length = 100)
  var title: String = "",
  @(Column @field)(name = "making_time", nullable = false, length = 100)
  var makingTime: String = "",
  @(Column @field)(name = "serves", nullable = false, length = 100)
  var serves: String = "",
  @(Column @field)(name = "ingredients", nullable = false, length = 300)
  var ingredients: String = "",
  @(Column @field)(name = "cost", nullable = false)
  var cost: Int = 0,
  @(Column @field)(name = "created_at", nullable = false, updatable = false, insertable = false)
  var createdAt: LocalDateTime = LocalDateTime.now,
  @(Column @field)(name = "updated_at", nullable = false)
  var updatedAt: LocalDateTime = LocalDateTime.now
) {

  // Explicit no-arg constructor for JPA
  def this() = this(0L, "", "", "", "", 0, LocalDateTime.now, LocalDateTime.now)
}
