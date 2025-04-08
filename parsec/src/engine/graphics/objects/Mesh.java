package engine.graphics.objects;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private Vertex[] vertices; //vertex array
    private int[] indices; //order that vertices are drawn in
    private Material material;
    private int vertexArrayObject;
    private int vertexBufferObject;
    private int indicesBufferObject;
    private int colourBufferObject;
    private int textureBufferObject;

    public Mesh(Vertex[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
    }

    public void generateMesh() throws RequiredFileNotFoundException {

        material.createMaterial();

        vertexArrayObject = GL30.glGenVertexArrays(); // generate vertexArrayObject
        GL30.glBindVertexArray(vertexArrayObject); // bind vertexArrayObject

        //======= verticies ========

        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        float[] vertexData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            vertexData[i * 3] = vertices[i].getPosition().getX();
            vertexData[i * 3 + 1] = vertices[i].getPosition().getY();
            vertexData[i * 3 + 2] = vertices[i].getPosition().getZ();
        }
        verticesBuffer.put(vertexData).flip();

        vertexBufferObject = storeData(verticesBuffer, 0, 3);

        //======= colour ==========

        FloatBuffer colourBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        float[] colourData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            colourData[i * 3] = vertices[i].getColour().getRed();
            colourData[i * 3 + 1] = vertices[i].getColour().getGreen();
            colourData[i * 3 + 2] = vertices[i].getColour().getBlue();
        }
        colourBuffer.put(colourData).flip();

        colourBufferObject = storeData(colourBuffer, 1, 3);

        //======== texture coords =======

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        float[] textureData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i * 2] = vertices[i].getTextureCoord().getX();
            textureData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
        }
        textureBuffer.put(textureData).flip();

        textureBufferObject = storeData(textureBuffer, 2, 2);


        // ====== indicies ========

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        indicesBufferObject = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferObject);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    private int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public Material getMaterial() {
        return material;
    }

    public int getVertexArrayObject() {
        return vertexArrayObject;
    }

    public int getVertexBufferObject() {
        return vertexBufferObject;
    }

    public int getColourBufferObject() {
        return colourBufferObject;
    }

    public int getTextureBufferObject() {
        return textureBufferObject;
    }

    public int getIndicesBufferObject() {
        return indicesBufferObject;
    }

    public void destroy() {
        GL15.glDeleteBuffers(vertexBufferObject);
        GL15.glDeleteBuffers(colourBufferObject);
        GL15.glDeleteBuffers(indicesBufferObject);
        GL15.glDeleteBuffers(textureBufferObject);

        GL30.glDeleteVertexArrays(vertexArrayObject);

        material.destroy();
    }
}
