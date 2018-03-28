package nn;

import java.util.ArrayList;
import java.util.List;

import nn.act.Tanh;
import nn.fld.One;
import nn.fld.Sum;

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

    public static NeuralNetwork cnnSharedLayer(int inpW, int inpH, int winW, int winH, Activation activation) {

        int outW = inpW - winW + 1;
        int outH = inpH - winH + 1;

        int inpSize = inpW * inpH;
        int outSize = outW * outH;

        int numWeights = 0;
        Neuron[] neurons = new Neuron[outSize];

        int dendrites = winW * winH;
        int[] wid = new int[dendrites];
        for (int d = 0; d < dendrites; d++) {
            wid[d] = numWeights++;
        }

        for (int x = 0, i = 0; x < outW; x++) {
            for (int y = 0; y < outH; y++, i++) {

                int[] from = new int[dendrites];

                for (int dx = 0, j = 0; dx < winW; dx++) {
                    for (int dy = 0; dy < winH; dy++, j++) {
                        from[j] = (x + dx) * inpH + (y + dy);
                    }
                }

                neurons[i] = new Sum(dendrites, from, wid, inpSize + i, activation);
            }
        }

        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static NeuralNetwork maxPoolLayer(int inpW, int inpH, int scaleW, int scaleH, Activation activation) {

        int outW = inpW / scaleW;
        int outH = inpH / scaleH;

        int inpSize = inpW * inpH;
        int outSize = outW * outH;

        int numWeights = 0;
        Neuron[] neurons = new Neuron[outSize];

        int dendrites = scaleW * scaleH;
        int[] wid = new int[dendrites];
        for (int d = 0; d < dendrites; d++) {
            wid[d] = numWeights++;
        }

        for (int x = 0, i = 0; x < outW; x++) {
            for (int y = 0; y < outH; y++, i++) {

                int[] from = new int[dendrites];

                for (int dx = 0, j = 0; dx < scaleW; dx++) {
                    for (int dy = 0; dy < scaleH; dy++, j++) {
                        from[j] = (x * scaleW + dx) * inpH + (y * scaleH + dy);
                    }
                }

                neurons[i] = new Sum(dendrites, from, wid, inpSize + i, activation);
            }
        }

        return new NeuralNetwork(inpSize, outSize, numWeights, neurons);
    }

    public static void main(String[] args) {
        NeuralNetwork network = full(7, 1, 3);
        System.out.println(network.size);
    }
}
