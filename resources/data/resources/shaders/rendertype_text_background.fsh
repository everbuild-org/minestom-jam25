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

float Dist(vec2 p) {
    // HEX:
    p = abs(p);
    float c = dot(p, normalize(vec2(1, 1.73)));
    c = max(c, p.x);
    return c * 1.02;
}

vec4 Coords(vec2 uv) {
    const vec2 r = vec2(1, 1.73);
    const vec2 h = r * .5;

    vec2 a = mod(uv, r) - h;
    vec2 b = mod(uv - h, r) - h;

    vec2 gv = dot(a, a) < dot(b, b) ? a : b;

    float x = atan(gv.x, gv.y);
    float y = .5 - Dist(gv);
    vec2 id = uv - gv;
    return vec4(x, y, id.x, id.y);
}

void main() {
    vec4 color = vertexColor * ColorModulator;
    if (functionId == 0) {
        float t = GameTime * 10.0;
        vec2 uv = (vPos.xz - 0.5 * ScreenSize.xy) / ScreenSize.y;
        float scaleFactor = 25.0;
        uv *= scaleFactor;

        vec2 uv1 = uv + vec2(0, sin(uv.x * 5. + t) * .02);

        vec2 uv2 = .5 * uv1 + .5 * uv + vec2(sin(uv.y * 5. + t) * .02, 0);
        float a = 1. + t * .05;
        float c = cos(a);
        float s = sin(a);
        uv2 *= mat2(c, -s, s, c);

        vec4 col = vec4(0);
        col += mix(vec4(.0, .1, 0.5, 0.), vec4(.0, .2, 1., 1.), smoothstep(.05, .0, Coords(uv1 * 5.).y)) * 1.5;
        col += mix(vec4(.0, .025, .065, 0.), vec4(.0, .1, .3, 1.), smoothstep(.1, .0, Coords(uv2 * 20.).y)) * 1.5;

        col *= dot(sin(uv * vec2(cos(uv.x * 1.3), 7.) + t * 2.), vec2(.7, .55974)) * 1.2 + 3.;
        if (col.a < 0.2) {
            discard;
        }
        fragColor = col;
        return;
    }
    if (color.a < 0.1) {
        discard;
    }
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
