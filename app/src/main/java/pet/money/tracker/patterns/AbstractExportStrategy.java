package pet.money.tracker.patterns;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.exception.AppException;
import pet.money.tracker.model.Transaction;

/**
 * Eliminates duplicate directory-creation and error-handling boilerplate
 * shared by all concrete export strategies.
 */
public abstract class AbstractExportStrategy implements ExportStrategy {

    @Override
    public final void export(List<Transaction> transactions, Path outputPath) {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            doExport(transactions, outputPath);
        } catch (IOException e) {
            throw new AppException(
                    "Failed to export " + getFormatName() + " to " + outputPath, e);
        }
    }

    /**
     * Performs format-specific serialization after the parent directory has been created.
     *
     * @param transactions the list of transactions to write
     * @param outputPath   destination file path (parent directory is guaranteed to exist)
     * @throws IOException if writing fails
     */
    protected abstract void doExport(List<Transaction> transactions, Path outputPath)
            throws IOException;
}
