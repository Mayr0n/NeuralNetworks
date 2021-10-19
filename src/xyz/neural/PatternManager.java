package xyz.neural;

import xyz.neural.matrices.Vector;

import java.util.Hashtable;

public interface PatternManager {
    public Hashtable<Vector, Vector> getPattern();
    public Vector getRandomKey();
}
