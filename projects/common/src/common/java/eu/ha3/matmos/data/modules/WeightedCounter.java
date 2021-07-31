package eu.ha3.matmos.data.modules;

public interface WeightedCounter<T> {
    public abstract void increment(T thing, int weight);
}
