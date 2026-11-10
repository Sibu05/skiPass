package com.skipass;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/** Versioned, authenticated local vault. All entry metadata is encrypted. */
public final class vault {
    private static final byte[] MAGIC = {'S','K','I','P','A','S','S',1};
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LIMIT = 16 * 1024 * 1024;
    public record Login(String username, String password, String url) {}

    private static byte[] key(char[] password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 600_000, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        finally { spec.clearPassword(); }
    }

    public static Map<String, Login> load(Path path, char[] password) throws IOException, GeneralSecurityException {
        if (Files.isSymbolicLink(path)) throw new IOException("Vault cannot be a symbolic link.");
        byte[] blob;
        try (InputStream stream = Files.newInputStream(path)) { blob = stream.readNBytes(LIMIT + 1); }
        if (blob.length < 52 || blob.length > LIMIT || !Arrays.equals(MAGIC, Arrays.copyOf(blob, 8)))
            throw new IOException("Unsupported or damaged vault.");
        byte[] salt = Arrays.copyOfRange(blob, 8, 24), nonce = Arrays.copyOfRange(blob, 24, 36);
        byte[] derived = key(password, salt), plaintext = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(derived, "AES"), new GCMParameterSpec(128, nonce));
            cipher.updateAAD(Arrays.copyOf(blob, 36));
            plaintext = cipher.doFinal(blob, 36, blob.length - 36);
            Map<String, Login> result = new TreeMap<>();
            try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(plaintext))) {
                int count = in.readInt();
                if (count < 0 || count > 100_000) throw new IOException("Invalid entry count.");
                for (int i = 0; i < count; i++) {
                    String name = in.readUTF();
                    if (result.put(name, new Login(in.readUTF(), in.readUTF(), in.readUTF())) != null)
                        throw new IOException("Duplicate entry in vault.");
                }
                if (in.available() != 0) throw new IOException("Invalid vault contents.");
            }
            return result;
        } finally {
            Arrays.fill(derived, (byte) 0);
            if (plaintext != null) Arrays.fill(plaintext, (byte) 0);
        }
    }

    /** Caller holds the vault lock for the whole read-modify-write transaction. */
    public static void save(Path path, Map<String, Login> entries, char[] password) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(buffer)) {
            out.writeInt(entries.size());
            for (var entry : entries.entrySet()) {
                out.writeUTF(entry.getKey()); out.writeUTF(entry.getValue().username());
                out.writeUTF(entry.getValue().password()); out.writeUTF(entry.getValue().url());
            }
        }
        byte[] plaintext = buffer.toByteArray();
        if (plaintext.length > LIMIT - 52) throw new IOException("Vault size limit exceeded.");
        byte[] salt = new byte[16], nonce = new byte[12];
        RANDOM.nextBytes(salt); RANDOM.nextBytes(nonce);
        byte[] derived = key(password, salt);
        Path temporary = null;
        try {
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            blob.write(MAGIC); blob.write(salt); blob.write(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(derived, "AES"), new GCMParameterSpec(128, nonce));
            cipher.updateAAD(blob.toByteArray());
            blob.write(cipher.doFinal(plaintext));
            temporary = Files.createTempFile(path.getParent(), ".skipass-", ".tmp");
            if (Files.getFileStore(temporary).supportsFileAttributeView("posix"))
                Files.setPosixFilePermissions(temporary, PosixFilePermissions.fromString("rw-------"));
            try (var channel = java.nio.channels.FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                var bytes = java.nio.ByteBuffer.wrap(blob.toByteArray());
                while (bytes.hasRemaining()) channel.write(bytes);
                channel.force(true);
            }
            Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Arrays.fill(derived, (byte) 0); Arrays.fill(plaintext, (byte) 0);
            if (temporary != null) Files.deleteIfExists(temporary);
        }
    }
}
