package pet.money.tracker.patterns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.exception.AppException;
import pet.money.tracker.model.Transaction;

/** Strategy that serialises transactions to a pretty-printed JSON file. */
public class JsonExportStrategy implements ExportStrategy {

    @Override
    public void export(List<Transaction> transactions, Path outputPath) {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(outputPath.toFile(), transactions);
        } catch (IOException e) {
            throw new AppException("Failed to export JSON to " + outputPath, e);
        }
    }

    @Override
    public String getFormatName() {
        return "json";
    }
}
