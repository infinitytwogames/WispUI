package org.infinitytwogames.wispui.ui.base;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.ui.Rectangle;
import org.infinitytwogames.wispui.ui.base.animations.Updatable;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.infinitytwogames.wispui.ui.base.layout.Scene;

import java.util.concurrent.atomic.AtomicInteger;

import static org.joml.Math.clamp;
import static org.joml.Math.lerp;

/**
 * A smooth, animated progress bar component.
 * <p>
 * Unlike a standard progress bar, this component uses Linear Interpolation (LERP)
 * within its {@link #update(float)} method to transition the bar's width smoothly
 * toward the target value, providing a high-quality "filling" effect.
 * </p>
 * * <h2>Animation Logic</h2>
 * <p>
 * The bar's width is calculated every frame using:
 * <code>lerp(currentWidth, targetWidth, delta * speed)</code>.
 * This allows the bar to react dynamically to changes in progress while
 * maintaining visual continuity.
 * </p>
 *
 * @author Infinity Two Games
 */
public class ProgressBar extends UI implements Updatable {
    protected AtomicInteger total = new AtomicInteger(100);
    protected AtomicInteger current = new AtomicInteger(0);
    protected Rectangle bar;
    
    protected int percentage = 0;
    protected float speed = 10;
    
    public ProgressBar(Scene renderer, int max) {
        super(renderer.getRenderer());
        bar = new Rectangle(renderer.getRenderer());
        total.set(max);
        
        bar.setBackgroundColor(new RGBA(1, 0, 1f, 1));
        bar.setPosition(new Anchor(0, 0.5f), new Pivot(0, 0.5f));
        bar.setParent(this);
    }
    
    public ProgressBar(Scene renderer) {
        super(renderer.getRenderer());
        bar = new Rectangle(renderer.getRenderer());
        
        bar.setBackgroundColor(new RGBA(1, 0, 1f, 1));
        bar.setPosition(new Anchor(0, 0.5f), new Pivot(0, 0.5f));
        bar.setParent(this);
    }
    
    public static ProgressBarBuilder builder(Scene renderer, int max) {
        return new ProgressBarBuilder(renderer, max);
    }
    
    /**
     * Sets the fill color of the progress bar.
     *
     * @param color
     *         The RGBA color data.
     */
    public void setColor(RGBA color) {
        bar.setBackgroundColor(color);
    }
    
    /**
     * Sets the fill color of the progress bar.
     */
    public void setColor(float r, float g, float b, float a) {
        bar.setBackgroundColor(r, g, b, a);
    }
    
    public int getTotal() {
        return total.get();
    }
    
    public void setTotal(int total) {
        this.total.set(total);
    }
    
    public int getCurrent() {
        return current.get();
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setCurrent(int current) {
        if (current <= total.get()) this.current.set(current);
        else this.current.set(total.get());
    }
    
    public void incrementCurrent() {
        if (current.get() + 1 > total.get()) return;
        current.incrementAndGet();
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        bar.setHeight(height);
    }
    
    @Override
    public void draw() {
        super.draw();
        bar.draw();
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
    
    /**
     * Updates the animated width of the progress bar.
     * <p>
     * Calculates the percentage and the target width based on the virtual coordinate
     * space, then interpolates the actual width of the {@link #bar} component.
     * </p>
     *
     * @param delta
     *         The time elapsed since the last frame (in seconds).
     */
    @Override
    public void update(float delta) {
        percentage = (int) (((float) current.get() / total.get()) * 100);
        float targetWidth = width * ((float) current.get() / total.get());
        bar.setWidth((int) clamp(lerp(bar.getWidth(), targetWidth, delta * speed), 0, width));
    }
    
    public static class ProgressBarBuilder extends UIBuilder<ProgressBar> {
        public ProgressBarBuilder(Scene renderer, int max) {
            super(new ProgressBar(renderer, max));
        }
        
        public ProgressBarBuilder max(int max) {
            ui.setTotal(max);
            return this;
        }
        
        public ProgressBarBuilder current(int current) {
            ui.setCurrent(current);
            return this;
        }
        
        public ProgressBarBuilder bar(Rectangle.RectangleBuilder builder) {
            ui.bar = builder.build();
            return this;
        }
        
        @Override
        public ProgressBarBuilder applyDefault() {
            return this;
        }
    }
}

