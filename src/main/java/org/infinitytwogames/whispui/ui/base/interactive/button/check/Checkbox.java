package org.infinitytwogames.whispui.ui.base.interactive.button.check;

import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.ui.base.Label;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Scene;

import java.util.List;

// MODIFIED
public abstract class Checkbox extends UI implements Container {
    protected Check check;
    protected Label label;
    protected int gap = 12; // Gap between box and text
    
    private List<UI> ui;
    
    @Override
    public List<UI> getUIs() {
        return ui;
    }
    
    public Checkbox(Scene scene, String fontPath, String text) {
        super(scene.getRenderer());
        
        label = new Label(scene, fontPath);
        label.setText(text);
        label.setParent(this);
        
        check = new Check(renderer) {
            @Override
            public void onCheck(boolean checkState) {
                Checkbox.this.onCheck(checkState);
            }
        };
        check.setParent(this);
        ui = List.of(check, label);
        
        // Default size for the checkbox part
        check.setSize(24, 24);
    }
    
    @Override
    public void draw() {
        // Position the checkbox to the left, vertically centered
        check.setOffset(0, (getHeight() - check.getHeight()) / 2);
        
        // Position the label to the right of the checkbox
        label.setOffset(check.getWidth() + gap, (getHeight() - label.getHeight()) / 2);
        
        // Draw children
        super.draw(); // Draws background if any
        check.draw();
        label.draw();
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        // Optional: Highlight the whole component on hover
    }
    
    @Override
    public void onMouseHoverEnded() {
        check.setHovering(false);
        check.onMouseHoverEnded();
    }
    
    @Override
    public void cleanup() {
        label.close();
        check.close();
    }
    
    public Check getCheckPart() {
        return check;
    }
    
    public Label getLabel() {
        return label;
    }
    
    public abstract void onCheck(boolean check);
}