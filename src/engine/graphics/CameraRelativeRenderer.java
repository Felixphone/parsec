package engine.graphics;

import engine.engine.EngineCore;
import engine.graphics.objects.Mesh;
import engine.graphics.objects.Shader;
import engine.maths.Matrix4f;
import engine.maths.Vector3f;
import game.world.objects.GameObject;
import game.world.player.Camera;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class CameraRelativeRenderer {

    private static final String THIS_THREAD_SOURCE = "[ENGINE/RENDERER]";

    public CameraRelativeRenderer() {
        //Logger.success(THIS_THREAD_SOURCE, "Successfully initialised renderer");
    }

    public static void render(Camera camera, GameObject gameObject) {
        render(camera, gameObject.getShader(), gameObject.getMesh(), gameObject.getPosition(), gameObject.getRotation(), gameObject.getScale());
    }

    public static void render(Camera camera, Shader shader, Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
        GL30.glBindVertexArray(mesh.getVertexArrayObject());
        GL30.glEnableVertexAttribArray(0); // enable index positions for variables to be passed into shaders
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndicesBufferObject());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMaterial().getTextureID());
        GL13.glBlendFunc(GL13.GL_SRC_ALPHA, GL13.GL_ONE_MINUS_SRC_ALPHA); // enable alpha blending
        GL13.glEnable(GL13.GL_BLEND); // enable alpha blending | to get to work, render the transparent object last

        shader.setUniform("model", Matrix4f.transform(position, rotation, scale));
        shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
        shader.setUniform("projection", EngineCore.getGraphicsEngine().getProjectionMatrix());
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL13.glDisable(GL13.GL_BLEND);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

    }

    public void destroy() {
        //Logger.success(THIS_THREAD_SOURCE, "Successfully destroyed renderer!");
    }
}
