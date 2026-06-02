package com.nutritrack.service;

import com.nutritrack.model.Food;
import com.nutritrack.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for food database queries, search, and management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;

    // ---- Create ----
    public Food addFood(Food food) {
        log.info("Adding new food to database: {}", food.getName());
        return foodRepository.save(food);
    }

    // ---- Read ----
    @Transactional(readOnly = true)
    public Food getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Food> searchFoods(String query, String category) {
        if (query != null && !query.isBlank() && category != null && !category.isBlank()) {
            return foodRepository.findByNameContainingIgnoreCaseAndCategoryIgnoreCase(query, category);
        } else if (query != null && !query.isBlank()) {
            return foodRepository.findByNameContainingIgnoreCase(query);
        } else if (category != null && !category.isBlank()) {
            return foodRepository.findByCategoryIgnoreCase(category);
        }
        return foodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return foodRepository.findAllCategories();
    }

    @Transactional(readOnly = true)
    public List<Food> getVegetarianFoods() {
        return foodRepository.findByIsVegetarianTrue();
    }

    @Transactional(readOnly = true)
    public List<Food> getVeganFoods() {
        return foodRepository.findByIsVeganTrue();
    }

    @Transactional(readOnly = true)
    public List<Food> getGlutenFreeFoods() {
        return foodRepository.findByIsGlutenFreeTrue();
    }

    @Transactional(readOnly = true)
    public List<Food> getHalalFoods() {
        return foodRepository.findByIsHalalTrue();
    }

    @Transactional(readOnly = true)
    public List<Food> getLowCalorieFoods(int maxCalories) {
        return foodRepository.findByCaloriesPerServingLessThanEqual(maxCalories);
    }

    @Transactional(readOnly = true)
    public List<Food> getHighProteinFoods(double minProteinGrams) {
        return foodRepository.findByProteinGramsGreaterThanEqual(minProteinGrams);
    }

    // ---- Update ----
    public Food updateFood(Long id, Food updatedData) {
        Food existing = getFoodById(id);
        existing.setName(updatedData.getName());
        existing.setCategory(updatedData.getCategory());
        existing.setServingDescription(updatedData.getServingDescription());
        existing.setCaloriesPerServing(updatedData.getCaloriesPerServing());
        existing.setProteinGrams(updatedData.getProteinGrams());
        existing.setCarbsGrams(updatedData.getCarbsGrams());
        existing.setFatGrams(updatedData.getFatGrams());
        existing.setFiberGrams(updatedData.getFiberGrams());
        existing.setSodiumMg(updatedData.getSodiumMg());
        existing.setSugarGrams(updatedData.getSugarGrams());
        existing.setIsVegetarian(updatedData.getIsVegetarian());
        existing.setIsVegan(updatedData.getIsVegan());
        existing.setIsGlutenFree(updatedData.getIsGlutenFree());
        existing.setIsDairyFree(updatedData.getIsDairyFree());
        existing.setIsHalal(updatedData.getIsHalal());
        existing.setIsKosher(updatedData.getIsKosher());
        return foodRepository.save(existing);
    }

    // ---- Delete ----
    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
        log.info("Deleted food with ID: {}", id);
    }
}
