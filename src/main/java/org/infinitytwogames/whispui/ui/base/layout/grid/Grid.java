package org.infinitytwogames.whispui.ui.base.layout.grid;

import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.UIBuilder;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.joml.Vector2i;

import java.util.*;

// MODIFIED
public class Grid extends UI implements Container {
    protected Map<UI, Cell> uis = new LinkedHashMap<>();
    protected List<UI> temp = Collections.synchronizedList(new ArrayList<>());
    protected int columns;
    protected int rows;
    protected int space;
    protected int padding;
    protected Vector2i cellSize = new Vector2i();
    protected boolean layoutDirty = true;

    public Grid(Scene renderer) {
        this(renderer.getRenderer());
    }
    
    public Grid(UIRenderer renderer) {
        super(renderer);
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getSpace() {
        return space;
    }

    public int getPadding() {
        return padding;
    }

    public UI get(int row, int column) {
        return get(row * columns + column);
    }

    public UI get(int index) {
        int i = 0;
        for (UI ui : uis.keySet()) {
            if (i++ == index)
                return ui;
        }
        return null;
    }
    
    public void updateSize() {
        int totalWidth = (columns * cellSize.x) + ((columns - 1) * space) + (padding * 2);
        int totalHeight = (rows * cellSize.y) + ((rows - 1) * space) + (padding * 2);
        super.setSize(totalWidth, totalHeight);
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        layoutDirty = true;
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
    
    }
    
    private List<UI> getTempUIs() {
        temp.clear();
        temp.addAll(uis.keySet());
        return temp;
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    
    }
    
    @Override
    public void onMouseHoverEnded() {
    
    }
    
    @Override
    public void cleanup() {
        for (UI ui : uis.keySet()) {
            ui.close();
        }
        
        uis.clear();
        temp.clear();
    }
    
    @Override
    public void draw() {
        if (layoutDirty) {
            System.out.println("Updating layout");
            layout();
            updateSize();
            layoutDirty = false;
        }
        
        super.draw(); // Draw the grid's background/border
        
        for (UI ui : uis.keySet()) {
            ui.draw();
        }
    }
    
    private void layout() {
        for (Map.Entry<UI, Cell> entry : uis.entrySet()) {
            UI ui = entry.getKey();
            Cell cell = entry.getValue();
            
            // Calculate dimensions
            int w = cellSize.x < 0 ? getWidth() / Math.max(1, columns) : cellSize.x;
            int h = cellSize.y < 0 ? getHeight() / Math.max(1, rows) : cellSize.y;
            
            ui.setSize(w, h);
            ui.setOffset(
                    (cell.x * (w + space)) + padding,
                    (cell.y * (h + space)) + padding
            );
        }
    }
    
    public int put(UI ui, int row, int column) {
        ui.setParent(this);
        layoutDirty = true;
        
        uis.put(ui, new Cell(column, row)); // column = x, row = y
        return uis.size() - 1;
    }
    
    public void add(UI ui) {
        int index = uis.size();
        int row = index / columns;
        int col = index % columns;
        put(ui, row, col);
        
        // Update rows count dynamically if needed
        this.rows = (int) Math.ceil((double) uis.size() / columns);
        this.layoutDirty = true;
    }

    public void setCellSize(int size) {
        setCellSize(size,size);
    }
    
    public void setCellSize(int width, int height) {
        cellSize.set(width,height);
    }
    
    public List<UI> getUIs() {
        return getTempUIs();
    }
    
    public void setPadding(int padding) {
        this.padding = padding;
        updateSize();
    }
    
    protected record Cell(int x, int y) { }

    public static class Builder extends UIBuilder<Grid> {

        public Builder(Scene scene) {
            super(new Grid(scene));
        }

        public Builder rows(int rows) {
            ui.rows = rows;
            return this;
        }

        public Builder columns(int columns) {
            ui.columns = columns;
            return this;
        }

        public Builder cellSize(int size) {
            ui.cellSize.set(size);
            return this;
        }

        public Builder margin(int margin) {
            ui.space = margin;
            return this;
        }

        public Builder padding(int padding) {
            ui.padding = padding;
            return this;
        }

        @Override
        public Builder applyDefault() {
            return this;
        }
        
        public Builder cellSize(int width, int height) {
            ui.setCellSize(width,height);
            return this;
        }
    }

    public void clearCells() {
        uis.clear();
    }
}
