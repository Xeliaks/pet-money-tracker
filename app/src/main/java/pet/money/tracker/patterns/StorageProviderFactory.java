package pet.money.tracker.patterns;

import java.nio.file.Path;
import pet.money.tracker.storage.StorageProvider;

/**
 * Factory Method base class for creating {@link StorageProvider} instances.
 *
 * <p>Use {@link #forFormat(String)} to obtain the concrete factory for a given
 * storage format, then call {@link #createProvider(Path)} to get the provider.
 */
public abstract class StorageProviderFactory {

    /**
     * Creates a new {@link StorageProvider} backed by the given file path.
     *
     * @param path destination file for reading and writing transaction data
     * @return a ready-to-use {@link StorageProvider}
     */
    public abstract StorageProvider createProvider(Path path);

    /**
     * Returns the concrete factory for the requested format name.
     *
     * @param format the short format name, e.g. {@code "json"}
     * @return the matching factory
     * @throws IllegalArgumentException if the format is not recognised
     */
    public static StorageProviderFactory forFormat(String format) {
        if ("json".equalsIgnoreCase(format)) {
            return new JsonStorageProviderFactory();
        }
        throw new IllegalArgumentException("Unknown storage format: " + format);
    }
}
