package pet.money.tracker.patterns;

import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.model.Transaction;

/**
 * Context for the Export Strategy pattern.
 *
 * <p>Holds an {@link ExportStrategy} and delegates the export call to it,
 * decoupling callers from the concrete export implementation.
 */
public class ExportContext {

    private final ExportStrategy strategy;

    /**
     * @param strategy the export strategy to use
     */
    public ExportContext(ExportStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Executes the held strategy to write transactions to the output file.
     *
     * @param transactions the list of transactions to export
     * @param outputPath   destination file path
     */
    public void execute(List<Transaction> transactions, Path outputPath) {
        strategy.export(transactions, outputPath);
    }
}
