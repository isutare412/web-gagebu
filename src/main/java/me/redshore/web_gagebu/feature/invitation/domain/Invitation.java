package me.redshore.web_gagebu.feature.invitation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redshore.web_gagebu.common.entity.BaseEntity;
import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import org.springframework.lang.Nullable;

@Entity
@Table(
    name = "invitations",
    indexes = {
        @Index(columnList = "expiration"),
        @Index(columnList = "account_book_id")})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Invitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_book_id", nullable = false)
    private AccountBook accountBook;

    private @Nullable ZonedDateTime expiration;

}
