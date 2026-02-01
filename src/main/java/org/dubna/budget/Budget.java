package org.dubna.budget;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.dubna.category.Category;
import org.dubna.user.UserEntity;

import java.time.OffsetDateTime;

@Entity
@Table(name = "budget")
@Getter
public class Budget extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(nullable = false, updatable = false, insertable = false)
    private OffsetDateTime created;

    public Operation createOperation(@Max(255) String keyword,
                                     @Min(0) Float change,
                                     OperationType type) {
        Operation op = Operation.builder()
                .keyword(keyword)
                .budget(this)
                .change(change)
                .type(type)
                .build();

        Category.findByNameOrKeywordAndUserId(keyword, creatorId)
                .ifPresent(category -> {
                    op.setCategory(category);
                    if (op.getType() == null) {
                        op.setType(category.getType());
                    }
                });


        op.persist();
        return op;
    }

    public Operation createOperation(@Min(0) Float change, OperationType type) {
        Operation op = Operation.builder()
                .change(change)
                .budget(this)
                .type(type)
                .build();

        op.persist();
        return op;
    }

    public static Budget createUserBudget(UserEntity user) {
        Budget budget = new Budget();
        budget.creatorId = user.getId();
        budget.name = user.getFullName();
        budget.persist();
        return budget;
    }

    public static Budget findByCreatorId(Long creatorId) {
        return find("creatorId", creatorId).firstResult();
    }

}
