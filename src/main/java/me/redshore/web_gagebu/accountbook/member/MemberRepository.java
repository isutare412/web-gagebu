package me.redshore.web_gagebu.accountbook.member;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    public boolean existsByAccountBookIdAndUserId(UUID accountBookId, UUID userId);

    public boolean existsByAccountBookIdAndUserIdAndRole(UUID accountBookId, UUID userId,
                                                         MemberRole role);

}
