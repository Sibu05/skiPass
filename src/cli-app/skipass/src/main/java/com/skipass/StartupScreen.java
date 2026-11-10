package com.skipass;

import java.io.Console;
import java.util.concurrent.ThreadLocalRandom;

/** A compact ASCII wordmark, optional slogan, and fact before vault input. */
final class StartupScreen {
    // Add your slogan here when you have chosen it. Blank means no slogan line.
    private static final String SLOGAN = "";
    private static final String LOGO = String.join("\n",
            "       _    _ ___",
            "   ___| | _(_) _ \\__ _ ___ ___",
            "  (_-<| ' <| |  _/ _` (_-<(_-<",
            "  /__/|_|\\_\\_|_| \\__,_/__/__/");
    private static final int FACT_WIDTH = 72;

    static void show(Console console, boolean creating) {
        console.printf("%n%s%n%n", LOGO);
        if (!SLOGAN.isBlank()) {
            console.printf("%s%n%n", SLOGAN);
        }
        showFact(console);
        console.printf("%n  %s%n", creating ? "Create your vault" : "Welcome back");
        if (creating) {
            console.printf("  Choose a master password with 12+ characters.%n");
            console.printf("  Keep it safe. There is no password recovery.%n");
        }
        console.printf("  Password input is hidden.%n%n");
    }

    private static void showFact(Console console) {
        if (psswordStats.STATS.isEmpty()) return;
        var fact = psswordStats.STATS.get(ThreadLocalRandom.current().nextInt(psswordStats.STATS.size()));
        printWrapped(console, fact.text());
        printWrapped(console, " ~ " + fact.source());
    }

    private static void printWrapped(Console console, String text) {
        StringBuilder line = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (word.isEmpty()) continue;
            if (!line.isEmpty() && line.length() + 1 + word.length() > FACT_WIDTH) {
                console.printf("%s%n", line);
                line.setLength(0);
            }
            if (!line.isEmpty()) line.append(' ');
            line.append(word);
        }
        if (!line.isEmpty()) console.printf("%s%n", line);
    }
}
