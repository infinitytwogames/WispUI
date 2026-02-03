package org.infinitytwogames.whispui.ui.base.interactive;

import org.infinitytwogames.whispui.Display;
import org.infinitytwogames.whispui.VectorMath;
import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.input.keyboard.CharacterInputEvent;
import org.infinitytwogames.whispui.event.input.keyboard.KeyPressEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.manager.Mouse;
import org.infinitytwogames.whispui.ui.base.Label;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Pivot;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.joml.Vector2i;

import static org.infinitytwogames.whispui.Display.transformWindowToVirtual;
import static org.joml.Math.clamp;
import static org.lwjgl.glfw.GLFW.*;

public abstract class TextInput extends Label {
    protected int index = 0;
    protected final Caret caret;
    protected boolean input;
    protected final StringBuilder builder = new StringBuilder();
    protected boolean submitted;
    protected boolean disabled;
    protected final Scene scene;
    protected String hint = "";
    protected RGBA original = new RGBA();
    
    public TextInput(Scene scene, String path) {
        super(scene, path);
        this.scene = scene;
        
        caret = new Caret(scene.getRenderer());
        caret.setActive(false);
        caret.setHeight((int) ((textRenderer.getFontHeight()) + 3));
        caret.setParent(this);
        caret.setPosition(new Anchor(0, 0.5f), new Pivot(0, 0.5f));
        caret.setWidth(5);
        caret.setBackgroundColor(0, 0, 0, 1);
        
        setCursorType(Mouse.CursorType.IBEAM);
        setTextPosition(new Anchor(0, 0.5f), new Pivot(0, 0.5f), new Vector2i(5, 0));
        
        EventBus.connect(this);
    }
    
    public String getHint() {
        return hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        caret.setHeight(height - 5);
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        if (submitted) {
            clean();
            submitted = false;
        }
        index = getCaretIndexAtMouse(transformWindowToVirtual(scene.getWindow(), e.x, e.y).x, getPosition().x + 5 + text.getOffset().x);
        focus();
    }
    
    @SubscribeEvent
    public void onMousePressed(MouseButtonEvent e) {
        if (!VectorMath.isPointInRect(
                getPosition(),
                getEndPoint(),
                Display.transformWindowToVirtual(e.window, e.x),
                Display.transformWindowToVirtual(e.window, e.y)
        )) {
            unfocus();
        }
    }
    
