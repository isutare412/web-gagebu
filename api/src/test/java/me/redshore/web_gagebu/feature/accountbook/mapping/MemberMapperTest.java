package me.redshore.web_gagebu.feature.accountbook.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.user.domain.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @Nested
    class Describe_toView {

        private final Member member = Member.builder()
                                            .id(UUID.randomUUID())
                                            .user(User.builder().id(UUID.randomUUID()).build())
                                            .role(MemberRole.OWNER)
                                            .build();

        @Test
        void it_maps_Member_to_MemberDto() {
            final var memberDto = memberMapper.toDto(this.member);
            assertEquals(member.getId(), memberDto.id());
            assertEquals(member.getUser().getId(), memberDto.userId());
            assertEquals(member.getRole(), memberDto.role());
        }

    }

}
