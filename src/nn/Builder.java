package nn;

import java.util.Arrays;

import nn.act.Tanh;
import nn.fld.Fold;
import nn.fld.Sum;

public class Builder {

    public static NeuralNetwork fullLayer(int inpSize, int outSize, Fold fold) {
        Neuron[] neurons = new Neuron[outSize];

        int numWeights = 0;
        int length = inpSize;

        int[] sid = new int[length];
        for (int d = 0; d < length; d++) {
            sid[d] = d;
        }

        for (int i = 0; i < outSize; i++) {
            int[] wid = new int[length + 1];
            for (int d = 0; d <= length; d++) {
                wid[d] = numWeights++;
            }
            neurons[i] = new Neuron(length, sid, wid, inpSize + i, fold);
        }
        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static NeuralNetwork connect(NeuralNetwork... layers) {
        int depth = layers.length;

        if (depth <= 0) {
            throw new IllegalArgumentException("empty");
        }

        if (depth == 1) {
            return layers[0];
        }

        for (int i = 1; i < depth; i++) {
            if (layers[i - 1].outSize != layers[i].inpSize) {
                throw new IllegalArgumentException((i - 1) + " and " + i + " layers has differnt output input sizes " + layers[i - 1].outSize + " " + layers[i].inpSize);
            }
        }

        int inpSize = layers[0].inpSize;
        int outSize = layers[depth - 1].outSize;

        int numWeights = 0;
        int numNeurons = 0;

        for (int i = 0; i < depth; i++) {
            numNeurons += layers[i].neurons.length;
            numWeights += layers[i].numWeights;
        }

        Neuron[] neurons = Arrays.copyOf(layers[0].neurons, numNeurons);

        int indexOffset = 0, weightOffset = 0;
        int p = 0;
        for (int i = 1; i < depth; i++) {
            indexOffset += layers[i - 1].neurons.length;
            weightOffset += layers[i - 1].numWeights;
            for (Neuron neuron : layers[i].neurons) {
                neurons[p++] = neuron.copyWithOffset(indexOffset, weightOffset);
            }
        }
        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static NeuralNetwork cnnSharedLayer(int inpW, int inpH, int inpD, int winW, int winH, int outD, Fold fold) {

        int outW = inpW - winW + 1;
        int outH = inpH - winH + 1;

        int inpSize = inpW * inpH * inpD;
        int outSize = outW * outH * outD;

        int numWeights = 0;
        Neuron[] neurons = new Neuron[outSize];

        int length = winW * winH * inpD;

        int[][] wid = new int[outD][length + 1];

        for (int z = 0; z < outD; z++) {
            for (int d = 0; d <= length; d++) {
                wid[z][d] = numWeights++;
            }
        }

        for (int x = 0, i = 0; x < outW; x++) {
            for (int y = 0; y < outH; y++) {

                int[] sid = new int[length];

                for (int dx = 0, j = 0; dx < winW; dx++) {
                    int fx = x + dx;
                    for (int dy = 0; dy < winH; dy++) {
                        int fy = y + dy;
                        for (int fz = 0; fz < inpD; fz++, j++) {
                            sid[j] = ((fx) * inpH + fy) * inpD + fz;
                        }
                    }
                }

                for (int z = 0; z < outD; z++, i++) {
                    neurons[i] = new Neuron(length, sid, wid[z], inpSize + i, fold);
                }
            }
        }

        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static NeuralNetwork maxPoolLayer(int inpW, int inpH, int inpD, int scaleW, int scaleH, int outD, Fold fold) {

        int outW = inpW / scaleW;
        int outH = inpH / scaleH;

        int inpSize = inpW * inpH * inpD;
        int outSize = outW * outH * outD;

        int numWeights = 0;
        Neuron[] neurons = new Neuron[outSize];

        int length = scaleW * scaleH * inpD;

        int[][] wid = new int[outD][length + 1];

        for (int z = 0; z < outD; z++) {
            for (int d = 0; d <= length; d++) {
                wid[z][d] = numWeights++;
            }
        }

        for (int x = 0, i = 0; x < outW; x++) {
            for (int y = 0; y < outH; y++) {

                int[] sid = new int[length];

                for (int dx = 0, j = 0; dx < scaleW; dx++) {
                    int fx = x * scaleW + dx;
                    for (int dy = 0; dy < scaleH; dy++) {
                        int fy = y * scaleH + dy;
                        for (int fz = 0; fz < inpD; fz++, j++) {
                            sid[j] = ((fx) * inpH + fy) * inpD + fz;
                        }
                    }
                }

                for (int z = 0; z < outD; z++, i++) {
                    neurons[i] = new Neuron(length, sid, wid[z], inpSize + i, fold);
                }
            }
        }

        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static void main(String[] args) {
        Fold fold = new Sum(new Tanh());
        {
            NeuralNetwork network = cnnSharedLayer(256, 64, 1, 17, 17, 2, fold);
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = maxPoolLayer(240, 48, 2, 4, 2, 3, fold);
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = cnnSharedLayer(60, 24, 3, 5, 5, 4, fold);
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = maxPoolLayer(56, 20, 4, 4, 2, 5, fold);
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = cnnSharedLayer(14, 10, 5, 7, 3, 6, fold);
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

        {
            NeuralNetwork network = maxPoolLayer(8, 8, 6, 4, 2, 5, fold);
            System.out.println(network.inpSize + " " + network.outSize);
            System.out.println(network.size);
            System.out.println(network.numWeights);
        }

    }
}
