package engine.graphics.UI.UIComponents.graphical;

import engine.graphics.UI.UIComponents.UIComponent;
import engine.graphics.objects.Mesh;
import engine.maths.Vector3f;
import org.newdawn.slick.opengl.Texture;

public abstract class GraphicalUIComponent extends UIComponent {
    public GraphicalUIComponent(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        super(position, rotation, scale, mesh);
    }

    /*private float x;
    private float y;
    private float width;
    private float height;

    private float rotation = 0.0f;

    private ResourceLocation resourceLocation = new ResourceLocation("");
    private Texture texture;
    private int textureID;

    public GraphicalUIComponent(float x, float y, float width, float height, ResourceLocation resourceLocation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.resourceLocation = resourceLocation;
    }

    public GraphicalUIComponent(float x, float y, float width, float height, float rotation, ResourceLocation resourceLocation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.resourceLocation = resourceLocation;
    }

    public final void loadTexture() throws RequiredFileNotFoundException {
        texture = ResourceLoader.getTexture(resourceLocation);
        textureID = texture.getTextureID();
    }

    public final void setTexture(ResourceLocation resourceLocation){
        this.resourceLocation = resourceLocation;
    }

    public final void render() {
        Renderer renderer = GraphicsEngine.getRenderer();
        renderer.bindShader(ShaderManager.GRADIENT_SHADER());
        renderer.bindTexture(textureID);
        renderer.enableBlend();
        renderer.staticDraw(new Vector3f(x, 0.0f, y), new Vector3f(rotation, 0.0f, 0.0f), new Vector3f(width, height, 0.0f));
        renderer.cleanUp();
    }

    public abstract void update();

    public void destroy() {
        texture.release();
    }*/
}
