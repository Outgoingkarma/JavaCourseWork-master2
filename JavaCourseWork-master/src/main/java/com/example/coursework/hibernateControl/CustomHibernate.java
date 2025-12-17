package com.example.coursework.hibernateControl;

import com.example.coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class CustomHibernate extends GenericHibernate {
    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String password) {
        try {
            entityManager = entityManagerFactory.createEntityManager();

            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (user == null) return null;

            String stored = user.getPassword();
            if (stored == null || stored.isBlank()) return null;

            boolean ok;
            // bcrypt hash usually starts with $2a$, $2b$, $2y$
            if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
                ok = BCrypt.checkpw(password, stored);
            } else {
                // legacy plaintext support
                ok = stored.equals(password);

                // optional auto-upgrade plaintext -> bcrypt
                if (ok) {
                    entityManager.getTransaction().begin();
                    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
                    entityManager.merge(user);
                    entityManager.getTransaction().commit();
                }
            }

            return ok ? user : null;

        } catch (Exception e) {
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
            entityManager = null;
        }
    }

    public List<Dishes> getDishesByRestaurant(int restaurantId) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<Dishes> dishes = entityManager.createQuery(
                            "SELECT DISTINCT d FROM Dishes d LEFT JOIN FETCH d.dishesIngredients WHERE d.restaurant.id = :restaurantId",
                            Dishes.class)
                    .setParameter("restaurantId", restaurantId)
                    .getResultList();
            dishes.forEach(dish -> dish.getOrders().size());
            return dishes;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            entityManager = null;
        }
    }

    public List<DishesIngredients> getDishIngredients(int dishId) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return entityManager.createQuery(
                            "SELECT ingredient FROM Dishes d JOIN d.dishesIngredients ingredient WHERE d.id = :dishId",
                            DishesIngredients.class)
                    .setParameter("dishId", dishId)
                    .getResultList();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            entityManager = null;
        }
    }

//    public List<Dishes> getDishesByRestaurant(int restaurantId) {
//        List<Dishes> dishes = null;
//        try {
//            entityManager = entityManagerFactory.createEntityManager();
//            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//            CriteriaQuery<Dishes> query = cb.createQuery(Dishes.class);
//            Root<Dishes> root = query.from(Dishes.class);
//
//            query.select(root).where(
//                    cb.equal(root.get("restaurant_id"), restaurantId)
//            );
//            Query q = entityManager.createQuery(query);
//            dishes = q.getResultList();
//        } catch (Exception e) {
//        }
//
//        return dishes;
//    }

    public FoodOrder getFoodOrderWithDishes(int orderId) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<FoodOrder> orders = entityManager.createQuery(
                            "SELECT f FROM FoodOrder f LEFT JOIN FETCH f.dishes WHERE f.id = :orderId",
                            FoodOrder.class)
                    .setParameter("orderId", orderId)
                    .getResultList();
            return orders.isEmpty() ? null : orders.get(0);
        } catch (Exception e) {
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            entityManager = null;
        }

    }


}
