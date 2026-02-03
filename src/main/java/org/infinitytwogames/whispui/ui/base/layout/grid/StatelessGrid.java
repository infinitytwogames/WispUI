package org.infinitytwogames.whispui.ui.base.layout.grid;

import org.infinitytwogames.whispui.ui.base.UI;
import org.joml.Vector2i;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// MODIFIED
public class StatelessGrid {
    protected Map<UI, Vector2i> uis = new LinkedHashMap<>(); // UI mapped to Grid Coordinates (Col, Row)
    protected int columns = 1;
    protected int space;
    protected int padding;
    protected Vector2i cellSize = new Vector2i();
    
    public StatelessGrid(int columns, int space, int padding, Vector2i cellSize) {
        this.columns = Math.max(1, columns);
        this.space = space;
        this.padding = padding;
        this.cellSize = cellSize;
    }
    
    private StatelessGrid() {}
    
    /**
     * The heart of the manager: It just sets the positions.
     * Call this whenever the window resizes or items are added.
     */
    public void updateLayout() {
        for (Map.Entry<UI, Vector2i> entry : uis.entrySet()) {
            UI ui = entry.getKey();
            Vector2i gridPos = entry.getValue(); // x = col, y = row
            
            int xOffset = padding + (gridPos.x * (cellSize.x + space));
            int yOffset = padding + (gridPos.y * (cellSize.y + space));
            
            ui.setSize(cellSize.x, cellSize.y);
            ui.setOffset(xOffset, yOffset);
        }
    }
    
    public void add(UI ui) {
        int index = uis.size();
        int row = index / columns;
        int col = index % columns;
        uis.put(ui, new Vector2i(col, row));
        updateLayout();
    }
    
    public void clear() {
        uis.clear();
    }
    
    public int getColumns() {
        return columns;
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
    }
    
    public int getSpace() {
        return space;
    }
    
    public void setSpace(int space) {
        this.space = space;
    }
    
    public int getPadding() {
        return padding;
    }
    
    public void setPadding(int padding) {
        this.padding = padding;
    }
    
    public Vector2i getCellSize() {
        return cellSize;
    }
    
    public void setCellSize(Vector2i cellSize) {
        this.cellSize.set(cellSize);
    }
    
    public Set<UI> getUIs() {
        return uis.keySet();
    }
    
    public void setCellSizeX(int width) {
        cellSize.x = width;
    }
    
    public void setCellSizeY(int height) {
        cellSize.y = height;
    }
    
    public static class Builder {
        private final StatelessGrid manager = new StatelessGrid();
        
        public Builder columns(int columns) {
            manager.columns = columns;
            return this;
        }
        
        public Builder cellSize(int size) {
            manager.cellSize.set(size);
            return this;
        }
        
        public Builder cellSize(int width, int height) {
            manager.cellSize.set(width, height);
            return this;
        }
        
        public Builder margin(int margin) {
            manager.space = margin;
            return this;
        }
        
        public Builder padding(int padding) {
            manager.padding = padding;
            return this;
        }
        
        public StatelessGrid build() {
            return manager;
        }
    }
}
