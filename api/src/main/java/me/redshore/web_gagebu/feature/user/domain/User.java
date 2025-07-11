package me.redshore.web_gagebu.feature.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redshore.web_gagebu.common.entity.BaseTimeEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.lang.Nullable;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idp_type", "idp_identifier"})
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<UserRole> roles = new ArrayList<>();

    @Column(length = 64, nullable = false)
    private String nickname;

    @Column(length = 16, nullable = false)
    private IdpType idpType;

    @Column(nullable = false)
    private String idpIdentifier;

    @Column(length = 1024)
    @Nullable
    private String pictureUrl;

    @Column(length = 256)
    @Nullable
    private String email;

    public boolean addRole(UserRole role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            return true;
        }
        return false;
    }

}
