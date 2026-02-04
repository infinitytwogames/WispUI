package org.infinitytwogames.wispui.ui.base.layout.grid;


import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.layout.Scene;
import org.infinitytwogames.wispui.ui.base.layout.menu.scroll.ScrollableMenu;

import static java.lang.Math.max;

/**
 * A specialized grid layout that organizes UI elements in a single vertical column.
 * <p>
 * The {@code VerticalGrid} automatically handles row incrementing and height
 * calculations. It supports "Auto-Height" logic, where rows can either be a
 * fixed size or adapt to the height of the individual UI components.
 * </p>
 *
 *
 *
 * <h2>Key Features</h2>
 * <ul>
 * <li><b>Auto-Flow:</b> Adding a UI element via {@link #put(UI)} automatically
 * places it in the next available row.</li>
 * <li><b>Width Stretching:</b> Tracks the widest element in the grid and can
 * optionally stretch all children to match that width.</li>
 * <li><b>Dynamic Resizing:</b> Updates its total height after every layout
 * change, ensuring compatibility with {@link ScrollableMenu}.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
public class VerticalGrid extends Grid {
    protected int current;
    protected int maxWidth;
    
    public VerticalGrid(Scene scene) {
        super(scene);
        columns = 1;
    }
    
    /**
     * Places a UI element into the next vertical row.
     * <p>
     * This method automatically handles the indexing and updates the
     * internal {@code maxWidth} to ensure the grid container spans correctly.
     * </p>
     *
     * @param ui
     *         The UI component to add.
     */
    public void put(UI ui) {
        put(ui, current++, 0);
        maxWidth = max(ui.getWidth(), maxWidth);
        ui.setWidth(maxWidth);
    }
    
    /**
     * Recalculates the positions of all children if the layout is marked as dirty.
     * <p>
     * If {@code cellSize.y} is set to 0, the grid will use the height of the
     * UI component itself plus spacing to determine the next Y-offset.
     * </p>
     */
    @Override
    public void draw() {
        if (layoutDirty) {
            // Calculate total required height and track max width
            int currentY = padding;
            for (UI ui : uis.keySet()) {
                // If cellSize.y is 0, use auto-height
                int h = (cellSize.y <= 0)? ui.getHeight() : cellSize.y;
                
                ui.setOffset(padding, currentY);
                // Optionally: ui.setWidth(maxWidth); // Stretch to fit
                
                currentY += h + space;
            }
            // Update grid total size so scrollbars/parents know how big we are
            super.setSize(maxWidth + (padding * 2), currentY + padding - space);
            layoutDirty = false;
        }
        
        super.draw();
        uis.keySet().forEach(UI::draw);
    }
}
