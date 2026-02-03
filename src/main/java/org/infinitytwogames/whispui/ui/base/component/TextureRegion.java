package org.infinitytwogames.whispui.ui.base.component;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.data.texture.TextureAtlas;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.UIBuilder;

public class TextureRegion extends UI implements Component {
    protected int textureIndex;
    protected TextureAtlas atlas;
    protected RGBA foregroundColor = new RGBA();
    
    public TextureRegion(int textureIndex, TextureAtlas atlas, UIRenderer renderer) {
        super(renderer);
        this.textureIndex = textureIndex;
        this.atlas = atlas;
    }
    
    public RGBA getForegroundColor() {
        return foregroundColor;
    }
    
    public void setForegroundColor(RGBA foregroundColor) {
        this.foregroundColor = foregroundColor;
    }
    
    public int getTextureIndex() {
        return textureIndex;
    }
    
    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }
    
    public TextureAtlas getAtlas() {
        return atlas;
    }
    
    public void setAtlas(TextureAtlas atlas) {
        this.atlas = atlas;
    }
    
    @Override
    public void draw() {
        super.draw();
        
        if (atlas != null && textureIndex >= 0) {
            renderer.queueTextured(
                    getTextureIndex(), atlas, foregroundColor, this);
        }
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
    
    }
    
    @Override
    public Component copy() {
        return new TextureRegion(textureIndex, atlas, renderer);
    }
    
    public static class Builder extends UIBuilder<TextureRegion> {
        public Builder(UIRenderer renderer, TextureAtlas atlas, int index) {
            super(new TextureRegion(index, atlas, renderer));
        }
        
        @Override
        public UIBuilder<TextureRegion> applyDefault() {
            return this;
        }
    }
}
