package org.dubna.budget.statistic;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OperationsStatistic {

    private LocalDate from;

    private LocalDate to;

    private double income;

    private double expense;

    private List<CategoryStatistic> analyzes;

    public double getTotal() {
        return income - expense;
    }

}
