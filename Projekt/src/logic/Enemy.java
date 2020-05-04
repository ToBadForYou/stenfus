package logic;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is used to create an Enemy.
 * The Enemies are created in the MapArea and exist for the Player to kill.
 */

public class Enemy extends AbstractBattleCharacter
{
    private EnemyAI enemyAi;
    private static Random rnd = new Random();
    private int skipMove;

    public Enemy(int level, SpawnPoint spawnPoint, EnemyAI enemyAi, int id)
    {
	super(level, new Point(spawnPoint), id, "enemies", spawnPoint.getTeamID(), objectData.getData(id, "enemies"));
	this.enemyAi = enemyAi;
	this.skipMove = 0;
    }

    public static Enemy randomEnemy(int level, SpawnPoint spawnPoint, EnemyAI enemyAi, List<Integer> enemyTypes){
        return new Enemy(level, spawnPoint, enemyAi, enemyTypes.get(rnd.nextInt(enemyTypes.size())));
    }

    public EnemyAI getEnemyAi() {
	return enemyAi;
    }

    public int getID(){return id;}

    public void setStatusColor(Color statusColor){
        this.statusColor = statusColor;
    }

    public boolean checkSkipMove(){
        if (skipMove == 5) {
            skipMove = 0;
            return true;
        }
        else {
            skipMove ++;
            return false;
        }

    }
}
