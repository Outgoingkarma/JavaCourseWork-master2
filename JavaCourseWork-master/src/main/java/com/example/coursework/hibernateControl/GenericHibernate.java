package com.example.coursework.hibernateControl;

import com.example.coursework.utils.FxUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {

    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    public <T> void create(T entity) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Failed to persist " + entity.getClass().getSimpleName(), e);

        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public <T> void update(T entity) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateExceptionAlert(e);

        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    //    public <T> T update(T entity) {
//        EntityManager em = null;
//        EntityTransaction tx = null;
//        try {
//            em = entityManagerFactory.createEntityManager();
//            tx = em.getTransaction();
//            tx.begin();
//            T merged = em.merge(entity);
//            tx.commit();
//            return merged;
//        } catch (RuntimeException e) {
//            if (tx != null && tx.isActive()) tx.rollback();
//            throw e;
//        } finally {
//            if (em != null) em.close();
//        }
//    }
    public <T, ID> T updateById(Class<T> type, ID id, java.util.function.Consumer<T> mutator) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            T managed = em.find(type, id);
            if (managed == null)
                throw new IllegalArgumentException("Entity not found: " + type.getSimpleName() + " id=" + id);

            mutator.accept(managed);

            tx.commit();
            return managed;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (em != null) em.close();
        }
    }


//    public <T> void delete(T entity, int id) {
//        try {
//            entityManager = entityManagerFactory.createEntityManager();
//            entityManager.getTransaction().begin();
//            entityManager.remove(entity);
//        } catch (Exception e) {
//            //FxUtils.generateExceptionAlert(e);
//            System.out.println("Error deleting entity with id: " + id);
//            e.printStackTrace();
//        } finally {
//            if (entityManager != null) entityManager.close();
//        }
//    }

    public <T> void delete(Class<T> entityClass, Object id) {
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();
            T ref = em.getReference(entityClass, id);
            em.remove(ref);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            if (em != null) em.close();
        }
    }


    public <T> List<T> getAllRecords(Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(entityClass);
            query.select(query.from(entityClass));
            Query q = entityManager.createQuery(query);
            list = q.getResultList();
        } catch (Exception e) {
            FxUtils.generateExceptionAlert(e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return list;
    }

    public <T> T getRecordById(Class<T> entityClass, int id) {
        T entity = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entity = entityManager.find(entityClass, id);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateExceptionAlert(e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return entity;
    }


    public <T> List<T> findByName(Class<T> entityClass, String name) {
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            String pattern = "%" + (name == null ? "" : name.trim().toLowerCase()) + "%";
            cq.select(root).where(
                    cb.or(
                            cb.like(cb.lower(root.get("name")), pattern)
                    )
            );

            return em.createQuery(cq).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    public <T> List<T> findBySurname(Class<T> entityClass, String surname) {
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);


            String pattern = "%" + (surname == null ? "" : surname.trim().toLowerCase()) + "%";
            cq.select(root).where(
                    cb.or(
                            cb.like(cb.lower(root.get("surname")), pattern)
                    )
            );

            return em.createQuery(cq).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    public <T> List<T> findByNameAndSurname(Class<T> entityClass, String name, String surname) {
        String n = name == null ? "" : name.trim();
        String s = surname == null ? "" : surname.trim();
        if (n.isEmpty() || s.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();
            var cb = em.getCriteriaBuilder();
            var cq = cb.createQuery(entityClass);
            var root = cq.from(entityClass);
            String pName = "%" + escapeLike(n.toLowerCase()) + "%";
            String pSurname = "%" + escapeLike(s.toLowerCase()) + "%";
            cq.select(root).where(
                    cb.and(
                            cb.like(cb.lower(root.get("name")), pName, '\\'),
                            cb.like(cb.lower(root.get("surname")), pSurname, '\\')
                    )
            );
            return em.createQuery(cq).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }


}