    @Override
    public void draw() {
        caret.update(scene.getDelta());
        
        if (builder.isEmpty()) {
            super.setText(hint);
            super.setColor(original.addNew(-0.25f));
        } else super.setColor(original);
        
        super.draw();
        caret.draw();
    }
    
    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        super.setBackgroundColor(r, g, b, a);
        RGBA contrastColor = backgroundColor.getContrastColor();
        caret.setBackgroundColor(contrastColor);
        super.setColor(contrastColor);
    }
    
    @Override
    public RGBA getColor() {
        return original;
    }
    
    @Override
    public void setColor(RGBA color) {
        super.setColor(color);
        original.set(color);
    }
    
    @SubscribeEvent
    public void onKeyPress(KeyPressEvent e) {
        if (e.getAction() == GLFW_PRESS ||
                e.getAction() == GLFW_REPEAT
        ) {
            
            caret.reset();
            boolean submittedLocally = false; // New flag to track submission
            
            if (e.getKey() == GLFW_KEY_ESCAPE) unfocus();
            else if (e.getKey() == GLFW_KEY_RIGHT) index = clamp(0, builder.length(), index + 1);
            else if (e.getKey() == GLFW_KEY_LEFT) index = clamp(0, builder.length(), index - 1);
            else if (e.getKey() == GLFW_KEY_BACKSPACE) if (e.mods == GLFW_MOD_CONTROL) {
                bulkBackspace();
            } else backspace();
            else if (e.getKey() == GLFW_KEY_HOME) index = 0;
            else if (e.getKey() == GLFW_KEY_END) index = builder.length();
            else if (e.getKey() == GLFW_KEY_ENTER) {
                submitted = true;
                submittedLocally = true; // Set flag
                submit(builder.toString());
                unfocus();
                
            } else if (e.getKey() == GLFW_KEY_V && e.mods == GLFW_MOD_CONTROL) {
                String clipboard = scene.getWindow().getClipboardContent();
                if (clipboard != null) {
                    // Sanitize: Remove newlines and tabs
                    clipboard = clipboard.replace("\n", "").replace("\r", "").replace("\t", "");
                    builder.insert(index, clipboard);
                    index += clipboard.length();
                    updateCursorPosition();
                }
            }
            
            // Only update cursor if the command wasn't "Enter" (submission)
            if (!submittedLocally) {
                updateCursorPosition();
            }
        }
    }
    
    protected void bulkBackspace() {
        if (index == 0 || builder.isEmpty()) return;
        
        int originalIndex = index;
        int searchIndex = index - 1;
        
        // 1. Skip trailing spaces (e.g., "| Hello   " -> " Hello|")
        while (searchIndex >= 0 && Character.isWhitespace(builder.charAt(searchIndex))) {
            searchIndex--;
        }
        
        // 2. Skip the actual word (e.g., " Hello|" -> "| Hello")
        while (searchIndex >= 0 && !Character.isWhitespace(builder.charAt(searchIndex))) {
            searchIndex--;
        }
        
        // The searchIndex is now at the character before the word (or -1)
        // We want to delete from (searchIndex + 1) to originalIndex
        int deleteFrom = searchIndex + 1;
        builder.delete(deleteFrom, originalIndex);
        
        // Update cursor to the start of the deleted area
        index = deleteFrom;
        
        updateCursorPosition();
    }
    
    protected void unfocus() {
        input = false;
        caret.setActive(false);
        String visible = getVisibleText(textRenderer, builder.toString(), 0, width - 10);
        setText(visible);
        updateCursorPosition();
    }
    
    protected void focus() {
        if (disabled) {
            unfocus();
            return;
        }
        input = true;
        caret.setActive(true);
        String visible = getVisibleText(textRenderer, builder.toString(), index, width - 10);
        text.setText(visible);
        updateCursorPosition();
    }
    
    public int getCaretIndexAtMouse(float mouseX, float textX) {
        // Use the same authoritative state used for rendering
        TextState state = getVisibleTextState(builder.toString(), index, width - 10);
        String visible = state.text();
        
        // Prefix length for "..."
        int prefixLen = (state.startIndex() > 0) ? ellipsis.length() : 0;
        float currentX = textX;
        
        for (int i = 0; i < visible.length(); i++) {
            float charWidth = textRenderer.getStringWidth(String.valueOf(visible.charAt(i)));
            
            // If click is within the first half of this character
            if (mouseX < currentX + charWidth / 2f) {
                // Map the visible index 'i' back to the builder index
                // We subtract the prefixLen to "skip" the system dots
                return clamp(0, builder.length(), state.startIndex() + (i - prefixLen));
            }
            currentX += charWidth;
        }
        
        // If clicked at the very end of the visible string
        return clamp(0, builder.length(), state.startIndex() + (visible.length() - prefixLen));
    }
    
    @SubscribeEvent
    public void onCharacterPressed(CharacterInputEvent e) {
        if (input) {
            caret.reset();
            caret.forceDraw();
            // NEW: If the previous state was submitted, clear the builder before inserting.
            if (submitted) {
                builder.setLength(0);
                index = 0;
                submitted = false;
            }
            
            builder.insert(index, e.character); // insert at index
            index++;
            String data = builder.toString();
            setText(data);
            updateCursorPosition();
            caret.reset();
            input(data);
        }
    }
    
    public void updateCursorPosition() {
        if (getTextComponent() == null || isDisabled()) return;
        // 1. Get the state directly from the new Label overload
        TextState state = getVisibleTextState(builder.toString(), index, width - 10);
        setText(state.text());
        
        // 2. Calculate the system ellipsis offset
        // If startIndex > 0, the Label added "..." at the beginning
        String visiblePartToCursor = getVisiblePartToCursor(state);
        int cursorX = (int) textRenderer.getStringWidth(visiblePartToCursor);
        
        caret.setOffset(new Vector2i(cursorX, 0).add(getTextComponent().getOffset()));
    }
    
    private String getVisiblePartToCursor(TextState state) {
        int prefixLength = (state.startIndex() > 0) ? ellipsis.length() : 0;
        
        // 3. Calculate localIndex:
        // (index - state.startIndex()) is the cursor's position relative to the raw text segment.
        // We add prefixLength to account for the "..." at the very start of the visible string.
        int localIndex = (index - state.startIndex()) + prefixLength;
        
        // 4. Measure the exact width of the visible string up to the localIndex
        return state.text().substring(0, clamp(0, state.text().length(), localIndex));
    }
    
    protected void backspace() {
        caret.draw();
        // 1. Check if there is anything to delete (index > 0 means cursor is NOT at the start)
        if (index == 0 || builder.isEmpty()) return;
        
        // 2. The character to delete is the one *before* the cursor position (index - 1)
        int deleteIndex = index - 1;
        
        // 3. Delete the character
        builder.deleteCharAt(deleteIndex);
        
        // 4. Move the cursor back one position
        index = deleteIndex;
        
        // 5. Update UI
        setText(builder.toString());
        updateCursorPosition();
    }
    
    protected void clean() {
        unfocus();
        setText("");
        builder.setLength(0);
        // Explicitly reset index for safety
        index = 0;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    @Override
    public void cleanup() {
        setDisabled(true);
        super.cleanup();
    }
    
    public abstract void submit(String data);
    public abstract void input(String data);
}
