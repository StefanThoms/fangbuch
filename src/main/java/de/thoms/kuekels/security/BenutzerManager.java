package de.thoms.kuekels.security;

public class BenutzerManager {
    private static int angemeldeteBenutzer = 0;

    public static synchronized void benutzerAnmelden() {
        angemeldeteBenutzer++;
    }

    public static synchronized void benutzerAbmelden() {
        angemeldeteBenutzer--;
    }

    public static int getAnzahlAngemeldeteBenutzer() {
        return angemeldeteBenutzer;
    }
}
