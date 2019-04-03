package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

import clsf.Dataset;
import experiments.MetaSystem;
import experiments.PrepareData;
import fitness_function.MahalanobisDistance;
import mfextraction.CMFExtractor;
import mfextraction.KNNLandMark;
import mfextraction.SVMLandMark;
import utils.ArrayUtils;
import utils.MatrixUtils;
import utils.StatUtils;

public class TestMetaSystem {

    public static void main(String[] args) throws IOException {

        double[][] metaData = new double[1024][];

        List<Dataset> datasets = PrepareData.readData("data.csv", new File("data"));
        final int numData = datasets.size();

        CMFExtractor extractor = new CMFExtractor();
        int numMF = extractor.length();

        for (int i = 0; i < numData; i++) {
            metaData[i] = extractor.apply(datasets.get(i));
        }

        double[][] cov = StatUtils.covarianceMatrix(numData, numMF, metaData);
        ArrayUtils.print(cov);
        double[][] invCov = MatrixUtils.inv(numMF, cov);
        ArrayUtils.print(invCov);

        double[] invSigma = new double[numMF];

        for (int i = 0; i < numMF; i++) {
            invSigma[i] = invCov[i][i];
        }

        MahalanobisDistance distance = new MahalanobisDistance(numMF, invCov);
        ToDoubleFunction<Dataset> fscore = new KNNLandMark();

        double meanRMSE = 0;

        for (int r = 0; r < 4; r++) {

            List<Dataset> train = new ArrayList<>();
            List<Dataset> test = new ArrayList<>();

            for (Dataset dataset : datasets) {
                if ((dataset.name.hashCode() & 3) == r) {
                    test.add(dataset);
                } else {
                    train.add(dataset);
                }
            }

            MetaSystem metaSystem = new MetaSystem(train, extractor, fscore);

            double sumSquareErrors = 0;

            for (Dataset dataset : test) {
                double real = fscore.applyAsDouble(dataset);
                // System.out.printf(Locale.ENGLISH, "%5s ", dataset.name);
                // System.out.printf(Locale.ENGLISH, "%.3f ", real);

                double mean = StatUtils.mean(metaSystem.classifyDataset(dataset));
                double diff = real - mean;
                sumSquareErrors += diff * diff;

                // System.out.printf(Locale.ENGLISH, " %.3f", mean);

            }
            meanRMSE += Math.sqrt(sumSquareErrors / test.size());

        }
        System.out.println(meanRMSE / 4);

    }

}
