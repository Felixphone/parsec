package engine.graphics;

import engine.dataItems.exceptions.InsufficientBindingsException;
import engine.engine.EngineCore;
import engine.graphics.objects.Mesh;
import engine.graphics.objects.Shader;
import engine.maths.Matrix4f;
import engine.maths.Vector3f;
import org.lwjgl.opengl.*;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.dataItems.constants.TestConstants;
import game.world.player.Camera;

public class Renderer {

    private final Logger logger;
    
    private Shader shader;
    private Mesh mesh;
    private int textureID = 0;

    public Renderer() {
        logger = EngineCore.getLogger();
        logger.success("Successfully initialised renderer", "", new ThreadState(Thread.currentThread()));
    }

    public void bindShader(Shader shader) {
        this.shader = shader;
        GL20.glUseProgram(shader.getProgramID());
    }

    public void unbindShader() {
        shader = null;
        GL20.glUseProgram(0);
    }

    public void bindMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public void unbindMesh() {
        this.mesh = null;
    }

    public void bindTexture(int textureID) {
        this.textureID = textureID;
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    public void unbindTexture() {
        this.textureID = 0;
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void enableBlend() {
        GL13.glBlendFunc(GL13.GL_SRC_ALPHA, GL13.GL_ONE_MINUS_SRC_ALPHA); // enable alpha blending
        GL13.glEnable(GL13.GL_BLEND); // enable alpha blending | to get to work, render the transparent object last
    }

    public void disableBlend() {
        GL13.glDisable(GL13.GL_BLEND);
    }

    private boolean assertSufficientBindings() {
        if (shader == null) {
            throw new InsufficientBindingsException("Insuficcient bindings: No shader was bound prior to drawing", "Insuficcient bindings: No shader was bound prior to drawing, shader is null", "Expression ( shader == null ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        if (mesh == null) {
            throw new InsufficientBindingsException("Insuficcient bindings: No mesh was bound prior to drawing", "Insuficcient bindings: No mesh was bound prior to drawing, mesh is null", "Expression ( mesh == null ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        if (textureID == 0) {
            throw new InsufficientBindingsException("Insuficcient bindings: No texture was bound prior to drawing", "Insuficcient bindings: No texture was bound prior to drawing, texture is null", "Expression ( textureID == null ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        return true;
    }

    public void staticDraw(Vector3f position, Vector3f rotation, Vector3f scale) {

        if (assertSufficientBindings()) {
            GL30.glBindVertexArray(mesh.getVertexArrayObject());
            GL30.glEnableVertexAttribArray(0); // enable index positions for variables to be passed into shaders
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndicesBufferObject());

            shader.setUniform("model", Matrix4f.transform(position, rotation, scale));
            shader.setUniform("view", Matrix4f.view(TestConstants.ONE_VEC3F, TestConstants.ONE_VEC3F));
            shader.setUniform("projection", EngineCore.getGraphicsEngine().getProjectionMatrix());
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        }
    }

    public void draw(Vector3f position, Vector3f rotation, Vector3f scale, Camera camera) {

        if (assertSufficientBindings()) {
            GL30.glBindVertexArray(mesh.getVertexArrayObject());
            GL30.glEnableVertexAttribArray(0); // enable index positions for variables to be passed into shaders
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndicesBufferObject());

            shader.setUniform("model", Matrix4f.transform(position, rotation, scale));
            shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
            shader.setUniform("projection", EngineCore.getGraphicsEngine().getProjectionMatrix());
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        }
    }

    public void cleanUp() {
        unbindShader();
        disableBlend();
        unbindTexture();
        unbindMesh();
        disableBlend();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void destroy() {
        logger.success("Successfully destroyed renderer!", "", new ThreadState(Thread.currentThread()));
    }
}
