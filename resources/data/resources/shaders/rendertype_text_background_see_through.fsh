#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:globals.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 vPos;
flat in int functionId;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    if (functionId == 0) {
        vec3 gradientDirection = vec3(0.5, 0.5, 0.7555);
        float gradientScale = 0.5;
        float scrollSpeed = 2.0;
        float secondTimer = 1 - fract(GameTime * 1200);
        float slowerTimer = 1 - fract(GameTime * 1200 * 0.5);
        float timeOffset = secondTimer * scrollSpeed;
        float projectedPos = dot(vPos, gradientDirection) * gradientScale + timeOffset;
        float gradientFactor = fract(projectedPos);
        vec4 colorA = vec4(0.0, 1.0, 1.0, 1.0); // Cyan
        vec4 colorB = vec4(0.0, 1.0, 1.0, 0.0); // Transparent
        fragColor = mix(mix(colorA, colorB, gradientFactor * 1.75), colorB, mix(0.0, 0.8, abs(slowerTimer * 2 - 1)));
        return;
    }
    if (color.a < 0.1) {
        discard;
    }
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
