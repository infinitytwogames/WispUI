package org.infinitytwogames.whispui.ui.base.component;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.data.texture.Texture;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;

// MODIFIED
public class Image extends UI implements Component {
    protected Texture texture;
    protected RGBA tint = new RGBA(1,1,1,1);
    
    public Image(UIRenderer renderer) {
        super(renderer);
    }
    
    public void setForegroundColor(float r, float g, float b, float a) {
        tint.set(r, g, b, a);
    }
    
    public RGBA setForegroundColor(RGBA color) {
        return tint.set(color);
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    @Override
    public void draw() {
        if (texture == null) return;
        renderer.queueTextureDirect(
                texture,
                tint,
                this
        );
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
    
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    
    }
    
    @Override
    public void onMouseHoverEnded() {
    
    }
    
    @Override
    public void cleanup() {
        if (texture == null) return;
        texture.cleanup();
    }
    
    @Override
    public Component copy() {
        return new Image(renderer);
    }
}
