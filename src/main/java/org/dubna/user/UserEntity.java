package org.dubna.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dubna.budget.Budget;
import org.dubna.category.Category;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Getter
@Slf4j
@Table(name = "\"user\"")
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true, name = "tg_id")
    private Long tgId;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "registered",
            nullable = false,
            insertable = false,
            updatable = false)
    private OffsetDateTime registered;

    public String getFullName() {
        List<String> nameParts = Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .toList();

        return String.join(" ", nameParts);
    }

    public static UserEntity saveTgUser(User user) {
        if (existsByTgId(user.getId())) {
            update("""
                            firstName = ?1,
                            lastName = ?2
                            WHERE tgId = ?3
                            """, user.getFirstName(), user.getLastName(), user.getId());

            return find("tgId", user.getId()).firstResult();
        }
        log.info("New TG user: {}", user);

        UserEntity userEntity = new UserEntity();
        userEntity.tgId = user.getId();
        userEntity.firstName = user.getFirstName();
        userEntity.lastName = user.getLastName();
        userEntity.persist();

        Budget.createUserBudget(userEntity);
        Category.initCategoriesForNewUser(userEntity.id);
        return userEntity;
    }

    public static boolean existsByTgId(Long tgId) {
        return count("tgId", tgId) > 0;
    }

}
