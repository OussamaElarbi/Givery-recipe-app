package repository

import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.EntityManager
import model.Recipe
import play.db.jpa.JPAApi

import java.util.function.Consumer
import scala.jdk.CollectionConverters.*

@Singleton
/** Data access for Recipe entities using Play JPAApi. Each method executes inside a transaction. */
class RecipeRepository @Inject() (jpaApi: JPAApi) {

  /** Fetch all recipes. */
  def findAll(): List[Recipe] =
    jpaApi.withTransaction { em =>
      em.createQuery("SELECT r FROM Recipe r", classOf[Recipe]).getResultList.asScala.toList
    }

  /** Find a recipe by its id. Returns None if not found. */
  def findById(id: Long): Option[Recipe] =
    jpaApi.withTransaction { em =>
      Option(em.find(classOf[Recipe], id))
    }

  /** Persist a new recipe and return the managed entity (with generated id). */
  def save(recipe: Recipe): Recipe =
    jpaApi.withTransaction { em =>
      em.persist(recipe)
      recipe
    }

  /** Merge changes into an existing recipe and return the managed entity. */
  def update(recipe: Recipe): Recipe =
    jpaApi.withTransaction { em =>
      em.merge(recipe)
    }

  /** Delete a recipe by id if it exists; otherwise no-op. */
  def delete(id: Long): Unit =
    jpaApi.withTransaction(new Consumer[EntityManager] {
      override def accept(em: EntityManager): Unit = {
        val recipe = em.find(classOf[Recipe], id)
        if (recipe != null) em.remove(recipe)
      }
    })
}
