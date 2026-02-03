package org.infinitytwogames.whispui.ui.base.interactive.button;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.ui.base.layout.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// MODIFIED
public abstract class ToggleButton extends Button {
    protected boolean toggle;
    protected RGBA color;
    
    public ToggleButton(Scene renderer, String path, String s) {
        super(renderer, path, s);
        color = new RGBA(getBackgroundColor());
    }
    
    @Override
    public void click(MouseButtonEvent e) {
        if (e.action != GLFW_RELEASE) {
            setToggle(!toggle);
            onToggle(toggle);
        }
    }
    
    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        super.setBackgroundColor(r, g, b, a);
    }
    
    public abstract void onToggle(boolean toggle);
    
    public boolean isToggled() {
        return toggle;
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        updateVisuals(true);
    }
    
    @Override
    public void onMouseHoverEnded() {
        updateVisuals(false);
    }
    
    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        updateVisuals(false); // Update to current toggle state
    }
    
    private void updateVisuals(boolean isHovering) {
        float d = 0.0f;
        if (toggle) d += 0.25f;      // Darken for toggle
        if (isHovering) d += 0.15f;  // Darken slightly more for hover
        
        backgroundColor.set(
                Math.max(0, original.r() - d),
                Math.max(0, original.g() - d),
                Math.max(0, original.b() - d),
                original.a()
        );
    }
}
