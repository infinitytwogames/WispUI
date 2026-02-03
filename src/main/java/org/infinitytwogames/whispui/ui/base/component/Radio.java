package org.infinitytwogames.whispui.ui.base.component;

import org.infinitytwogames.whispui.ui.base.UI;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

// MODIFIED
public class Radio implements Component {
    protected boolean active = false;
    protected Consumer<Boolean> event;
    protected List<UI> uis = new ArrayList<>();
    
    public Radio(Consumer<Boolean> event) {
        this.event = event;
    }
    
    public Radio() {
    }
    
    public int size() {
        return uis.size();
    }
    
    public boolean remove(UI o) {
        return uis.remove(o);
    }
    
    public ListIterator<UI> listIterator(int i) {
        return uis.listIterator(i);
    }
    
    public boolean removeIf(Predicate<UI> filter) {
        return uis.removeIf(filter);
    }
    
    public UI get(int i) {
        return uis.get(i);
    }
    
    public <T> T[] toArray(T[] ts) {
        return uis.toArray(ts);
    }
    
    public int lastIndexOf(UI o) {
        return uis.lastIndexOf(o);
    }
    
    public void forEach(Consumer<UI> action) {
        uis.forEach(action);
    }
    
    public void clear() {
        uis.clear();
    }
    
    public boolean add(UI ui) {
        return uis.add(ui);
    }
    
    public Stream<UI> stream() {
        return uis.stream();
    }
    
    public boolean contains(UI o) {
        return uis.contains(o);
    }
    
    public Iterator<UI> iterator() {
        return uis.iterator();
    }
    
    public UI remove(int i) {
        return uis.remove(i);
    }
    
    public int indexOf(UI o) {
        return uis.indexOf(o);
    }
    
    public Stream<UI> parallelStream() {
        return uis.parallelStream();
    }
    
    public Object[] toArray() {
        return uis.toArray();
    }
    
    public boolean addAll(Collection<UI> collection) {
        return uis.addAll(collection);
    }
    
    public void addFirst(UI ui) {
        uis.addFirst(ui);
    }
    
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return uis.toArray(generator);
    }
    
    public void add(int i, UI ui) {
        uis.add(i, ui);
    }
    
    public boolean isEmpty() {
        return uis.isEmpty();
    }
    
    public void activate() {
        active = true;
        if (event != null) event.accept(true);
    }
    
    public void deactivate() {
        active = false;
        if (event != null) event.accept(false);
    }
    
    @Override
    public void draw() {
    }
    
    @Override
    public void setAngle(float angle) {
    
    }
    
    @Override
    public void setDrawOrder(int z) {
    
    }
    
    @Override
    public int getDrawOrder() {
        return 0;
    }
    
    @Override
    public void cleanup() {
        uis.clear();
        event = null;
    }
    
    @Override
    public void setParent(UI ui) {
    
    }
    
    @Override
    public Component copy() {
        Radio radio = new Radio();
        radio.uis = new ArrayList<>(uis);
        return radio;
    }
    
    public boolean isActive() {
        return active;
    }
}
