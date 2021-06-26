package xyz.neural.parts;

import xyz.neural.matrices.Matrix;

import java.util.LinkedList;
import java.util.Random;
import java.util.function.Function;
import java.lang.Math.*;

public class Neuron {
    private Matrix weights;
    private float bias = 1;
    private Function<Float, Float> function = x -> 1/(1+(float) Math.exp(-x));
    private Function<Float, Float> derivative = x -> (float) Math.exp(-x)/(float) Math.pow(Math.exp(-x) + 1, 2);

    public Neuron(LinkedList<Float> weights, Function<Float, Float> func, Function<Float, Float> dfunc){
        this.weights = Matrix.vectorialize(weights);
        if(null != func && null != dfunc){
            this.function = func;
            this.derivative = dfunc;
        }
    }

    public Neuron(int nbEntries, Function<Float, Float> func, Function<Float, Float> dfunc){
        LinkedList<Float> weights = new LinkedList<>();
        for(int i = 0 ; i < nbEntries ; i++){
            weights.add(new Random().nextFloat());
        }
        this.weights = Matrix.vectorialize(weights);
        if(null != func && null != dfunc){
            this.function = func;
            this.derivative = dfunc;
        }
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Neuron){
            return ((Neuron) obj).getWeights().equals(this.getWeights());
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < this.weights.getNumberLines() ; i++){
            sb.append(this.weights.getElement(0, i)).append("\n");
        }
        return sb.toString();
    }

    public float getWeight(int index){
        return this.weights.getElement(0, index);
    }

    public Matrix getWeights() {
        return weights;
    }

    public void setWeight(int index, float w){
        this.weights.setElement(0, index, w);
    }

    public void setWeights(LinkedList<Float> weights) {
        this.weights = Matrix.vectorialize(weights);
    }

    public float weightedSum(Matrix entries){
        assert entries.getNumberLines() == this.weights.getNumberLines();
        return Matrix.scalaire(entries, this.weights) + this.bias;
    }

    public float feedForward(Matrix entries){
        return this.function.apply(this.weightedSum(entries));
    }

    public float dfunc(float f){
        return this.derivative.apply(f);
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }
}
