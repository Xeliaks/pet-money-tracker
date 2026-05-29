package pet.money.tracker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/** Smoke test — verifies the application class can be loaded without errors. */
class AppTest {

    @Test
    void applicationClassLoads() {
        assertDoesNotThrow(() -> Class.forName("pet.money.tracker.App"));
    }
}