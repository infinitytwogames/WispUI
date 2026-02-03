package org.infinitytwogames.whispui.ui.base.interactive;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.UIBuilder;
import org.infinitytwogames.whispui.ui.base.animations.Updatable;

public class Caret extends UI implements Updatable {
    private float blinkTimer = 0f;
    private boolean visible = true;
    private boolean active = true;

    public static CaretBuilder builder(UIRenderer renderer) {
        return new CaretBuilder(renderer);
    }

    public Caret(UIRenderer renderer) {
        super(renderer);
        setWidth(10);
        setBackgroundColor(new RGBA(1,1,1,1));
    }

    public void update(float delta) {
        if (!active) {
            return;
        }
        
        blinkTimer += delta;
        if (blinkTimer >= 0.5f) {
            blinkTimer = 0f;
            visible = !visible;
        }
    }

    public void reset() {
        blinkTimer = 0f;
    }

    @Override
    public void draw() {
        if (visible && active) {
            super.draw();
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void forceDraw() {
        super.draw();
    }
    
    public static class CaretBuilder extends UIBuilder<Caret> {
        public CaretBuilder(UIRenderer renderer) {
            super(new Caret(renderer));
        }

        public CaretBuilder active(boolean active) {
            ui.setActive(active);
            return this;
        }

        @Override
        public CaretBuilder applyDefault() {
            return this;
        }
    }
}

