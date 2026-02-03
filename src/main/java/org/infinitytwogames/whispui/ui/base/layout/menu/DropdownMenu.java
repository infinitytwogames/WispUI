package org.infinitytwogames.whispui.ui.base.layout.menu;

import org.infinitytwogames.whispui.Display;
import org.infinitytwogames.whispui.VectorMath;
import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.manager.SceneManager;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.interactive.button.Button;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.infinitytwogames.whispui.ui.base.layout.grid.StatelessGrid;
import org.infinitytwogames.whispui.ui.base.layout.menu.scroll.ScrollableMenu;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// MODIFIED
public abstract class DropdownMenu extends Button implements Container {
    protected final Scene scene;
    protected final StatelessGrid grid;
    protected ScrollableMenu menu;
    protected Map<String, Button> selections = new ConcurrentHashMap<>();
    protected String selected;
    
    private final List<UI> temp;
    
    public DropdownMenu(Scene scene, String path, Window window) {
        super(scene, path, "> None");
        menu = new ScrollableMenu(scene, window);
        temp = List.of(menu);
        
        tip = "None";
        this.scene = scene;
        
        menu.setParent(this);
        menu.setAnchor(new Anchor(0,1));
        menu.setDrawOrder(100);
        menu.setHidden(true);
        
        grid = new StatelessGrid(1, 0, 16, new Vector2i());
        
        EventBus.connect(this);
    }
    
    public void addOption(String selection) {
        Button button = new Button(scene, path, selection) {
            @Override
            public void click(MouseButtonEvent e) {
                System.out.println(selection);
                select(selection);
            }
        };
        button.setSize(getWidth(), getHeight());
        button.setColor(1,1,1,1);
        button.setDrawOrder(menu.getDrawOrder()+1);
        
        grid.add(button);
        menu.addUI(button);
        selections.put(selection, button);
    }
    
    public void select(String selection) {
        Button button = selections.get(selection);
        if (button != null) {
            menu.setHidden(true);
            selected = selection;
            setText(selection);
            onSelect(selected);
        }
    }
    
    public void setMenuHeight(int height) {
        menu.setHeight(height);
    }
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        menu.setWidth(width);
        grid.setCellSizeX(width);
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        grid.setCellSizeY(height);
    }
    
    @Override
    public void draw() {
        super.draw();
        menu.draw();
    }
    
    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        super.setBackgroundColor(r, g, b, a);
        menu.setBackgroundColor(r,g,b,a);
    }
    
    @Override
    public void setBorderColor(float r, float g, float b, float a) {
        super.setBorderColor(r, g, b, a);
        menu.setBorderColor(r,g,b,a);
    }
    
    @Override
    public void setBorderThickness(float borderThickness) {
        super.setBorderThickness(borderThickness);
        menu.setBorderThickness(borderThickness);
    }
    
    @Override
    public void setCornerRadius(float cornerRadius) {
        super.setCornerRadius(cornerRadius);
        menu.setCornerRadius(cornerRadius);
    }
    
    @SubscribeEvent
    public void onMouseClickGlobal(MouseButtonEvent e) {
        Vector2i point = Display.transformWindowToVirtual(e.window, e.x, e.y);
        if (
                !VectorMath.isPointWithinRectangle(getPosition(), point, getEndPoint())
                && (!VectorMath.isPointWithinRectangle(menu.getPosition(), point, menu.getEndPoint()))
        ) {
            menu.setHidden(true);
        }
    }
    
    @Override
    public Vector2i getLastDrawEndPoint() {
        // If the menu is closed, use the normal button bounds
        if (menu.isHidden()) {
            return super.getLastDrawEndPoint();
        }
        
        // If open, the clickable area extends to the bottom of the menu
        return new Vector2i(
                super.getEndPoint().x,
                menu.getLastDrawPosition().y + menu.getHeight()
        );
    }
    
    @Override
    public void click(MouseButtonEvent e) {
        // 1. Convert click to virtual coordinates
        Vector2i point = Display.transformWindowToVirtual(e.window, e.x, e.y);
        
        // 2. Check: Is the click inside the open menu area?
        if (!menu.isHidden() && VectorMath.isPointWithinRectangle(menu.getLastDrawPosition(), point, menu.getLastDrawEndPoint())) {
            // Let the Scene Manager's recursion handle the children inside 'menu'
            // Do NOT toggle hidden here.
            SceneManager.propagateMouseClick(e, menu.getUIs()); // I added this and somehow didn't work...
            return;
        }
        
        // 3. Otherwise, if the click is on the main button face, toggle the menu
        if (e.action == GLFW_RELEASE) {
            menu.setHidden(!menu.isHidden());
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        menu.close();
    }
    
    @Override
    public List<UI> getUIs() {
        return temp;
    }
    
    public abstract void onSelect(String selected);
    
    public void clear() {
        grid.clear();
        menu.clear(); // This is correct, because this calls close() on UIs
        selections.clear();
    }
}
