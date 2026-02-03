package org.infinitytwogames.whispui.ui.base;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.FontRenderer;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.component.Text;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Pivot;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.infinitytwogames.whispui.ui.base.layout.TruncateMode;
import org.joml.Vector2i;

// MODIFIED
public class Label extends UI {
    protected FontRenderer textRenderer;
    protected Text text;
    protected String ellipsis = "...";
    protected final String path;
    private String str = "";
    
    public Label(Scene renderer, String path) {
        super(renderer.getRenderer());
        this.textRenderer = new FontRenderer(path,16);
        text = new Text(textRenderer, renderer);
        this.path = path;
        text.setParent(this);
    }

    public void setTextPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
        text.setPosition(anchor, pivot, offset);
    }

    @Override
    public void draw() {
        super.draw();
        text.setDrawOrder(drawOrder);
        text.setText(getVisibleText());
        text.draw();
    }
    
    private String getVisibleText() {
        return getVisibleText(textRenderer,str,0,width);
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        textRenderer.cleanup();
        textRenderer = new FontRenderer(path, (float) height /2);
        text.setRenderer(textRenderer);
    }
    
    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        super.setBackgroundColor(r, g, b, a);
        text.setColor(super.getBackgroundColor().getContrastColor());
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

    public String getText() {
        return str;
    }

    public void setText(String text) {
        this.str = text;
        tip = str;
    }

    public RGBA getColor() {
        return text.getColor();
    }

    public void setColor(RGBA color) {
        setColor(color.r(), color.g(), color.b(), color.a());
    }

    public Vector2i getTextPosition() {
        return text.getPosition();
    }

    public Text getTextComponent() {
        return text;
    }

    @Override
    public void cleanup() {
        textRenderer.cleanup();
        text.getScene().unregister(this);
        text = null;
    }

    public String getVisibleText(FontRenderer renderer, String fullText, int maxWidth, TruncateMode mode) {
        if (renderer.getStringWidth(fullText) <= maxWidth) return fullText;

        switch (mode) {
            case END: {
                int cut = fullText.length();
                while (cut > 0 && renderer.getStringWidth(fullText.substring(0, cut) + ellipsis) > maxWidth) {
                    cut--;
                }
                return fullText.substring(0, cut) + ellipsis;
            }
            case MIDDLE: {
                int ellipsisWidth = (int) renderer.getStringWidth(ellipsis);
                int remainingWidth = maxWidth - ellipsisWidth;

                // Find how many characters can fit on both sides combined
                int totalChars = 0;
                while (totalChars < fullText.length() && renderer.getStringWidth(fullText.substring(0, totalChars + 1)) <= remainingWidth) {
                    totalChars++;
                }

                // Split the available characters evenly
                int startChars = totalChars / 2;
                int endChars = totalChars - startChars;

                String start = fullText.substring(0, startChars);
                String end = fullText.substring(fullText.length() - endChars);

                return start + ellipsis + end;
            }
            default:
                return fullText;
        }
    }
    
    public TextState getVisibleTextState(String fullText, int caretIndex, int maxWidth) {
        // 1. If it fits, we are at index 0 with no ellipsis
        if (textRenderer.getStringWidth(fullText) <= maxWidth) {
            return new TextState(fullText, 0);
        }
        
        // 2. Expand outwards from the caret index
        int left = caretIndex;
        int right = caretIndex;
        
        while (true) {
            int addLeft = (left > 0) ? 1 : 0;
            int addRight = (right < fullText.length()) ? 1 : 0;
            
            // Break if we can't expand further
            if (addLeft == 0 && addRight == 0) break;
            
            int newLeft = left - addLeft;
            int newRight = right + addRight;
            
            String candidate = fullText.substring(newLeft, newRight);
            boolean needsL = newLeft > 0;
            boolean needsR = newRight < fullText.length();
            
            // Check width including the markers
            String display = (needsL ? ellipsis : "") + candidate + (needsR ? ellipsis : "");
            if (textRenderer.getStringWidth(display) > maxWidth) break;
            
            left = newLeft;
            right = newRight;
        }
        
        String result = fullText.substring(left, right);
        if (left > 0) result = ellipsis + result;
        if (right < fullText.length()) result = result + ellipsis;
        
        return new TextState(result, left);
    }
    
    @Override
    public void setAngle(float angle) {
        super.setAngle(angle);
        text.setAngle(angle);
    }

    public String getVisibleText(FontRenderer renderer, String fullText, int caretIndex, int maxWidth) {
        int ellipsisWidth = (int) renderer.getStringWidth(ellipsis);
        if ((int) renderer.getStringWidth(fullText) <= maxWidth) return fullText;
        int left = caretIndex;
        int right = caretIndex;

        while (true) {
            int addLeft = (left > 0) ? 1 : 0;
            int addRight = (right < fullText.length()) ? 1 : 0;

            int newLeft = left - addLeft;
            int newRight = right + addRight;

            String candidate = fullText.substring(newLeft, newRight);
            boolean needsLeftEllipsis = newLeft > 0;
            boolean needsRightEllipsis = newRight < fullText.length();

            String withEllipsis = (needsLeftEllipsis ? ellipsis : "") + candidate + (needsRightEllipsis ? ellipsis : "");
            int candidateWidth = (int) renderer.getStringWidth(withEllipsis);

            if (candidateWidth > maxWidth) break;

            left = newLeft;
            right = newRight; // Stop if both sides reached the limits
            if (addLeft == 0 && addRight == 0) break;
        }

        String finalText = fullText.substring(left, right);
        if (left > 0) finalText = ellipsis + finalText;
        if (right < fullText.length()) finalText = finalText + ellipsis;

        return finalText;
    }
    
    public void setTextPosition(Anchor anchor, Pivot pivot) {
        text.setPosition(anchor,pivot);
    }
    
    public void setColor(float r, float g, float b, float a) {
        text.setColor(r,g,b,a);
    }
    
    public static class LabelBuilder<T extends Label> extends UIBuilder<T> {
        public LabelBuilder(UIRenderer renderer, T element) {
            super(element);
        }

        public UIBuilder<T> textPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
            ui.setTextPosition(anchor, pivot, offset);
            return this;
        }

        public UIBuilder<T> text(String text) {
            ui.setText(text);
            return this;
        }

        @Override
        public UIBuilder<T> applyDefault() {
            return this;
        }
    }
    
    public record TextState(String text, int startIndex) {}
}
