#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

#define PI 3.14159

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler2;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

out vec2 uv;
out vec3 vPos;
flat out int functionId;
flat out int arcSegment;

void main() {
    vPos = Position;
    vec4 vColor = Color;
    ivec4 iColor = ivec4(round(Color * 255));

    if (iColor.r == 254 && iColor.b < 255) {
        vColor = vec4(1.0, 1.0, 1.0, 1.0);
        functionId = iColor.b;
        if (functionId == 0) {
            vPos.y -= 11;
        } else if (functionId == 1) {
            if (iColor.g == 254) {
                // label
                functionId = -1;
                vPos.y -= 11;
            } else {
                // radial menu
                arcSegment = iColor.g >> 5;
                int anim = (iColor.g >> 3) % 4;
                float magnitude = 5.0 * (float(anim) / 3.0);
                float angle = (PI / 2.0 - float(arcSegment) * (2.0 * PI / 5.0)) - (PI / 2.0);
                vec2 outward_direction = vec2(cos(angle), sin(angle));
                vec2 shift = outward_direction * magnitude;
                vPos.xy += shift;
                vColor.rgb = vec3(0.5 + 0.5 * (float(anim) / 3.0));
                vColor.a = 0.7 + 0.3 * (float(anim) / 3.0);
            }
        }
    } else {
        functionId = -1;
    }

    gl_Position = ProjMat * ModelViewMat * vec4(vPos, 1.0);

    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    vertexColor = vColor * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;
    int vertexId = gl_VertexID % 4;
    if (vertexId == 0) {
        uv = vec2(0.0, 1.0);
    } else if (vertexId == 1) {
        uv = vec2(0.0, 0.0);
    } else if (vertexId == 2) {
        uv = vec2(1.0, 0.0);
    } else {
        uv = vec2(1.0, 1.0);
    }
}
