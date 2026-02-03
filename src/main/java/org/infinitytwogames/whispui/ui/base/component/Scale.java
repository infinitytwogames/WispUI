package org.infinitytwogames.whispui.ui.base.component;

import org.infinitytwogames.whispui.Display;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.state.WindowResizedEvent;
import org.infinitytwogames.whispui.ui.base.UI;

/**
 * Scale component to resize UI elements relative to their parent or the display.
 * <p>
 * xRatio and yRatio:
 * - If >= 0: scale relative to the parent's parent size (or Display size if no parent).
 * - If < 0: use the parent's own dimension directly.
 * <p>
 * Offsets are added in pixels after ratio scaling.
 */
public class Scale implements Component {
    protected UI parent;
    protected float xRatio, yRatio;
    protected int width, height;
    protected int offsetWidth, offsetHeight;
    
    public Scale(float xRatio, float yRatio) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        
        updateSize(); // initialize once
        EventBus.connect(this);
    }
    
    public Scale(float xRatio, float yRatio, UI ui) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.parent = ui;
        
        updateSize(); // initialize once
        EventBus.connect(this);
    }
    
    public void setOffset(int width, int height) {
        setOffsetHeight(height);
        setOffsetWidth(width);
    }
    
    public void setOffset(int same) {
        setOffset(same, same);
    }
    
    public int getOffsetWidth() {
        return offsetWidth;
    }
    
    public void setOffsetWidth(int offsetWidth) {
        this.offsetWidth = offsetWidth;
        updateSize();
    }
    
    public int getOffsetHeight() {
        return offsetHeight;
    }
    
    public void setOffsetHeight(int offsetHeight) {
        this.offsetHeight = offsetHeight;
        updateSize();
    }
    
    public UI getParent() {
        return parent;
    }
    
    public void setParent(UI parent) {
        this.parent = parent;
    }
    
    @Override
    public Scale copy() {
        return new Scale(xRatio, yRatio);
    }
    
    private void updateSize() {
        if (parent != null) {
            width = xRatio < 0
                    ? parent.getWidth()
                    : (parent.getParent() != null
                    ? (int) (parent.getParent().getWidth() * xRatio)
                    : (int) (Display.width * xRatio));
            
            height = yRatio < 0
                    ? parent.getHeight()
                    : (parent.getParent() != null
                    ? (int) (parent.getParent().getHeight() * yRatio)
                    : (int) (Display.height * yRatio));
            
            width += offsetWidth;
            height += offsetHeight;
            parent.setSize(width, height);
        } else {
            width = (int) (Display.width * xRatio);
            height = (int) (Display.height * yRatio);
            width += offsetWidth;
            height += offsetHeight;
        }
    }
    
    public void setRatios(float xRatio, float yRatio) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        updateSize();
    }
    
    @SubscribeEvent
    public void windowResize(WindowResizedEvent e) {
        updateSize();
    }
    
    @Override
    public void draw() {
        // Scale is a logical component; no drawing needed.
    }
    
    @Override
    public void setAngle(float angle) {
        // Not applicable for Scale component.
    }
    
    @Override
    public void setDrawOrder(int z) {
        // Not applicable for Scale component.
    }
    
    @Override
    public int getDrawOrder() {
        return 0;
    }
    
    @Override
    public void cleanup() {
        EventBus.disconnect(this);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public float getRatioY() {
        return yRatio;
    }
    
    public float getRatioX() {
        return xRatio;
    }
}
