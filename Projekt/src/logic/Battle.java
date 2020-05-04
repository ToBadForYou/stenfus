package logic;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is created in MapArea and handles the turnorder in a battle
 */
public class Battle
{
    private List<BattleCharacter> battlers;
    private int currentBattlerIndex;
    private List<Enemy> deadEnemies;

    public Battle(final List<BattleCharacter> team1, final List<BattleCharacter> team2, List<SpawnPoint> battleStartPos) {
        List<BattleCharacter> teamA = new ArrayList<>(team1);
        List<BattleCharacter> teamB = new ArrayList<>(team2);
        this.battlers = new ArrayList<>();
        battlers.addAll(team1);
        battlers.addAll(team2);
        deadEnemies = new ArrayList<>();
        for (SpawnPoint spawnPoint : battleStartPos) {
            int i  = spawnPoint.getTeamID();
            BattleCharacter bc = null;
            if(i == 0){
                if (!teamA.isEmpty()){ bc = teamA.remove(0); }
            }
            else if(i == 1){
                if (!teamB.isEmpty()){ bc = teamB.remove(0); }
            }
            if(bc != null){ bc.move(spawnPoint);}

            if(teamA.isEmpty() && teamB.isEmpty()){ break; }

        }
        currentBattlerIndex = 0;
    }

    public BattleCharacter getcurrentBattler(){
        return battlers.get(currentBattlerIndex);
    }

    public List<BattleCharacter> getBattlers() {
        return battlers;
    }

    public List<Enemy> getDeadEnemies() { return deadEnemies; }

    public boolean isBattling(BattleCharacter bc){
        return battlers.contains(bc);
    }

    public void removeBattler(BattleCharacter bc){
        if (battlers.indexOf(bc) < currentBattlerIndex){
            currentBattlerIndex -= 1;
        }
        if (bc.getTeamID() != 0){ deadEnemies.add((Enemy)bc); }
        battlers.remove(bc);
    }

    public void endTurn(BattleCharacter bc){
        if(getcurrentBattler().equals(bc)) {
            currentBattlerIndex = (currentBattlerIndex + 1) % battlers.size();
            getcurrentBattler().restoreAP();
        }
    }

    public void update(){
        if(!getcurrentBattler().isType("player")){
            Enemy enemy = (Enemy) getcurrentBattler();
            enemy.getEnemyAi().decide(enemy);
        }
    }
}
