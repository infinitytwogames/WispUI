package org.infinitytwogames.whispui.data.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class DefaultParam implements TextureParameter {
    private static final DefaultParam param = new DefaultParam();
    
    public static DefaultParam get() {
        return param;
    }
    
    private DefaultParam() {}
    
    @Override
    public void apply() {
        // Set texture wrapping parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // S coordinate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // T coordinate
        
        // Set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Mipmap filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // Magnification filtering
    }
    
    @Override
    public void generateMipmap() {
        glGenerateMipmap(GL_TEXTURE_2D); // Generate mipmaps for optimized rendering at different distances
    }
    
    @Override
    public boolean flipTexture() {
        return false;
    }
}
