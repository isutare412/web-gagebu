package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    public boolean existsByAccountBookIdAndUserId(UUID accountBookId, UUID userId);

    public boolean existsByAccountBookIdAndUserIdAndRole(UUID accountBookId, UUID userId,
                                                         MemberRole role);

}
