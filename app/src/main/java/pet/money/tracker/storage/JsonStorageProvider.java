package pet.money.tracker.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import pet.money.tracker.exception.StorageException;
import pet.money.tracker.model.Transaction;

/** Reads and writes the transaction list as a pretty-printed JSON file. */
public class JsonStorageProvider implements StorageProvider {

    private final Path filePath;
    private final ObjectMapper mapper;

    /**
     * @param filePath path to the JSON data file (created on first save if absent)
     */
    public JsonStorageProvider(Path filePath) {
        this.filePath = filePath;
        this.mapper = buildMapper();
    }

    @Override
    public List<Transaction> loadAll() throws StorageException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(filePath.toFile(), new TypeReference<List<Transaction>>() {});
        } catch (IOException e) {
            throw new StorageException("Failed to read data from " + filePath, e);
        }
    }

    @Override
    public void saveAll(List<Transaction> transactions) throws StorageException {
        try {
            Files.createDirectories(filePath.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), transactions);
        } catch (IOException e) {
            throw new StorageException("Failed to write data to " + filePath, e);
        }
    }

    private ObjectMapper buildMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}