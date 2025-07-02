package me.redshore.web_gagebu.feature.accountbook.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redshore.web_gagebu.common.entity.BaseEntity;
import me.redshore.web_gagebu.feature.user.domain.User;

@Entity
@Table(
    name = "records",
    indexes = {
        @Index(columnList = "account_book_id, date, created_at")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_book_id", nullable = false)
    private AccountBook accountBook;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 16, nullable = false)
    RecordType recordType;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 32, nullable = false)
    private String summary;

    @Column(length = 512, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

}
