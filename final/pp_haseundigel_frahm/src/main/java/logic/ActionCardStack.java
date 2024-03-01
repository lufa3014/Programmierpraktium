package logic;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import static logic.ActionCard.*;

/**
 * The `ActionCardStack` class represents the stack of action cards. <br>
 * It provides methods to draw cards from the stack or to reshuffle it.
 * @see ActionCard
 */
class ActionCardStack {
    /**
     * The stack of action cards.
     * @see ActionCard
     */
    private final Stack<ActionCard> cards = new Stack<>();

    /**
     * Create a new empty action card stack. <br>
     */
    ActionCardStack() {

    }

    /**
     * Create a new action card stack. <br>
     * The stack is shuffled using the given seed.
     * @param seed The seed to use for shuffling.
     */
    ActionCardStack(long seed) {
        cards.addAll(Arrays.asList(
                FreeLastMove, FreeLastMove,
                ExchangeCarrots, ExchangeCarrots,
                GetSuspended,
                FallBackRank, FallBackRank,
                ConsumeSalad,
                MoveUpRank,
                TakeTurnAgain,
                MoveToNextCarrotField,
                MoveToLastCarrotField
        ));

        shuffle(seed);
    }

    /**
     * Matches the current stack to the given action card stack.
     * This will clear the current stack and copy all cards from the given stack.
     * @param other The ActionCardStack to match to.
     */
    public void matchStack(ActionCard[] other) {
        cards.clear(); // Clear the current stack
        cards.addAll(Arrays.stream(other).toList()); // Add all cards from the other stack
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ActionCardStack other) {
            return cards.equals(other.cards);
        }

        return false;
    }

    /**
     * Draw the top card of the stack and put it at the bottom of the stack.
     * @return The top card of the stack.
     */
    ActionCard draw() {
        ActionCard card = cards.pop();
        cards.add(0, card);
        return card;
    }

    /**
     * Peek at the top card of the stack without removing it.
     * @return The top card of the stack.
     */
    @SuppressWarnings("unused") // currently not needed, but seems wrong not to have...
    ActionCard peek() {
        return cards.peek();
    }

    /**
     * Create a copy of this stack.
     * @return The copy of this stack.
     */
    ActionCardStack copy() {
        ActionCardStack copy = new ActionCardStack();
        copy.cards.addAll(cards);
        return copy;
    }

    /**
     * Shuffle the stack.
     * @param seed The seed to use for shuffling.
     */
    private void shuffle(long seed) {
        java.util.Collections.shuffle(cards, new Random(seed));
    }
}
