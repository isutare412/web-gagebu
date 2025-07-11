package me.redshore.web_gagebu.feature.invitation.mapping;

import me.redshore.web_gagebu.feature.invitation.domain.Invitation;
import me.redshore.web_gagebu.feature.invitation.dto.InvitationDto;
import me.redshore.web_gagebu.feature.invitation.dto.InvitationListResult;
import me.redshore.web_gagebu.feature.invitation.dto.response.InvitationListResponose;
import me.redshore.web_gagebu.feature.invitation.dto.response.InvitationView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    InvitationDto toDto(Invitation invitation);

    InvitationView toView(InvitationDto dto);

    InvitationListResponose toListResponse(InvitationListResult result);

}
