package org.infinitytwogames.wispui.ui.base.layout.menu;

import org.infinitytwogames.wispui.Window;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.manager.SceneManager;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.interactive.button.ToggleButton;
import org.infinitytwogames.wispui.ui.base.layout.Scene;
import org.infinitytwogames.wispui.ui.base.layout.grid.Grid;
import org.infinitytwogames.wispui.ui.base.layout.menu.scroll.HorizontalScrollableMenu;
import org.infinitytwogames.wispui.ui.base.layout.menu.scroll.ScrollableMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A horizontal tabbed navigation container.
 * <p>
 * This component consists of a horizontal row of toggleable buttons at the top
 * and a dynamic content area below. It is ideal for main menu categories or
 * multi-page settings panels.
 * </p>
 * *
 *
 * <h2>Internal Architecture</h2>
 * <ul>
 * <li><b>Tab Bar:</b> A {@link HorizontalScrollableMenu} containing a 1-row {@link Grid}.
 * This allows the tabs to scroll horizontally if they exceed the width of the container.</li>
 * <li><b>Content Pane:</b> A vertical {@link ScrollableMenu} that displays the UI
 * elements associated with the currently selected tab.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
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
    
    /**
     * Registers a new tab to the navigation bar.
     * <p>
     * Creates a {@link Tab} button in the horizontal grid and a corresponding
     * {@link ScrollableMenu} for content. The first tab added becomes the default.
     * </p>
     *
     * @param name
     *         The label displayed on the tab button.
     *
     * @return The unique UUID string of the created tab.
     */
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
        content.setHeight(height - tabMenu.getHeight());
        
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
        if (menu == null) throw new NullPointerException("Couldn't find for tab with uuid \"" + id + "\"");
        menu.addUI(ui);
    }
    
    @Override
    public void draw() {
        super.draw();
        
        tabMenu.draw();
        
        current = current != null? current : defaultTab;
        screen.get(current).draw();
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
