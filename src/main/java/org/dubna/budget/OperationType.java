package org.dubna.budget;

import java.util.function.Function;

public enum OperationType {
    DEBIT('+', Math::abs),
    TOPUP('-', sum -> -Math.abs(sum)),;

    public final char sign;
    private final Function<Float, Float> prepareSumFunction;

    OperationType(final char sign, final Function<Float, Float> prepareSumFunction) {
        this.sign = sign;
        this.prepareSumFunction = prepareSumFunction;
    }

    public Float setSign(Float sum) {
        return prepareSumFunction.apply(sum);
    }

}
