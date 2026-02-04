package org.infinitytwogames.wispui.ui.base.interactive.button;

import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.layout.Container;
import org.infinitytwogames.wispui.ui.base.layout.Scene;

import java.util.*;

/**
 * A logical controller for a group of buttons where only one may be active at a time.
 * <p>
 * The {@code RadioButtons} class manages a collection of {@link RadioButton} children.
 * When one button is selected, the controller automatically deselects all others
 * in the group, enforcing a "Mutual Exclusion" (Mutex) logic.
 * </p>
 * *
 *
 * <h2>Key Features</h2>
 * <ul>
 * <li><b>Exclusive Selection:</b> Uses a UUID-based lookup to toggle siblings off
 * when a new target is activated.</li>
 * <li><b>Mandatory Selection:</b> The inner {@code RadioButton} class prevents
 * deactivation of the currently selected item, ensuring at least one option stays active.</li>
 * <li><b>Automatic UUID Tracking:</b> Buttons are stored in a {@code LinkedHashMap}
 * using generated UUID strings as keys to ensure reliable selection propagation.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
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
    
    /**
     * Abstract callback executed when a new selection is made.
     * @param button The newly active {@link RadioButton} instance.
     */
    public abstract void onSelect(RadioButton button);
    
    public boolean contains(String id) {
        return buttons.containsKey(id);
    }
    
    /**
     * Updates the state of all buttons in the group.
     * <p>
     * This method iterates through the collection, setting the target to {@code true}
     * and all others to {@code false}, then triggers the {@link #onSelect} callback.
     * </p>
     * @param uuid The unique identifier of the button to be activated.
     */
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
