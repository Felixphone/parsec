package engine.graphics;

import engine.dataItems.exceptions.FailedToCompileShaderException;
import engine.dataItems.exceptions.FailedToLinkShaderException;
import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.dataItems.exceptions.fatal.FailedToInitShaderException;
import engine.engine.EngineCore;
import engine.graphics.objects.Shader;
import engine.utilities.resources.ResourceLocation;
import tracerUtils.data.ThreadState;

import java.io.IOException;

public final class ShaderManager {

    private ShaderManager(){}

    private static Shader UNIFORM_COLOUR_SHADER;
    private static Shader GRADIENT_SHADER;
    private static Shader TEXTURE_SHADER;

    private static final ResourceLocation UNIFORM_COLOUR_SHADER_VERTEX_RESOURCE_LOCATION = new ResourceLocation("shaders/mainVertex.vert");
    private static final ResourceLocation UNIFORM_COLOUR_SHADER_FRAGMENT_RESOURCE_LOCATION = new ResourceLocation("shaders/colourFragment.frag");

    private static final ResourceLocation GRADIENT_SHADER_VERTEX_RESOURCE_LOCATION = new ResourceLocation("shaders/mainVertex.vert");
    private static final ResourceLocation GRADIENT_SHADER_FRAGMENT_RESOURCE_LOCATION = new ResourceLocation("shaders/gradientFragment.frag");

    private static final ResourceLocation TEXTURE_SHADER_VERTEX_RESOURCE_LOCATION = new ResourceLocation("shaders/mainVertex.vert");
    private static final ResourceLocation TEXTURE_SHADER_FRAGMENT_RESOURCE_LOCATION = new ResourceLocation("shaders/textureFragment.frag");


    static void initialiseShaders() {

        try {
            UNIFORM_COLOUR_SHADER = new Shader(UNIFORM_COLOUR_SHADER_VERTEX_RESOURCE_LOCATION, UNIFORM_COLOUR_SHADER_FRAGMENT_RESOURCE_LOCATION);
        } catch (IOException | RequiredFileNotFoundException e) {
            throw new FailedToInitShaderException("Shader file could not be found", "Shader file \"shaders/mainVertex.vert\" could not be found for shader: UNIFORM_COLOUR_SHADER. " + e.getMessage(), "IOException or RequiredFileNotFoundException thrown", new ThreadState(Thread.currentThread()));
        }
        try {
            UNIFORM_COLOUR_SHADER().createShader();
        } catch (FailedToCompileShaderException | FailedToLinkShaderException | RequiredFileNotFoundException e) {
            throw new FailedToInitShaderException(e.getMessage(), e.getDetailedMessage(), "FailedToCompileShaderException or FailedToLinkShaderException thrown", new ThreadState(Thread.currentThread()));
        }

        try {
            GRADIENT_SHADER = new Shader(GRADIENT_SHADER_VERTEX_RESOURCE_LOCATION, GRADIENT_SHADER_FRAGMENT_RESOURCE_LOCATION);
        } catch (IOException | RequiredFileNotFoundException e) {
            throw new FailedToInitShaderException("Shader file could not be found", "Shader file \"shaders/mainVertex.vert\" could not be found for shader: GRADIENT_SHADER", "IOException or RequiredFileNotFoundException thrown", new ThreadState(Thread.currentThread()));
        }
        try {
            GRADIENT_SHADER().createShader();
        } catch (FailedToCompileShaderException | FailedToLinkShaderException | RequiredFileNotFoundException e) {
            throw new FailedToInitShaderException(e.getMessage(), e.getDetailedMessage(), "FailedToCompileShaderException or FailedToLinkShaderException thrown", new ThreadState(Thread.currentThread()));
        }

        try {
            TEXTURE_SHADER = new Shader(TEXTURE_SHADER_VERTEX_RESOURCE_LOCATION, TEXTURE_SHADER_FRAGMENT_RESOURCE_LOCATION);
        } catch (IOException | RequiredFileNotFoundException e) {
            throw new FailedToInitShaderException("Shader file could not be found", "Shader file \"shaders/mainVertex.vert\" could not be found for shader: TEXTURE_SHADER", "IOException or RequiredFileNotFoundException thrown", new ThreadState(Thread.currentThread()));
        }
        try {
            TEXTURE_SHADER.createShader();
        } catch (FailedToCompileShaderException | FailedToLinkShaderException | RequiredFileNotFoundException e) {
            throw new FailedToInitShaderException(e.getMessage(), e.getDetailedMessage(), "FailedToCompileShaderException or FailedToLinkShaderException thrown", new ThreadState(Thread.currentThread()));
        }

        EngineCore.getLogger().success("Shaders successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    public static Shader UNIFORM_COLOUR_SHADER() {
        return UNIFORM_COLOUR_SHADER;
    }

    public static Shader GRADIENT_SHADER() {
        return GRADIENT_SHADER;
    }

    public static Shader TEXTURE_SHADER() {
        return TEXTURE_SHADER;
    }

}
