package me.redshore.web_gagebu.feature.accountbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.feature.accountbook.dto.CategoryListUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.request.CategoryListUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.CategoryListResponse;
import me.redshore.web_gagebu.feature.accountbook.mapping.CategoryMapper;
import me.redshore.web_gagebu.feature.accountbook.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Category")
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/account-books/{accountBookId}/categories")
    @Operation(summary = "Get categories for account book")
    public CategoryListResponse getCategories(@PathVariable UUID accountBookId) {
        final var categoryDtos = this.categoryService.listCategories(accountBookId);
        final var categoryViews = categoryDtos.stream()
                                              .map(this.categoryMapper::toView)
                                              .toList();
        return new CategoryListResponse(categoryViews);
    }

    @PutMapping("/account-books/{accountBookId}/categories")
    @Operation(summary = "Replace categories for account book",
               description = "If a category is not included in the request, it will be deleted. " +
                             "New categories can be added by providing a null ID.")
    public CategoryListResponse updateCategories(@PathVariable UUID accountBookId,
                                                 @Valid @RequestBody CategoryListUpdateRequest body) {
        final var categoryCommands = body.categories()
                                         .stream()
                                         .map(this.categoryMapper::toCommand)
                                         .toList();
        final var updateCommand = CategoryListUpdateCommand.builder()
                                                           .accountBookId(accountBookId)
                                                           .categories(categoryCommands)
                                                           .build();
        final var categoryDtos = this.categoryService.updateCategories(updateCommand);
        final var categoryViews = categoryDtos.stream()
                                              .map(this.categoryMapper::toView)
                                              .toList();
        return new CategoryListResponse(categoryViews);
    }

}
