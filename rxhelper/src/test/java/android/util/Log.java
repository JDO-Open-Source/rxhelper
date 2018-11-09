package android.util;

/**
 * The type Log.
 */
public class Log {
    /**
     * D int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int d(String tag, String msg) {
        System.out.println("DEBUG: " + tag + ": " + msg);
        return 0;
    }

    /**
     * int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int i(String tag, String msg) {
        System.out.println("INFO: " + tag + ": " + msg);
        return 0;
    }

    /**
     * W int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int w(String tag, String msg) {
        System.out.println("WARN: " + tag + ": " + msg);
        return 0;
    }

    /**
     * E int.
     *
     * @param tag the tag
     * @param msg the msg
     * @return the int
     */
    public static int e(String tag, String msg) {
        System.out.println("ERROR: " + tag + ": " + msg);
        return 0;
    }

    // add other methods if required...
}