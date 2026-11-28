package data_access;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import use_case.mark_pet.MarkedPetsDataAccessInterface;

/**
 * Keeps each adopter's marked cats in a JSON file, keyed by adopter id.
 * A missing or unreadable file is treated as "nothing marked yet".
 */
public class FileMarkedPetsDataAccessObject implements MarkedPetsDataAccessInterface {
    private final Path jsonPath;
    private final Map<String, Set<String>> marked = new HashMap<>();

    /**
     * Opens the file, reading whatever it holds.
     *
     * @param jsonPath the file to read and write
     */
    public FileMarkedPetsDataAccessObject(Path jsonPath) {
        this.jsonPath = jsonPath;
        load();
    }

    @Override
    public synchronized Set<String> findMarkedPetIds(String adopterId) {
        return Set.copyOf(marked.getOrDefault(adopterId, Set.of()));
    }

    @Override
    public synchronized void mark(String adopterId, String petId) {
        if (marked.computeIfAbsent(adopterId, id -> new HashSet<>()).add(petId)) {
            write();
        }
    }

    @Override
    public synchronized void unmark(String adopterId, String petId) {
        final Set<String> ids = marked.get(adopterId);
        if (ids != null && ids.remove(petId)) {
            write();
        }
    }

    private void load() {
        try {
            final JSONObject root = new JSONObject(Files.readString(jsonPath));
            for (String adopterId : root.keySet()) {
                final Set<String> ids = new HashSet<>();
                final JSONArray array = root.getJSONArray(adopterId);
                for (int i = 0; i < array.length(); i++) {
                    ids.add(array.getString(i));
                }
                marked.put(adopterId, ids);
            }
        } catch (NoSuchFileException | JSONException error) {
            marked.clear();
        } catch (IOException error) {
            throw new IllegalStateException("Could not read " + jsonPath, error);
        }
    }

    private void write() {
        final JSONObject root = new JSONObject();
        for (Map.Entry<String, Set<String>> entry : marked.entrySet()) {
            root.put(entry.getKey(), new JSONArray(entry.getValue()));
        }
        try {
            Files.writeString(jsonPath, root.toString(2));
        } catch (IOException error) {
            throw new IllegalStateException("Could not write " + jsonPath, error);
        }
    }
}
