package me.redshore.web_gagebu.feature.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.redshore.web_gagebu.feature.user.domain.IdpType;
import me.redshore.web_gagebu.feature.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    public Optional<User> findByIdpTypeAndIdpIdentifier(IdpType idpType, String idpIdentifier);

}
