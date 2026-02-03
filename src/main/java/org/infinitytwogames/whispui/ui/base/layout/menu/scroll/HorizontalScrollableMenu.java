package org.infinitytwogames.whispui.ui.base.layout.menu.scroll;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseCoordinatesEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseScrollEvent;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Pivot;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import java.util.ArrayList;

import static org.infinitytwogames.whispui.manager.SceneManager.propagateMouseClick;
import static org.infinitytwogames.whispui.manager.SceneManager.propagateMouseHover;

// MODIFIED
public class HorizontalScrollableMenu extends UI {
    
    protected ArrayList<UI> uis = new ArrayList<>();
    protected int padding = 5;
    protected int contentWidth;
    protected HorizontalScrollButton scrollButton;
    protected Scene scene;
    protected int scrollX;
    
    public HorizontalScrollableMenu(Scene scene, Window window) {
        super(scene.getRenderer());
        this.scene = scene;
        
        scrollButton = new HorizontalScrollButton(scene.getRenderer(), this, window);
        scrollButton.setParent(this);
        scrollButton.setDrawOrder(drawOrder + 2);
        scrollButton.setPosition(new Anchor(0, 1), new Pivot(0, 1)); // bottom-left
        
        EventBus.connect(this);
        setupScrollbar();
    }
    
    public void addUI(UI ui) {
        uis.add(ui);
        ui.setParent(this);
        
        contentWidth = Math.max(ui.getWidth() + ui.getPosition().x() + padding, contentWidth);
        ui.setDrawOrder(drawOrder + ui.getDrawOrder() + 1);
        
        setupScrollbar();
    }
    
    public void removeUI(UI ui) {
        uis.remove(ui);
        
        contentWidth = 0;
        for (UI u : uis) {
            contentWidth = Math.max(u.getWidth() + u.getPosition().x() + padding, contentWidth);
        }
        
        setupScrollbar();
    }
    
    @Override
    public void draw() {
        super.draw();
        renderer.enableScissor(getPosition(), width, height);
        
        for (UI ui : uis) {
            ui.addOffset(scrollX, 0);
            ui.draw();
            ui.addOffset(-scrollX, 0);
        }
        
        renderer.disableScissor();
    }
    
    @Override
    public void setDrawOrder(int drawOrder) {
        super.setDrawOrder(drawOrder);
        for (UI ui : uis) ui.setDrawOrder(drawOrder + 1);
    }
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        scrollButton.setWidth(width);
        setupScrollbar();
    }
    
    public int getContentWidth() {
        return contentWidth;
    }
    
    public int getScrollX() {
        return scrollX;
    }
    
    public void setScrollX(int scrollX) {
        int maxScrollDistance = Math.max(0, contentWidth - width);
        this.scrollX = Math.min(maxScrollDistance, scrollX);
        
        if (scrollButton != null) {
            scrollButton.updateHandlePosition(this.scrollX);
        }
    }
    
    private void setupScrollbar() {
        int trackWidth = this.width;
        int maxScrollDistance = contentWidth - trackWidth;
        
        if (maxScrollDistance <= 0) {
            scrollButton.setHidden(true);
            scrollX = 0;
            return;
        }
        
        scrollButton.setHidden(false);
        
        float widthRatio = (float) trackWidth / contentWidth;
        int handleWidth = (int) (trackWidth * widthRatio);
        
        int minHandleWidth = 20;
        scrollButton.setScrollWidth(Math.max(minHandleWidth, handleWidth));
        
        scrollButton.updateHandlePosition(scrollX);
    }
    
    @SubscribeEvent
    public void onMouseScroll(MouseScrollEvent e) {
        if (!hovering) return;
        
        int rawNewScrollX = this.scrollX - (int) (e.x * 0.5f);
        setScrollX(rawNewScrollX);
    }
    
    @SubscribeEvent
    public void onMouseMove(MouseCoordinatesEvent e) {
        if (isHovering()) {
            propagateMouseHover(new MouseHoverEvent(e), uis);
        }
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        propagateMouseClick(e,uis);
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    }
    
    @Override public void onMouseHoverEnded() {}
    
    @Override
    public void cleanup() {
        for (UI ui : uis) ui.close();
    }
}

