package engine.graphics.objects;

import engine.dataItems.exceptions.FailedToCompileShaderException;
import engine.dataItems.exceptions.FailedToLinkShaderException;
import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.maths.Matrix4f;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import engine.utilities.resources.ResourceLoader;
import engine.utilities.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import tracerUtils.data.ThreadState;

import java.io.IOException;
import java.nio.FloatBuffer;

public class Shader {

    private ResourceLocation vertexShaderResourceLocation, fragmentShaderResourceLocation;
    private String vertexShaderFile, fragmentShaderFile;
    private int vertexShaderID, fragmentShaderID, programID;

    public Shader(ResourceLocation vertexShaderResourceLocation, ResourceLocation fragmentShaderResourceLocation) throws IOException, RequiredFileNotFoundException {

        this.vertexShaderResourceLocation = vertexShaderResourceLocation;
        this.fragmentShaderResourceLocation = fragmentShaderResourceLocation;

        vertexShaderFile = ResourceLoader.loadAsString(vertexShaderResourceLocation.getPath());
        fragmentShaderFile = ResourceLoader.loadAsString(fragmentShaderResourceLocation.getPath());

    }

    public void createShader() throws FailedToCompileShaderException, RequiredFileNotFoundException, FailedToLinkShaderException {

        programID = GL20.glCreateProgram();

        vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShaderID, vertexShaderFile);
        GL20.glCompileShader(vertexShaderID);
        if (GL20.glGetShaderi(vertexShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new FailedToCompileShaderException("Failed to compile shader", "Failed to compile shader from vertex file: \"" + vertexShaderResourceLocation.getPath() + "\" Error: " + GL20.glGetShaderInfoLog(vertexShaderID), "Expression ( GL20.glGetShaderi(vertexShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }


        fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShaderID, fragmentShaderFile);
        GL20.glCompileShader(fragmentShaderID);
        if (GL20.glGetShaderi(fragmentShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new FailedToCompileShaderException("Failed to compile shader", "Failed to compile shader from fragment file: \"" + fragmentShaderResourceLocation.getPath() + "\" Error: " + GL20.glGetShaderInfoLog(fragmentShaderID), "Expression ( GL20.glGetShaderi(fragmentShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }


        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);

        GL20.glLinkProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new FailedToLinkShaderException("Failed to link shader", "Failed to link shader from vertex file: \"" + vertexShaderResourceLocation.getPath() + "\" and fragment file: \"" + fragmentShaderResourceLocation.getPath() + "\" to program id: \"" + programID + "\" Error: " + GL20.glGetProgramInfoLog(programID), "Expression ( GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        GL20.glValidateProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            throw new FailedToLinkShaderException("Failed to validate linking of shader", "Failed to validate linking of shader from vertex file: \"" + vertexShaderResourceLocation.getPath() + "\" and fragment file: \"" + fragmentShaderResourceLocation.getPath() + "\" to program id: \"" + programID + "\" Error: " + GL20.glGetProgramInfoLog(programID), "Expression ( GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
    }

    public int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(programID, name);
    }

    public void setUniform(String name, float value) {
        GL20.glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform(String name, int value) {
        GL20.glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform(String name, boolean value) {
        GL20.glUniform1i(getUniformLocation(name), value ? 1 : 0);
    }

    public void setUniform(String name, Vector2f value) {
        GL20.glUniform2f(getUniformLocation(name), value.getX(), value.getY());
    }

    public void setUniform(String name, Vector3f value) {
        GL20.glUniform3f(getUniformLocation(name), value.getX(), value.getY(), value.getZ());
    }

    public void setUniform(String name, Matrix4f value) {
        FloatBuffer matrix = MemoryUtil.memAllocFloat(value.SIZE * value.SIZE);
        matrix.put(value.getAll()).flip();
        GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
    }

    public int getProgramID() {
        return programID;
    }

    public void destroy() {
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }
}
