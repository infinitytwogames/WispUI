package org.infinitytwogames.whispui.data.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class MSDFParam implements TextureParameter {
    private static final MSDFParam param = new MSDFParam();
    
    public static MSDFParam get() {
        return param;
    }
    
    private MSDFParam() {}
    
    @Override
    public void apply() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }
    
    @Override
    public void generateMipmap() {
    
    }
    
    @Override
    public boolean flipTexture() {
        return false;
    }
}
