package com.skipass;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javax.crypto.AEADBadTagException;

public class main {
    private static Console console;
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("skiPass [vault-path]\nCreate or unlock a local encrypted password vault.");
            return;
        }
        if (args.length > 1) { System.err.println("Usage: skiPass [vault-path]"); System.exit(1); }
        console = System.console();
        if (console == null) {
            System.err.println("Run skiPass in a terminal for hidden password input."); System.exit(1);
        }
        Path path = (args.length == 1 ? Path.of(args[0]) : Path.of(System.getProperty("user.home"), ".local", "share", "skipass", "vault.skp")).toAbsolutePath();
        char[] master = null;
        try {
            Files.createDirectories(path.getParent());
            try (FileChannel channel = FileChannel.open(Path.of(path + ".lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS);
                 FileLock lock = channel.tryLock()) {
                if (lock == null) throw new IOException("Vault is open in another skiPass session.");
                Map<String, vault.Login> entries;
                boolean creating = !Files.exists(path, LinkOption.NOFOLLOW_LINKS);
                StartupScreen.show(console, creating);
                if (!creating) {
                    master = secret("  Master password > ");
                    entries = vault.load(path, master);
                } else {
                    master = newMaster();
                    entries = new TreeMap<>();
                    vault.save(path, entries, master);
                }
                console.printf("%n  Vault unlocked. Ready when you are.%n%n");
                while (true) {
                    String action = line("[list / add / get / delete / change-master / quit] > ");
                    switch (action) {
                        case "list" -> {
                            if (entries.isEmpty()) console.printf("Vault is empty.%n");
                            entries.keySet().forEach(name -> console.printf("%s%n", safe(name)));
                        }
                        case "add" -> {
                            String name = line("Entry name: ").trim();
                            if (name.isEmpty() || entries.containsKey(name)) { console.printf("Name is empty or already exists.%n"); break; }
                            String username = line("Username: ");
                            char[] password = secret("Login password (blank generates one): ");
                            try {
                                String value = password.length == 0 ? generate() : new String(password);
                                entries.put(name, new vault.Login(username, value, line("URL (optional): ")));
                                vault.save(path, entries, master);
                                console.printf("Login saved.%n");
                            } finally { Arrays.fill(password, '\0'); }
                        }
                        case "get" -> {
                            var login = entries.get(line("Entry name: "));
                            if (login == null) { console.printf("Entry not found.%n"); break; }
                            console.printf("Username: %s%nURL: %s%n", safe(login.username()), safe(login.url()));
                            if (line("Reveal password on screen? Type yes: ").equals("yes")) console.printf("%s%n", safe(login.password()));
                        }
                        case "delete" -> {
                            String name = line("Entry name: ");
                            if (!entries.containsKey(name)) { console.printf("Entry not found.%n"); break; }
                            if (line("Delete this login? Type yes: ").equals("yes")) {
                                entries.remove(name); vault.save(path, entries, master); console.printf("Login deleted.%n");
                            }
                        }
                        case "change-master" -> {
                            char[] replacement = newMaster();
                            try { vault.save(path, entries, replacement); Arrays.fill(master, '\0'); master = replacement.clone(); }
                            finally { Arrays.fill(replacement, '\0'); }
                            console.printf("Master password changed.%n");
                        }
                        case "quit" -> { return; }
                        default -> console.printf("Choose one of the listed commands.%n");
                    }
                }
            }
        } catch (AEADBadTagException e) {
            System.err.println("Wrong master password or damaged vault."); System.exit(1);
        } catch (IOException | GeneralSecurityException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage()); System.exit(1);
        } finally { if (master != null) Arrays.fill(master, '\0'); }
    }
    private static String line(String label) throws EOFException {
        String result = console.readLine("%s", label);
        if (result == null) throw new EOFException("Input closed; vault locked.");
        return result;
    }
    private static char[] secret(String label) throws EOFException {
        char[] result = console.readPassword("%s", label);
        if (result == null) throw new EOFException("Input closed; vault locked.");
        return result;
    }
    private static char[] newMaster() throws IOException {
        while (true) {
            char[] first = secret("  New master password (12+ characters) > "), second = null;
            boolean accepted = false;
            try {
                second = secret("  Confirm master password > ");
                accepted = first.length >= 12 && Arrays.equals(first, second);
                if (accepted) return first;
                console.printf("%n  Use at least 12 characters and matching passwords.%n%n");
            } finally {
                if (!accepted) Arrays.fill(first, '\0');
                if (second != null) Arrays.fill(second, '\0');
            }
        }
    }
    private static String generate() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 24; i++) result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        return result.toString();
    }
    private static String safe(String text) {
        StringBuilder result = new StringBuilder();
        text.codePoints().forEach(c -> {
            if (Character.isISOControl(c) || Character.getType(c) == Character.FORMAT) result.append(String.format("\\u%04x", c));
            else result.appendCodePoint(c);
        });
        return result.toString();
    }
}
