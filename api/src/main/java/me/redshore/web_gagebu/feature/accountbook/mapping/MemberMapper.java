package me.redshore.web_gagebu.feature.accountbook.mapping;

import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.dto.MemberDto;
import me.redshore.web_gagebu.feature.accountbook.dto.response.MemberView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "nickname", source = "user.nickname")
    @Mapping(target = "pictureUrl", source = "user.pictureUrl")
    MemberDto toDto(Member member);

    MemberView toView(MemberDto memberDto);

}
