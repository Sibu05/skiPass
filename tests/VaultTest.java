package com.skipass;
import java.nio.file.*;
import java.util.*;
import javax.crypto.AEADBadTagException;

public class VaultTest {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("skipass-test-");
        Path path = directory.resolve("vault.skp");
        char[] master = "test master password".toCharArray();
        var entries = new TreeMap<String, vault.Login>();
        entries.put("Example", new vault.Login("alice", "secret-password", "https://example.test"));
        try {
            vault.save(path, entries, master);
            check(entries.equals(vault.load(path, master)), "round trip");
            byte[] original = Files.readAllBytes(path);
            check(!new String(original, java.nio.charset.StandardCharsets.ISO_8859_1).contains("secret-password"), "encrypted at rest");
            try { vault.load(path, "wrong".toCharArray()); throw new AssertionError("wrong password accepted"); }
            catch (AEADBadTagException expected) {}
            byte[] damaged = original.clone(); damaged[damaged.length - 1] ^= 1; Files.write(path, damaged);
            try { vault.load(path, master); throw new AssertionError("tampering accepted"); }
            catch (AEADBadTagException expected) {}
            Files.write(path, Arrays.copyOf(original, 20));
            try { vault.load(path, master); throw new AssertionError("truncation accepted"); }
            catch (java.io.IOException expected) {}
            vault.save(path, entries, master);
            check(!Arrays.equals(original, Files.readAllBytes(path)), "fresh salt and nonce");
            char[] replacement = "replacement password".toCharArray();
            vault.save(path, entries, replacement);
            check(entries.equals(vault.load(path, replacement)), "master password change");
            try { vault.load(path, master); throw new AssertionError("old password accepted"); }
            catch (AEADBadTagException expected) {}
            if (Files.getFileStore(path).supportsFileAttributeView("posix"))
                check(Files.getPosixFilePermissions(path).equals(java.nio.file.attribute.PosixFilePermissions.fromString("rw-------")), "private file permissions");
            entries.clear(); vault.save(path, entries, replacement);
            check(vault.load(path, replacement).isEmpty(), "deletion persists");
            System.out.println("Vault checks passed.");
        } finally { Files.deleteIfExists(path); Files.deleteIfExists(directory); }
    }
    static void check(boolean result, String label) { if (!result) throw new AssertionError(label); }
}
