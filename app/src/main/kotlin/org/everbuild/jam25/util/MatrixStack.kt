package org.everbuild.jam25.util

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.metadata.display.TextDisplayMeta

// generated via gemini, don't try to debug

/**
 * Represents the decomposed components of a 4x4 transformation matrix.
 *
 * A standard decomposition yields a single rotation. To fit the requirement for two,
 * the full rotation is stored in `leftRotation`, and `rightRotation` is an identity quaternion.
 *
 * @property scale The scaling component (x, y, z).
 * @property leftRotation The primary rotation component as a quaternion.
 * @property rightRotation A secondary rotation, initialized to identity (no rotation).
 * @property translation The translation component (x, y, z).
 */
data class DecomposedTransform(
    val scale: Vector3f = Vector3f(1f, 1f, 1f),
    val leftRotation: Quaternionf = Quaternionf(),
    val rightRotation: Quaternionf = Quaternionf(),
    val translation: Vector3f = Vector3f()
) {
    override fun toString(): String {
        return """
        DecomposedTransform:
          - Translation:  (${"%.3f".format(translation.x)}, ${"%.3f".format(translation.y)}, ${"%.3f".format(translation.z)})
          - Scale:        (${"%.3f".format(scale.x)}, ${"%.3f".format(scale.y)}, ${"%.3f".format(scale.z)})
          - Left Rotation (Quat): (${"%.3f".format(leftRotation.x)}, ${"%.3f".format(leftRotation.y)}, ${"%.3f".format(leftRotation.z)}, w: ${"%.3f".format(leftRotation.w)})
          - Right Rotation (Quat):(${"%.3f".format(rightRotation.x)}, ${"%.3f".format(rightRotation.y)}, ${"%.3f".format(rightRotation.z)}, w: ${"%.3f".format(rightRotation.w)})
        """.trimIndent()
    }

    fun apply(meta: TextDisplayMeta) {
        meta.translation = Vec(translation.x.toDouble(), translation.y.toDouble(), translation.z.toDouble())
        meta.scale = Vec(scale.x.toDouble(), scale.y.toDouble(), scale.z.toDouble())
        meta.leftRotation = floatArrayOf(leftRotation.x, leftRotation.y, leftRotation.z, leftRotation.w)
        meta.rightRotation = floatArrayOf(rightRotation.x, rightRotation.y, rightRotation.z, rightRotation.w)
    }
}

/**
 * A utility for managing a stack of 4x4 transformation matrices.
 * This is useful for hierarchical transformations, like those in a scene graph or graphics rendering.
 */
class MatrixStack {

    // The stack of matrices. We start with an identity matrix.
    private val stack: Deque<Matrix4f> = ArrayDeque<Matrix4f>().apply {
        add(Matrix4f()) // Add the initial identity matrix
    }

    /**
     * Gets the current transformation matrix from the top of the stack without removing it.
     */
    val currentMatrix: Matrix4f
        get() = stack.peek()

    /**
     * Saves the current state by duplicating the matrix at the top of the stack.
     */
    fun pushMatrix() {
        stack.push(Matrix4f(currentMatrix))
    }

    /**
     * Restores the previous state by removing the matrix at the top of the stack.
     * Throws an exception if you try to pop the last matrix.
     */
    fun popMatrix() {
        if (stack.size > 1) {
            stack.pop()
        } else {
            throw IllegalStateException("Cannot pop the last matrix from the stack.")
        }
    }

    /**
     * Resets the stack to a single identity matrix.
     */
    fun loadIdentity() {
        stack.clear()
        stack.push(Matrix4f())
    }

    /**
     * Applies a translation to the current matrix.
     */
    fun translate(x: Float, y: Float, z: Float) {
        currentMatrix.translate(x, y, z)
    }

    /**
     * Applies a rotation to the current matrix.
     * @param angle The angle of rotation in degrees.
     * @param x The x-component of the rotation axis.
     * @param y The y-component of the rotation axis.
     * @param z The z-component of the rotation axis.
     */
    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        currentMatrix.rotate(Math.toRadians(angle.toDouble()).toFloat(), x, y, z)
    }

    /**
     * Applies a scaling transformation to the current matrix.
     */
    fun scale(x: Float, y: Float, z: Float) {
        currentMatrix.scale(x, y, z)
    }

    fun mul(rotationMatrix: Matrix4f) {
        currentMatrix.mul(rotationMatrix)
    }

    /**
     * Decomposes the current transformation matrix into its scale, rotation, and translation components.
     * This method assumes the matrix is an affine transformation composed of only translation, rotation,
     * and uniform/non-uniform scaling. It does not handle shear.
     *
     * @return A [DecomposedTransform] object containing the components.
     */
    fun decompose(): DecomposedTransform {
        val matrix = currentMatrix

        // 1. Extract Translation
        val translation = matrix.getTranslation(Vector3f())

        // 2. Extract Scale
        // The scale is the length of the basis vectors (the first three columns).
        val scale = matrix.getScale(Vector3f())

        // 3. Extract Rotation
        // The getUnnormalizedRotation method extracts the rotation part of the matrix
        // and returns it as a quaternion. It correctly handles non-uniform scaling.
        val rotation = matrix.getUnnormalizedRotation(Quaternionf())

        // As per the request for a left and right rotation, we put the full rotation
        // in `leftRotation` and use an identity for `rightRotation`.
        return DecomposedTransform(
            scale = scale,
            leftRotation = rotation,
            rightRotation = Quaternionf(), // Identity quaternion
            translation = translation
        )
    }
}
