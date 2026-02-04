package org.infinitytwogames.wispui.ui;

import org.infinitytwogames.wispui.Display;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.UIBuilder;
import org.infinitytwogames.wispui.ui.base.component.Scale;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.joml.Vector2i;

/**
 * A built-in UI component that is designed to always cover the entire window.
 * It automatically subscribes to WindowResizedEvent to maintain full-screen size.
 */
public class Background extends UI {
    public Scale scale = new Scale(1, 1);
    
    public Background(UIRenderer renderer) {
        super(renderer);
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        // Background usually doesn't handle clicks, unless it's blocking
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        // Background usually doesn't need hover logic
    }
    
    @Override
    public void onMouseHoverEnded() {
        // Background usually doesn't need hover logic
    }
    
    @Override
    public void cleanup() {
    
    }
    
    @Override
    public void draw() {
        setWidth(scale.getWidth());
        setHeight(scale.getHeight());
        super.draw();
    }
    
    /**
     * Builder for the Background UI element, enforcing full-screen and centered properties.
     */
    public static class Builder extends UIBuilder<Background> {
        public Builder(UIRenderer renderer) {
            super(new Background(renderer));
        }
        
        @Override
        public UIBuilder<Background> width(int width) {
            // Enforce full width and height, ignoring the passed width.
            // Call super to ensure base UIBuilder logic is also executed.
            super.width(Display.width);
            super.height(Display.height);
            return this;
        }
        
        @Override
        public UIBuilder<Background> height(int height) {
            // Enforce full height and width, ignoring the passed height.
            // Call super to ensure base UIBuilder logic is also executed.
            super.height(Display.height);
            super.width(Display.width);
            return this;
        }
        
        // --- Position Overrides to Enforce Centering ---
        
        private static final Anchor CENTER_ANCHOR = new Anchor(0.5f, 0.5f);
        private static final Pivot CENTER_PIVOT = new Pivot(0.5f, 0.5f);
        
        @Override
        public UIBuilder<Background> position(Anchor anchor, Pivot pivot, Vector2i offset) {
            // Enforce centered Anchor and Pivot, but respect the offset if provided.
            super.position(CENTER_ANCHOR, CENTER_PIVOT, offset);
            return this;
        }
        
        @Override
        public UIBuilder<Background> position(Anchor anchor, Pivot pivot) {
            // Enforce centered Anchor and Pivot with no offset.
            super.position(CENTER_ANCHOR, CENTER_PIVOT);
            return this;
        }
        
        @Override
        public UIBuilder<Background> applyDefault() {
            // Apply the standard full-screen, centered default properties
            super.position(CENTER_ANCHOR, CENTER_PIVOT);
            super.width(Display.width);
            super.height(Display.height);
            return this;
        }
    }
}
