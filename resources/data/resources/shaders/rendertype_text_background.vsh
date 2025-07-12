#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in ivec2 UV2;

uniform sampler2D Sampler2;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec3 vPos;
flat out int functionId;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    ivec4 iColor = ivec4(round(Color * 255));
    vPos = Position;
    if (iColor.r == 254 && iColor.b < 255) {
        functionId = iColor.b;
    } else {
        functionId = -1;
    }

    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
}
