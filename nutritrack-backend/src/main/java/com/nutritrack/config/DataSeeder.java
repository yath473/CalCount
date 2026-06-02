package com.nutritrack.config;

import com.nutritrack.model.Food;
import com.nutritrack.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the food database with 70+ foods across multiple cuisines
 * on first application startup (only if the table is empty).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final FoodRepository foodRepository;

    @Override
    public void run(String... args) {
        if (foodRepository.count() > 0) {
            log.info("Food database already seeded. Skipping.");
            return;
        }
        log.info("Seeding food database...");
        foodRepository.saveAll(buildFoodList());
        log.info("Food database seeded with {} items.", foodRepository.count());
    }

    private List<Food> buildFoodList() {
        return List.of(
            // ── American ──
            food("Cheeseburger",              "American",     540, 30.0, 40.0, 28.0, 2.0,  "1 burger",     false, false, false, false, false),
            food("Grilled Chicken Sandwich",  "American",     420, 36.0, 38.0, 12.0, 2.0,  "1 sandwich",   false, false, false, false, false),
            food("Caesar Salad",              "American",     310, 12.0, 18.0, 22.0, 3.0,  "1 bowl",       false, false, true,  false, false),
            food("French Fries",              "American",     365,  4.0, 48.0, 17.0, 4.0,  "medium",       true,  true,  true,  true,  false),
            food("Mac and Cheese",            "American",     470, 18.0, 58.0, 18.0, 2.0,  "1 cup",        true,  false, false, false, false),
            food("BBQ Ribs",                  "American",     620, 48.0, 12.0, 42.0, 1.0,  "6 oz",         false, false, true,  false, false),
            food("Pancakes (3)",              "American",     350,  8.0, 60.0,  8.0, 2.0,  "3 pancakes",   true,  false, false, false, false),
            food("Club Sandwich",             "American",     480, 34.0, 40.0, 20.0, 3.0,  "1 sandwich",   false, false, false, false, false),
            food("Apple Pie Slice",           "American",     411,  4.0, 58.0, 19.0, 2.0,  "1 slice",      true,  false, false, false, false),
            food("Clam Chowder",              "American",     290, 14.0, 28.0, 14.0, 1.0,  "1 bowl",       false, false, false, false, false),

            // ── Indian ──
            food("Butter Chicken",            "Indian",       380, 28.0, 14.0, 24.0, 2.0,  "1 cup",        false, false, true,  false, false),
            food("Dal Makhani",               "Indian",       270, 12.0, 36.0, 10.0, 8.0,  "1 cup",        true,  false, true,  false, false),
            food("Biryani (Chicken)",         "Indian",       490, 30.0, 56.0, 16.0, 3.0,  "1 plate",      false, false, true,  false, true),
            food("Naan Bread",                "Indian",       262,  8.0, 46.0,  5.0, 2.0,  "1 piece",      true,  false, false, false, false),
            food("Samosa (2)",                "Indian",       320,  6.0, 38.0, 16.0, 3.0,  "2 pieces",     true,  true,  false, false, false),
            food("Palak Paneer",              "Indian",       310, 14.0, 16.0, 22.0, 5.0,  "1 cup",        true,  false, true,  false, false),
            food("Chicken Tikka",             "Indian",       280, 32.0,  6.0, 14.0, 1.0,  "6 oz",         false, false, true,  false, true),
            food("Roti (Whole Wheat)",        "Indian",       120,  4.0, 22.0,  2.0, 3.0,  "1 piece",      true,  true,  false, true,  false),
            food("Mango Lassi",               "Indian",       210,  6.0, 38.0,  4.0, 1.0,  "1 glass",      true,  false, true,  false, false),
            food("Chana Masala",              "Indian",       260, 14.0, 40.0,  6.0,10.0,  "1 cup",        true,  true,  true,  true,  true),
            food("Idli (2)",                  "Indian",       120,  4.0, 22.0,  1.0, 2.0,  "2 pieces",     true,  true,  true,  true,  false),
            food("Dosa (Plain)",              "Indian",       168,  4.0, 32.0,  3.0, 2.0,  "1 piece",      true,  true,  true,  true,  false),

            // ── Mexican ──
            food("Chicken Burrito",           "Mexican",      590, 38.0, 68.0, 18.0, 8.0,  "1 burrito",    false, false, false, false, false),
            food("Beef Tacos (2)",            "Mexican",      430, 24.0, 38.0, 20.0, 5.0,  "2 tacos",      false, false, false, false, false),
            food("Guacamole + Chips",         "Mexican",      420,  5.0, 44.0, 26.0, 8.0,  "1 serving",    true,  true,  true,  true,  false),
            food("Chicken Quesadilla",        "Mexican",      490, 30.0, 40.0, 22.0, 4.0,  "1 quesadilla", false, false, false, false, false),
            food("Enchiladas (2)",            "Mexican",      510, 26.0, 54.0, 22.0, 6.0,  "2 pieces",     false, false, false, false, false),
            food("Tamale",                    "Mexican",      285, 10.0, 38.0, 11.0, 4.0,  "1 piece",      false, false, false, false, false),

            // ── Chinese ──
            food("Fried Rice",                "Chinese",      420, 16.0, 58.0, 14.0, 3.0,  "1 cup",        false, false, true,  false, false),
            food("Kung Pao Chicken",          "Chinese",      430, 30.0, 24.0, 24.0, 3.0,  "1 cup",        false, false, true,  false, false),
            food("Dim Sum (4 pcs)",           "Chinese",      280, 14.0, 30.0, 10.0, 2.0,  "4 pieces",     false, false, false, false, false),
            food("Hot and Sour Soup",         "Chinese",      160, 10.0, 18.0,  5.0, 2.0,  "1 bowl",       false, false, true,  false, false),
            food("Spring Rolls (2)",          "Chinese",      240,  6.0, 28.0, 12.0, 2.0,  "2 rolls",      false, false, false, false, false),
            food("Peking Duck",               "Chinese",      520, 38.0, 12.0, 36.0, 1.0,  "6 oz",         false, false, true,  false, false),
            food("Beef Lo Mein",              "Chinese",      460, 24.0, 60.0, 14.0, 4.0,  "1 plate",      false, false, false, false, false),

            // ── Italian ──
            food("Spaghetti Bolognese",       "Italian",      520, 26.0, 62.0, 16.0, 4.0,  "1 plate",      false, false, false, false, false),
            food("Margherita Pizza (2 slices)","Italian",     480, 20.0, 58.0, 18.0, 3.0,  "2 slices",     true,  false, false, false, false),
            food("Risotto",                   "Italian",      380, 10.0, 60.0, 12.0, 2.0,  "1 cup",        true,  false, true,  false, false),
            food("Tiramisu",                  "Italian",      310,  6.0, 34.0, 16.0, 1.0,  "1 slice",      true,  false, false, false, false),
            food("Bruschetta",                "Italian",      180,  6.0, 26.0,  6.0, 2.0,  "2 pieces",     true,  false, false, false, false),

            // ── Japanese ──
            food("Salmon Sushi Roll (8 pcs)", "Japanese",     380, 22.0, 52.0, 10.0, 2.0,  "8 pieces",     false, false, true,  false, false),
            food("Ramen (Tonkotsu)",          "Japanese",     560, 32.0, 62.0, 20.0, 4.0,  "1 bowl",       false, false, false, false, false),
            food("Miso Soup",                 "Japanese",      80,  6.0,  8.0,  3.0, 2.0,  "1 bowl",       true,  true,  true,  true,  false),
            food("Edamame",                   "Japanese",     120, 11.0,  9.0,  5.0, 5.0,  "1 cup",        true,  true,  true,  true,  false),
            food("Teriyaki Chicken",          "Japanese",     370, 34.0, 22.0, 14.0, 1.0,  "6 oz",         false, false, true,  false, false),

            // ── Mediterranean ──
            food("Greek Salad",               "Mediterranean",220,  8.0, 14.0, 16.0, 4.0,  "1 bowl",       true,  false, true,  false, false),
            food("Hummus + Pita",             "Mediterranean",310, 12.0, 42.0, 12.0, 7.0,  "1 serving",    true,  true,  false, true,  false),
            food("Falafel (4 pcs)",           "Mediterranean",330, 14.0, 36.0, 16.0, 6.0,  "4 pieces",     true,  true,  false, true,  false),
            food("Shawarma Wrap",             "Mediterranean",480, 32.0, 46.0, 18.0, 4.0,  "1 wrap",       false, false, false, false, true),
            food("Tabbouleh",                 "Mediterranean",170,  4.0, 20.0,  9.0, 4.0,  "1 cup",        true,  true,  false, true,  true),

            // ── Korean ──
            food("Bibimbap",                  "Korean",       490, 26.0, 72.0, 12.0, 6.0,  "1 bowl",       false, false, true,  false, false),
            food("Kimchi Jjigae",             "Korean",       280, 18.0, 24.0, 10.0, 5.0,  "1 bowl",       false, false, true,  false, false),
            food("Japchae",                   "Korean",       360, 10.0, 62.0,  8.0, 3.0,  "1 serving",    false, false, true,  false, false),

            // ── Thai ──
            food("Pad Thai",                  "Thai",         500, 22.0, 60.0, 18.0, 4.0,  "1 plate",      false, false, true,  false, false),
            food("Green Curry",               "Thai",         380, 24.0, 28.0, 20.0, 4.0,  "1 cup",        false, false, true,  false, false),
            food("Tom Yum Soup",              "Thai",         180, 14.0, 12.0,  8.0, 3.0,  "1 bowl",       false, false, true,  false, false),

            // ── African ──
            food("Jollof Rice",               "African",      340,  8.0, 60.0,  8.0, 4.0,  "1 cup",        true,  true,  true,  true,  false),
            food("Suya (Beef Skewer)",        "African",      290, 28.0,  8.0, 16.0, 1.0,  "4 oz",         false, false, true,  false, true),
            food("Egusi Soup",                "African",      350, 22.0, 14.0, 24.0, 5.0,  "1 cup",        false, false, true,  false, false),

            // ── Breakfast ──
            food("Scrambled Eggs (2)",        "Breakfast",    180, 14.0,  2.0, 13.0, 0.0,  "2 eggs",       true,  false, true,  false, false),
            food("Oatmeal with Fruit",        "Breakfast",    280,  8.0, 50.0,  5.0, 7.0,  "1 bowl",       true,  true,  true,  true,  false),
            food("Greek Yogurt (Plain)",      "Breakfast",    150, 17.0,  9.0,  4.0, 0.0,  "1 cup",        true,  false, true,  false, false),
            food("Avocado Toast",             "Breakfast",    320,  9.0, 32.0, 18.0, 8.0,  "2 slices",     true,  true,  false, true,  false),

            // ── Fruit ──
            food("Banana",                    "Fruit",        105,  1.0, 27.0,  0.0, 3.0,  "1 medium",     true,  true,  true,  true,  false),
            food("Apple",                     "Fruit",         95,  0.0, 25.0,  0.0, 4.0,  "1 medium",     true,  true,  true,  true,  false),
            food("Orange",                    "Fruit",         62,  1.0, 15.0,  0.0, 3.0,  "1 medium",     true,  true,  true,  true,  false),

            // ── Snacks ──
            food("Mixed Nuts (1 oz)",         "Snack",        172,  5.0,  6.0, 15.0, 2.0,  "1 oz",         true,  true,  true,  true,  false),
            food("Protein Bar",               "Snack",        200, 20.0, 24.0,  7.0, 4.0,  "1 bar",        true,  false, false, false, false),
            food("Dark Chocolate (1 oz)",     "Snack",        170,  2.0, 13.0, 12.0, 3.0,  "1 oz",         true,  true,  true,  true,  false),
            food("Baby Carrots",              "Snack",         52,  1.0, 12.0,  0.0, 4.0,  "1 cup",        true,  true,  true,  true,  false),
            food("String Cheese",             "Snack",         80,  7.0,  1.0,  5.0, 0.0,  "1 stick",      true,  false, true,  false, false),
            food("Granola Bar",               "Snack",        190,  3.0, 32.0,  6.0, 2.0,  "1 bar",        true,  false, false, false, false),
            food("Popcorn (Air-popped)",      "Snack",        120,  4.0, 24.0,  1.0, 5.0,  "3 cups",       true,  true,  true,  true,  false),

            // ── Drinks ──
            food("Orange Juice",              "Drink",        112,  2.0, 26.0,  0.0, 0.0,  "1 cup",        true,  true,  true,  true,  false),
            food("Whole Milk",                "Drink",        150,  8.0, 12.0,  8.0, 0.0,  "1 cup",        true,  false, true,  false, false),
            food("Latte (whole milk)",        "Drink",        190,  9.0, 15.0, 10.0, 0.0,  "12oz",         true,  false, true,  false, false),
            food("Smoothie (Berry)",          "Drink",        220,  4.0, 48.0,  2.0, 6.0,  "16oz",         true,  true,  true,  true,  false)
        );
    }

    /** Helper to build a Food object with dietary flags. */
    private Food food(String name, String cat, int cal, double protein, double carbs,
                      double fat, double fiber, String serving,
                      boolean vegetarian, boolean vegan, boolean glutenFree,
                      boolean dairyFree, boolean halal) {
        return Food.builder()
                .name(name).category(cat)
                .caloriesPerServing(cal)
                .proteinGrams(protein).carbsGrams(carbs).fatGrams(fat).fiberGrams(fiber)
                .servingDescription(serving)
                .isVegetarian(vegetarian).isVegan(vegan)
                .isGlutenFree(glutenFree).isDairyFree(dairyFree).isHalal(halal)
                .isKosher(false)
                .build();
    }
}
