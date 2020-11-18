package pagerank;

import Jama.Matrix;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        double d = 0.5;
        int size = Integer.parseInt(sc.nextLine());
        String sites[] = sc.nextLine().split(" ");
        double[][] l = new double[size][size];
        for (int i = 0; i < size; i++) {
            String line[] = sc.nextLine().split(" ");
            for (int j = 0; j < size; j++) {
                l[i][j] = Double.parseDouble(line[j]);
            }
        }
        String query = sc.nextLine();
        Matrix m = new Matrix(l);
        Matrix r = pageRank(m, d);
        double[] res = r.getColumnPackedCopy();
        Map<String, Double> ratings = new HashMap<>();
        for (int i = 0; i < sites.length; i++) {
            ratings.putIfAbsent(sites[i], res[i]);
        }

        Map<String, Double> top =  ratings.entrySet().stream()
                .sorted(Map.Entry.<String,Double>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int limit = 5;
        for (Map.Entry<String, Double> entry : top.entrySet()) {
            if (entry.getKey().contains(query)) {
                System.out.println(entry.getKey());
                limit--;
            }
            if(limit == 0) break;
        }
        if (limit > 0) {
            for (Map.Entry<String, Double> entry : top.entrySet()) {
                if (!entry.getKey().contains(query)) {
                    System.out.println(entry.getKey());
                    limit--;
                }
                if (limit == 0) break;
            }
        }
    }

    public static Matrix pageRank(Matrix transitionMatrix, double d) {
        int n = transitionMatrix.getRowDimension(); //We will determine dimension for better clarity
        Matrix j = new Matrix(n, n, 1.).times((1-d) / n);
        Matrix m = transitionMatrix.times(d).plus(j);
        double[] r = new double[n];
        for (int i = 0; i < r.length; i++) {
            r[i] = 1.00;
            r[i] = 100 * r[i] / n;
        }
        Matrix r0 = new Matrix(r, 1).transpose();
        Matrix prevR = r0;
        while (true) {
            Matrix nextR = m.times(prevR);
            if (prevR.minus(nextR).normInf() < 0.01) {
                r0 = nextR;
                break;
            }
            prevR = nextR;
        }
        return r0;
    }
}