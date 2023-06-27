import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class H {

    public static void main(String[] args) {
        MyReader reader = new MyReader();
        int k = Integer.parseInt(reader.nextLine());
        int n = Integer.parseInt(reader.nextLine());
        long[] sumY = new long[k];
        long[] sqrY = new long[k];
        long[] cntY = new long[k];

        for (int i = 0; i < n; i++) {
            String[] line = reader.nextLine().split(" ");
            int x = Integer.parseInt(line[0]) - 1;
            int y = Integer.parseInt(line[1]);
            sumY[x] += y;
            sqrY[x] += (long) y * y;
            cntY[x]++;
        }

        System.out.println(calculateDisp(sumY, sqrY, cntY, k, n));
    }

    private static double calculateDisp(long[] sumY, long[] sqrY, long[] cntY, int k, int n){
        double res = 0;
        for (int i = 0; i < k; i++) {
            if (cntY[i] != 0) {
                double mean = ((double) sumY[i]) / cntY[i];
                double condVar = ((double) sqrY[i] - 2 * mean * sumY[i] + cntY[i] * Math.pow(mean, 2)) / cntY[i];
                res += condVar * cntY[i] / n;
            }
        }
        return res;
    }

    static class MyReader {
        final BufferedReader reader;
        String curLine = null;
        int ind = 0;

        public MyReader() {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        public String nextLine() {
            if (curLine == null || ind >= curLine.length()) {
                ind = 0;
                try {
                    return reader.readLine();
                } catch (IOException ignored) {
                }
            }
            return curLine;
        }

        // Minus only in Numbers
        public int nextInt() {
            if (curLine == null) {
                try {
                    ind = 0;
                    while (curLine == null || curLine.length() == 0) {
                        curLine = reader.readLine();
                    }
                } catch (IOException ignored) {

                }
            }
            StringBuilder builder = new StringBuilder();
            while (curLine.charAt(ind) != '-' && !Character.isDigit(curLine.charAt(ind))) {
                ind++;
                if (ind >= curLine.length()) {
                    try {
                        curLine = reader.readLine();
                    } catch (IOException ignored) {

                    }
                    ind = 0;
                }
            }
            builder.append(curLine.charAt(ind));
            ind++;
            while (ind < curLine.length() && Character.isDigit(curLine.charAt(ind))) {
                builder.append(curLine.charAt(ind));
                ind++;
            }
            if (curLine.length() == ind) {
                curLine = null;
                ind = 0;
            }
            return Integer.parseInt(builder.toString());
        }
    }
}

