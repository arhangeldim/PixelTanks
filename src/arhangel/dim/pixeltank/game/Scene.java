package arhangel.dim.pixeltank.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/****/
public class Scene {
    Logger logger = LoggerFactory.getLogger(Scene.class);
    private Set<Unit> units = new HashSet<>();
    private Map<Integer, Unit> unitMap = new HashMap<>();

    public Set<Unit> getUnits() {
        return units;
    }

    public void setUnits(Set<Unit> units) {
        this.units = units;
    }

    public void addUnit(Unit unit) {
        logger.info("Added new unit {}", unit);
        units.add(unit);
        unitMap.put(unit.id, unit);
    }

    public Unit getUnit(int id) {
        Unit unit = unitMap.get(id);
        logger.info("Get unit by id: {}, {}", id, unit);
        return unit;
    }
}
