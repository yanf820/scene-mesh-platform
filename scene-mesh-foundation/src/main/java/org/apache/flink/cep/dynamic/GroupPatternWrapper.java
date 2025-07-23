package org.apache.flink.cep.dynamic;

import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.Quantifier;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;

public class GroupPatternWrapper<T, F extends T> extends PatternWrapper<T, F> {

    /** Group pattern representing the pattern definition of this group. */
    private final Pattern<T, ? extends T> groupPattern;

    GroupPatternWrapper(
            final String groupName,
            final Pattern<T, ? extends T> previous,
            final Pattern<T, ? extends T> groupPattern,
            final Quantifier.ConsumingStrategy consumingStrategy,
            final AfterMatchSkipStrategy afterMatchSkipStrategy) {
        super(groupName, previous, consumingStrategy, afterMatchSkipStrategy);
        this.groupPattern = groupPattern;
    }

    @Override
    public Pattern<T, F> where(IterativeCondition<F> condition) {
        throw new UnsupportedOperationException("GroupPattern does not support where clause.");
    }

    @Override
    public Pattern<T, F> or(IterativeCondition<F> condition) {
        throw new UnsupportedOperationException("GroupPattern does not support or clause.");
    }

    @Override
    public <S extends F> Pattern<T, S> subtype(final Class<S> subtypeClass) {
        throw new UnsupportedOperationException("GroupPattern does not support subtype clause.");
    }

    public Pattern<T, ? extends T> getRawPattern() {
        return groupPattern;
    }

    /**
     * Starts a new pattern sequence. The provided pattern is the initial pattern of the new
     * sequence.
     *
     * @param group the pattern to begin with
     * @param afterMatchSkipStrategy the {@link AfterMatchSkipStrategy.SkipStrategy} to use after
     *     each match.
     * @return The first pattern of a pattern sequence
     */
    public static <T, F extends T> GroupPatternWrapper<T, F> begin(String groupName,
                                                                   final Pattern<T, F> group, final AfterMatchSkipStrategy afterMatchSkipStrategy) {
        return new GroupPatternWrapper<>(groupName, null, group, Quantifier.ConsumingStrategy.STRICT, afterMatchSkipStrategy);
    }

    /**
     * Starts a new pattern sequence. The provided pattern is the initial pattern of the new
     * sequence.
     *
     * @param group the pattern to begin with
     * @return the first pattern of a pattern sequence
     */
    public static <T, F extends T> GroupPatternWrapper<T, F> begin(String groupName, Pattern<T, F> group) {
        return new GroupPatternWrapper<>(
                groupName, null, group, Quantifier.ConsumingStrategy.STRICT, AfterMatchSkipStrategy.noSkip());
    }

    /**
     * Appends a new group pattern to the existing one. The new pattern enforces non-strict temporal
     * contiguity. This means that a matching event of this pattern and the preceding matching event
     * might be interleaved with other events which are ignored.
     *
     * @param group the pattern to append
     * @return A new pattern which is appended to this one
     */
    public GroupPatternWrapper<T, F> followedBy(String groupName, Pattern<T, F> group) {
        return new GroupPatternWrapper<>(
                groupName, this, group, Quantifier.ConsumingStrategy.SKIP_TILL_NEXT, this.getAfterMatchSkipStrategy());
    }

    /**
     * Appends a new group pattern to the existing one. The new pattern enforces non-strict temporal
     * contiguity. This means that a matching event of this pattern and the preceding matching event
     * might be interleaved with other events which are ignored.
     *
     * @param group the pattern to append
     * @return A new pattern which is appended to this one
     */
    public GroupPatternWrapper<T, F> followedByAny(String groupName, Pattern<T, F> group) {
        return new GroupPatternWrapper<>(
                groupName,this, group, Quantifier.ConsumingStrategy.SKIP_TILL_ANY, this.getAfterMatchSkipStrategy());
    }

    /**
     * Appends a new group pattern to the existing one. The new pattern enforces strict temporal
     * contiguity. This means that the whole pattern sequence matches only if an event which matches
     * this pattern directly follows the preceding matching event. Thus, there cannot be any events
     * in between two matching events.
     *
     * @param group the pattern to append
     * @return A new pattern which is appended to this one
     */
    public GroupPatternWrapper<T, F> next(String groupName, Pattern<T, F> group) {
        return new GroupPatternWrapper<>(groupName, this, group, Quantifier.ConsumingStrategy.STRICT, this.getAfterMatchSkipStrategy());
    }
}
