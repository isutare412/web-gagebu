package me.redshore.web_gagebu.feature.accountbook.mapping;

import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Record;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordCreateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordDto;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordListResult;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordSummaryDto;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordCreateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordListResponse;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordSummaryView;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    @Mapping(target = ".", source = "request")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "accountBookId", source = "accountBookId")
    RecordCreateCommand toCreateCommand(RecordCreateRequest request, UUID userId,
                                        UUID accountBookId);

    @Mapping(target = ".", source = "request")
    @Mapping(target = "recordId", source = "recordId")
    RecordUpdateCommand toUpdateCommand(RecordUpdateRequest request, UUID recordId);

    @Mapping(target = "userNickname", source = "user.nickname")
    @Mapping(target = "userPictureUrl", source = "user.pictureUrl")
    @Mapping(target = "category", source = "category.name")
    RecordDto toDto(Record record);

    @Mapping(target = "userNickname", source = "user.nickname")
    @Mapping(target = "userPictureUrl", source = "user.pictureUrl")
    @Mapping(target = "category", source = "category.name")
    RecordSummaryDto toSummaryDto(Record record);

    RecordView toView(RecordDto recordDto);

    RecordSummaryView toSummaryView(RecordSummaryDto recordDto);

    @Mapping(target = "page", expression = "java(listResult.page() + 1)")
    RecordListResponse toListResponse(RecordListResult listResult);

    Record toEntity(RecordCreateCommand command);

}
