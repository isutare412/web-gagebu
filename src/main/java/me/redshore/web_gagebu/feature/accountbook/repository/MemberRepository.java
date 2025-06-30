package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.List;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    boolean existsByAccountBookIdAndUserId(UUID accountBookId, UUID userId);

    boolean existsByAccountBookIdAndUserIdAndRole(UUID accountBookId, UUID userId,
                                                  MemberRole role);

    List<Member> findAllByUserId(UUID userId);

}
