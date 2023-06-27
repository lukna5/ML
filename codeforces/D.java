import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class D {
    private static class Node {
        String type;
        boolean isProcessed = false;
        double[][] res;
        double[][] dRes;
        int n;
        int m;

        public Node(int n, int m) {
            this.n = n;
            this.m = m;
            res = new double[n][m];
            dRes = new double[n][m];
        }

        public Node(double[][] res) {
            this.n = res.length;
            this.m = res[0].length;
            this.res = res;
            dRes = new double[n][m];
        }

        public Node(int n, int m, String type) {
            this.n = n;
            this.m = m;
            res = new double[n][m];
            dRes = new double[n][m];
            this.type = type;
        }

    }

    private static Node Wf;
    private static Node Wi;
    private static Node Wo;
    private static Node Wc;
    private static Node Uf;
    private static Node Ui;
    private static Node Uo;
    private static Node Uc;
    private static Node Bf;
    private static Node Bi;
    private static Node Bo;
    private static Node Bc;

    private static int N;
    private static int M;
    private static MyReader reader;

    private static Node[] h;
    private static Node[] c;
    private static Node[] x;
    private static Node[] o;
    private static Node[] f;
    private static Node[] iN;
    private static Node[] tanhs;
    private static Node[] sumi;
    private static Node[] sumc;
    private static Node[] sumo;
    private static Node[] sumtanh;
    private static Node[] sumf;
    private static Node[] hadc1;
    private static Node[] hadc2;



    private static Node readMatrixNode(String type){
        Node node = new Node(N, N, type);
        for (int i = 0; i < N; i++) {
            String[] line = reader.nextLine().split(" ");
            for (int j = 0; j < N; j++) {
                node.res[i][j] = Integer.parseInt(line[j]);
            }
        }
        return node;
    }

    private static double[][] readMatrix(){
        double[][] res = new double[N][N];
        for (int i = 0; i < N; i++) {
            String[] line = reader.nextLine().split(" ");
            for (int j = 0; j < N; j++) {
                res[i][j] = Integer.parseInt(line[j]);
            }
        }
        return res;
    }

    private static Node readVectorNode(String type){
        Node node = new Node(N, 1, type);
        String[] line = reader.nextLine().split(" ");
        for (int i = 0; i < N; i++) {
            node.res[i][0] = Integer.parseInt(line[i]);
        }
        return node;
    }

    private static double[][] readVector(){
        double[][] res = new double[N][1];
        String[] line = reader.nextLine().split(" ");
        for (int i = 0; i < N; i++) {
            res[i][0] = Integer.parseInt(line[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        reader = new MyReader();
        N = reader.nextInt();
        Wf = readMatrixNode("matrix");
        Uf = readMatrixNode("matrix");
        Bf = readVectorNode("vector");
        Wi = readMatrixNode("matrix");
        Ui = readMatrixNode("matrix");
        Bi = readVectorNode("vector");
        Wo = readMatrixNode("matrix");
        Uo = readMatrixNode("matrix");
        Bo = readVectorNode("vector");
        Wc = readMatrixNode("matrix");
        Uc = readMatrixNode("matrix");
        Bc = readVectorNode("vector");
        M = Integer.parseInt(reader.nextLine());
        h = new Node[M + 1];
        c = new Node[M + 1];
        x = new Node[M + 1];
        o = new Node[M + 1];
        f = new Node[M + 1];
        iN = new Node[M + 1];
        tanhs = new Node[M + 1];
        sumc = new Node[M + 1];
        sumo = new Node[M + 1];
        sumi = new Node[M + 1];
        sumf = new Node[M + 1];
        sumtanh = new Node[M + 1];
        hadc1 = new Node[M + 1];
        hadc2 = new Node[M + 1];

        h[0] = readVectorNode("vector");
        c[0] = readVectorNode("vector");
        for (int i = 1; i <= M; i++) {
            x[i] = readVectorNode("matrix");
        }
        for (int i = 1; i < M; i++) {
            h[i] = new Node(N, 1, "vector");
            c[i] = new Node(N, 1, "vector");
        }
        h[M] = new Node(N, 1, "vector");
        h[M].dRes = readVector();
        c[M] = new Node(N, 1, "vector");
        c[M].dRes = readVector();
        for (int i = M; i >= 1; i--) {
            o[i] = new Node(N, 1, "vector");
            o[i].dRes = readVector();
        }

        for (int i = 1; i <= M; i++) {
            calculateBlock(i);
        }
        for (int i = 1; i <= M; i++) {
            printMatrix(o[i].res);
        }
        printMatrix(h[M].res);
        printMatrix(c[M].res);

        for (int i = M; i >= 1; i--) {
            dCalculateBlock(i);
            printMatrix(x[i].dRes);
        }
        printMatrix(h[0].dRes);
        printMatrix(c[0].dRes);

        printMatrix(Wf.dRes);
        printMatrix(Uf.dRes);
        printMatrix(Bf.dRes);
        printMatrix(Wi.dRes);
        printMatrix(Ui.dRes);
        printMatrix(Bi.dRes);
        printMatrix(Wo.dRes);
        printMatrix(Uo.dRes);
        printMatrix(Bo.dRes);
        printMatrix(Wc.dRes);
        printMatrix(Uc.dRes);
        printMatrix(Bc.dRes);
    }

    private static void dCalculateBlock(int ind){
        Node dot = o[ind];
        Node dh = h[ind];
        Node dc = c[ind];
        Node dh_1 = h[ind - 1];
        Node dc_1 = c[ind - 1];
        Node dx = x[ind];
        Node df = f[ind];
        Node di = iN[ind];
        Node dtanh = tanhs[ind];

        dot.dRes = sumMatrix(dot.dRes, hadMatrix(dh.dRes, dc.res));
        dc.dRes = sumMatrix(dc.dRes, hadMatrix(dh.dRes, dot.res));

        double[][] dSigm = hadMatrix(dSigmoidMatrix(sumo[ind].res), dot.dRes);
        Bo.dRes = sumMatrix(Bo.dRes, dSigm);
        Wo.dRes = sumMatrix(Wo.dRes, multiplyMatrix(dSigm, transposeMatrix(dx.res)));
        dx.dRes = sumMatrix(dx.dRes, multiplyMatrix(transposeMatrix(Wo.res), dSigm));
        Uo.dRes = sumMatrix(Uo.dRes, multiplyMatrix(dSigm, transposeMatrix(dh_1.res)));
        dh_1.dRes = sumMatrix(dh_1.dRes, multiplyMatrix(transposeMatrix(Uo.res), dSigm));

        double[][] had1 = dc.dRes;
        dtanh.dRes = sumMatrix(dtanh.dRes, hadMatrix(di.res, had1));
        double[][] sum = hadMatrix(dTnhMatrix(sumtanh[ind].res), dtanh.dRes);
        Bc.dRes = sumMatrix(Bc.dRes, sum);
        Uc.dRes = sumMatrix(Uc.dRes, multiplyMatrix(sum, transposeMatrix(dh_1.res)));
        dh_1.dRes = sumMatrix(dh_1.dRes, multiplyMatrix(transposeMatrix(Uc.res), sum));
        Wc.dRes = sumMatrix(Wc.dRes, multiplyMatrix(sum, transposeMatrix(dx.res)));
        dx.dRes = sumMatrix(dx.dRes, multiplyMatrix(transposeMatrix(Wc.res), sum));

        di.dRes = hadMatrix(dtanh.res, had1);
        dSigm = hadMatrix(dSigmoidMatrix(sumi[ind].res), di.dRes);
        Bi.dRes = sumMatrix(Bi.dRes, dSigm);
        Ui.dRes = sumMatrix(Ui.dRes, multiplyMatrix(dSigm, transposeMatrix(dh_1.res)));
        dh_1.dRes = sumMatrix(dh_1.dRes, multiplyMatrix(transposeMatrix(Ui.res), dSigm));
        Wi.dRes = sumMatrix(Wi.dRes, multiplyMatrix(dSigm, transposeMatrix(dx.res)));
        dx.dRes = sumMatrix(dx.dRes, multiplyMatrix(transposeMatrix(Wi.res), dSigm));

        double[][] had2 = dc.dRes;
        dc_1.dRes = sumMatrix(dc_1.dRes, hadMatrix(df.res, had2));
        df.dRes = sumMatrix(df.dRes, hadMatrix(dc_1.res, had2));
        dSigm = hadMatrix(dSigmoidMatrix(sumf[ind].res), df.dRes);
        Bf.dRes = sumMatrix(Bf.dRes, dSigm);
        Uf.dRes = sumMatrix(Uf.dRes, multiplyMatrix(dSigm, transposeMatrix(dh_1.res)));
        dh_1.dRes = sumMatrix(dh_1.dRes, multiplyMatrix(transposeMatrix(Uf.res), dSigm));
        Wf.dRes = sumMatrix(Wf.dRes, multiplyMatrix(dSigm, transposeMatrix(dx.res)));
        dx.dRes = sumMatrix(dx.dRes, multiplyMatrix(transposeMatrix(Wf.res), dSigm));
    }
    private static double sigmoid(double num){
        return 1.0 / (1 + Math.exp(-1 * num));
    }

    private static double dSigmoid(double num){
        return sigmoid(num) * (1 - sigmoid(num));
    }

    private static double[][] sigmoidMatrix(double[][] a){
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = sigmoid(a[i][j]);
            }
        }
        return res;
    }

    private static double[][] dSigmoidMatrix(double[][] a){
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = dSigmoid(a[i][j]);
            }
        }
        return res;
    }

    private static double[][] tnhMatrix(double[][] a) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = Math.tanh(a[i][j]);
            }
        }
        return res;
    }

    public static double dtanhFun(double x) {
        double coshx = Math.cosh(x);
        return 1.0 / (double) (coshx * coshx);
    }

    private static double[][] dTnhMatrix(double[][] a) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = dtanhFun(a[i][j]);
            }
        }
        return res;
    }


    private static void calculateBlock(int ind){
        Node h1 = h[ind - 1];
        Node x1 = x[ind];
        double[][] mult1 = multiplyMatrix(Wf.res, x1.res);
        double[][] mult2 = multiplyMatrix(Uf.res, h1.res);
        double[][] sum = sumMatrix(sumMatrix(mult1, mult2), Bf.res);
        sumf[ind] = new Node(sum);
        Node Ft = new Node(sigmoidMatrix(sum));
        f[ind] = Ft;

        mult1 = multiplyMatrix(Wo.res, x1.res);
        mult2 = multiplyMatrix(Uo.res, h1.res);
        sum = sumMatrix(sumMatrix(mult1, mult2), Bo.res);
        sumo[ind] = new Node(sum);
        o[ind].res = sigmoidMatrix(sum);
        Node Ot = o[ind];
        o[ind] = Ot;

        mult1 = multiplyMatrix(Wi.res, x1.res);
        mult2 = multiplyMatrix(Ui.res, h1.res);
        sum = sumMatrix(sumMatrix(mult1, mult2), Bi.res);
        sumi[ind] = new Node(sum);
        Node It = new Node(sigmoidMatrix(sum));
        iN[ind] = It;

        mult1 = multiplyMatrix(Wc.res, x1.res);
        mult2 = multiplyMatrix(Uc.res, h1.res);
        sum = sumMatrix(sumMatrix(mult1, mult2), Bc.res);
        sumtanh[ind] = new Node(sum);
        double[][] tanh = tnhMatrix(sum);
        tanhs[ind] = new Node(tanh);

        double[][] had1 = hadMatrix(tanh, It.res);
        hadc1[ind] = new Node(had1);
        double[][] had2 = hadMatrix(Ft.res, c[ind - 1].res);
        hadc2[ind] = new Node(had2);

        c[ind].res = sumMatrix(had1, had2);
        Node Ct = c[ind];

        h[ind].res = hadMatrix(Ot.res, Ct.res);
    }


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

    private static double[][] hadMatrix(double[][] a, double[][] b){
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = a[i][j] * b[i][j];
            }
        }
        return res;
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
