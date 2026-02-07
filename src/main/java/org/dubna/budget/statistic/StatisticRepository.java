package org.dubna.budget.statistic;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.dubna.budget.Operation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Startup
@ApplicationScoped
public class StatisticRepository implements PanacheRepository<Operation> {

    private static StatisticRepository INSTANCE;

    @PostConstruct
    public void init() {
        INSTANCE = this;
    }

    public static StatisticRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Get Analyze of Operations for period.
     *
     * @param userId User ID
     * @param from start date
     * @param to end date
     * @return Analyze
     */
    public OperationsStatistic getAnalyze(Long userId,
                                          LocalDate from,
                                          LocalDate to) {
        ZoneOffset offset = ZoneOffset.ofHours(3);

        OffsetDateTime start = from.atStartOfDay().atOffset(offset);
        OffsetDateTime end = to.atTime(23, 59, 59).atOffset(offset);

        List<CategoryStatistic> analyzes = getEntityManager()
                .createQuery("""
                SELECT NEW org.dubna.budget.statistic.CategoryStatistic(
                    COALESCE(c.name, 'Без категории'),
                    SUM(o.change)
                )
                FROM Operation o
                LEFT JOIN o.category c
                JOIN o.budget b
                WHERE o.budget.creatorId = :userId
                AND o.created BETWEEN :startDate AND :endDate
                GROUP BY c.name
                HAVING SUM(o.change) != 0
                ORDER BY SUM(o.change) DESC
                """, CategoryStatistic.class)
                .setParameter("userId", userId)
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .getResultList();

        double expense = 0;
        double income = 0;

        for (CategoryStatistic c : analyzes) {
            if (c.change() > 0) {
                income += c.change();

            } else {
                expense += c.change();
            }
        }

        return OperationsStatistic.builder()
                .analyzes(analyzes)
                .expense(expense)
                .income(income)
                .from(from)
                .to(to)
                .build();
    }

}
