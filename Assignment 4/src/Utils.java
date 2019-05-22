public class Utils {
    private static int p = 15486907;

    public static long getHornerValue(String str) {
        int hornerValue = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            int charValue = (int) str.charAt(i);
            hornerValue = hornerValue * 256 + charValue;
        }
        return hornerValue;
    }

    // Returns hash as described in the assignment description.
    public static int getHash(long hornerValue, int a, int b, int m) {
        return (int) ((a * hornerValue + b) % p) % m;
    }
}
