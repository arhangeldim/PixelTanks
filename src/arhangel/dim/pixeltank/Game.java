package arhangel.dim.pixeltank;

import arhangel.dim.pixeltank.connection.GameServer;
import arhangel.dim.pixeltank.game.RocketFactory;
import arhangel.dim.pixeltank.game.TankFactory;
import arhangel.dim.pixeltank.game.controller.InputController;
import arhangel.dim.pixeltank.game.controller.PhysicalController;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.protocol.SimpleProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 */
public class Game {
    private static Logger logger = LoggerFactory.getLogger(Game.class);

    public static final int GAME_SERVER_PORT = 19000;
    private GameServer server;

    public Game() {
        server = new GameServer(GAME_SERVER_PORT);
        // 50 x 50 tiles
        Scene scene = new Scene(30, 20, 20);
        TankFactory tankFactory = (TankFactory) TankFactory.getObjectFactory(scene);
        RocketFactory rocketFactory = RocketFactory.getObjectFactory(scene);

        InputController inputController = new InputController();
        inputController.setServer(server);
        inputController.setScene(scene);
        inputController.setRocketFactory(rocketFactory);
        inputController.setTankFactory(tankFactory);

        server.setClientListener(inputController);
        server.setProtocol(new SimpleProtocol());

        PhysicalController physicalController = new PhysicalController();
        physicalController.setScene(scene);

        inputController.setPhysicalController(physicalController);

    }

    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            logger.error("Server failed: {}\nShutting down.", e.getMessage());
            if (server != null) {
                server.stop();
            }
        }
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
