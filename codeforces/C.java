import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class C {
    public enum Type {
        RELU, POOL, BIAS, CNVM, CNVE, CNVC, START
    }

    private static class Node {
        double[][][] res;
        double[][][] dRes; // По входу
        double[][][][] filters;
        double[][][] paddedIn;
        double[][][][] parameters;
        List<Integer> biasB;
        Node inFather;
        Type type;
        int d;
        int s;
        int n;
        int m;

        int d1;
        int n1;
        int m1;
        double reluA;
        int p;
        int k;
        int h;

        public Node(double[][][] res) {
            this.res = res;
            this.d = res.length;
            this.n = res[0].length;
            this.m = res[0][0].length;
        }

        public Node(int d, int n, int m, Type type, Node inFather) {
            this.d = d;
            this.n = n;
            this.m = m;
            this.d1 = inFather.d;
            this.n1 = inFather.n;
            this.m1 = inFather.m;
            res = new double[d][n][m];
            dRes = new double[d1][n1][m1];
            this.type = type;
            this.inFather = inFather;
        }

        public Node(double[][][] res, Node inFather) {
            this.res = res;
            this.d = res.length;
            this.n = res[0].length;
            this.m = res[0][0].length;
            this.d1 = inFather.d;
            this.n1 = inFather.n;
            this.m1 = inFather.m;
            this.dRes = new double[d1][n1][m1];
            this.inFather = inFather;
        }

        public Node(double[][][] res, Type type, Node inFather) {
            this.res = res;
            this.d = res.length;
            this.n = res[0].length;
            this.m = res[0][0].length;
            this.d1 = inFather.d;
            this.n1 = inFather.n;
            this.m1 = inFather.m;
            this.dRes = new double[d1][n1][m1];
            this.type = type;
            this.inFather = inFather;
        }

        public Node(double[][][] res, boolean copy, Type type, Node inFather) {
            if (!copy) {
                new Node(res, type, inFather);
                return;
            }
            this.type = type;
            this.d = res.length;
            this.n = res[0].length;
            this.m = res[0][0].length;
            this.d1 = inFather.d;
            this.n1 = inFather.n;
            this.m1 = inFather.m;
            this.res = new double[d][n][m];
            this.dRes = new double[d1][n1][m1];
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < m; k++) {
                        this.res[i][j][k] = res[i][j][k];
                    }
                }
            }
            this.inFather = inFather;
        }
    }

    private static double[][][] readTensor(int d, int n, int m, String[] line, int s) {
        double[][][] res = new double[d][n][m];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < m; k++) {
                    res[i][j][k] = Integer.parseInt(line[s++]);
                }
            }
        }
        return res;
    }

    private static MyReader reader;

    public static void main(String[] args) {
        reader = new MyReader();
        String[] line = reader.nextLine().split(" ");
        int N = Integer.parseInt(line[0]);
        int D = Integer.parseInt(line[1]);
        double[][][] inTens = readTensor(D, N, N, line, 2);
        Node preStart = new Node(inTens);
        Node node = new Node(inTens, Type.START, preStart);
        int L = Integer.parseInt(reader.nextLine());
        List<Node> list = new ArrayList<>();
        list.add(node);
        for (int i = 0; i < L; i++) {
            list.add(calculateBlock(list.get(i)));
        }
        printTensor(list.get(L));
        Node last = list.get(L);
        double[][][] dy = readTensor(last.d, last.n, last.m, reader.nextLine().split(" "), 0);
        List<double[][][][]> params = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            dCalculateBlock(list.get(i), dy);
            double[][][][] parameters = list.get(i).parameters;
            if (parameters != null) {
                params.add(parameters);
            }
            dy = list.get(i).dRes;
        }
        printTensor(list.get(0).dRes);
        for (int i = params.size() - 1; i >= 0; i--) {
            printMegaTensor(params.get(i));
        }
    }

    private static void printTensor(Node a) {
        double[][][] res = a.res;
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                for (int k = 0; k < res[0][0].length; k++) {
                    System.out.print(res[i][j][k] + " ");
                }
            }
        }
        System.out.println();
    }

    private static void printMegaTensor(double[][][][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                for (int k = 0; k < a[0][0].length; k++) {
                    for (int l = 0; l < a[0][0][0].length; l++) {
                        System.out.print(a[i][j][k][l] + " ");
                    }
                }
            }
        }
        System.out.println();
    }

    private static void printTensor(double[][][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                for (int k = 0; k < a[0][0].length; k++) {
                    System.out.print(a[i][j][k] + " ");
                }
            }
        }
        System.out.println();
    }

    private static void dCalculateBlock(Node node, double[][][] dy) {
        switch (node.type) {
            case START -> {
                node.dRes = dy;
            }
            case RELU -> {
                drlu(node, dy);
            }
            case POOL -> {
                dPool(node, dy);
            }
            case BIAS -> {
                dBias(node, dy);
            }
            case CNVM -> {
                dCnv(node, dy);
            }
            case CNVE -> {
                dCnv(node, dy);
            }
            case CNVC -> {
                dCnv(node, dy);
            }

        }
    }

    private static Node calculateBlock(Node node) {
        String[] line = reader.nextLine().split(" ");
        switch (line[0]) {
            case "relu" -> {
                double a = 1.0 / Integer.parseInt(line[1]);
                return rlu(node, a);
            }
            case "pool" -> {
                int s = Integer.parseInt(line[1]);
                return pool(node, s);
            }
            case "bias" -> {
                List<Integer> b = new ArrayList<>();
                for (int i = 1; i < line.length; i++) {
                    b.add(Integer.parseInt(line[i]));
                }
                Node res = bias(node, b);
                res.inFather = node;
                return bias(node, b);
            }
            case "cnvm" -> {
                int h = Integer.parseInt(line[1]);
                int k = Integer.parseInt(line[2]);
                int s = Integer.parseInt(line[3]);
                int p = Integer.parseInt(line[4]);
                int d = node.res.length;
                double[][][][] filters = new double[h][d][k][k];
                int count = 5;
                for (int h1 = 0; h1 < h; h1++) {
                    for (int d1 = 0; d1 < d; d1++) {
                        for (int i = 0; i < k; i++) {
                            for (int j = 0; j < k; j++) {
                                filters[h1][d1][i][j] = Integer.parseInt(line[count++]);
                            }
                        }
                    }
                }
                double[][][] expanded = expandMatrix(node.res, p, "m");
                double[][][] resA = cnv(expanded, filters, p, s, h, d, k);
                Node res = new Node(resA, node);
                res.type = Type.CNVM;
                res.inFather = node;
                res.p = p;
                res.k = k;
                res.s = s;
                res.filters = filters;
                res.paddedIn = expanded;
                res.h = h;
                return res;

            }
            case "cnve" -> {
                int h = Integer.parseInt(line[1]);
                int k = Integer.parseInt(line[2]);
                int s = Integer.parseInt(line[3]);
                int p = Integer.parseInt(line[4]);
                int d = node.res.length;
                double[][][][] filters = new double[h][d][k][k];
                int count = 5;
                for (int h1 = 0; h1 < h; h1++) {
                    for (int d1 = 0; d1 < d; d1++) {
                        for (int i = 0; i < k; i++) {
                            for (int j = 0; j < k; j++) {
                                filters[h1][d1][i][j] = Integer.parseInt(line[count++]);
                            }
                        }
                    }
                }
                double[][][] expanded = expandMatrix(node.res, p, "e");
                double[][][] resA = cnv(expanded, filters, p, s, h, d, k);
                Node res = new Node(resA, node);
                res.type = Type.CNVE;
                res.inFather = node;
                res.p = p;
                res.k = k;
                res.s = s;
                res.filters = filters;
                res.paddedIn = expanded;
                res.h = h;
                return res;
            }
            case "cnvc" -> {
                int h = Integer.parseInt(line[1]);
                int k = Integer.parseInt(line[2]);
                int s = Integer.parseInt(line[3]);
                int p = Integer.parseInt(line[4]);
                int d = node.res.length;
                double[][][][] filters = new double[h][d][k][k];
                int count = 5;
                for (int h1 = 0; h1 < h; h1++) {
                    for (int d1 = 0; d1 < d; d1++) {
                        for (int i = 0; i < k; i++) {
                            for (int j = 0; j < k; j++) {
                                filters[h1][d1][i][j] = Integer.parseInt(line[count++]);
                            }
                        }
                    }
                }
                double[][][] expanded = expandMatrix(node.res, p, "c");
                double[][][] resA = cnv(expanded, filters, p, s, h, d, k);
                Node res = new Node(resA, node);
                res.type = Type.CNVC;
                res.inFather = node;
                res.p = p;
                res.k = k;
                res.s = s;
                res.filters = filters;
                res.paddedIn = expanded;
                res.h = h;
                return res;
            }
        }
        throw new IllegalArgumentException();
    }

    private static double[][][] cnv(double[][][] a, double[][][][] filters, int p, int s, int H, int D, int K) {
        int O = (a[0].length - K) / s + 1;
        double[][][] res = new double[H][O][O];
        int countX = 0;
        int countY = 0;
        for (int h = 0; h < H; h++) {
            countX = 0;
            for (int i = 0; i + K <= a[0].length; i += s) {
                countY = 0;
                for (int j = 0; j + K <= a[0][0].length; j += s) {
                    double sum = 0.0;
                    for (int k = i; k < i + K; k++) {
                        for (int l = j; l < j + K; l++) {
                            for (int d = 0; d < D; d++) {
                                sum += a[d][k][l] * filters[h][d][k - i][l - j];
                            }
                        }
                    }
                    res[h][countX][countY] = sum;
                    countY++;
                }
                countX++;
            }
        }
        return res;
    }

    private static Node bias(Node x, List<Integer> b) {
        Node node = new Node(x.res, true, Type.BIAS, x);
        node.biasB = b;
        for (int i = 0; i < x.d; i++) {
            for (int j = 0; j < x.n; j++) {
                for (int k = 0; k < x.m; k++) {
                    node.res[i][j][k] += b.get(i);
                }
            }
        }
        return node;
    }

    private static Node pool(Node x, int s) {
        Node res = new Node(x.d, x.n / s, x.m / s, Type.POOL, x);
        res.s = s;
        for (int i = 0; i < x.d; i++) {
            for (int j = 0; j < res.n; j++) {
                for (int k = 0; k < res.m; k++) {
                    double max = Integer.MIN_VALUE;
                    for (int l = j * s; l < (j + 1) * s; l++) {
                        for (int m = k * s; m < (k + 1) * s; m++) {
                            max = Math.max(max, x.res[i][l][m]);
                        }
                    }
                    res.res[i][j][k] = max;
                }
            }
        }
        res.inFather = x;
        return res;
    }

    private static void dPool(Node x, double[][][] dy) {
        Node father = x.inFather;
        double[][][] in = father.res;
        int s = x.s;
        for (int i = 0; i < father.d; i++) {
            for (int j = 0; j < father.n / s; j++) {
                for (int k = 0; k < father.m / s; k++) {
                    double max = Integer.MIN_VALUE * 1.0;
                    for (int l = j * s; l < (j + 1) * s; l++) {
                        for (int m = k * s; m < (k + 1) * s; m++) {
                            max = Math.max(max, in[i][l][m]);
                        }
                    }

                    for (int l = j * s; l < (j + 1) * s; l++) {
                        for (int m = k * s; m < (k + 1) * s; m++) {
                            if (Math.abs(in[i][l][m] - max) < 0.0000000001) {
                                x.dRes[i][l][m] = dy[i][j][k];
                            } else {
                                x.dRes[i][l][m] = 0;
                            }
                        }
                    }

                }
            }
        }
    }

    private static void dBias(Node x, double[][][] dy) {
        Node father = x.inFather;
        List<Integer> biasB = x.biasB;
        double[][][][] parameters = new double[1][1][1][biasB.size()];
        x.parameters = parameters;
        x.dRes = dy;
        for (int i = 0; i < dy.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < dy[0].length; j++) {
                for (int k = 0; k < dy[0][0].length; k++) {
                    sum += dy[i][j][k];
                }
            }
            parameters[0][0][0][i] = sum;
        }
    }

    private static void dCnv(Node x, double[][][] dy) {
        double[][][] res = x.dRes;
        double[][][] paddingData = unExpand(dCnvmWithPad(x, dy), x.p, x.type, x.inFather.d, x.inFather.n,
                x.inFather.m);
        for (int i = 0; i < x.inFather.d; i++) {
            for (int j = 0; j < x.inFather.n; j++) {
                for (int k = 0; k < x.inFather.m; k++) {
                    res[i][j][k] += paddingData[i][j + x.p][k + x.p];
                }
            }
        }
    }

    private static double[][][] dCnvmWithPad(Node x, double[][][] dy) {
        double[][][][] a = x.filters;
        double[][][] paddedIn = x.paddedIn;
        int outN = (x.inFather.n - x.k + 2 * x.p) / x.s + 1;
        int outM = (x.inFather.m - x.k + 2 * x.p) / x.s + 1;
        double[][][][] derivativeA = new double[x.h][x.inFather.res.length][x.k][x.k];
        x.parameters = derivativeA;

        double[][][] result = new double[x.inFather.d][x.inFather.n + 2 * x.p][x.inFather.m + 2 * x.p];
        for (int od = 0; od < x.h; od++) {
            for (int id = 0; id < x.inFather.d; id++) {
                for (int i = 0; i < outN; i++) {
                    for (int j = 0; j < outM; j++) {
                        for (int i2 = 0, i3 = i * x.s; i2 < x.k; i2++, i3++) {
                            for (int j2 = 0, j3 = j * x.s; j2 < x.k; j2++, j3++) {
                                result[id][i3][j3] += a[od][id][i2][j2] * dy[od][i][j];
                                derivativeA[od][id][i2][j2] += paddedIn[id][i3][j3]
                                        * dy[od][i][j];
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static double[][][] dCnvm1(Node x, double[][][] dy) {
        double[][][] a = x.inFather.res;
        double[][][][] filters = x.filters;
        double[][][][] derivativeA = new double[filters.length][filters[0].length][filters[0][0].length]
                [filters[0][0][0].length];
        x.parameters = derivativeA;
        int countX = 0;
        int countY = 0;
        double[][][] result = new double[x.d][x.n][x.m];
        for (int h = 0; h < a.length; h++) {
            countX = 0;
            for (int i = 0; i + x.k <= a[0].length; i += x.s) {
                countY = 0;
                for (int j = 0; j + x.k <= a[0][0].length; j += x.s) {
                    double sum = 0.0;
                    for (int k = i; k < i + x.k; k++) {
                        for (int l = j; l < j + x.k; l++) {
                            for (int d = 0; d < x.res.length; d++) {
                                //result[h][][]
                                sum += a[d][k][l] * filters[h][d][k - i][l - j];
                            }
                        }
                    }
                    //res[h][countX][countY] = sum;
                    countY++;
                }
                countX++;
            }
        }
        return null;
    }

    private static double[][][] unExpand(double[][][] a, int padding, Type type,
                                         int inputDepth, int inputHeight, int inputWidth) {
        switch (type) {
            case CNVM:
                for (int d = 0; d < inputDepth; d++) {
                    int n = inputHeight;
                    int m = inputWidth;

                    for (int j = 0; j < m + 2 * padding; j++) {
                        for (int i = 0; i < padding; i++) {
                            a[d][2 * padding - i][j] += a[d][i][j];
                        }

                        for (int i = n + padding; i < n + 2 * padding; i++) {
                            a[d][2 * (n + padding) - i - 2][j] += a[d][i][j];
                        }
                    }

                    for (int i = padding; i < padding + n; i++) {
                        for (int j = 0; j < padding; j++) {
                            a[d][i][padding - j + padding] += a[d][i][j];
                        }

                        for (int j = m + padding; j < m + 2 * padding; j++) {
                            a[d][i][2 * m + padding - j - 2 + padding] += a[d][i][j];
                        }
                    }
                }
                return a;
            case CNVE:
                for (int d = 0; d < inputDepth; d++) {
                    int n = inputHeight;
                    int m = inputWidth;

                    for (int j = 0; j < m + 2 * padding; j++) {
                        for (int i = 0; i < padding; i++) {
                            a[d][padding][j] += a[d][i][j];
                        }

                        for (int i = n + padding; i < n + 2 * padding; i++) {
                            a[d][n + padding - 1][j] += a[d][i][j];
                        }
                    }

                    for (int i = padding; i < padding + n; i++) {
                        for (int j = 0; j < padding; j++) {
                            a[d][i][padding] += a[d][i][j];
                        }

                        for (int j = m + padding; j < m + 2 * padding; j++) {
                            a[d][i][m + padding - 1] += a[d][i][j];
                        }
                    }
                }
                return a;
            case CNVC:
                for (int d = 0; d < inputDepth; d++) {
                    int n = inputHeight;
                    int m = inputWidth;

                    for (int j = 0; j < m + 2 * padding; j++) {
                        for (int i = padding - 1; i > -1; i--) {
                            a[d][i + n][j] += a[d][i][j];
                        }

                        for (int i = n + padding; i < n + 2 * padding; i++) {
                            a[d][i - n][j] += a[d][i][j];
                        }
                    }

                    for (int i = padding; i < padding + n; i++) {
                        for (int j = padding - 1; j > -1; j--) {
                            a[d][i][j + m] += a[d][i][j];
                        }

                        for (int j = m + padding; j < m + 2 * padding; j++) {
                            a[d][i][j - m] += a[d][i][j];
                        }
                    }
                }
                return a;
        }
        throw new IllegalStateException();
    }

    private static double[][][] expandMatrix(double[][][] a, int p, String type) {
        double[][][] res = new double[a.length][a[0].length + p * 2][a[0][0].length + p * 2];
        if (a[0].length == 1 && a[0][0].length == 1) {
            for (int d = 0; d < a.length; d++) {
                for (int i = 0; i < p * 2 + 1; i++) {
                    for (int j = 0; j < p * 2 + 1; j++) {
                        res[d][i][j] = a[d][0][0];
                    }
                }
            }
        }
        int n = a[0].length;
        int m = a[0][0].length;
        switch (type) {
            case "m" -> {
                for (int d = 0; d < res.length; d++) {
                    for (int i = 0; i < res[0].length; i++) {
                        for (int j = 0; j < res[0][0].length; j++) {
                            int i1;
                            int j1;
                            if (i < p) { // bot
                                i1 = p - i;
                            } else if (i >= n + p) { // up
                                i1 = n - (i - n - p + 1) - 1;
                            } else { // mid
                                i1 = i - p;
                            }

                            if (j < p) { // left
                                j1 = p - j;
                            } else if (j >= m + p) { // right
                                j1 = m - (j - m - p + 1) - 1;
                            } else { // mid
                                j1 = j - p;
                            }
                            res[d][i][j] = a[d][i1][j1];
                        }
                    }
                }
                return res;
            }
            case "e" -> {
                for (int d = 0; d < res.length; d++) {
                    for (int i = 0; i < res[0].length; i++) {
                        for (int j = 0; j < res[0][0].length; j++) {
                            int i1;
                            int j1;
                            if (i < p) { // bot
                                i1 = 0;
                            } else if (i >= n + p) { // up
                                i1 = n - 1;
                            } else { // mid
                                i1 = i - p;
                            }

                            if (j < p) { // left
                                j1 = 0;
                            } else if (j >= m + p) { // right
                                j1 = m - 1;
                            } else { // mid
                                j1 = j - p;
                            }
                            res[d][i][j] = a[d][i1][j1];
                        }
                    }
                }
                return res;
            }
            case "c" -> {
                for (int d = 0; d < res.length; d++) {
                    for (int i = 0; i < res[0].length; i++) {
                        for (int j = 0; j < res[0][0].length; j++) {
                            int i1;
                            int j1;
                            if (i < p) { // bot
                                i1 = n - (p - i);
                            } else if (i >= n + p) { // up
                                i1 = (i - n - p + 1) - 1;
                            } else { // mid
                                i1 = i - p;
                            }

                            if (j < p) { // left
                                j1 = m - (p - j);
                            } else if (j >= m + p) { // right
                                j1 = (j - m - p + 1) - 1;
                            } else { // mid
                                j1 = j - p;
                            }
                            res[d][i][j] = a[d][i1][j1];
                        }
                    }
                }
                return res;
            }
        }
        return null;
    }

    public static double relu(double x, double a) {
        if (x < 0) {
            return a * x;
        }
        return x;
    }

    public static double drelu(double x, double a) {
        if (x >= 0) return 1;
        return a;
    }

    private static Node rlu(Node x, double a) {
        Node res = new Node(x.d, x.n, x.m, Type.RELU, x);
        res.reluA = a;
        for (int i = 0; i < x.d; i++) {
            for (int j = 0; j < x.n; j++) {
                for (int k = 0; k < x.m; k++) {
                    res.res[i][j][k] = relu(x.res[i][j][k], a);
                }
            }
        }
        res.inFather = x;
        return res;
    }

    private static void drlu(Node x, double[][][] dy) {
        Node father = x.inFather;
        double a = x.reluA;
        for (int i = 0; i < father.d; i++) {
            for (int j = 0; j < father.n; j++) {
                for (int k = 0; k < father.m; k++) {
                    x.dRes[i][j][k] = drelu(father.res[i][j][k], a) * dy[i][j][k];
                }
            }
        }
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
