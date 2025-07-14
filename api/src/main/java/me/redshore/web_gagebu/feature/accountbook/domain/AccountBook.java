package me.redshore.web_gagebu.feature.accountbook.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redshore.web_gagebu.common.entity.BaseEntity;

@Entity
@Table(name = "account_books")
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
public class AccountBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 64, nullable = false)
    private String name;

    @OneToMany(mappedBy = "accountBook", orphanRemoval = true, cascade = {CascadeType.PERSIST})
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "accountBook", orphanRemoval = true, cascade = {CascadeType.PERSIST})
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    public void addMember(Member member) {
        this.members.add(member);
        member.setAccountBook(this);
    }

    public void addCategories(List<Category> categories) {
        categories.forEach(category -> {
            this.categories.add(category);
            category.setAccountBook(this);
        });
    }

}
