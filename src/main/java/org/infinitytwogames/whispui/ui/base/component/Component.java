package org.infinitytwogames.whispui.ui.base.component;

import org.infinitytwogames.whispui.ui.base.UI;

// MODIFIED
public interface Component {
    void draw();
    void setAngle(float angle);
    void setDrawOrder(int z);
    int getDrawOrder();
    void cleanup();
    
    void setParent(UI ui);
    
    Component copy();
}
