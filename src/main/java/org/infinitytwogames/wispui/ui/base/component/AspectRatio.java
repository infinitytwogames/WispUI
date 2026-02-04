package org.infinitytwogames.wispui.ui.base.component;

import org.infinitytwogames.wispui.Display;
import org.infinitytwogames.wispui.event.SubscribeEvent;
import org.infinitytwogames.wispui.event.bus.EventBus;
import org.infinitytwogames.wispui.event.state.WindowResizedEvent;
import org.infinitytwogames.wispui.ui.base.UI;
import org.joml.Vector2i;

/**
 * A layout component that enforces a fixed width-to-height ratio for its parent UI.
 * <p>
 * The {@code AspectRatio} component listens for window resize events and
 * automatically calculates the largest possible dimensions for a UI element
 * that fit within the available screen space while preserving the target ratio.
 * </p>
 *
 *
 *
 * <h2>Scaling Logic</h2>
 * <ul>
 * <li><b>Width-Limited:</b> If the window is relatively tall, the UI expands to
 * fill the width, adding horizontal black bars (letterboxing).</li>
 * <li><b>Height-Limited:</b> If the window is relatively wide, the UI expands to
 * fill the height, adding vertical black bars (pillarboxing).</li>
 * <li><b>Automatic Centering:</b> Calculates an {@code offset} vector to keep
 * the UI perfectly centered in the window.</li>
 * </ul>
 *
 * @author InfinityTwo Games
 */
public class AspectRatio implements Component {
    protected int width;
    protected int height;
    protected UI parent;

    protected float defaultWidth;
    protected float defaultHeight;
    protected float ratio; // Calculated as defaultWidth / defaultHeight
    protected Vector2i offset = new Vector2i(0, 0); // Initialize offset

    /**
     * Constructs an AspectRatio component using the provided dimensions to establish the fixed aspect ratio.
     * @param defaultWidth The original width, used to calculate the ratio.
     * @param defaultHeight The original height, used to calculate the ratio.
     */
    public AspectRatio(int defaultWidth, int defaultHeight) {
        // Store as floats for calculation consistency
        this.defaultHeight = (float) defaultHeight;
        this.defaultWidth = (float) defaultWidth;

        EventBus.connect(this);

        // Initial ratio calculation and dimension update
        recalculateRatio();
        updateDimensions();
    }

    /**
     * Centralized function to calculate the ratio and handle invalid dimensions.
     * This ensures robust ratio calculation, preventing division by zero or negative dimensions.
     */
    private void recalculateRatio() {
        if (this.defaultWidth <= 0 || this.defaultHeight <= 0) {
            System.err.println("AspectRatio component dimensions must be positive. Using 1:1 ratio.");
            this.ratio = 1.0f; // Default to 1:1 square ratio if input is invalid
        } else {
            // Calculate the fixed ratio (W / H)
            this.ratio = this.defaultWidth / this.defaultHeight;
        }
    }

    // --- Core Aspect Ratio Logic ---

    /**
     * Calculates and applies the dimensions to maintain the fixed aspect ratio.
     * This logic determines whether the window's width or height is the limiting factor.
     */
    private void updateDimensions() {
        if (this.ratio <= 0) {
            // Safety check: if the ratio is invalid (e.g., set to 0), do nothing.
            return;
        }

        // Get the available space (the Display dimensions)
        final float availableWidth = Display.width;
        final float availableHeight = Display.height;

        // 1. Calculate the height required if we use the full available width.
        // Height = width / ratio (ratio = W/H)
        float heightByWidth = availableWidth / this.ratio;

        if (heightByWidth <= availableHeight) {
            // Case 1 (Width-Limited): The calculated height fits. Use full available width.
            this.width = (int) availableWidth;
            this.height = (int) heightByWidth;

            // Calculate vertical offset to center (creating letterboxing).
            this.offset = new Vector2i(0, (int) ((availableHeight - this.height) / 2));
        } else {
            // Case 2 (Height-Limited): The calculated height is too tall. Use full available height.
            // Width = height * ratio (ratio = W/H)
            float widthByHeight = availableHeight * this.ratio;

            this.width = (int) widthByHeight;
            this.height = (int) availableHeight;

            // Calculate horizontal offset to center (creating pillarboxing).
            this.offset = new Vector2i((int) ((availableWidth - this.width) / 2), 0);
        }
        
        if (parent != null)
            parent.setSize(width,height);
    }

    // --- Event Handling ---

    @SubscribeEvent
    public void onWindowResize(WindowResizedEvent e) {
        // Recalculate dimensions every time the window size changes
        updateDimensions();
    }

    @Override
    public void draw() {
        // Drawing logic goes here. Ratio calculation happens in the constructor and setters.
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
        EventBus.disconnect(this);
    }
    
    @Override
    public void setParent(UI ui) {
    
    }
    
    @Override
    public Component copy() {
        return new AspectRatio((int) defaultWidth, (int) defaultHeight);
    }
    
    // --- Getters and Setters ---

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2i getOffset() {
        return offset;
    }

    public float getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * Updates the default width, recalculates the ratio, and updates dimensions immediately.
     */
    public void setDefaultWidth(float defaultWidth) {
        this.defaultWidth = defaultWidth;
        recalculateRatio();
        updateDimensions();
    }

    public float getDefaultHeight() {
        return defaultHeight;
    }

    /**
     * Updates the default height, recalculates the ratio, and updates dimensions immediately.
     */
    public void setDefaultHeight(float defaultHeight) {
        this.defaultHeight = defaultHeight;
        recalculateRatio();
        updateDimensions();
    }
}
