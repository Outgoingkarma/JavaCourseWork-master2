package com.example.coursework.hibernateControl;

import com.example.coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class CustomHibernate extends GenericHibernate {
    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String password) {
        User user = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.and(
                    cb.equal(root.get("login"), username),
                    cb.equal(root.get("password"), password)
            ));
            Query q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();
        } catch (Exception e) {
        }

        return user;
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
