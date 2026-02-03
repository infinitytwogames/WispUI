package org.infinitytwogames.whispui.ui.base.layout.menu.scroll;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseScrollEvent;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.animations.Animation;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Pivot;
import org.infinitytwogames.whispui.ui.base.layout.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.infinitytwogames.whispui.manager.SceneManager.propagateMouseClick;

// MODIFIED
public class ScrollableMenu extends UI implements Container {
    protected List<UI> uis = new ArrayList<>();
    protected int padding = 5;
    protected int contentHeight;
    protected ScrollButton scrollButton;
    protected Scene scene;
    protected int scrollY;
    protected float sensitivity;
    protected float targetScrollY;
    protected float scrollLerpSpeed = 50f; // Higher = faster/snappier, Lower = smoother/slower
    
    private final ArrayList<UI> handle = new ArrayList<>();
    
    // Update setScrollY to only handle the target
    public void setTargetScrollY(float target) {
        int maxScrollDistance = Math.max(0, contentHeight - height);
        // Clamp target between -maxScrollDistance and 0
        this.targetScrollY = Math.max(-maxScrollDistance, Math.min(0, target));
    }
    
    public ScrollableMenu(Scene scene, Window window) {
        super(scene.getRenderer());
        this.scene = scene;
        
        scrollButton = new ScrollButton(scene.getRenderer(),this,window);
        scrollButton.setParent(this);
        scrollButton.setDrawOrder(drawOrder+2);
        scrollButton.setPosition(new Anchor(1,0),new Pivot(1,0));
        
        handle.add(scrollButton);
        EventBus.connect(this);
        
        setupScrollbar();
    }
    
    public void addUI(UI ui) {
        ui.setParent(this);
        uis.add(ui);
        update();
        
        contentHeight = Math.max(ui.getHeight()+ui.getPosition().y()+padding,contentHeight);
        ui.setDrawOrder(drawOrder+ui.getDrawOrder()+1);
        
        Collections.sort(uis);
        setupScrollbar();
    }
    
    private void update() {
        for (UI ui : uis) {
            if (!handle.contains(ui)) {
                handle.add(ui);
            }
        }
    }
    
    public void clear() {
        for (UI ui : uis) ui.close();
        uis.clear();
        contentHeight = 0;
        setupScrollbar();
    }
    
    public void removeUI(UI ui) {
        uis.remove(ui);
        handle.remove(ui);
        
        contentHeight = 0;
        for (UI u : uis) {
            contentHeight = Math.max(u.getHeight() + u.getPosition().y() + padding, contentHeight);
        }
        
        Collections.sort(uis);
        setupScrollbar();
    }
    
    public void removeAll(Collection<? extends UI> collection) {
        uis.removeAll(collection);
    }
    
    public float getSensitivity() {
        return sensitivity;
    }
    
    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
    
    @Override
    public void draw() {
        if (isHidden()) return;
        // Smoothly interpolate scrollY towards targetScrollY
        // Formula: current = current + (target - current) * speed * delta
        if (Math.abs(targetScrollY - scrollY) > 0.1f) {
            // Use a float for internal scroll calculation to keep it smooth,
            // but cast to int for rendering/clamping if necessary
            this.scrollY = (int) Animation.linear(scrollY, targetScrollY, scrollLerpSpeed, scene.getDelta());
            
            // Update scroll button handle while moving
            if (scrollButton != null) {
                scrollButton.updateHandlePosition(Math.abs(this.scrollY));
            }
        } else {
            this.scrollY = (int) targetScrollY;
        }
        
        if (contentHeight >= getHeight()) scrollButton.setHidden(false);
        
        super.draw();
        
        renderer.enableScissor(getPosition(), width, height);
        for (UI ui : uis) {
            ui.addOffset(0, scrollY);
            ui.draw();
            ui.addOffset(0, -scrollY);
        }
        renderer.disableScissor();
        
        scrollButton.draw();
    }
    
    @Override
    public void setDrawOrder(int drawOrder) {
        super.setDrawOrder(drawOrder);
        
        for (UI ui : uis) ui.setDrawOrder(drawOrder+1);
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        scrollButton.setHeight(height);
        
        setupScrollbar();
    }
    
    public List<UI> getUIs() {
        return handle;
    }
    
    public int getPadding() {
        return padding;
    }
    
    public void setPadding(int padding) {
        this.padding = padding;
    }
    
    public ScrollButton getScrollButton() {
        return scrollButton;
    }
    
    public void setScrollButton(ScrollButton scrollButton) {
        this.scrollButton = scrollButton;
    }
    
    public int getContentHeight() {
        return contentHeight;
    }
    
    public int getScrollY() {
        return scrollY;
    }
   
    private void setupScrollbar() {
        // 1. Determine total track height (which is the menu's height)
        int trackHeight = this.height;
        
        // 2. Calculate the total scrollable distance
        int maxScrollDistance = contentHeight - trackHeight;
        
        // 3. Handle visibility: Hide the scrollbar if the content fits entirely.
        if (maxScrollDistance <= 0) {
            scrollButton.setHidden(true);
            this.scrollY = 0; // Ensure scroll is reset if content shrinks
            return;
        }
        
        scrollButton.setHidden(false);
        
        // 4. Calculate the proportional handle height (clamped to prevent huge handles)
        float heightRatio = (float) trackHeight / contentHeight;
        int handleHeight = (int) (trackHeight * heightRatio);
        
        // Minimum handle height to ensure it's always draggable (e.g., 20 virtual units)
        int minHandleHeight = 20;
        
        int finalHandleHeight = Math.max(minHandleHeight, handleHeight);
        
        // 5. Apply the calculated height to the draggable button
        scrollButton.setScrollHeight(finalHandleHeight);
        
        // Also update the position in case content height changed
        scrollButton.updateHandlePosition(this.scrollY);
    }
    
    @SubscribeEvent
    public void onMouseScroll(MouseScrollEvent e) {
        if (!isHovering()) return;
        
        
        // Most mice return 1.0 or -1.0. Multiply by a 'step' size (e.g., 30 pixels)
        int scrollStep = (int) (30 * sensitivity);
        int scrollAmount = e.y * scrollStep;
        
        // To scroll DOWN, we want ScrollY to become more NEGATIVE
        // To scroll UP, we want ScrollY to become more POSITIVE (closer to 0)
        this.setTargetScrollY(this.scrollY + scrollAmount);
    }
    
    public void setScrollY(int scrollY) {
        // ContentHeight - Height gives us the total distance content can move
        // This distance should be represented as a negative limit
        int maxScrollDistance = Math.max(0, contentHeight - height);
        
        // Clamp between -maxScrollDistance (bottom) and 0 (top)
        this.scrollY = Math.max(-maxScrollDistance, Math.min(0, scrollY));
        
        if (scrollButton != null) {
            // Pass the absolute value to the button for visual positioning
            scrollButton.updateHandlePosition(Math.abs(this.scrollY));
        }
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        // 1. Check the scrollbar handle first (it's the top-most layer)
        if (scrollButton.isHovering()) {
            scrollButton.onMouseClicked(e);
            return;
        }
        
        // 2. Otherwise, propagate to the mod cards
        propagateMouseClick(e, uis);
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    }
    
    @Override
    public void onMouseHoverEnded() {
        for (UI ui : uis) ui.onMouseHoverEnded();
    }
    
    @Override
    public void cleanup() {
        for (UI ui : uis) ui.close();
    }
    
    public <U extends UI> void addAll(Collection<U> collection) {
        for (U ui : collection) addUI(ui);
    }
}
