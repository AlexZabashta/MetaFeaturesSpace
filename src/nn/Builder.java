package nn;

import java.util.ArrayList;
import java.util.List;

import nn.act.Tanh;
import nn.fld.Max;
import nn.fld.One;
import nn.fld.Sum;
import weka.core.logging.OutputLogger.OutputPrintStream;

public class Builder {
    public static NeuralNetwork full(int... layerSizes) {
        int len = layerSizes.length;

        int inputSize = layerSizes[0];
        int outputSize = layerSizes[len - 1];
        List<Neuron> neurons = new ArrayList<>();

        int numWeights = 0;

        int constId = inputSize;
        neurons.add(new One(constId));

        int lid = 0;
        int nid = constId + 1;

        for (int i = 1; i < len; i++) {
            int currSize = layerSizes[i - 0];
            int prevSize = layerSizes[i - 1];

            int dendrites = prevSize + 1;

            int[] from = new int[dendrites];
            for (int d = 0; d < prevSize; d++) {
                from[d] = lid + d;
            }
            from[prevSize] = constId;

            for (int j = 0; j < currSize; j++) {
                int[] wid = new int[dendrites];
                for (int d = 0; d < dendrites; d++) {
                    wid[d] = numWeights++;
                }
                neurons.add(new Sum(dendrites, from, wid, nid++, new Tanh()));
            }

            lid += prevSize;

            if (lid == constId) {
                ++lid;
            }
        }

        return new NeuralNetwork(inputSize, outputSize, numWeights, neurons.toArray(new Neuron[0]));

    }

    public static NeuralNetwork fullLayer(int inpSize, int outSize, Activation activation) {
        Neuron[] neurons = new Neuron[outSize];

        int numWeights = 0;
        int dendrites = inpSize;

        int[] from = new int[dendrites];
        for (int d = 0; d < dendrites; d++) {
            from[d] = d;
        }

        for (int i = 0; i < outSize; i++) {
            int[] wid = new int[dendrites];
            for (int d = 0; d < dendrites; d++) {
                wid[d] = numWeights++;
            }
            neurons[i] = new Sum(dendrites, from, wid, inpSize + i, activation);
        }
        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static NeuralNetwork cnnSharedLayer(int inpW, int inpH, int inpD, int winW, int winH, int outD, Activation activation) {

        int outW = inpW - winW + 1;
        int outH = inpH - winH + 1;

        int inpSize = inpW * inpH * inpD;
        int outSize = outW * outH * outD;

        int numWeights = 0;
        Neuron[] neurons = new Neuron[outSize];

        int dendrites = winW * winH * inpD;

        int[][] wid = new int[outD][dendrites];

        for (int z = 0; z < outD; z++) {
            for (int d = 0; d < dendrites; d++) {
                wid[z][d] = numWeights++;
            }
        }

        for (int x = 0, i = 0; x < outW; x++) {
            for (int y = 0; y < outH; y++) {

                int[] from = new int[dendrites];

                for (int dx = 0, j = 0; dx < winW; dx++) {
                    int fx = x + dx;
                    for (int dy = 0; dy < winH; dy++) {
                        int fy = y + dy;
                        for (int fz = 0; fz < inpD; fz++, j++) {
                            from[j] = ((fx) * inpH + fy) * inpD + fz;
                        }
                    }
                }

                for (int z = 0; z < outD; z++, i++) {
                    neurons[i] = new Sum(dendrites, from, wid[z], inpSize + i, activation);
                }
            }
        }

        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static NeuralNetwork maxPoolLayer(int inpW, int inpH, int inpD, int scaleW, int scaleH, int outD, Activation activation) {

        int outW = inpW / scaleW;
        int outH = inpH / scaleH;

        int inpSize = inpW * inpH * inpD;
        int outSize = outW * outH * outD;

        int numWeights = 0;
        Neuron[] neurons = new Neuron[outSize];

        int dendrites = scaleW * scaleH * inpD;

        int[][] wid = new int[outD][dendrites];

        for (int z = 0; z < outD; z++) {
            for (int d = 0; d < dendrites; d++) {
                wid[z][d] = numWeights++;
            }
        }

        for (int x = 0, i = 0; x < outW; x++) {
            for (int y = 0; y < outH; y++) {

                int[] from = new int[dendrites];

                for (int dx = 0, j = 0; dx < scaleW; dx++) {
                    int fx = x * scaleW + dx;
                    for (int dy = 0; dy < scaleH; dy++) {
                        int fy = y * scaleH + dy;
                        for (int fz = 0; fz < inpD; fz++, j++) {
                            from[j] = ((fx) * inpH + fy) * inpD + fz;
                        }
                    }
                }

                for (int z = 0; z < outD; z++, i++) {
                    neurons[i] = new Max(dendrites, from, wid[z], inpSize + i, activation);
                }
            }
        }

        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static void main(String[] args) {
        {
            NeuralNetwork network = cnnSharedLayer(256, 64, 1, 17, 17, 2, new Tanh());
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = maxPoolLayer(240, 48, 2, 4, 2, 3, new Tanh());
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = cnnSharedLayer(60, 24, 3, 5, 5, 4, new Tanh());
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = maxPoolLayer(56, 20, 4, 4, 2, 5, new Tanh());
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = cnnSharedLayer(14, 10, 5, 7, 3, 6, new Tanh());
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = maxPoolLayer(8, 8, 6, 4, 2, 5, new Tanh());
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

    }
}
