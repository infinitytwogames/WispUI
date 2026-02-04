package org.infinitytwogames.wispui.ui.base;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.FontRenderer;
import org.infinitytwogames.wispui.ui.base.component.Text;
import org.infinitytwogames.wispui.ui.base.layout.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * A multi-line text container that supports automatic word-wrapping and paragraph splitting.
 * <p>
 * {@code TextArea} calculates line breaks based on the component's current width. It
 * decomposes a single raw string into multiple {@link Text} components, allowing for
 * efficient rendering of large blocks of text within a constrained UI space.
 * </p>
 * * <h2>Text Wrapping Algorithm</h2>
 * <p>
 * The component uses a greedy word-wrap approach:
 * <ol>
 * <li>Splits the input by newline characters (hard breaks).</li>
 * <li>Iterates through words, measuring their width via {@link FontRenderer#getStringWidth(String)}.</li>
 * <li>If a word exceeds the current line's bounds, it pushes the current buffer to a new {@link Text} component.</li>
 * </ol>
 * </p>
 *
 * @author Infinity Two Games
 * @deprecated Under active development.
 */
@Deprecated
public class TextArea extends UI {
    protected FontRenderer fontRenderer;
    protected List<Text> lines = new ArrayList<>();
    protected String rawText = "";
    protected int lineGap = 2; // Pixels between lines
    protected RGBA color = new RGBA(1, 1, 1, 1);
    protected Scene scene;
    
    public TextArea(Scene scene, String fontPath, int fontSize) {
        super(scene.getRenderer());
        this.fontRenderer = new FontRenderer(fontPath, fontSize);
        this.scene = scene;
    }
    
    /**
     * Updates the text content and immediately triggers the wrapping calculation.
     * <p>
     * <b>Performance Note:</b> This operation involves string splitting and multiple
     * font measurements. Avoid calling this every frame; use it only when content changes.
     * </p>
     *
     * @param text
     *         The new string to display.
     */
    public void setText(String text) {
        this.rawText = text;
        wrapText();
    }
    
    /**
     * Logic for calculating line breaks.
     * <p>
     * This method adjusts the {@code height} of the {@code TextArea} based on
     * how many lines were generated.
     * </p>
     */
    private void wrapText() {
        lines.clear();
        if (width <= 0) return;
        
        String[] paragraphs = rawText.split("\n");
        int yOffset = 0;
        
        for (String paragraph : paragraphs) {
            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                String testLine = currentLine.isEmpty()? word : currentLine + " " + word;
                float lineWidth = fontRenderer.getStringWidth(testLine);
                
                if (lineWidth > width && !currentLine.isEmpty()) {
                    createLineComponent(currentLine.toString(), yOffset);
                    yOffset += (int) (fontRenderer.getFontHeight() + lineGap);
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }
            
            if (!currentLine.isEmpty()) {
                createLineComponent(currentLine.toString(), yOffset);
                yOffset += (int) (fontRenderer.getFontHeight() + lineGap);
            }
        }
        
        this.height = yOffset - lineGap; // Remove last gap for accurate height
    }
    
    private void createLineComponent(String content, int yOffset) {
        Text line = new Text(fontRenderer, scene); // Pass scene for registration
        line.setText(content);
        line.setColor(color);
        line.setParent(this);
        // Position line relative to TextArea top-left
        line.setOffset(0, yOffset);
        lines.add(line);
    }
    
    @Override
    public void draw() {
        super.draw();
        for (Text line : lines) {
            line.setDrawOrder(this.drawOrder + 1);
            line.draw();
        }
    }
    
    /**
     * Resizes the container. If the width is modified, {@link #wrapText()} is
     * automatically invoked to ensure text fits the new bounds.
     */
    @Override
    public void setSize(int width, int height) {
        boolean widthChanged = width != this.width;
        super.setSize(width, height);
        if (widthChanged) wrapText(); // Re-wrap if width changes
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
    
    }
    
    public void setColor(RGBA color) {
        this.color.set(color);
        for (Text line : lines) line.setColor(color);
    }
}