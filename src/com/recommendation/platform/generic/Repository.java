package com.recommendation.platform.generic;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Unit 5 Requirement: Generics Implementation
 * Generic repository interface defining standard CRUD and filtering operations.
 *
 * @param <T>  Entity type
 * @param <ID> Unique identifier type
 */
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> filter(Predicate<T> predicate);
    void deleteById(ID id);
    int count();
}
