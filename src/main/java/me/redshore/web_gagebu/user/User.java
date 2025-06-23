package me.redshore.web_gagebu.user;

import java.time.ZonedDateTime;
import java.util.UUID;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 64, nullable = false)
    private String nickname;

    @Column(length = 16, nullable = false)
    private IdpType idpType;

    @Column(length = 255, nullable = false)
    private String idpIdentifier;

    @Column(length = 1024, nullable = true)
    @Nullable
    private String pictureUrl;

    @Column(length = 256, nullable = true)
    @Nullable
    private String email;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

}
