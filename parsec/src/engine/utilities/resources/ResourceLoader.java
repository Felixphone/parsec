package engine.utilities.resources;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import tracerUtils.data.ThreadState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ResourceLoader {

    public static String loadAsString(String path) throws IOException, RequiredFileNotFoundException {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResourceAsStream(path))));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();

        } catch (NullPointerException e) {
            throw new RequiredFileNotFoundException("File could not be found", "File could not be found. Given path (\"" + path + "\") is likely to be incorrect", "NullPointerException thrown", new ThreadState(Thread.currentThread()));
        }
    }

    public static Texture getTexture(String path) throws RequiredFileNotFoundException {
        try {

            return TextureLoader.getTexture(path.split("[.]")[1], Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResourceAsStream(path)), GL11.GL_NEAREST);

        } catch (NullPointerException | IOException e) {
            throw new RequiredFileNotFoundException("File could not be found", "File could not be found. Given path (\"" + path + "\") is likely to be incorrect", "NullPointerException or IOException thrown", new ThreadState(Thread.currentThread()));
        }
    }

    public static Texture getTexture(ResourceLocation resourceLocation) throws RequiredFileNotFoundException {
        return getTexture(resourceLocation.getPath());
    }
}
