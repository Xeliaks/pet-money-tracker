package pet.money.tracker.patterns;

import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.model.Transaction;

/** Strategy interface for exporting transactions to a specific file format. */
public interface ExportStrategy {

    /**
     * Exports the given transactions to the specified output path.
     *
     * @param transactions the list of transactions to export
     * @param outputPath   destination file
     */
    void export(List<Transaction> transactions, Path outputPath);

    /**
     * @return the short format name, e.g. "json" or "csv"
     */
    String getFormatName();
}
