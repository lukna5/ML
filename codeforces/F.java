import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class F {
    public static void main(String[] args) throws IOException {
        MyReader reader = new MyReader();
        int k = Integer.parseInt(reader.nextLine());
        int n = Integer.parseInt(reader.nextLine());
        int[] xL = new int[n];
        int[] yL = new int[n];
        for (int i = 0; i < n; i++) {
            String[] line = reader.nextLine().split(" ");
            int x = Integer.parseInt(line[0]);
            int y = Integer.parseInt(line[1]);
            xL[i] = x;
            yL[i] = y;
        }
        System.out.println(inner_dist(xL, yL));
        System.out.println(outerDistance(xL, yL));


    }
    private static long distance(int[] xL){
        int[] a = Arrays.copyOf(xL, xL.length);
        Arrays.sort(a);
        long r = 0;
        for (int j : a) {
            r += j;
        }
        long l = 0;
        long res = 0;
        for (int i = 0; i < a.length; i++) {
            l += a[i];
            res += a[i] * (i * 2L + 1 - a.length) + r - l;
            r -= a[i];
        }
        return res;
    }

    private static long inner_dist(int[] x, int[] y){
        Map<Integer, ArrayList<Integer>> map = new HashMap<>();
        for (int j : y) {
            map.put(j, new ArrayList<>());
        }
        for (int i = 0; i < x.length; i++) {
            map.get(y[i]).add(x[i]);
        }
        long sum = 0;
        for (Integer key: map.keySet()) {
            sum += distance(map.get(key).stream().mapToInt(Integer::intValue).toArray());
        }
        return sum;
    }

    private static long outerDistance(int[] x, int[] y){
        return distance(x) - inner_dist(x, y);
    }
    static class MyReader {
        BufferedReader reader;
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
