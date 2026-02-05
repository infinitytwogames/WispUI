package org.infinitytwogames.wispui.ui.base.component;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.data.TextureAtlas;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.UIBuilder;

/**
 * A UI component used to display a specific sub-region (sprite) from a Texture Atlas.
 * <p>
 * TextureRegion allows for memory-efficient rendering by referencing a single large
 * texture sheet and using a {@code textureIndex} to determine which portion to draw.
 * It also supports a {@code foregroundColor} for tinting or overlay effects.
 * </p>
 *
 *
 *
 * <h2>Key Features</h2>
 * <ul>
 * <li><b>Atlas Integration:</b> Links directly to a {@link TextureAtlas} for batch-optimized rendering.</li>
 * <li><b>Foreground Tinting:</b> Uses an {@link RGBA} color to apply masks or highlights
 * without modifying the original source texture.</li>
 * <li><b>Component Pattern:</b> Implements {@code copy()} for easy duplication
 * within complex UI hierarchies or template systems.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
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
