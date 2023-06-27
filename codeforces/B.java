import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class B {
    private static class Node {
        double a = 0.0;
        String type;
        List<Node> sonsMultLeft = new ArrayList<>();

        List<Node> sonsMultRight = new ArrayList<>();

        Node multFather1;
        Node multFather2;
        boolean isVar = false;
        boolean isProcessed = false;
        double[][] res;
        double[][] dRes;
        int n;
        int m;
        List<Node> sons = new ArrayList<>();
        List<Node> fathers = new ArrayList<>();

        public Node(int n, int m, String type) {
            this.n = n;
            this.m = m;
            res = new double[n][m];
            dRes = new double[n][m];
            this.type = type;
        }

        public Node(int n, int m, boolean isOne, String type) {
            this.n = n;
            this.m = m;
            res = new double[n][m];
            for (int i = 0; i < n; i++) {
                Arrays.fill(res[i], 1);
            }
            dRes = new double[n][m];
            this.type = type;
        }

        public Node(int n, int m, Node father, String type) {
            this.n = n;
            this.m = m;
            res = new double[n][m];
            dRes = new double[n][m];
            father.addSon(this);
            this.type = type;
        }

        public Node(double[][] matrix, Node father1, Node father2, String type) {
            this.n = matrix.length;
            this.m = matrix[0].length;
            res = matrix;
            dRes = new double[n][m];
            father1.addSon(this);
            father2.addSon(this);
            this.type = type;
        }

        public Node(double[][] matrix, List<Node> fathers, String type) {
            this.n = matrix.length;
            this.m = matrix[0].length;
            res = matrix;
            dRes = new double[n][m];
            addFathers(fathers);
            this.type = type;
        }


        public void addSon(Node son) {
            sons.add(son);
            son.fathers.add(this);
        }

        public void addFathers(List<Node> fathers) {
            for (Node father : fathers) {
                father.addSon(this);
            }
        }

    }

    public static void main(String[] args) {
        MyReader reader = new MyReader();
        int n = reader.nextInt();
        int m = reader.nextInt();
        int k = reader.nextInt();
        List<Node> nodes = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            String[] line = reader.nextLine().split(" ");
            int r = Integer.parseInt(line[1]);
            int c = Integer.parseInt(line[2]);
            Node node = new Node(r, c, "var");
            nodes.add(node);
            node.isVar = true;
        }
        for (int i = 0; i < n - m; i++) {
            commands.add(reader.nextLine());
        }
        for (int i = 0; i < m; i++) {
            Node node = nodes.get(i);
            for (int j = 0; j < node.n; j++) {
                String[] line = reader.nextLine().split(" ");
                for (int l = 0; l < node.m; l++) {
                    node.res[j][l] = Integer.parseInt(line[l]);
                }
            }
        }
        for (int i = m; i < n; i++) {
            String[] line = commands.get(i - m).split(" ");
            String type = line[0];
            switch (type) {
                case "tnh" -> {
                    int x = Integer.parseInt(line[1]) - 1;
                    nodes.add(tnh(nodes.get(x)));
                }
                case "rlu" -> {
                    double a = 1.0 / Double.parseDouble(line[1]);
                    int x = Integer.parseInt(line[2]) - 1;
                    nodes.add(rlu(nodes.get(x), a));

                }
                case "mul" -> {
                    int x = Integer.parseInt(line[1]) - 1;
                    int y = Integer.parseInt(line[2]) - 1;
                    nodes.add(multiply(nodes.get(x), nodes.get(y)));
                }
                case "sum" -> {
                    int len = Integer.parseInt(line[1]);
                    List<Node> sums = new ArrayList<>();
                    for (int j = 0; j < len; j++) {
                        sums.add(nodes.get(Integer.parseInt(line[j + 2]) - 1));
                    }
                    nodes.add(sum(sums));
                }
                case "had" -> {
                    int len = Integer.parseInt(line[1]);
                    List<Node> adamars = new ArrayList<>();
                    for (int j = 0; j < len; j++) {
                        adamars.add(nodes.get(Integer.parseInt(line[j + 2]) - 1));
                    }
                    nodes.add(adamar(adamars));
                }
            }
        }

        for (int i = n - k; i < n; i++) {
            Node node = nodes.get(i);
            printMatrix(node.res);
            for (int j = 0; j < node.dRes.length; j++) {
                String[] line = reader.nextLine().split(" ");
                for (int l = 0; l < node.dRes[0].length; l++) {
                    node.dRes[j][l] = Integer.parseInt(line[l]);
                }
            }
            node.isProcessed = true;
        }

        if (n > n - k) {
            nodes.subList(n - k, n).clear();
        }
        List<Node> res = new ArrayList<>();
        while (nodes.size() > m) {
            for (int i = m; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                boolean stop = false;
                for (Node son : node.sons) {
                    if (!son.isProcessed) {
                        stop = true;
                        break;
                    }
                }
                if (stop) {
                    continue;
                }
                dNode(node);
                node.isProcessed = true;
                nodes.remove(i);
                break;
            }
        }

        for (Node node : nodes) {
            dNode(node);
            printMatrix(node.dRes);
        }


    }

    public static double dtanhFun(double x) {
        double coshx = Math.cosh(x);
        return 1.0 / (double) (coshx * coshx);
    }

    public static double drelu(double x, double a) {
        if (x >= 0) return 1;
        return a;
    }

    private static void dNode(Node x) {
        for (Node son : x.sons) {
            switch (son.type) {
                case "tnh" -> {
                    double[][] dRes = new double[x.res.length][x.res[0].length];
                    for (int i = 0; i < x.res.length; i++) {
                        for (int j = 0; j < x.res[0].length; j++) {
                            dRes[i][j] = dtanhFun(x.res[i][j]);
                        }
                    }
                    x.dRes = sumMatrix(x.dRes, scalarMult(dRes, son.dRes));
                }
                case "relu" -> {
                    double[][] dRes = new double[x.res.length][x.res[0].length];
                    for (int i = 0; i < x.res.length; i++) {
                        for (int j = 0; j < x.res[0].length; j++) {
                            dRes[i][j] = drelu(x.res[i][j], son.a);
                        }
                    }
                    x.dRes = sumMatrix(x.dRes, scalarMult(dRes, son.dRes));
                }
                case "sum" -> {
                    for (int i = 0; i < x.res.length; i++) {
                        for (int j = 0; j < x.res[0].length; j++) {
                            x.dRes[i][j] += son.dRes[i][j];
                        }
                    }
                }
                case "mult" -> {
                    if (son.fathers.get(0) == son.fathers.get(1)){
                        Node father2 = son.fathers.get(1);
                        double[][] dRes = new double[x.dRes.length][x.dRes[0].length];
                        dRes = multiplyMatrix(son.dRes, transposeMatrix(father2.res));
                        Node father1 = son.fathers.get(0);
                        dRes = sumMatrix(dRes, multiplyMatrix(transposeMatrix(father1.res), son.dRes));
                        for (int i = 0; i < x.dRes.length; i++) {
                            for (int j = 0; j < x.dRes[0].length; j++) {
                                dRes[i][j] /= 2;
                            }
                        }
                        x.dRes = sumMatrix(x.dRes, dRes);
                        continue;
                    }
                    if (x.sonsMultLeft.contains(son)) {
                        Node father2 = son.fathers.get(1);
                        x.dRes = sumMatrix(x.dRes, multiplyMatrix(son.dRes, transposeMatrix(father2.res)));
                    } else {
                        Node father1 = son.fathers.get(0);
                        x.dRes = sumMatrix(x.dRes, multiplyMatrix(transposeMatrix(father1.res), son.dRes));
                    }
                }
                case "adamar" -> {
                    double[][] sum = new double[x.dRes.length][x.dRes[0].length];
                    for (int i = 0; i < sum.length; i++) {
                        for (int j = 0; j < sum[0].length; j++) {
                            sum[i][j] = 1;
                        }
                    }
                    boolean was = false;
                    for (Node node : son.fathers) {
                        if (node == x && !was) {
                            was = true;
                            continue;
                        }
                        sum = scalarMult(sum, node.res);
                    }
                    sum = scalarMult(sum, son.dRes);
                    x.dRes = sumMatrix(x.dRes, sum);
                }
            }
        }
    } // adamar var1 var1

    private static void printMatrix(double[][] a) {
        for (double[] doubles : a) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(doubles[j] + " ");
            }
            System.out.println();
        }
    }

    private static double[][] transposeMatrix(double[][] a) {
        double[][] res = new double[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                res[j][i] = a[i][j];
            }
        }
        return res;
    }

    public static double[][] copyMatrix(double[][] original) {
        int numRows = original.length;
        int numCols = original[0].length;
        double[][] copy = new double[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, numCols);
        }
        return copy;
    }

    private static Node adamar(List<Node> adamars) {
        Node node = new Node(adamars.get(0).res.length, adamars.get(0).res[0].length, true, "adamar");
        node.addFathers(adamars);
        for (Node a : adamars) {
            for (int j = 0; j < node.n; j++) {
                for (int k = 0; k < node.m; k++) {
                    node.res[j][k] *= a.res[j][k];
                }
            }
        }
        return node;
    }

    private static double[][] scalarMult(double[][] a, double[][] b) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = a[i][j] * b[i][j];
            }
        }
        return res;
    }

    private static Node sum(List<Node> sums) {
        double[][] m = new double[sums.get(0).res.length][sums.get(0).res[0].length];
        for (Node sumNode : sums) {
            for (int i = 0; i < m.length; i++) {
                for (int j = 0; j < m[0].length; j++) {
                    m[i][j] += sumNode.res[i][j];
                }
            }
        }
        return new Node(m, sums, "sum");
    }

    private static double[][] sumMatrix(double[][] a, double[][] b) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = a[i][j] + b[i][j];
            }
        }
        return res;
    }

    private static Node multiply(Node a, Node b) {
        double[][] m = multiplyMatrix(a.res, b.res);
        Node node = new Node(m, a, b, "mult");
        node.multFather1 = a;
        node.multFather2 = b;
        a.sonsMultLeft.add(node);
        b.sonsMultRight.add(node);
        return node;
    }

    private static double[][] multiplyMatrix(double[][] a, double[][] b) {
        double[][] res = new double[a.length][b[0].length];
        int n = a.length;
        int m = a[0].length;
        int k = b[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int z = 0; z < k; z++) {
                    res[i][z] += a[i][j] * b[j][z];
                }
            }
        }
        return res;
    }

    private static double relu(double x, double a) {
        if (x < 0) {
            return a * x;
        }
        return x;
    }

    private static Node rlu(Node x, double a) {
        Node node = new Node(x.n, x.m, x, "relu");
        node.a = a;
        for (int i = 0; i < x.n; i++) {
            for (int j = 0; j < x.m; j++) {
                node.res[i][j] = relu(x.res[i][j], a);
            }
        }
        return node;
    }

    private static Node tnh(Node x) {
        Node node = new Node(x.n, x.m, x, "tnh");
        for (int i = 0; i < x.n; i++) {
            for (int j = 0; j < x.m; j++) {
                node.res[i][j] = Math.tanh(x.res[i][j]);
            }
        }
        return node;
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

