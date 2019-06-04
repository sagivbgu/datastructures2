public class Utils {
    private static int p = 15486907;

    public static long getHornerValue(String str) {
        long hornerValue = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            int charValue = (int) str.charAt(i);
            hornerValue = (hornerValue * 256 + charValue) % p;
        }
        return hornerValue;
    }

    // Returns hash as described in the assignment description.
    public static int getHash(long hornerValue, int a, int b, int m) {
        return (int) ((a * hornerValue + b) % p) % m;
    }

    public static String getElapsedTimeInMs(Runnable action) {
        long startTime = System.nanoTime();
        Thread actionThread = new Thread(action);
        actionThread.start();
        try {
            actionThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / 1000000.0;
        return String.format("%.4f", elapsedTime);
    }
}
