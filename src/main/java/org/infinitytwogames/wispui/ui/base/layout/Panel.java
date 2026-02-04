package org.infinitytwogames.wispui.ui.base.layout;

import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.ui.base.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic container for grouping and managing multiple {@link UI} elements.
 * <p>
 * The {@code Panel} serves as a structural building block. It allows for the
 * creation of complex UI hierarchies where children are positioned relative
 * to the panel's own coordinates.
 * </p>
 * * <h2>Hierarchy & Rendering</h2>
 * <p>
 * When a Panel is drawn, it first renders its own background (via {@code super.draw()})
 * and then iterates through its children to render them. Note that children
 * are rendered in the order they were added unless specific draw orders are set.
 * </p>
 *
 * @author Infinity Two Games
 */
public class Panel extends UI implements Container {
    private final List<UI> children = new ArrayList<>();
    
    public Panel(Scene scene) {
        super(scene.getRenderer());
    }
    
    /**
     * Adds a UI element to this panel and establishes the parent-child relationship.
     * <p>
     * Once added, the child's {@link #getPosition()} will be calculated
     * relative to this panel's top-left corner.
     * </p>
     *
     * @param ui
     *         The element to nest.
     */
    public void addUI(UI ui) {
        children.add(ui);
        ui.setParent(this);
    }
    
    /**
     * Removes a UI element and clears its parent reference.
     *
     * @param ui
     *         The element to remove.
     */
    public void removeUI(UI ui) {
        children.remove(ui);
        ui.setParent(null);
    }
    
    /**
     * @return The list of nested UI elements for hit-testing and iteration.
     */
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

