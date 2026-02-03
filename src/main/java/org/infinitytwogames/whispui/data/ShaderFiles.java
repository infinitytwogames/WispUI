package org.infinitytwogames.whispui.data;

public abstract class ShaderFiles {
    public static final String uiVertex = """
             #version 330 core
            
             layout (location = 0) in vec3 aPos;
             layout (location = 1) in vec4 aColor;
             layout (location = 2) in vec2 aTexCoord;
             layout (location = 3) in vec2 aSize;      // Width and Height of the quad
             layout (location = 4) in float aRadius;   // Corner radius
             layout (location = 5) in vec2 aLocalUV;   // 0.0 to 1.0 within the quad
             layout (location = 6) in float aBorderThickness;
             layout (location = 7) in vec4 aBorderColor;
            
             uniform mat4 projection;
            
             out vec4 vColor;
             out vec2 vTexCoord;
             out vec2 vSize;
             out float vRadius;
             out vec2 vLocalUV;
             out float vBorderThickness;
             out vec4 vBorderColor;
            
             void main() {
                 gl_Position = projection * vec4(aPos, 1.0);
                 vColor = aColor;
                 vTexCoord = aTexCoord;
                 vSize = aSize;
                 vRadius = aRadius;
                 vLocalUV = aLocalUV;
                 vBorderThickness = aBorderThickness;
                 vBorderColor = aBorderColor;
             }
             """;

    public static final String uiFragment = """
             #version 330 core
             
             in vec4 vColor;
             in vec2 vTexCoord;
             in vec2 vSize;
             in float vRadius;
             in vec2 vLocalUV;
             in float vBorderThickness; // From location 6
             in vec4 vBorderColor;     // From location 7
             
             uniform sampler2D u_texture;
             uniform bool useTexture;
             uniform bool useMSDF;
             uniform float msdfRange; // usually 4.0 or 8.0 (depends on generator)
             
             out vec4 FragColor;
             
             float sdRoundedBox(vec2 p, vec2 b, float r) {
                 vec2 q = abs(p) - b + r;
                 return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
             }
             
             float median(float r, float g, float b) {
                 return max(min(r, g), min(max(r, g), b));
             }
            
             float msdfAlpha(vec3 sample, float pxRange) {
                 float sd = median(sample.r, sample.g, sample.b) - 0.5;
                 float screenPxRange = pxRange * length(vec2(dFdx(vTexCoord.x), dFdy(vTexCoord.y)));
                 return clamp(sd / screenPxRange + 0.5, 0.0, 1.0);
             }
             
             void main() {
                // --- 1. HANDLE TEXT / FULL-QUAD RENDERING ---
                // If vSize is 0, we assume this is a glyph or a simple texture quad
                if (vSize.x <= 0.0 || vSize.y <= 0.0) {
                    if (useTexture) {
                       vec4 tex = texture(u_texture, vTexCoord);
            
                       float alpha;
                       if (useMSDF) {
                           alpha = msdfAlpha(tex.rgb, msdfRange);
                       } else {
                           // fallback for bitmap / single-channel SDF
                           alpha = tex.r;
                       }
            
                       FragColor = vec4(vColor.rgb, vColor.a * alpha);
            
                    } else {
                        FragColor = vColor;
                    }
             
                    if (FragColor.a < 0.005) discard;
                    return; // Skip rounded box logic entirely
                }
             
                // --- 2. EXISTING ROUNDED BOX LOGIC ---
                vec2 halfSize = vSize * 0.5;
                vec2 p = (vLocalUV - 0.5) * vSize;
                float edgeRadius = min(vRadius, min(halfSize.x, halfSize.y));
             
                float d = sdRoundedBox(p, halfSize, edgeRadius);
                float smoothing = fwidth(d);
             
                float shapeMask = 1.0 - smoothstep(-smoothing, smoothing, d);
                float innerRadius = max(edgeRadius - vBorderThickness, 0.0);
                vec2 innerHalfSize = halfSize - vec2(vBorderThickness);
             
                float dInner = sdRoundedBox(p, innerHalfSize, innerRadius);
             
                float borderMask = smoothstep(-smoothing, smoothing, dInner)
                                 - smoothstep(-smoothing, smoothing, d);
             
                vec4 texColor = useTexture ? texture(u_texture, vTexCoord) : vec4(1.0);
                vec4 bgColor = texColor * vColor;
             
                vec4 resultColor = mix(bgColor, vBorderColor, borderMask);
             
                FragColor = vec4(resultColor.rgb, resultColor.a * shapeMask);
                if (FragColor.a < 0.005) discard;
             }
             """;

    public static final String textVertex = """
            #version 330 core
            layout(location = 0) in vec2 inPos;
            layout(location = 1) in vec2 inUV;
            
            uniform mat4 uProj;
            uniform mat4 uModel;
            
            out vec2 vUV;
            
            void main() {
                gl_Position = uProj * uModel * vec4(inPos, 0, 1);
                vUV = inUV;
            }
            
            """;
    public static final String textFragment = """
            #version 330 core
            in vec2 vUV;
            
            uniform sampler2D uFontAtlas;
            uniform vec4 uTextColor;
            
            out vec4 FragColor;
            
            void main(){
                float alpha = texture(uFontAtlas, vUV).r;
                if (alpha < 0.3) {
                        discard;
                }
                FragColor = vec4(uTextColor.rgb, alpha * uTextColor.a);
            }
            """;
}
