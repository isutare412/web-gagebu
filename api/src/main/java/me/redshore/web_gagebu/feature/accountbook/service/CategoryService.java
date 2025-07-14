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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AccountBookRepository accountBookRepository;
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
        final var existingCategories =
            this.categoryRepository.findAllByAccountBookIdOrderByCreatedAtAsc(command.accountBookId())
                                   .stream()
                                   .collect(Collectors.toMap(Category::getId, Function.identity()));

        // Process category updates/creates
        final var processedCategoryIds = command.categories()
            .stream()
            .map(
                categoryRequest -> upsertCategory(categoryRequest, existingCategories, accountBook))
            .map(Category::getId)
            .toList();

        // Delete categories that are not in the request
        if (!processedCategoryIds.isEmpty()) {
            // Delete only non-basic categories that are not in the request
            this.categoryRepository.deleteAllByAccountBookIdAndIdNotInAndIsBasicFalse(
                command.accountBookId(),
                processedCategoryIds);
        } else {
            // If no categories in request, delete all non-basic categories
            this.categoryRepository.findAllByAccountBookIdOrderByCreatedAtAsc(command.accountBookId())
                                   .stream()
                                   .filter(category -> !category.getIsBasic())
                                   .forEach(this.categoryRepository::delete);
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

}
