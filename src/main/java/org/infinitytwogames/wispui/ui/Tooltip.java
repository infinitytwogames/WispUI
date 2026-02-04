package org.infinitytwogames.wispui.ui;

import org.infinitytwogames.wispui.data.Constants;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.event.state.WindowResizedEvent;
import org.infinitytwogames.wispui.renderer.FontRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.component.Scale;
import org.infinitytwogames.wispui.ui.base.component.Text;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.infinitytwogames.wispui.ui.base.layout.Scene;

public class Tooltip extends UI {
    protected Text text;
    protected Scale scale = new Scale(0.75f, 0.15f);
    protected FontRenderer fontRenderer;
    
    public Tooltip(Scene scene) {
        super(scene.getRenderer());
        
        fontRenderer = new FontRenderer(Constants.fontFilePath, 64);
        
        setBackgroundColor(0, 0, 0, 0.5f);
        
        setPosition(new Anchor(0.5f, 1), new Pivot(0.5f, 1));
        text = new Text(fontRenderer, scene);
        text.setPosition(new Anchor(0.5f, 0.5f), new Pivot(0.5f, 0.5f));
        text.setParent(this);
        
        scale.windowResize(new WindowResizedEvent(scene.getWindow()));
    }
    
    public void setText(String text) {
        this.text.setText(text);
    }
    
    public String getText() {
        return text.getText();
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
        fontRenderer.cleanup();
    }
    
    @Override
    public void draw() {
        setSize(scale.getWidth(), scale.getHeight());
        super.draw();
    }
}
