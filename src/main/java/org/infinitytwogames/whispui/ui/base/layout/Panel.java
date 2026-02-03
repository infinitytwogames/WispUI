package org.infinitytwogames.whispui.ui.base.layout;

import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.ui.base.UI;

import java.util.ArrayList;
import java.util.List;

public class Panel extends UI implements Container {
    private final List<UI> children = new ArrayList<>();
    
    public Panel(Scene scene) {
        super(scene.getRenderer());
    }
    
    public void addUI(UI ui) {
        children.add(ui);
        ui.setParent(this);
    }
    
    public void removeUI(UI ui) {
        children.remove(ui);
        ui.setParent(null);
    }
    
    @Override
    public List<UI> getUIs() {
        return children;
    }
    
    @Override
    public void draw() {
        super.draw();
        for (UI child : children) {
            child.draw();
        }
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
    
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    
    }
    
    @Override
    public void onMouseHoverEnded() {
    
    }
    
    @Override
    public void cleanup() {
    
    }
}

