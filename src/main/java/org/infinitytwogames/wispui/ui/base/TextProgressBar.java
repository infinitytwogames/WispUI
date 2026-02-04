package org.infinitytwogames.wispui.ui.base;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.ui.Rectangle;
import org.infinitytwogames.wispui.ui.base.animations.Updatable;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.infinitytwogames.wispui.ui.base.layout.Scene;
import org.joml.Vector2i;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A specialized UI component that visually represents progress using a filling bar
 * and an overlayed percentage label.
 * <p>
 * This class inherits from {@link Label} to display percentage text and manages
 * an internal {@link Rectangle} to render the progress bar itself.
 * </p>
 * * <h2>Thread Safety</h2>
 * <p>
 * Progress values ({@code current} and {@code total}) are stored using {@link AtomicLong}.
 * This allows background threads (e.g., file loaders) to safely update progress
 * without blocking the main render thread.
 * </p>
 *
 * @author Infinity Two Games
 */
public class TextProgressBar extends Label implements Updatable {
    protected Rectangle bar;
    protected String text;
    
    protected int percentage = 0;
    
    protected AtomicLong total = new AtomicLong(100);
    protected AtomicLong current = new AtomicLong(0);
    
    // Update methods to use long
    public void setTotal(long total) {
        this.total.set(total);
    }
    
    public void setCurrent(long current) {
        this.current.set(Math.min(current, total.get()));
    }
    
    /**
     * Updates the internal percentage and formats the label text.
     * <p>
     * Logic: {@code percentage = (current / total) * 100}
     * </p>
     *
     * @param d
     *         Delta time since the last frame.
     */
    @Override
    public void update(float d) {
        if (total.get() > 0) {
            // Use double to avoid integer division issues
            percentage = (int) (((double) current.get() / total.get()) * 100);
        }
        super.setText(percentage + "%" + (text != null && !text.isEmpty()? " (" + text + ")" : ""));
    }
    
    public TextProgressBar(Scene renderer, String font) {
        super(renderer, font);
        bar = new Rectangle(renderer.getRenderer());
        
        super.setText("0%");
        setTextPosition(new Anchor(0.5f, 0.5f), new Pivot(0.5f, 0.5f), new Vector2i());
        
        bar.setBackgroundColor(new RGBA(1, 0, 1f, 1));
        bar.setPosition(new Anchor(0, 0.5f), new Pivot(0, 0.5f));
        bar.setParent(this);
    }
    
    @Override
    public RGBA getColor() {
        return bar.getBackgroundColor();
    }
    
    @Override
    public void setColor(RGBA color) {
        bar.setBackgroundColor(color);
    }
    
    @Override
    public void setColor(float r, float g, float b, float a) {
        bar.setBackgroundColor(r, g, b, a);
    }
    
    public TextProgressBar(Scene renderer, String path, int total) {
        this(renderer, path);
        this.total.set(total);
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public void setText(String text) {
        this.text = text;
    }
    
    public static ProgressBarBuilder builder(Scene renderer, String font) {
        return new ProgressBarBuilder(renderer, font);
    }
    
    public long getTotal() {
        return total.get();
    }
    
    public void setTotal(int total) {
        this.total.set(total);
    }
    
    public long getCurrent() {
        return current.get();
    }
    
    /**
     * Safely increments the current progress by 1.
     * Use this for granular tasks like "Files Processed".
     */
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
        bar.setWidth((int) (width * (percentage / 100f)));
        super.draw();
        bar.draw();
    }
    
    public static class ProgressBarBuilder extends UIBuilder<TextProgressBar> {
        public ProgressBarBuilder(Scene renderer, String font) {
            super(new TextProgressBar(renderer, font, 10));
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
