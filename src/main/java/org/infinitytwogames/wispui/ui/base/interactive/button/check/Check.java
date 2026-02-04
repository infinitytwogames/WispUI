package org.infinitytwogames.wispui.ui.base.interactive.button.check;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.Rectangle;
import org.infinitytwogames.wispui.ui.base.UI;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// MODIFIED
public abstract class Check extends UI {
    protected Rectangle check;
    protected boolean checked;
    
    public Check(UIRenderer renderer) {
        super(renderer);
        
        check = new Rectangle(renderer);
        check.setParent(this);
        check.setBackgroundColor(RGBA.fromRGBA(3, 161, 252, 1));
        check.setAnchor(0.5f, 0.5f);
        check.setPivot(-0.5f, -0.5f); // Centers the inner box relative to the parent
        
        setBorderThickness(5);
    }
    
    public void setColor(RGBA color) {
        setColor(color.r(), color.g(), color.b(), color.a());
    }
    
    public void setColor(float r, float g, float b, float a) {
        check.setBackgroundColor(r,g,b,a);
    }
    
    public RGBA getColor() {
        return check.getBackgroundColor();
    }
    
    @Override
    public void draw() {
        // Dynamically calculate inner size if layout changed
        int margin = (int) (getWidth() * 0.2f); // 20% margin
        check.setSize(getWidth() - (margin * 2), getHeight() - (margin * 2));
        
        super.draw();
        if (isChecked()) check.draw();
    }
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        check.setWidth(width - 16);
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        check.setHeight(height - 16);
    }
    
    public boolean isChecked() {
        return checked;
    }
    
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        if (e.action == GLFW_RELEASE) {
            setChecked(!isChecked());
            onCheck(checked);
        }
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
    
    public abstract void onCheck(boolean check);
}
