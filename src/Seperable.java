/**
 * An element that can be searched or matched against a given key.
 */
public interface Seperable {

    /**
     * Determines whether the object matches the given search key.
     *
     * @param key the search keyword (never null)
     * @return true if matches, false otherwise
     */
    boolean matches(String key);
}
