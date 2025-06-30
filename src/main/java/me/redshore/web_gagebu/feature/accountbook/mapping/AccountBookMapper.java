package me.redshore.web_gagebu.feature.accountbook.mapping;

import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookDto;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookSummaryDto;
import me.redshore.web_gagebu.feature.accountbook.dto.response.AccountBookSummaryView;
import me.redshore.web_gagebu.feature.accountbook.dto.response.AccountBookView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, CategoryMapper.class})
public interface AccountBookMapper {

    AccountBookDto toDto(AccountBook accountBook);

    AccountBookView toView(AccountBookDto accountBookDto);

    AccountBookSummaryDto toSummaryDto(AccountBook accountBook);

    AccountBookSummaryView toSummaryView(AccountBookSummaryDto accountBookDto);

}
