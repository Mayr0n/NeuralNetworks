package xyz.neural.parts;

import xyz.neural.matrices.Vector;

import java.util.LinkedList;
import java.util.Random;
import java.util.function.Function;

public class Neuron {
    private Vector weights;
    private float bias;
    private Function<Float, Float> function = x -> 1/(1+(float) Math.exp(-x));
    private Function<Float, Float> derivative = x -> (float) Math.exp(-x)/(float) Math.pow(Math.exp(-x) + 1, 2);

    public Neuron(LinkedList<Float> weights, float bias, Function<Float, Float> func, Function<Float, Float> dfunc){
        this.weights = Vector.vectorialize(weights);
        this.bias = bias;
        if(null != func && null != dfunc){
            this.function = func;
            this.derivative = dfunc;
        }
    }

    public Neuron(int nbEntries, Function<Float, Float> func, Function<Float, Float> dfunc){
        LinkedList<Float> weights = new LinkedList<>();
        for(int i = 0 ; i < nbEntries ; i++){
            boolean neg = new Random().nextInt(2) == 1;
            weights.add(neg ? -new Random().nextFloat() : new Random().nextFloat());
        }
        this.weights = Vector.vectorialize(weights);
        this.bias = 1;
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
            sb.append(this.weights.get(i)).append("\n");
        }
        return sb.toString();
    }

    public float getWeight(int index){
        return this.weights.get(index);
    }

    public Vector getWeights() {
        return weights;
    }

    public void setWeight(int index, float w){
        this.weights.set(index, w);
    }

    public void setWeights(Vector v) {
        this.weights = v;
    }

    public float weightedSum(Vector entries){
        assert entries.getNumberLines() == this.weights.getNumberLines();
        return Vector.scalaire(this.weights, entries) + this.bias;
    }

    public float feedForward(Vector entries){
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
