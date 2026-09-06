package com.scaler.parking_lot.respositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.scaler.parking_lot.models.BaseModel;

public class InMemoryRepository <T extends BaseModel>  {
    // This class is a placeholder for an in-memory repository implementation.
    // It can be used to store and retrieve data related to the parking lot system.
    protected final Map<Long, T> items = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public  T save(T item) {
        if (item.getId() == 0) {
            item.setId(nextId.getAndIncrement());
            item.setCreatedAt(new Date());
        }
        item.setUpdatedAt(new Date());
        items.put(item.getId(), item);
        return item;
    }

    public  Optional<T> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public  List<T> findAll() {
        return new ArrayList<>(items.values());
    }

    
}
