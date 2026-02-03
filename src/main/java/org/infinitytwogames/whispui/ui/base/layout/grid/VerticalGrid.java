package org.infinitytwogames.whispui.ui.base.layout.grid;


import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.layout.Scene;

import static java.lang.Math.max;

public class VerticalGrid extends Grid {
    protected int current;
    protected int maxWidth;
    
    public VerticalGrid(Scene scene) {
        super(scene);
        columns = 1;
    }
    
    public void put(UI ui) {
        put(ui, current++, 0);
        maxWidth = max(ui.getWidth(), maxWidth);
        ui.setWidth(maxWidth);
    }
    
    @Override
    public void draw() {
        if (layoutDirty) {
            // Calculate total required height and track max width
            int currentY = padding;
            for (UI ui : uis.keySet()) {
                // If cellSize.y is 0, use auto-height
                int h = (cellSize.y <= 0) ? ui.getHeight() : cellSize.y;
                
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
