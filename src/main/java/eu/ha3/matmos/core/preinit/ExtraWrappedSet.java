package eu.ha3.matmos.core.preinit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Wrapper around a set that contains the items of the wrapped set, plus the contents of an extra set. */

public class ExtraWrappedSet<T> implements Set<T> {

    private Set<T> original;
    
    private Set<T> extra = new HashSet<T>();
    
    public ExtraWrappedSet(Set<T> original){
        this.original = original;
    }
    
    @Override
    public boolean add(T e) {
        return original.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return original.addAll(c);
    }

    @Override
    public void clear() {
        original.clear();
        extra.clear();
    }

    @Override
    public boolean contains(Object o) {
        return original.contains(o) || extra.contains(o);
    }
    
    private Set<T> getUnionSet(){
        HashSet<T> union = new HashSet<T>();
        union.addAll(original);
        union.addAll(extra);
        return union;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getUnionSet().containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty() && extra.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return getUnionSet().iterator(); // this won't work correctly if Iterator.remove() is called
    }

    @Override
    public boolean remove(Object o) {
        return original.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return original.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return original.retainAll(c);
    }

    @Override
    public int size() {
        return original.size() + extra.size();
    }

    @Override
    public Object[] toArray() {
        return getUnionSet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getUnionSet().toArray(a);
    }
    
    public Set<T> getExtraSet(){
        return extra;
    }
}
