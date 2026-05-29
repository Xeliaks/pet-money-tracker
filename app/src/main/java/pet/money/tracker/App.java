package pet.money.tracker;

import pet.money.tracker.cli.MainCommand;

/**
 * Thin launcher that delegates to the PicoCLI entry point.
 *
 * <p>The Gradle Application plugin points {@code mainClass} at
 * {@code pet.money.tracker.cli.MainCommand} directly; this class is kept
 * as a convenience alias.
 */
public class App {

    private App() {
    }

    /**
     * @param args command-line arguments forwarded to {@link MainCommand}
     */
    public static void main(String[] args) {
        MainCommand.main(args);
    }
}