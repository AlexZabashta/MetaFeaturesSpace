package clsf.gen_op;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import clsf.Dataset;
import utils.BooleanArray;

public class DatasetCrossover implements BiFunction<Dataset, Dataset, Pair<Dataset, Dataset>> {

    public final Random random;

    public DatasetCrossover(Random random) {
        this.random = random;
    }

    int[] classDistribution(Dataset dataset) {
        int n = dataset.numClasses();
        int m = dataset.numObjects();

        int[] distribution = new int[n];

        for (int i = 0; i < m; i++) {
            ++distribution[dataset.classValue(i)];
        }

        return distribution;
    }

    int[] order(int[] array) {
        int n = array.length;
        Integer[] order = new Integer[n];

        for (int i = 0; i < n; i++) {
            order[i] = i;
        }

        Arrays.sort(order, new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                return Integer.compare(array[j], array[i]);
            }
        });

        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = order[i];
        }
        return p;
    }

    int mid(int a, int b, Random random) {
        int sum = a + b;

        if (sum % 2 == 0 || random.nextBoolean()) {
            return (sum / 2) + 0;
        } else {
            return (sum / 2) + 1;
        }

    }

    int[][] indices(Dataset d) {
        int[] s = classDistribution(d);
        int n = s.length;

        int[][] indices = new int[n][];

        for (int i = 0; i < n; i++) {
            indices[i] = new int[s[i]];
            s[i] = 0;
        }
        int m = d.numObjects();

        for (int i = 0; i < m; i++) {
            int c = d.classValue(i);
            indices[c][s[c]++] = i;
        }

        return indices;

    }

    @Override
    public Pair<Dataset, Dataset> apply(Dataset d1, Dataset d2) {

        int k = mid(d1.numClasses(), d2.numClasses(), random);

        while (k != d1.numClasses() || k != d2.numClasses()) {
            d1 = ChangeNumClasses.apply(d1, random, k);
            d2 = ChangeNumClasses.apply(d2, random, k);
            k = Math.min(d1.numClasses(), d2.numClasses());
        }

        int[] s1 = classDistribution(d1);
        int[] s2 = classDistribution(d2);

        int[] p1 = order(s1);
        int[] p2 = order(s2);

        int ac1 = d1.numCatAttr();
        int ac2 = d2.numCatAttr();

        int ac = ac1 + ac2;

        int ar1 = d1.numRatAttr();
        int ar2 = d2.numRatAttr();

        int ar = ar1 + ar2;

        int[][][] cat = new int[k][][];
        double[][][] rat = new double[k][][];

        int[][] id1 = indices(d1);
        int[][] id2 = indices(d2);

        int numObjects = 0;

        for (int i = 0; i < k; i++) {
            int t1 = p1[i];
            int t2 = p2[i];

            int s = mid(s1[t1], s2[t2], random);

            numObjects += s;

            cat[i] = new int[s][ac];
            rat[i] = new double[s][ar];

            for (int j = 0; j < s; j++) {
                int i1 = id1[t1][random.nextInt(id1[t1].length)];
                int i2 = id2[t2][random.nextInt(id2[t2].length)];

                int pc = 0;

                for (int cid = 0; cid < ac1; cid++) {
                    cat[i][j][pc++] = d1.catValue(i1, cid);
                }

                for (int cid = 0; cid < ac2; cid++) {
                    cat[i][j][pc++] = d2.catValue(i2, cid);
                }

                int pr = 0;

                for (int rid = 0; rid < ar1; rid++) {
                    rat[i][j][pr++] = d1.ratValue(i1, rid);
                }

                for (int rid = 0; rid < ar2; rid++) {
                    rat[i][j][pr++] = d2.ratValue(i2, rid);
                }
            }
        }

        int cd1 = random.nextInt(ac + 1);

        int rd1 = random.nextInt(ar + 1);

        
        
        
        boolean[] cd = BooleanArray.random(ac, cd1, random);
        int cd2 = ac - cd1;
        boolean[] rd = BooleanArray.random(ar, rd1, random);
        int rd2 = ar - rd1;

        int[][] cv1 = new int[numObjects][cd1 + 1];
        double[][] rv1 = new double[numObjects][rd1];

        int[][] cv2 = new int[numObjects][cd2 + 1];
        double[][] rv2 = new double[numObjects][rd2];

        int p = 0;
        for (int i = 0; i < k; i++) {
            int s = cat[i].length;

            for (int j = 0; j < s; j++, p++) {
                cv1[p][cd1] = i;
                cv2[p][cd2] = i;

                int cp1 = 0, cp2 = 0;
                for (int cid = 0; cid < ac; cid++) {
                    if (cd[cid]) {
                        cv1[p][cp1++] = cat[i][j][cid];
                    } else {
                        cv2[p][cp2++] = cat[i][j][cid];
                    }
                }

                int rp1 = 0, rp2 = 0;
                for (int rid = 0; rid < ar; rid++) {
                    if (rd[rid]) {
                        rv1[p][rp1++] = rat[i][j][rid];
                    } else {
                        rv2[p][rp2++] = rat[i][j][rid];
                    }
                }

            }
        }

        Dataset c1 = new Dataset(numObjects, cd1, cv1, rd1, rv1);
        Dataset c2 = new Dataset(numObjects, cd2, cv2, rd2, rv2);

        return Pair.of(c1, c2);
    }

    static void print(Dataset dataset) {
        int n = dataset.numObjects();
        int c = dataset.numCatAttr();
        int r = dataset.numRatAttr();

        for (int i = 0; i < n; i++) {
            System.out.print("{");
            for (int j = 0; j < c; j++) {
                System.out.printf(" %3d", dataset.catValue(i, j));
            }
            System.out.print("} ");

            System.out.print("(");
            for (int j = 0; j < r; j++) {
                System.out.printf(" %3.0f", dataset.ratValue(i, j));
            }
            System.out.print(")");

            System.out.printf(" %3d%n", dataset.classValue(i));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Random random = new Random();
        DatasetCrossover dc = new DatasetCrossover(random);

        int x = 0;

        int n1 = 9;
        int nc1 = 2;
        int nr1 = 2;

        int[][] c1 = new int[n1][nc1 + 1];
        double[][] r1 = new double[n1][nr1];

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < nc1; j++) {
                c1[i][j] = x++;
            }
            for (int j = 0; j < nr1; j++) {
                r1[i][j] = x++;
            }
            c1[i][nc1] = random.nextInt(7);
        }

        Dataset d1 = new Dataset(n1, nc1, c1, nr1, r1);
        print(d1);

        int n2 = 9;
        int nc2 = 2;
        int nr2 = 2;

        int[][] c2 = new int[n2][nc2 + 1];
        double[][] r2 = new double[n2][nr2];

        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < nc2; j++) {
                c2[i][j] = x++;
            }
            for (int j = 0; j < nr2; j++) {
                r2[i][j] = x++;
            }
            c2[i][nc2] = random.nextInt(7);
        }

        Dataset d2 = new Dataset(n2, nc2, c2, nr2, r2);
        print(d2);

        Pair<Dataset, Dataset> p = dc.apply(d1, d2);
        print(p.getLeft());
        print(p.getRight());

    }

}
