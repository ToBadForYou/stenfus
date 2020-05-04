package logic;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class handles the the decision making of all the enemies inside the MapArea
 */

public class EnemyAI

{
    private List<BattleCharacter> enemies;
    private MapArea currentMap;
    private List<Direction.Dir> directions;
    private Random rnd;

    public EnemyAI(final List<BattleCharacter> enemies, final MapArea currentMap) {
	this.enemies = enemies;
	this.currentMap = currentMap;
	rnd = new Random();
	directions = Arrays.asList(Direction.Dir.values());
    }

    public void moveAll(){
	for (BattleCharacter bc : enemies) {
	    Enemy enemy = (Enemy) bc;
	    List<Point> path = findShortestPath(enemy);
	    if (path != null && path.size() <= 6){
	        currentMap.moveCharacter(path.get(1), enemy);
	        enemy.setStatusColor(Color.RED);
	    }
	    else if (enemy.checkSkipMove()) {
		int dir = rnd.nextInt(directions.size());
		currentMap.moveCharacter(enemy.directionToPoint(directions.get(dir)), enemy);
		enemy.setStatusColor(Color.YELLOW);
	    }
	}
    }

    public void decide(Enemy enemy){
        if (!currentMap.attemptAttack(enemy, currentMap.getPlayer())){
            List<Point> path = findShortestPath(enemy);
            if(path== null ||!currentMap.moveInBattle(path.get(1), enemy )){
                currentMap.getCurrentBattle().endTurn(enemy);
            }
        }
        if (enemy.getAP() <= 0){
            currentMap.getCurrentBattle().endTurn(enemy);
        }
    }

    private List<Point> findShortestPath(Enemy enemy){
	Set<Point> visited = new HashSet<>();
	Queue<List<Point>> queue = new LinkedList<>();
	List<Point> current = new ArrayList<>();
	current.add(enemy.getPos());
	queue.add(current);
	if(currentMap.getPlayer() == null){
	    return null;
	}
	Point target = currentMap.getPlayer().getPos();



	while(!queue.isEmpty()) {
	    List<Point> path = queue.remove();
	    Point node = path.get(path.size() - 1);

	    for (Point neighbor : currentMap.getTileNeighbors(node)) {
		if (!visited.contains(neighbor)) {
		    List<Point> newPath = new ArrayList<>(path);
		    newPath.add(neighbor);
		    visited.add(neighbor);
		    if (neighbor.equals(target)) { return newPath; } else { queue.add(newPath); }
		}
	    }
	}
	return null;
    }


    public void tryremoveEnemy(final BattleCharacter bc) {
        enemies.remove(bc);
    }
}
