package ph.kana.memory.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class FileLocationHolder {

    private static final String LOCKER_ROOT = System.getProperty("pstash.locker_root");
    private static final String TEMP_ROOT = System.getProperty("pstash.temp_dir");
    private static final Logger log = Logger.getLogger(FileLocationHolder.class.getName());

    private static final String TARGET_REGEX =
        "([0-9a-f]{8})(-)([0-9a-f]{4})(-4)([0-9a-f]{3})(-)([0-9a-f]{4})(-)([0-9a-f]{12})";

    private static final int ROOT_FILE = 0;
    private static final int AUTH_FILE = 1;
    private static final int DB_FILE = 2;
    private static final int KEY_FILE = 3;
    private static final int ZIP_FILE = 4;
    private static final int TEMP_DIR = 5;
    private static File[] fileCache = new File[6];

    private static final FileLocationHolder INSTANCE = new FileLocationHolder();

    private FileLocationHolder() {
        fileCache[ROOT_FILE] = new File(LOCKER_ROOT);
        fileCache[AUTH_FILE] = new File(LOCKER_ROOT + "/a");
        fileCache[DB_FILE] = new File(LOCKER_ROOT + "/d");
        fileCache[KEY_FILE] = new File(LOCKER_ROOT + "/k");
        fileCache[ZIP_FILE] = new File(LOCKER_ROOT + "/p");
        fileCache[TEMP_DIR] = new File(TEMP_ROOT);
        createHiddenDir(fileCache[ROOT_FILE]);
    }

    public static FileLocationHolder getInstance() {
        return INSTANCE;
    }

    public File getRoot() {
        return fileCache[ROOT_FILE];
    }

    public File getAuth() {
        return fileCache[AUTH_FILE];
    }

    public File getDb() {
        return fileCache[DB_FILE];
    }

    public File getZip() {
        return fileCache[ZIP_FILE];
    }

    public char[] getKey() {
        try {
            var key = fileCache[KEY_FILE];
            if (!key.exists()) {
                buildKeyFile(key);
            }
            return readKeyFile(key);
        } catch (IOException e) {
            log.severe(e::getMessage);
            return new char[0];
        }
    }

    public File getTempDir() {
        return fileCache[TEMP_DIR];
    }

    private void createHiddenDir(File file) {
        var osName = System.getProperty("os.name").toLowerCase();

        try {
            if (!file.exists() && !file.mkdir()) {
                throw new IOException("Failed to create directory: " + file);
            }
            if (osName.startsWith("windows")) {
                Files.setAttribute(file.toPath(), "dos:hidden", true);
            }
        } catch (IOException e) {
            log.severe(e::getMessage);
        }
    }

    private void buildKeyFile(File key) throws IOException {
        try (var writer = new PrintWriter(key)) {
            writer.println(generateKey());
            writer.flush();
        }
    }

    private char[] readKeyFile(File key) throws IOException {
        var keyData = Files.readString(key.toPath());
        return keyData.toCharArray();
    }

    private String generateKey() {
        var uuid = UUID.randomUUID().toString();
        var replacement = "$1" +
            randomAlphaNum() +
            "$3" +
            randomAlphaNum() +
            randomAlphaNum() +
            "$5" +
            randomAlphaNum() +
            "$7" +
            randomAlphaNum() +
            "$9";

        return uuid.replaceAll(TARGET_REGEX, replacement);
    }

    private char randomAlphaNum() {
        var alphabet = "abcdef0123456789";
        var rng = new Random();

        var i = rng.nextInt(alphabet.length());
        return alphabet.charAt(i);
    }
}
