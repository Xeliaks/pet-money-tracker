package pet.money.tracker.patterns;

import pet.money.tracker.model.Transaction;

/** Carries information about a mutation on a {@link Transaction}. */
public record TransactionEvent(EventType type, Transaction transaction) {

    /** The kind of mutation that triggered this event. */
    public enum EventType {
        ADDED,
        UPDATED,
        DELETED
    }
}
