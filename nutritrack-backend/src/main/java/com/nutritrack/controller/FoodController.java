package com.nutritrack.controller;

import com.nutritrack.model.Food;
import com.nutritrack.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API endpoints for the food database.
 * Supports search, filtering by cuisine, dietary flags, etc.
 *
 * Base URL: /api/foods
 */
@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodController {

    private final FoodService foodService;

    // GET /api/foods — Get all foods, with optional search/category filters
    // Example: GET /api/foods?query=chicken&category=Indian
    @GetMapping
    public ResponseEntity<List<Food>> getFoods(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(foodService.searchFoods(query, category));
    }

    // GET /api/foods/{id} — Get a specific food by ID
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFood(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    // GET /api/foods/categories — Get list of all cuisine categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(foodService.getAllCategories());
    }

    // GET /api/foods/vegetarian — Get all vegetarian foods
    @GetMapping("/vegetarian")
    public ResponseEntity<List<Food>> getVegetarian() {
        return ResponseEntity.ok(foodService.getVegetarianFoods());
    }

    // GET /api/foods/vegan — Get all vegan foods
    @GetMapping("/vegan")
    public ResponseEntity<List<Food>> getVegan() {
        return ResponseEntity.ok(foodService.getVeganFoods());
    }

    // GET /api/foods/gluten-free — Get all gluten-free foods
    @GetMapping("/gluten-free")
    public ResponseEntity<List<Food>> getGlutenFree() {
        return ResponseEntity.ok(foodService.getGlutenFreeFoods());
    }

    // GET /api/foods/halal — Get all halal foods
    @GetMapping("/halal")
    public ResponseEntity<List<Food>> getHalal() {
        return ResponseEntity.ok(foodService.getHalalFoods());
    }

    // GET /api/foods/low-calorie?max=300 — Get foods under a calorie threshold
    @GetMapping("/low-calorie")
    public ResponseEntity<List<Food>> getLowCalorie(
            @RequestParam(defaultValue = "300") int max) {
        return ResponseEntity.ok(foodService.getLowCalorieFoods(max));
    }

    // GET /api/foods/high-protein?min=20 — Get high protein foods
    @GetMapping("/high-protein")
    public ResponseEntity<List<Food>> getHighProtein(
            @RequestParam(defaultValue = "20") double min) {
        return ResponseEntity.ok(foodService.getHighProteinFoods(min));
    }

    // POST /api/foods — Add a new food to the database
    @PostMapping
    public ResponseEntity<Food> addFood(@Valid @RequestBody Food food) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodService.addFood(food));
    }

    // PUT /api/foods/{id} — Update an existing food
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable Long id,
                                            @Valid @RequestBody Food food) {
        return ResponseEntity.ok(foodService.updateFood(id, food));
    }

    // DELETE /api/foods/{id} — Remove a food from the database
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
