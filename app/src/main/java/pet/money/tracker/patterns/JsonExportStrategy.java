package pet.money.tracker.patterns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.model.Transaction;

/** Strategy that serialises transactions to a pretty-printed JSON file. */
public class JsonExportStrategy extends AbstractExportStrategy {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doExport(List<Transaction> transactions, Path outputPath) throws IOException {
        MAPPER.writeValue(outputPath.toFile(), transactions);
    }

    @Override
    public String getFormatName() {
        return "json";
    }
}
