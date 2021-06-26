package xyz.neural.parts;

import xyz.neural.SQLManager;
import xyz.neural.matrices.Matrix;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class NeuralNetwork {
    private LinkedList<Layer> layers = new LinkedList<>();
    public final float learningRate = 1f;

    public NeuralNetwork(LinkedList<Integer> compo, int nbEntries){
        for(int i = 0 ; i < compo.size() ; i++){
            if(i == 0){
                this.layers.add(new Layer(nbEntries, compo.get(i)));
            } else {
                this.layers.add(new Layer(this.layers.get(i-1).size(), compo.get(i)));
            }
        }
    }

    public static void delete(String name){
        LinkedHashMap<String, Object> search = new LinkedHashMap<>();
        search.put("id", 0);
        LinkedList<LinkedList<Object>> results = SQLManager.get(search, "neural", "name=\""+ name + "\"");
        assert results.size() == 1;

        int neuralID = getID("neural", "name=\"" + name + "\"");
        search.clear();
        search.put("id", 0);
        results = SQLManager.get(search, "layers", "network=" + neuralID);
        LinkedList<Integer> layersIDs = new LinkedList<>();
        for(LinkedList<Object> lay : results){
            layersIDs.add((int) lay.get(0));
        }
        for(int layerID: layersIDs){
            LinkedList<Integer> neuronsIDs = new LinkedList<>();
            results = SQLManager.get(search, "neurons", "layer=" + layerID);
            for(LinkedList<Object> neuro : results){
                neuronsIDs.add((int) neuro.get(0));
            }
            for(int neuronID : neuronsIDs){
                SQLManager.delete("weights", "neuron=" + neuronID);
            }
            SQLManager.delete("neurons", "layer=" + layerID);
        }
        SQLManager.delete("layers", "network=" + neuralID);
        SQLManager.delete("neural", "name=\"" + name + "\"");
    }
    public static NeuralNetwork load(String name){
        LinkedHashMap<String, Object> search = new LinkedHashMap<>();
        search.put("id", 0);
        LinkedList<LinkedList<Object>> results = SQLManager.get(search, "neural", "name=\""+ name + "\"");
        assert results.size() == 1;

        LinkedList<Layer> layers = new LinkedList<>();

        search.clear();
        search.put("id", 0);
        results = SQLManager.get(search, "layers", "network=" + getID("neural", "name=\"" + name + "\"") + " ORDER BY i");
        LinkedList<Integer> layersIDs = new LinkedList<>();
        for(LinkedList<Object> lay : results){
            layersIDs.add((int) lay.get(0));
        }
        for(int layerID: layersIDs){
            LinkedList<Integer> neuronsIDs = new LinkedList<>();
            results = SQLManager.get(search, "neurons", "layer=" + layerID + " ORDER BY i");
            for(LinkedList<Object> neuro : results){
                neuronsIDs.add((int) neuro.get(0));
            }
            LinkedList<Neuron> neurons = new LinkedList<>();
            for(int neuronID : neuronsIDs){
                LinkedList<Float> weights = new LinkedList<>();
                LinkedHashMap<String, Object> search2 = new LinkedHashMap<>();
                search2.put("value", 0f);

                results = SQLManager.get(search2, "weights", "neuron=" + neuronID + " ORDER BY i");
                for(LinkedList<Object> weight : results){
                    weights.add((float) weight.get(0));
                }
                Neuron neuron = new Neuron(weights, null, null);
                neurons.add(neuron);
            }
            layers.add(new Layer(neurons));
        }
        return new NeuralNetwork(layers);
    }
    public boolean save(String name){
        LinkedHashMap<String, Object> search = new LinkedHashMap<>();
        search.put("id", 0);
        LinkedList<LinkedList<Object>> results = SQLManager.get(search, "neural", "name=\""+ name + "\"");
        boolean exist = results.size() >= 1;
        LinkedHashMap<String, Object> input = new LinkedHashMap<>();
        if(!exist){
            input.put("name", name);
            SQLManager.insert(input, "neural");
        }
        int id = getID("neural", "name=\""+ name + "\"");
        for(int l = 0 ; l < this.size() ; l++){ // size | ID + valeur | table |
            if(!exist) insert("layers", "network", id, l);
            int layerID = getID("layers", "network=" + id + " AND i=" + l);
            for(int n = 0 ; n < this.layers.get(l).size() ; n++){
                if(!exist)  insert("neurons", "layer", layerID, n);
                int neuronID = getID( "neurons", "layer=" + layerID + " AND i=" + n);
                Neuron neuron = this.layers.get(l).getNeuron(n);
                for(int w = 0 ; w < neuron.getWeights().getNumberLines() ; w++){
                    input.clear();
                    if(!exist) {
                        input.put("neuron", neuronID);
                        input.put("i", w);
                        input.put("value", neuron.getWeight(w));
                        SQLManager.insert(input, "weights");
                    } else {
                        input.put("value", neuron.getWeight(w));
                        SQLManager.update(input, "weights", "neuron=" + neuronID + " AND i=" + w);
                    }
                }
            }
        }
        return exist;
    }

    private static int getID(String table, String condition){
        LinkedHashMap<String, Object> search = new LinkedHashMap<>();
        search.put("id", 0);
        return (int) SQLManager.get(search, table, condition).get(0).get(0);
    }

    private void insert(String table, String idS, int id, int index){
        LinkedHashMap<String, Object> input = new LinkedHashMap<>();
        input.put(idS, id);
        input.put("i", index);
        SQLManager.insert(input, table);
    }

    public NeuralNetwork(LinkedList<Layer> compo){
        this.layers = compo;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof NeuralNetwork){
            return ((NeuralNetwork) obj).getLayers().containsAll(this.getLayers());
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("^^^^^^^^^^^^^^^^^^^^^^^^\n");
        for(Layer l : this.layers){
            sb.append(l.toString()).append("\n");
        }
        sb.append("vvvvvvvvvvvvvvvvvvvvvvvvv\n");
        return sb.toString();
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
        assert ents.getNumberLines() == this.layers.get(0).getNeuron(0).getWeights().getNumberLines();
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

    public float dcost(Matrix entries, Matrix targets, int index){
        Matrix results = this.feedForward(entries);
        return 2/(float) results.getNumberLines() * (results.getElement(0, index) - targets.getElement(0, index));
    }

    public void train(LinkedList<Float> entries, LinkedList<Float> targets){
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
            for(int i = 0 ; i < layer.size() ; i++){ // pour chaque neurone de la couche
                Neuron neuron = layer.getNeuron(i); // neurone actuel
                float delta = neuron.dfunc(zS.get(l).getElement(0, i));
                // disjonction de cas si on est sur la dernière couche ou non
                if(l != this.size() - 1) {
                    // somme pondérée des erreurs de la couche d'après
                    Matrix weights = new Matrix(this.getLayer(l + 1).size(), 1);

                    for (int n2 = 0; n2 < this.getLayer(l + 1).size(); n2++) { // tous les neurones de la couche d'après
                        Neuron neuron2 = this.getLayer(l + 1).getNeuron(n2);
                        // récupère le poids relié au neurone de la couche d'après (n2) au neurone actuel (n)
                        weights.setElement(0, n2, neuron2.getWeight(i));
                    }
                    delta *= Matrix.scalaire(weights, deltas.get(l + 1));
                } else {
                    // si dernière couche, on n'utilise pas l'erreur des neurones de la couche suivante mais
                    // on utilise la dérivée de la fonction de cout
                    delta *= this.dcost(ents, tgs, i);
                }
                deltal.setElement(0, i, delta);
            }
            deltas.set(l, deltal);
        }

        // changement de tous les poids
        for(int l = 0; l < this.size() ; l++){ // défile toutes les couches
            Layer layer = this.getLayer(l); // couche actuelle
            for(int i = 0 ; i < layer.size() ; i++){ // défile tous les neurones de la couche actuelle
                Neuron neuron = layer.getNeuron(i); // neurone actuel
                float deltali = deltas.get(l).getElement(0, i); // delta du neurone actuel
                for(int j = 0 ; j < neuron.getWeights().getNumberLines() ; j++){ // défile tous les poids du neurone
                    float currentWeight = neuron.getWeight(j); // poids avant modification
                    float alj;
                    if(l == 0){
                        alj = ents.getElement(0, j);
                    } else {
                        alj = aS.get(l - 1).getElement(0, j); // resultat du neurone de la couche précédente lié au poids
                    }
                    // indiçage incorrect
                    neuron.setWeight(j, currentWeight - this.learningRate * alj * deltali);
                }
                neuron.setBias(neuron.getBias() - this.learningRate * deltali);
            }
        }
    }
}
