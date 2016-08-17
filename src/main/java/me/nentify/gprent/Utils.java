package me.nentify.gprent;

public class Utils {

    public static int getCurrentUnixTimestamp() {
        return (int) (System.currentTimeMillis() / 1000.0);
    }
}
