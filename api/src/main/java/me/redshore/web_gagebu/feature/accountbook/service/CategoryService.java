package me.redshore.web_gagebu.feature.accountbook.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import me.redshore.web_gagebu.feature.accountbook.domain.Category;
import me.redshore.web_gagebu.feature.accountbook.dto.CategoryDto;
import me.redshore.web_gagebu.feature.accountbook.dto.CategoryListUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.CategoryUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.mapping.CategoryMapper;
import me.redshore.web_gagebu.feature.accountbook.repository.AccountBookRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.CategoryRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.RecordRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AccountBookRepository accountBookRepository;
    private final RecordRepository recordRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canAccess(#accountBookId)")
    public List<CategoryDto> getCategories(UUID accountBookId) {
        return this.categoryRepository.findAllByAccountBookIdOrderByCreatedAtAsc(accountBookId)
                                      .stream()
                                      .map(this.categoryMapper::toDto)
                                      .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canManage(#command.accountBookId)")
    public List<CategoryDto> updateCategories(CategoryListUpdateCommand command) {
        // Verify account book exists
        final var accountBook = this.accountBookRepository
            .findById(command.accountBookId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                String.format("Account book with id %s not found", command.accountBookId())));

        // Get existing categories for this account book
        final var existingCategories = this.categoryRepository
            .findAllByAccountBookIdOrderByCreatedAtAsc(command.accountBookId())
            .stream()
            .collect(Collectors.toMap(Category::getId, Function.identity()));

        // Process category updates/creates
        final var processedCategoryIds = command.categories()
            .stream()
            .map(categoryRequest ->
                upsertCategory(categoryRequest, existingCategories, accountBook))
            .map(Category::getId)
            .toList();

        // Delete categories that are not in the request
        if (!processedCategoryIds.isEmpty()) {
            deleteUnprocessedCategories(command.accountBookId(), processedCategoryIds);
        } else {
            deleteAllNonBasicNonFallbackCategories(command.accountBookId());
        }

        // Return updated list
        return this.categoryRepository.findAllByAccountBookIdOrderByCreatedAtAsc(command.accountBookId())
                                      .stream()
                                      .map(this.categoryMapper::toDto)
                                      .toList();
    }

    private Category upsertCategory(CategoryUpdateCommand request,
                                    Map<UUID, Category> existingCategories,
                                    AccountBook accountBook) {

        if (request.id() == null) {
            // Create new category
            var newCategory = Category.builder()
                                      .name(request.name())
                                      .accountBook(accountBook)
                                      .isBasic(false)
                                      .isFallback(false)
                                      .build();
            return this.categoryRepository.save(newCategory);
        }

        // Update existing category
        var existingCategory = existingCategories.get(request.id());
        if (existingCategory == null) {
            throw new AppException(ErrorCode.NOT_FOUND,
                String.format("Category with id %s not found", request.id()));
        }

        // Check if category belongs to the account book
        if (!existingCategory.getAccountBook().getId().equals(accountBook.getId())) {
            throw new AppException(ErrorCode.CONFLICT,
                String.format("Category %s does not belong to account book %s",
                    request.id(), accountBook.getId()));
        }

        // Update name if different
        if (!existingCategory.getName().equals(request.name())) {
            existingCategory.setName(request.name());
            existingCategory = this.categoryRepository.save(existingCategory);
        }

        return existingCategory;
    }

    private void deleteUnprocessedCategories(UUID accountBookId, List<UUID> processedCategoryIds) {
        // Get categories to be deleted (non-basic, non-fallback categories not in the request)
        final var categoryIdsToDelete = 
            this.categoryRepository.findAllByAccountBookIdOrderByCreatedAtAsc(accountBookId)
                                   .stream()
                                   .filter(category ->
                                        !category.getIsBasic() && 
                                        !processedCategoryIds.contains(category.getId()))
                                   .map(Category::getId)
                                   .toList();

        if (categoryIdsToDelete.isEmpty()) {
            return;
        }

        // Update records to use fallback category before deleting
        updateRecordsToFallbackCategory(accountBookId, categoryIdsToDelete);

        // Delete only non-basic categories that are not in the request
        this.categoryRepository.deleteAllByAccountBookIdAndIsBasicFalseAndIdNotIn(accountBookId,
                                                                                    processedCategoryIds);
    }

    private void deleteAllNonBasicNonFallbackCategories(UUID accountBookId) {
        // If no categories in request, get all non-basic, non-fallback categories to delete
        final var categoryIdsToDelete = 
            this.categoryRepository.findAllByAccountBookIdOrderByCreatedAtAsc(accountBookId)
                                   .stream()
                                   .filter(category ->
                                        !category.getIsBasic() &&
                                        !category.getIsFallback())
                                   .map(Category::getId)
                                   .toList();

        if (categoryIdsToDelete.isEmpty()) {
            return;
        }

        // Update records to use fallback category before deleting
        updateRecordsToFallbackCategory(accountBookId, categoryIdsToDelete);

        // Delete all non-basic categories
        this.categoryRepository.deleteAllByAccountBookIdAndIsBasicFalse(accountBookId);
    }

    private void updateRecordsToFallbackCategory(UUID accountBookId, List<UUID> categoriesToDelete) {
        if (categoriesToDelete.isEmpty()) {
            return;
        }

        // Find the fallback category for this account book
        final var fallbackCategory = this.categoryRepository
            .findByAccountBookIdAndIsFallbackTrue(accountBookId)
            .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("No fallback category found for account book %s", accountBookId)));

        // Update all records using the categories to be deleted to use the fallback category
        this.recordRepository.updateCategoryToFallbackBatch(categoriesToDelete, fallbackCategory.getId());
    }

}
