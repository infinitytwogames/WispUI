package org.infinitytwogames.whispui.ui.base.layout.menu.scroll;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.manager.SceneManager;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.interactive.button.ToggleButton;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.infinitytwogames.whispui.ui.base.layout.grid.Grid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// MODIFIED
public class TabMenu extends UI {
    protected Map<String, Tab> buttons = new HashMap<>();
    protected Map<String, ScrollableMenu> screen = new HashMap<>();
    protected String current;
    protected String fontPath;
    protected final Scene scene;
    protected String defaultTab;
    protected final Window window;
    private final Grid grid;
    private final HorizontalScrollableMenu tabMenu;
    private int currentColumn = 0;
    
    public TabMenu(Scene scene, String fontPath, Window window, int sizeX, int sizeY) {
        super(scene.getRenderer());
        this.fontPath = fontPath;
        this.scene = scene;
        this.window = window;
        int offset = 16;
        
        grid = new Grid.Builder(scene)
                .cellSize(sizeX, sizeY)
                .rows(1)
                .build()
                ;
        
        tabMenu = new HorizontalScrollableMenu(scene, window);
        tabMenu.addUI(grid);
        tabMenu.setParent(this);
        tabMenu.setAnchor(0, 0);
        tabMenu.setPivot(0, 0);
        tabMenu.setSize(sizeX + offset, sizeY);
        tabMenu.setAnchor(0, 0);
        tabMenu.setPivot(0, 0);
    }
    
    public String addTab(String name) {
        Tab tab = new Tab(this, name);
        String id = tab.id;
        if (defaultTab == null) {
            defaultTab = id;
            tab.setToggle(true);
        }
        
        grid.put(tab, 0, currentColumn);
        
        ScrollableMenu content = new ScrollableMenu(scene, window);
        content.setWidth(tabMenu.getWidth());
        content.setHeight(height-tabMenu.getHeight());
        
        buttons.put(id, tab);
        screen.put(id, content);
        currentColumn++;
        return id;
    }
    
    protected void selectTab(String id) {
        buttons.values().forEach(tab -> {
            if (!tab.id.equals(id)) tab.setToggle(false);
        });
        
        current = id;
    }
    
    public void put(UI ui, String id) {
        ScrollableMenu menu = screen.get(id);
        if (menu == null) throw new NullPointerException("Couldn't find for tab with uuid \""+id+"\"");
        menu.addUI(ui);
    }
    
    @Override
    public void draw() {
        super.draw();
        
        tabMenu.draw();
        
        current = current != null? current : defaultTab;
        screen.get(current).getUIs().forEach(UI::draw);
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        tabMenu.onMouseClicked(e);
        SceneManager.propagateMouseClick(e, screen.get(current).getUIs());
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        tabMenu.onMouseHover(e);
    }
    
    @Override
    public void onMouseHoverEnded() {
        tabMenu.onMouseHoverEnded();
    }
    
    @Override
    public void cleanup() {
        tabMenu.close();
        buttons.values().forEach(UI::close);
        screen.values().forEach(list -> list.getUIs().forEach(UI::close));
    }
    
    protected static class Tab extends ToggleButton {
        protected final String id;
        protected final TabMenu tabMenu;
        
        public Tab(TabMenu tabMenu, String name) {
            super(tabMenu.scene, tabMenu.fontPath, name);
            id = UUID.randomUUID().toString();
            this.tabMenu = tabMenu;
        }
        
        @Override
        public void onToggle(boolean toggle) {
            if (!toggle) return;
            tabMenu.selectTab(id);
        }
    }
}
