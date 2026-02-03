package org.infinitytwogames.whispui.ui.base.interactive.button;

import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Scene;

import java.util.*;

public abstract class RadioButtons implements Container {
    protected Map<String, RadioButton> buttons = new LinkedHashMap<>();
    protected int cellWidth, cellHeight;
    protected final Scene scene;
    protected final String path;
    
    public RadioButtons(Scene renderer, String path) {
        this.scene = renderer;
        this.path = path;
    }
    
    @Override
    public List<UI> getUIs() {
        return List.copyOf(buttons.values());
    }
    
    public int getCellWidth() {
        return cellWidth;
    }
    
    public RadioButtons setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
        return this;
    }
    
    public int getCellHeight() {
        return cellHeight;
    }
    
    public RadioButtons setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
        return this;
    }
    
    public int size() {
        return buttons.size();
    }
    
    public boolean isEmpty() {
        return buttons.isEmpty();
    }
    
    public void clear() {
        buttons.clear();
    }
    
    public RadioButton get(String id) {
        return buttons.get(id);
    }
    
    public RadioButton put(String text) {
        RadioButton button = new RadioButton(this, text);
        String uuid = button.uuid.toString();
        buttons.put(uuid, button);
        return button;
    }
    
    public abstract void onSelect(RadioButton button);
    
    public boolean contains(String id) {
        return buttons.containsKey(id);
    }
    
    public void select(UUID uuid) {
        String targetId = uuid.toString();
        if (buttons.containsKey(targetId)) {
            onSelect(buttons.get(uuid.toString()));
            for (RadioButton button : buttons.values()) {
                boolean shouldBeActive = button.uuid.equals(uuid);
                if (button.isToggled() != shouldBeActive) {
                    button.setToggle(shouldBeActive);
                }
            }
        }
    }
    
    public Collection<RadioButton> getAll() {
        return buttons.values();
    }
    
    public static class RadioButton extends ToggleButton {
        private final RadioButtons radio;
        protected UUID uuid;
        
        public RadioButton(RadioButtons radio, String name) {
            super(radio.scene, radio.path, name);
            uuid = UUID.randomUUID();
            this.radio = radio;
        }
        
        @Override
        public void onToggle(boolean toggle) {
            if (!toggle) {
                // Re-activate if the user tried to deselect the active radio
                super.setToggle(true);
                return;
            }
            // Deactivate all siblings
            radio.select(uuid);
        }
    }
}
