import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class E {
    public static void main(String[] args) {
        MyReader reader = new MyReader();
        int n = Integer.parseInt(reader.nextLine());
        int[] o1 = new int[n];
        int[] o2 = new int[n];
        for (int i = 0; i < n; i++) {
            String[] line = reader.nextLine().split(" ");
            o1[i] = Integer.parseInt(line[0]);
            o2[i] = Integer.parseInt(line[1]);
        }
        int[] o1Sort = Arrays.copyOf(o1, o1.length);
        int[] o2Sort = Arrays.copyOf(o2, o2.length);
        Arrays.sort(o1Sort);
        Arrays.sort(o2Sort);

        Map<Integer, Integer> map1 = new HashMap<>();
        for (int i = 0; i < o1.length; i++) {
            int key = o1Sort[i];
            int value = i;
            map1.put(key, value);
        }

        Map<Integer, Integer> map2 = new HashMap<>();
        for (int i = 0; i < o2.length; i++) {
            int key = o2Sort[i];
            int value = i;
            map2.put(key, value);
        }

        double sum = 0;
        for (int i = 0; i < n; i++) {
            double delt = map1.get(o1[i]) - map2.get(o2[i]);
            sum += 6 * Math.pow(delt, 2);
        }

        System.out.println(1 - (sum / (Math.pow(n, 3) - n)));
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
