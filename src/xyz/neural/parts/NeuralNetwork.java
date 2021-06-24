package xyz.neural.parts;

import xyz.neural.matrices.Matrix;

import java.util.LinkedList;

public class NeuralNetwork {
    private LinkedList<Layer> layers = new LinkedList<>();
    private final float learningRate = 0.1f;

    public NeuralNetwork(LinkedList<Integer> compo, int nbEntries){
        for(int i = 0 ; i < compo.size() ; i++){
            if(i == 0){
                this.layers.add(new Layer(nbEntries, compo.get(i)));
            } else {
                this.layers.add(new Layer(this.layers.get(i-1).size(), compo.get(i)));
            }
        }
    }

    public NeuralNetwork(LinkedList<LinkedList<Neuron>> compo){
        for(LinkedList<Neuron> lay : compo){
            this.layers.add(new Layer(lay));
        }
    }

    public Layer getLayer(int i){
        return this.layers.get(i);
    }

    public Neuron getNeuron(int x, int y){
        return this.layers.get(x).getNeuron(y);
    }

    public LinkedList<Layer> getLayers() {
        return layers;
    }

    public int size(){
        return this.layers.size();
    }

    public Matrix feedForward(LinkedList<Float> entries){
        return this.getFeedResults(Matrix.vectorialize(entries)).getLast();
    }

    public Matrix feedForward(Matrix entries){
        return this.getFeedResults(entries).getLast();
    }

    public LinkedList<Matrix> getFeedResults(Matrix ents){ // permet de récupérer tous les a
        LinkedList<Matrix> results = new LinkedList<>();
        results.add(this.layers.get(0).feedForward(ents));
        for(int i = 1 ; i < this.size(); i++){
            results.add(this.layers.get(i).feedForward(results.get(i-1)));
        }
        return results;
    }

    public LinkedList<Matrix> getWeightedSums(Matrix ents){ // permet de récupérer tous les z
        LinkedList<Matrix> results = new LinkedList<>();
        results.add(this.layers.get(0).sumForward(ents));
        for(int i = 1 ; i < this.size(); i++){
            results.add(this.layers.get(i).feedForward(results.get(i-1)));
        }
        return results;
    }

    public float cost(LinkedList<Float> entries, Matrix targets, int nbSamples){
        Matrix els = Matrix.add(this.feedForward(entries), Matrix.multiply(-1, targets));
        float sum = 0;
        for(float el : els.getColumn(0)){
            sum += el * el;
        }
        return sum/nbSamples;
    }

    public float dcost(Matrix entries, Matrix targets, int indexResult){
        Matrix results = this.feedForward(entries);
        return results.getElement(0, indexResult) - targets.getElement(0, indexResult) * results.getElement(0, indexResult);
    }

    public void train(LinkedList<Float> entries, LinkedList<Float> targets, int nbTargets){
        Matrix ents = Matrix.vectorialize(entries);
        Matrix tgs = Matrix.vectorialize(targets);

        LinkedList<Matrix> aS = this.getFeedResults(ents);
        LinkedList<Matrix> zS = this.getWeightedSums(ents);
        // liste de vecteurs, un vecteur = une layer
        LinkedList<Matrix> deltas = new LinkedList<>();
        // permet d'initialiser la liste de deltas pour pouvoir manipuler les indices tranquillement
        for(int i = 0 ; i < this.size() ; i++){
            deltas.add(new Matrix(this.getLayer(i).size(), 1));
        }

        // commencement de la rétropropagation en commençant par la dernière couche, puis en remontant
        for(int l = this.size() - 1 ; l >= 0 ; l--){
            Layer layer = this.getLayer(l); // couche actuelle
            Matrix deltal = new Matrix(layer.size(), 1); // vecteur delta associé à la couche actuelle
            for(int n = 0 ; n < layer.size() ; n++){ // pour chaque neurone de la couche
                Neuron neuron = layer.getNeuron(n); // neurone actuel
                float delta = neuron.dfunc(zS.get(l).getElement(0, n));
                // disjonction de cas si on est sur la dernière ligne ou non
                if(l != this.size() - 1) {
                    // somme pondérée des erreurs de la couche d'après (rétropropagation = inversé)
                    float sum = 0;
                    for (int n2 = 0; n2 < this.getLayer(l + 1).size(); n2++) { // tous les neurones de la couche d'après
                        Neuron neuron2 = this.getLayer(l + 1).getNeuron(n2);
                        // récupère le poids lié au neurone actuel (n) et le multiplie à l'erreur du neurone de la couche suivante (n2)
                        sum += neuron2.getWeight(n) * deltas.get(l + 1).getElement(0, n2);
                    }
                    delta *= sum;
                } else {
                    // si dernière couche, on n'utilise pas l'erreur des neurones de la couche suivante mais
                    // on utilise la dérivée de la fonction de cout
                    delta *= this.dcost(ents, tgs, n);
                }
                deltal.setElement(0, n, delta);
            }
            deltas.set(l, deltal);
        }

        // changement de tous les poids
        for(int l = 0; l < this.size() ; l++){ // défile toutes les couches
            Layer layer = this.getLayer(l); // couche actuelle
            for(int n = 0 ; n < layer.size() ; n++){ // défile tous les neurones de la couche actuelle
                Neuron neuron = layer.getNeuron(n); // neurone actuel
                float deltali = deltas.get(l).getElement(0, n); // delta du neurone actuel
                for(int w = 0 ; w < neuron.getWeights().getNumberLines() ; w++){ // défile tous les poids du neurone
                    float currentWeight = neuron.getWeight(w); // poids avant modification
                    float alj = aS.get(l).getElement(0, n); // resultat du neurone de la couche précédente lié au poids
                    // indiçage incorrect
                    neuron.setWeight(w, currentWeight - this.learningRate * alj * deltali);
                }
                neuron.setBias(neuron.getBias() - this.learningRate * deltali);
            }
        }
    }
}
