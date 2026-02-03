package org.infinitytwogames.whispui.ui.base.layout.menu;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.interactive.button.ToggleButton;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.infinitytwogames.whispui.ui.base.layout.grid.Grid;
import org.infinitytwogames.whispui.ui.base.layout.menu.scroll.ScrollableMenu;

import java.util.*;

public class VerticalTabMenu extends UI implements Container {
    protected Map<String, Tab> buttons = new HashMap<>();
    protected Map<String, ScrollableMenu> screen = new HashMap<>();
    protected String current;
    protected String fontPath;
    protected final Scene scene;
    protected String defaultTab;
    protected final Window window;
    
    private final Grid grid;
    private final ScrollableMenu tabSidebar; // Changed to standard vertical ScrollableMenu
    private int currentRow = 0; // Track rows instead of columns
    private final List<UI> temp = new ArrayList<>();
    
    public VerticalTabMenu(Scene scene, String fontPath, Window window, int sidebarWidth, int sidebarHeight) {
        super(scene.getRenderer());
        this.fontPath = fontPath;
        this.scene = scene;
        this.window = window;
        
        // 1. Grid setup: Switch to 1 column and multiple rows
        grid = new Grid.Builder(scene)
                .cellSize(sidebarWidth, 50) // Fixed height for each tab button
                .columns(1)
                .build();
        
        // 2. Sidebar setup: Vertical scrolling for many versions
        tabSidebar = new ScrollableMenu(scene, window);
        tabSidebar.addUI(grid);
        tabSidebar.setParent(this);
        tabSidebar.setSize(sidebarWidth, sidebarHeight);
        tabSidebar.setAnchor(0, 0);
        tabSidebar.setPivot(0, 0);
    }
    
    public String addTab(String name) {
        Tab tab = new Tab(this, name);
        String id = tab.id;
        if (defaultTab == null) {
            defaultTab = id;
            tab.setToggle(true);
        }
        
        // 3. Put tab in the next vertical row
        grid.put(tab, currentRow, 0);
        
        // 4. Content Area: Positioned to the right of the sidebar
        ScrollableMenu content = new ScrollableMenu(scene, window);
        content.setOffset(new org.joml.Vector2i(tabSidebar.getWidth(), 0));
        content.setWidth(window.getWidth() - tabSidebar.getWidth());
        content.setHeight(window.getHeight());
        
        buttons.put(id, tab);
        screen.put(id, content);
        currentRow++;
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
        
        tabSidebar.draw();
        
        current = current != null? current : defaultTab;
        screen.get(current).draw();
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        tabSidebar.onMouseClicked(e);
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    }
    
    @Override
    public void onMouseHoverEnded() {
    }
    
    @Override
    public void cleanup() {
        tabSidebar.close();
        buttons.values().forEach(UI::close);
        screen.values().forEach(list -> list.getUIs().forEach(UI::close));
    }
    
    @Override
    public List<UI> getUIs() {
        temp.clear();
        temp.addAll(screen.get(current).getUIs());
        temp.add(tabSidebar);
        return temp;
    }
    
    protected static class Tab extends ToggleButton {
        protected final String id;
        protected final VerticalTabMenu tabSidebar;
        
        public Tab(VerticalTabMenu tabSidebar, String name) {
            super(tabSidebar.scene, tabSidebar.fontPath, name);
            id = UUID.randomUUID().toString();
            this.tabSidebar = tabSidebar;
        }
        
        @Override
        public void onToggle(boolean toggle) {
            if (!toggle) return;
            tabSidebar.selectTab(id);
        }
    }
}
