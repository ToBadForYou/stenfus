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
        this.battlers = new ArrayList<>();
        battlers.addAll(team1);
        battlers.addAll(team2);
        deadEnemies = new ArrayList<>();
        List<List<BattleCharacter>> teams = new ArrayList<>();
        teams.add(new ArrayList<>(team1));
        teams.add(new ArrayList<>(team2));
        for (SpawnPoint spawnPoint : battleStartPos) {
            int i  = spawnPoint.getID();
            BattleCharacter bc = null;
            if (!teams.get(i).isEmpty()){ bc = teams.get(i).remove(0); }

            if(bc != null){ bc.move(spawnPoint);}

            if(teams.get(0).isEmpty() && teams.get(1).isEmpty()){ break; }

        }
        currentBattlerIndex = 0;
    }

    public BattleCharacter getCurrentBattler(){
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
        if(getCurrentBattler().equals(bc)) {
            currentBattlerIndex = (currentBattlerIndex + 1) % battlers.size();
            getCurrentBattler().restoreAP();
        }
    }

    public void update(){
        if(!getCurrentBattler().isType("player")){
            Enemy enemy = (Enemy) getCurrentBattler();
            enemy.getEnemyAi().decide(enemy);
        }
    }
}
