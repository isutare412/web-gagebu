package me.redshore.web_gagebu.feature.user.repository;

import java.util.Optional;
import java.util.UUID;
import me.redshore.web_gagebu.feature.user.domain.IdpType;
import me.redshore.web_gagebu.feature.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdpTypeAndIdpIdentifier(IdpType idpType, String idpIdentifier);

}
