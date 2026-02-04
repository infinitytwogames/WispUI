package org.infinitytwogames.wispui.ui.base.interactive;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.UIBuilder;
import org.infinitytwogames.wispui.ui.base.animations.Updatable;

/**
 * A blinking vertical bar used to indicate the current text insertion point.
 * <p>
 * The {@code Caret} manages its own visibility state through an internal timer.
 * When active, it toggles visibility every 500ms to create a standard
 * blinking cursor effect.
 * </p>
 *
 *
 *
 * <h2>Operational Logic</h2>
 * <ul>
 * <li><b>Blinking:</b> Controlled via the {@link #update(float)} method. It uses
 * a {@code delta} time accumulator to switch the {@code visible} flag.</li>
 * <li><b>Resetting:</b> The {@link #reset()} method forces the caret back to
 * the start of its blink cycle (visible), which is useful when the user
 * types or moves the cursor to provide immediate feedback.</li>
 * <li><b>State Control:</b> If {@code active} is false, the caret remains
 * hidden and stops calculating blink logic.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
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
        setBackgroundColor(new RGBA(1, 1, 1, 1));
    }
    
    /**
     * Updates the blink timer based on elapsed frame time.
     * <p>
     * Every 0.5 seconds (500ms), the visibility is flipped. If the caret
     * is inactive, this logic is bypassed to save resources.
     * </p>
     *
     * @param delta
     *         The time elapsed since the last frame (in seconds).
     */
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
    
    /**
     * Resets the timer and makes the caret visible.
     * <p>
     * Call this whenever a character is typed to prevent the caret
     * from being "blinked out" at the exact moment a user is looking
     * for their new position.
     * </p>
     */
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

