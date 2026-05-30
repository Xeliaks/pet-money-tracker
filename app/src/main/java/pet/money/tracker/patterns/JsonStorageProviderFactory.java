package pet.money.tracker.patterns;

import java.nio.file.Path;
import pet.money.tracker.storage.JsonStorageProvider;
import pet.money.tracker.storage.StorageProvider;

/** Concrete Factory Method implementation that produces {@link JsonStorageProvider} instances. */
public class JsonStorageProviderFactory extends StorageProviderFactory {

    @Override
    public StorageProvider createProvider(Path path) {
        return new JsonStorageProvider(path);
    }
}
