package game.world.scenes.scenes;

import game.dataItems.interfaces.PlayerLogic;
import game.world.player.Camera;

public interface Scene {

    public void update();

    public void render(Camera camera);

    public PlayerLogic getPlayerLogic();

}
