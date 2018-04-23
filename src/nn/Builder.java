package nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    public static NeuralNetwork disperseLayer(int inpSize, int outSize, int deg, Random random, Fold fold) {
        Neuron[] neurons = new Neuron[outSize];

        Set<Integer>[] connections = new Set[outSize];

        for (int t = 0; t < outSize; t++) {
            Set<Integer> hashSet = new HashSet<>();
            for (int d = 0; d < deg; d++) {
                int f = random.nextInt(inpSize);
                hashSet.add(f);
            }
            connections[t] = (hashSet);
        }

        for (int f = 0; f < inpSize; f++) {
            for (int d = 0; d < deg; d++) {
                int t = random.nextInt(outSize);
                connections[t].add(f);
            }
        }

        int numWeights = 0;

        for (int i = 0; i < outSize; i++) {
            Set<Integer> hashSet = connections[i];
            int length = hashSet.size();

            int[] wid = new int[length + 1];
            for (int d = 0; d <= length; d++) {
                wid[d] = numWeights++;
            }

            int[] sid = new int[length];
            int p = 0;
            for (int s : hashSet) {
                sid[p++] = s;
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

        int indexOffset = inpSize, weightOffset = 0;
        int p = layers[0].neurons.length;
        for (int i = 1; i < depth; i++) {
            indexOffset += layers[i - 1].neurons.length;
            weightOffset += layers[i - 1].numWeights;
            for (Neuron neuron : layers[i].neurons) {
                neurons[p++] = neuron.copyWithOffset(indexOffset - layers[i].inpSize, weightOffset);
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

    static int stp(int inp, int out) {
        if (out <= 1) {
            return 0;
        } else {
            return (inp - 1) / (out - 1);
        }
    }

    public static NeuralNetwork convLayer(int inpW, int inpH, int inpD, int outW, int outH, int outD, Fold fold) {
        return convLayer(inpW, inpH, inpD, stp(inpW, outW), stp(inpH, outH), outW, outH, outD, fold);
    }

    public static NeuralNetwork convLayer(int inpW, int inpH, int inpD, int stpW, int stpH, int outW, int outH, int outD, Fold fold) {

        int winW = win(inpW, stpW, outW);
        if (winW <= 0) {
            throw new IllegalArgumentException(winW + " = winW = inpW - stpW * (outW - 1) should be > 0");
        }

        int winH = win(inpH, stpH, outH);
        if (winH <= 0) {
            throw new IllegalArgumentException(winH + " = winH = inpH - stpH * (outH - 1) should be > 0");
        }

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
                    int fx = x * stpW + dx;

                    for (int dy = 0; dy < winH; dy++) {
                        int fy = y * stpH + dy;

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

    static void testConnections() {
        Fold fold = new Sum(new Tanh());

        NeuralNetwork[] layers = new NeuralNetwork[6];

        layers[0] = cnnSharedLayer(256, 64, 1, 17, 17, 2, fold);
        layers[1] = maxPoolLayer(240, 48, 2, 4, 2, 3, fold);
        layers[2] = cnnSharedLayer(60, 24, 3, 5, 5, 4, fold);
        layers[3] = maxPoolLayer(56, 20, 4, 4, 2, 5, fold);
        layers[4] = cnnSharedLayer(14, 10, 5, 7, 3, 6, fold);
        layers[5] = maxPoolLayer(8, 8, 6, 4, 2, 5, fold);

        NeuralNetwork full = connect(layers);

        System.out.println(full.inpSize);
        System.out.println(full.outSize);
        System.out.println(full.numWeights);
        System.out.println(full.size);
    }

    static void test() {

        Fold fold = new Sum(new Tanh());

        NeuralNetwork nn = disperseLayer(12, 15, 4, new Random(), fold);

        System.out.println(nn.inpSize);
        System.out.println(nn.outSize);
        System.out.println(nn.numWeights);
        System.out.println(nn.size);
    }

    static int testStp(int inp, int out) {
        int stp = 10000;

        while (inp - stp * (out - 1) <= 0) {
            --stp;
        }

        return stp;
    }

    static int win(int inp, int stp, int out) {
        return inp - stp * (out - 1);
    }

    public static void main(String[] args) {
        Random random = new Random();

        int out = random.nextInt(100) + 2;
        int inp = random.nextInt(100) + out + 1;

        int stp1 = stp(inp, out);
        int stp2 = testStp(inp, out);

        System.out.println(inp + " " + out);
        System.out.println(stp1 + " " + win(inp, stp1, out));
        System.out.println(stp2 + " " + win(inp, stp2, out));

    }

    public static void test2() {
        Fold fold = new Sum(new Tanh());
        NeuralNetwork a = disperseLayer(7, 5, 1, new Random(23), fold);
        NeuralNetwork b = disperseLayer(5, 3, 1, new Random(23), fold);
        NeuralNetwork c = connect(a, b);
        System.out.println(a.size + " " + b.size + " " + c.size);
    }
}
