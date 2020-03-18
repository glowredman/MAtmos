package eu.ha3.matmos.core.preinit;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** A wrapper around a list that emits events when items are added to the list. */

public class AddListenableWrappedList<T> implements List<T> {
    List<T> original;
    
    private List<ListAddListener<T>> listeners = new LinkedList<>();
    
    public AddListenableWrappedList(List<T> original){
        this.original = original;
    }
    
    public void addListener(ListAddListener<T> l) {
        listeners.add(l);
    }
    
    public void removeListener(ListAddListener<T> l) {
        listeners.remove(l);
    }
    
    private void emit(int index, T element) {
        for(ListAddListener<T> l : listeners) {
            l.onElementAdded(index, element);
        }
    }
    
    @Override
    public boolean add(T e) {
        if(original.add(e)) {
            emit(size() - 1, e);
            return true;
        }
        return false;
    }

    @Override
    public void add(int index, T element) {
        original.add(index, element);
        
        emit(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if(original.addAll(c)) {
            int cIndex = 0;
            for(T e : c) {
                emit(index + cIndex, e);
                cIndex++;
            }
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        original.clear();
    }

    @Override
    public boolean contains(Object o) {
        return original.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return original.containsAll(c);
    }

    @Override
    public T get(int index) {
        return original.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return original.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return original.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return original.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return original.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return original.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        return original.remove(o);
    }

    @Override
    public T remove(int index) {
        return original.remove(index);
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
    public T set(int index, T element) {
        return original.set(index, element);
    }

    @Override
    public int size() {
        return original.size();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return original.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return original.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return original.toArray(a);
    }
}
