package com.fil.shauni.db.spring.deprecated;

import java.util.List;

/**
 *
 * @author Chiara
 */
public interface GenericDao<E, K> {
    public void add(E entity);
    public void remove(E entity);
    public void update(E entity);
    public E find(K key);
    public List<E> findWhen(K key);
    public List<E> list();
}
