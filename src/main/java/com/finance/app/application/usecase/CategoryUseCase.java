package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.entity.Subcategory;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.SubcategoryRepository;
import com.finance.app.web.dto.request.CreateCategoryRequest;
import com.finance.app.web.dto.request.UpdateCategoryRequest;
import com.finance.app.web.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import com.finance.app.domain.entity.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    public CategoryResponse create(CreateCategoryRequest request, UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        Category category = Category.builder()
                .userId(userId)
                .name(request.name())
                .colorHex(request.colorHex())
                .type(request.type())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.fromDomain(savedCategory);
    }

    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.finance.app.domain.exception.CategoryNotFoundException(id));

        category.setName(request.name());
        category.setColorHex(request.colorHex());
        category.setType(request.type());
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return CategoryResponse.fromDomain(updatedCategory);
    }

    public List<CategoryResponse> listByUser(UUID userId, TransactionType type) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        
        if (type != null) {
            categories = categories.stream().filter(c -> type.equals(c.getType())).toList();
        }
        List<UUID> categoryIds = categories.stream().map(Category::getId).toList();
        
        List<Subcategory> allSubcategories = categoryIds.isEmpty() ? List.of() : subcategoryRepository.findByCategoryIdIn(categoryIds);
        
        Map<UUID, List<Subcategory>> subcategoriesByCategoryId = allSubcategories.stream()
                .collect(Collectors.groupingBy(Subcategory::getCategoryId));

        return categories.stream()
                .map(category -> CategoryResponse.fromDomain(category, subcategoriesByCategoryId.getOrDefault(category.getId(), List.of())))
                .toList();
    }

    public Category getOrCreateUncategorized(UUID userId, TransactionType type) {
        return categoryRepository.findByUserIdAndNameAndType(userId, "Uncategorized", type)
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    Category category = Category.builder()
                            .userId(userId)
                            .name("Uncategorized")
                            .colorHex("#808080")
                            .type(type)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return categoryRepository.save(category);
                });
    }

    public void createDefaultCategories(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Food -> Delivery, Groceries, Restaurants
        Category food = categoryRepository.save(Category.builder()
                .userId(userId).name("Food").colorHex("#4CAF50").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(food.getId()).name("Delivery").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(food.getId()).name("Groceries").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(food.getId()).name("Restaurants").createdAt(now).updatedAt(now).build());

        // Housing -> Electricity, Rent, Condo, Internet, Water, Gas
        Category housing = categoryRepository.save(Category.builder()
                .userId(userId).name("Housing").colorHex("#2196F3").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(housing.getId()).name("Electricity").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(housing.getId()).name("Rent").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(housing.getId()).name("Condo").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(housing.getId()).name("Internet").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(housing.getId()).name("Water").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(housing.getId()).name("Gas").createdAt(now).updatedAt(now).build());

        // Health -> Pharmacy, Exams, Appointments, Gym, Supplements
        Category health = categoryRepository.save(Category.builder()
                .userId(userId).name("Health").colorHex("#E91E63").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(health.getId()).name("Pharmacy").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(health.getId()).name("Exams").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(health.getId()).name("Appointments").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(health.getId()).name("Gym").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(health.getId()).name("Supplements").createdAt(now).updatedAt(now).build());

        // Services -> Subscriptions, Service Providers
        Category services = categoryRepository.save(Category.builder()
                .userId(userId).name("Services").colorHex("#9C27B0").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(services.getId()).name("Subscriptions").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(services.getId()).name("Service Providers").createdAt(now).updatedAt(now).build());

        // Shopping -> Clothes, Office Supplies, Shoes, Accessories
        Category shopping = categoryRepository.save(Category.builder()
                .userId(userId).name("Shopping").colorHex("#E67E22").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(shopping.getId()).name("Clothes").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(shopping.getId()).name("Office Supplies").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(shopping.getId()).name("Shoes").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(shopping.getId()).name("Accessories").createdAt(now).updatedAt(now).build());

        // Pets -> Health Insurance, Food, Litter, Toys, Accessories
        Category pets = categoryRepository.save(Category.builder()
                .userId(userId).name("Pets").colorHex("#795548").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(pets.getId()).name("Health Insurance").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(pets.getId()).name("Food").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(pets.getId()).name("Litter").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(pets.getId()).name("Toys").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(pets.getId()).name("Accessories").createdAt(now).updatedAt(now).build());

        // Transportation -> Fuel, Mechanic, Parts
        Category transport = categoryRepository.save(Category.builder()
                .userId(userId).name("Transportation").colorHex("#607D8B").type(TransactionType.EXPENSE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(transport.getId()).name("Fuel").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(transport.getId()).name("Mechanic").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(transport.getId()).name("Parts").createdAt(now).updatedAt(now).build());

        // REVENUE Categories

        // Salary
        Category salary = categoryRepository.save(Category.builder()
                .userId(userId).name("Salary").colorHex("#4CAF50").type(TransactionType.REVENUE).createdAt(now).updatedAt(now).build());
        
        // Investments -> Dividends, Interest
        Category investments = categoryRepository.save(Category.builder()
                .userId(userId).name("Investments").colorHex("#2196F3").type(TransactionType.REVENUE).createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(investments.getId()).name("Dividends").createdAt(now).updatedAt(now).build());
        subcategoryRepository.save(Subcategory.builder().categoryId(investments.getId()).name("Interest").createdAt(now).updatedAt(now).build());

        // Other Income
        Category otherIncome = categoryRepository.save(Category.builder()
                .userId(userId).name("Other Income").colorHex("#FF9800").type(TransactionType.REVENUE).createdAt(now).updatedAt(now).build());
    }

}
