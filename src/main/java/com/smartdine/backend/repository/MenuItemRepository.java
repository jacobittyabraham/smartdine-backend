package com.smartdine.backend.repository;

import com.smartdine.backend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByAvailableTrue();

    List<MenuItem> findByCategoryIgnoreCase(String category);

    List<MenuItem> findByFeaturedTrueAndAvailableTrue();
}