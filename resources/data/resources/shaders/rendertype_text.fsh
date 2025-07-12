#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:globals.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 uv;
in vec3 vPos;

out vec4 fragColor;
flat in int functionId;
flat in int arcSegment;

// ((2 * pi) / 5)
#define FIFTH 1.256

// ((2 * pi) / 5) / 2)
#define HALF_FIFTH 0.628

// pi / 2
#define QUARTER_CIRCLE 1.57
#define PI 3.14

#define ANGLE_ORIGIN (PI + QUARTER_CIRCLE)

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    if (functionId == 2) {
        vec3 gradientDirection = vec3(1, 1, 0);
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

    if (functionId == 1) {
        int angle = arcSegment;
        float pxAngle = atan(uv.y - 0.5, uv.x - 0.5) + PI;
        if ((arcSegment == 3 && !(pxAngle < QUARTER_CIRCLE && pxAngle > QUARTER_CIRCLE - FIFTH))
        || (arcSegment == 4 && !(pxAngle < QUARTER_CIRCLE + FIFTH && pxAngle > QUARTER_CIRCLE))
        || (arcSegment == 0 && !(pxAngle < QUARTER_CIRCLE + (FIFTH * 2) && pxAngle > QUARTER_CIRCLE + FIFTH))
        || (arcSegment == 1 && !(pxAngle < QUARTER_CIRCLE + (FIFTH * 3) && pxAngle > QUARTER_CIRCLE + (FIFTH * 2)))
        || (arcSegment == 2 && !(pxAngle > QUARTER_CIRCLE + (FIFTH * 3) || pxAngle < QUARTER_CIRCLE - FIFTH))) {
            discard;
        }
    }

    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
