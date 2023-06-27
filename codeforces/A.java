import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class A {
    private static Map<Integer, Integer> countOccurrences(int[] arr) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int value : arr) {
            countMap.put(value, countMap.getOrDefault(value, 0) + 1);
        }
        return countMap;
    }

    private static void zeroIn(int[] a, int ind, int m) {
        if (a[ind] == 0) {
            double count = -0.5;
            int bitls = ind;
            for (int j = 0; j < m; j++) {
                if (bitls % 2 == 1) {
                    System.out.print("-1.0 ");
                    count++;
                } else {
                    System.out.print("1.0 ");
                }
                bitls /= 2;
            }
            System.out.println(count);
        }
    }

    private static void oneIn(int[] a, int ind, int m){
        switch (a[ind]) {
            case 1 -> {
                double counts = 0.5;
                int bitls = ind;

                for (int j = 0; j < m; j++) {
                    switch (bitls % 2) {
                        case 1 -> {
                            System.out.print("1.0 ");
                            counts--;
                        }
                        case 0 -> System.out.print("-1.0 ");
                    }
                    bitls = bitls / 2;
                }
                System.out.println(counts);
            }
            case 0 -> {
            }
        }
    }
    public static void main(String[] args) throws IOException {
        MyReader reader = new MyReader();
        //BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        int m = reader.nextInt();

        int[] a = new int[1 << m];
        for (int i = 0; i < a.length; i++) {
            a[i] = reader.nextInt();
        }
        Map<Integer, Integer> map = countOccurrences(a);
        int numZeros = map.getOrDefault(0, 0);
        int numOnes = map.getOrDefault(1, 0);

        if (numZeros == a.length || numOnes == a.length) {
            System.out.println("1");
            System.out.println("1");
            for (int i = 0; i < m; i++) {
                System.out.print("0 ");
            }
            System.out.print(numZeros == a.length ? "-0.5" : "0.5");
            return;
        }

        System.out.println("2");
        if (numZeros >= numOnes) {
            System.out.println(numOnes + " 1");
            for (int i = 0; i < a.length; i++) {
                oneIn(a, i, m);
            }
            String out = "1 ".repeat(Math.max(0, numOnes)) + "-0.5";
            System.out.println(out);

        } else {
            System.out.println(numZeros + " 1");
            for (int i = 0; i < a.length; i++) {
                zeroIn(a, i, m);
            }
            String out = "1 ".repeat(Math.max(0, numZeros)) + (0.5 - numZeros);
            System.out.println(out);
        }
    }
}

class MyReader {
    BufferedReader reader;
    String curLine = null;
    int ind = 0;

    public MyReader() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public String nextLine() {
        if (curLine == null) {
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