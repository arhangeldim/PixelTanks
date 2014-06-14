package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Player;

/**
 *
 */
public interface GameEventListener {

    public void onMove(GameObject object);
    public void onLogon(Player player);
    public void onLogout(Player player);
    public void onRocketHit(GameObject victim, GameObject rocket);
}
