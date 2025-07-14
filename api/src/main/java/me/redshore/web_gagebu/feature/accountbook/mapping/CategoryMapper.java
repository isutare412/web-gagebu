package me.redshore.web_gagebu.feature.accountbook.mapping;

import me.redshore.web_gagebu.feature.accountbook.domain.Category;
import me.redshore.web_gagebu.feature.accountbook.dto.CategoryDto;
import me.redshore.web_gagebu.feature.accountbook.dto.CategoryUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.request.CategoryUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.CategoryView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    CategoryView toView(CategoryDto categoryDto);

    CategoryUpdateCommand toCommand(CategoryUpdateRequest request);

}
