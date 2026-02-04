package org.infinitytwogames.wispui.ui;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.UIBuilder;

public class Rectangle extends UI {
    public Builder builder(UIRenderer renderer) {
        return new Builder(renderer);
    }
    
    public Rectangle(UIRenderer renderer) {
        super(renderer);
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
    
    public static class Builder extends UIBuilder<Rectangle> {
        public Builder(UIRenderer renderer) {
            super(new Rectangle(renderer));
        }
        
        @Override
        public UIBuilder<Rectangle> applyDefault() {
            return this;
        }
        
        public Rectangle build() {
            return ui;
        }
    }
    
    public static class RectangleBuilder extends UIBuilder<Rectangle> {
        public RectangleBuilder(UIRenderer renderer, Rectangle element) {
            super(element);
        }
        
        @Override
        public UIBuilder<Rectangle> applyDefault() {
            backgroundColor(new RGBA(0, 0, 0, 1));
            return this;
        }
    }
}
