package org.dubna.budget;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.dubna.category.Category;

import java.time.OffsetDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "operation")
public class Operation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created", insertable = false, updatable = false)
    private OffsetDateTime created;

    @Column(name = "edited", insertable = false, nullable = false)
    private OffsetDateTime edited;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Column(name = "change")
    private Float change;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OperationType type;

    public void updateCategory(Category category) {
        this.category = category;
        update("category=?1 WHERE id=?2", category, id);
    }

    public void updateType(OperationType type) {
        this.type = type;
        if (type != null) {
            change = type.setSign(change);
        }
        update("type=?1, change=?2 WHERE id=?3", type, change, id);
    }

    @PrePersist
    protected void prePersist() {
        this.change = (type == OperationType.TOPUP ? -1 : 1) * change;
    }

}
