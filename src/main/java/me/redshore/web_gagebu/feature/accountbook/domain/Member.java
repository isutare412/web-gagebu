package me.redshore.web_gagebu.feature.accountbook.domain;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redshore.web_gagebu.feature.user.domain.User;

@Entity
@Table(
    name = "members",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"account_book_id", "user_id"})
    },
    indexes = {
        @Index(columnList = "user_id")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_book_id", nullable = false)
    private AccountBook accountBook;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 16, nullable = false)
    private MemberRole role;

}
