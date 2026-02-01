package org.dubna.category;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category_keyword")
public class Keyword extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keyword", nullable = false, updatable = false)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Keyword(String keyword, Category category) {
        this.keyword = keyword;
        this.category = category;
    }

    public static boolean existsByKeywordAndCategory(String keyword, Category category) {
        return find("keyword = ?1 AND category = ?2", keyword, category).count() > 0;
    }

    public static void deleteByKeywordAndCategory(String keyword, Category category) {
        delete("keyword=?1 AND category=?2", keyword, category);
    }

}
