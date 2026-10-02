package com.smartdine.backend.controller;

import com.smartdine.backend.entity.MenuItem;
import com.smartdine.backend.repository.MenuItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:5173")
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    public List<MenuItem> getAvailableMenu() {
        return menuItemRepository.findByAvailableTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(
            @PathVariable Long id
    ) {
        return menuItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public List<MenuItem> getByCategory(
            @PathVariable String category
    ) {
        return menuItemRepository.findByCategoryIgnoreCase(category);
    }

    @GetMapping("/featured")
    public List<MenuItem> getFeaturedMenu() {
        return menuItemRepository.findByFeaturedTrueAndAvailableTrue();
    }
}