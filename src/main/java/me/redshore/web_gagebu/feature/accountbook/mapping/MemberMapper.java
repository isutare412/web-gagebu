package me.redshore.web_gagebu.feature.accountbook.mapping;

import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.dto.MemberDto;
import me.redshore.web_gagebu.feature.accountbook.dto.response.MemberView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.pictureUrl", target = "pictureUrl")
    MemberDto toDto(Member member);

    MemberView toView(MemberDto memberDto);

}
