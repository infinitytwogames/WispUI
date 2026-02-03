package org.infinitytwogames.whispui.ui.base.interactive.button;


import org.infinitytwogames.whispui.VectorMath;
import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public abstract class BasicButton extends UI {
    protected RGBA original = backgroundColor.copy();
    
    public BasicButton(UIRenderer renderer) {
        super(renderer);
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        if (e.action == GLFW_RELEASE) {
            if (VectorMath.isPointWithinRectangle(getPosition(),e.x,e.y, getEndPoint())) {
                clicked(e);
            }
        }
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        super.setBackgroundColor(
                original.r() - 0.25f,
                original.g() - 0.25f,
                original.b() - 0.25f,
                original.a()
        );
    }
    
    @Override
    public void onMouseHoverEnded() {
        backgroundColor.set(original);
    }
    
    @Override
    public void cleanup() {
    
    }
    
    public abstract void clicked(MouseButtonEvent e);
}
