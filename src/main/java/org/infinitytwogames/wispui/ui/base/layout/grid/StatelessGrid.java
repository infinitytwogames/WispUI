package org.infinitytwogames.wispui.ui.base.layout.grid;

import org.infinitytwogames.wispui.ui.base.UI;
import org.joml.Vector2i;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A mathematical layout manager that organizes UI elements into a fixed-column grid.
 * <p>
 * This class does not render pixels; it calculates and applies {@code offset} and
 * {@code size} values to its registered {@link UI} components. It is "stateless"
 * in the sense that it doesn't participate in the scene graph directly, but
 * rather manipulates the state of elements within it.
 * </p>
 *
 *
 *
 * <h2>Layout Algorithm</h2>
 * <p>
 * Elements are assigned a grid coordinate (column, row) based on their insertion order:
 * <ul>
 * <li><b>Row:</b> {@code index / columns}</li>
 * <li><b>Column:</b> {@code index % columns}</li>
 * </ul>
 * The pixel position is then derived by multiplying these coordinates by the
 * {@code cellSize} plus the {@code space} (gutter).
 * </p>
 *
 * @author InfinityTwo Games
 */
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
    
    private StatelessGrid() {
    }
    
    /**
     * Recomputes the pixel offsets and sizes for all registered UI elements.
     * <p>
     * This should be manually triggered after changing grid properties
     * (like columns or spacing) to refresh the visual layout.
     * </p>
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
    
    /**
     * Registers a UI element and automatically assigns it the next available
     * slot in the grid sequence.
     *
     * @param ui
     *         The element to manage.
     */
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
