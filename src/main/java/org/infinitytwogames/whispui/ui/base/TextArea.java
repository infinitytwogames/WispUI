package org.infinitytwogames.whispui.ui.base;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.FontRenderer;
import org.infinitytwogames.whispui.ui.base.component.Text;
import org.infinitytwogames.whispui.ui.base.layout.Scene;

import java.util.ArrayList;
import java.util.List;

// MODIFIED
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
    
    public void setText(String text) {
        this.rawText = text;
        wrapText();
    }
    
    /**
     * Splits rawText into multiple Text components based on this.width
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