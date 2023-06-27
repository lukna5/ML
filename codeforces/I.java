import java.io.*;
import java.util.*;

public class I1 {
    private static class Pair {
        int x;
        int y;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return x == pair.x && y == pair.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        MyReader myReader = new MyReader();

        int k1 = myReader.nextInt();
        int k2 = myReader.nextInt();
        int n = myReader.nextInt();

        Map<Pair, Integer> map = new HashMap<>();
        int[] xL = new int[k1];
        int[] yL = new int[k2];
        for (int i = 0; i < n; i++) {
            String[] line = myReader.nextLine().split(" ");
            int x = Integer.parseInt(line[0]);
            int y = Integer.parseInt(line[1]);
            if (map.containsKey(new Pair(x - 1, y - 1))) {
                map.put(new Pair(x - 1, y - 1), map.get(new Pair(x - 1, y - 1)) + 1);
            } else {
                map.put(new Pair(x - 1, y - 1), 1);
            }
            xL[x - 1] += 1;
            yL[y - 1] += 1;
        }

        double res = 0.0;
        double delta = 0.0;

        for (Pair pair : map.keySet()) {
            double c = ((double) xL[pair.x] * yL[pair.y]) / n;
            delta += c;
            res += Math.pow(map.get(pair) - c, 2) / c;
        }
        System.out.println(n - delta + res);
    }

    static class MyReader {
        BufferedReader reader;
        String curLine = null;
        int ind = 0;

        public MyReader() {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        public MyReader(String file) throws FileNotFoundException {
            reader = new BufferedReader(new FileReader(file));
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
