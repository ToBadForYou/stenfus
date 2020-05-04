package logic;

import logging.Logging;
import visual.MessageWindow;
import io.gsonfire.annotations.PostDeserialize;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

/**
 * This class handles the structure of the current area the player is currently in, it uses a double array of MapTile objects
 * these tiles represent the MapAreas logical layout, for example where it should be collision or not. MapArea also handles the
 * handles the different BattleCharacters actions for example moving and attacking. It also keep tracks of ExitPoints and calls
 * upon them when a player enters them
 */

public class MapArea implements Serializable
{
    private static final Logging LOGGER = new Logging("inventory");

    private MapTile[][] mapTiles = null;
    private List<SpawnPoint> enemySpawns = null, battleStartPos = null;
    private transient int width, height;
    private int id;
    private transient Battle currentBattle = null;
    private transient static Direction direction = new Direction();
    private int level;
    private transient EnemyAI enemyAi = null;
    private transient Player player = null;
    private transient List<MapListener> mapListeners = null;
    private List<ExitPoint> exitPoints = null;
    private List<Integer> enemyTypes = null;
    private transient List<BattleCharacter>[] teams = null;
    private transient List<NPC> npcList;
    private String name;

    public MapArea() {
        level = 1;
        name = "";
        npcList = new ArrayList<>();
    }

    @PostDeserialize
    public void postDeserializeLogic(){
        height = mapTiles.length;
        width = mapTiles[0].length;
        npcList = new ArrayList<>();
        teams = new List[getMaxTeamID(enemySpawns) + 1];
        teams[0] = new ArrayList<>();
        mapListeners = new ArrayList<>();
    }

    public void spawnEnemies(){
        List<BattleCharacter> enemies = new ArrayList<>();
        enemyAi = new EnemyAI(enemies, this);
        for (SpawnPoint enemySpawn : enemySpawns) {
            Enemy enemy = Enemy.randomEnemy(level, enemySpawn, enemyAi, enemyTypes);
            enemies.add(enemy);
            if(teams[enemy.getTeamID()] == null){
                List<BattleCharacter> newTeam = new ArrayList<>();
                teams[enemy.getTeamID()] = newTeam;
            }
            teams[enemy.getTeamID()].add(enemy);
        }
    }

    public int getMaxTeamID(List<SpawnPoint> list){
        if(list.isEmpty()){ return 0; }
        int max = list.get(0).getTeamID();
        for (int i = 1; i < list.size(); i++) {
            if(list.get(i).getTeamID() > max){
                max = list.get(i).getTeamID();
            }
        }
        return max;
    }

    public void despawnEnemies(){
        enemyAi = null;
        for (int i = 1; i < teams.length; i++) {
            teams[i] = null;
        }
    }

    //Action Functions

    public boolean attemptAttack(BattleCharacter attacker, BattleCharacter defender){
        if (defender != null && attacker.getAP() >= 2 && currentBattle != null &&
            attacker.equals(currentBattle.getcurrentBattler())) {
            int dx = (int) (defender.getPos().getX() - attacker.getPos().getX());
            int dy = (int) (defender.getPos().getY() - attacker.getPos().getY());
            if (dx * dx + dy * dy == 1 && defender.getTeamID() != attacker.getTeamID()) {
                attacker.reduceAP(2);
                attacker.attack(defender);
                checkIfDead(defender);
                notifyListeners(-1);
                return true;
            }
        }
        return false;
    }

    public void cleave(BattleCharacter attacker) {
        if(attacker.getAP() >= 4 && currentBattle != null && currentBattle.getcurrentBattler().equals(attacker)) {
            attacker.reduceAP(4);
            for (Direction.Dir direction: Direction.Dir.values()) {
                BattleCharacter defender = getBattleCharacterAt(attacker.directionToPoint(direction));
                if(defender != null){
                    attacker.attack(defender);
                    checkIfDead(defender);
                }
            }
            notifyListeners(-1);
        }
    }

    private void checkIfDead(final BattleCharacter defender) {
        if(defender.getHP() <= 0){
            teams[defender.getTeamID()].remove(defender);
            currentBattle.removeBattler(defender);
            enemyAi.tryremoveEnemy(defender);
            if(teams[defender.getTeamID()].isEmpty()){
                if (defender.getTeamID() == player.getTeamID()){ gameOver(); }
                else{
                    for (Enemy deadEnemy : currentBattle.getDeadEnemies()) {
                        player.gainXp((int)Math.pow(deadEnemy.getLevel() * 9, 6 / 5));
                    }
                    try {
                        player.addItemToInventory(ItemFactory.createItem(0));
                    } catch (DataFormatException e){
                        LOGGER.log(Level.WARNING, "Failed to add item to inventory: " + e.getMessage());
                        MessageWindow.showMessage("Failed to add item to inventory: " + e.getMessage(), "Creating item error");
                    }
                    player.updateQuests(currentBattle.getDeadEnemies());
                }
                currentBattle = null;
            }
        }
    }

    private void gameOver(){
        player = null;
        notifyListeners(-2);
        MessageWindow.showMessage("Game Over - Loading from last savepoint", "Player Death");
        }



    public boolean moveCharacter(Point p, BattleCharacter mover){
        BattleCharacter opponent = getBattleCharacterAt(p);

        if (opponent == null && !getCollisionAt((int) p.getX(), (int) p.getY())) {
            NPC npc = getNPCAt(p);
            if (npc != null && currentBattle == null){
                if(mover.equals(player)) {
                    npc.interaction(player);
                }
            }
            else {
                ExitPoint exitPoint = getExitPointAtLocation(p.getX(), p.getY());
                if (exitPoint != null && currentBattle == null) {
                    notifyListeners(exitPoint.getMapAreaID());
                    mapListeners = new ArrayList<>();
                } else {
                    mover.move(p);
                    notifyListeners(-1);
                    return true;
                }
            }
        }
        else if (opponent != null && opponent.getTeamID() != mover.getTeamID()){
            startBattle(mover, opponent);
        }
        notifyListeners(-1);
        return false;
    }

    public boolean moveInBattle(final Direction.Dir direction, final BattleCharacter battleCharacter) {
       return moveInBattle(battleCharacter.directionToPoint(direction), battleCharacter);
    }

    public boolean moveInBattle(final Point p, final BattleCharacter battleCharacter){
        if (battleCharacter.getAP() > 0 && battleCharacter.equals(currentBattle.getcurrentBattler()) && moveCharacter(p, battleCharacter)){
            battleCharacter.reduceAP(1);
            return true;
        }
        return false;
    }

    public void startBattle(BattleCharacter bc1, BattleCharacter bc2) {
        if (currentBattle == null){
            System.out.println("start");
            player.restoreAP();
            currentBattle = new Battle(teams[bc1.getTeamID()], teams[bc2.getTeamID()], battleStartPos);
            for (int i = 0; i < teams[1].size(); i++) {
                Enemy enemy = (Enemy)teams[1].get(i);
                enemy.setStatusColor(Color.RED);
            }
            notifyListeners(-1);
        }
    }

    public void switchPlayerState() {
        player.switchState();
        notifyListeners(-1);
    }

    public void useItem(int i){
        player.useItem(i);
        notifyListeners(-1);
    }

    // WorldEditor functions

    public void toggleEnemyType(int id, boolean toggle){
        int existIndex = enemyTypes.indexOf(id);
        if(toggle && existIndex == -1) {
            enemyTypes.add(id);
        } else if (!toggle && existIndex != -1) {
            enemyTypes.remove(existIndex);
        }
    }

    public void setTile(int x, int y, int id, boolean collision){
        MapTile tile = mapTiles[y][x];
        tile.setTile(id, collision);
    }

    public void setObject(int x, int y, int id, boolean collision){
        MapTile tile = mapTiles[y][x];
        tile.setObject(id, collision);
    }

    public void toggleExitPoint(int x, int y, int mapID){
        List<ExitPoint> removePoint = new ArrayList<>();
        boolean exists = false;
        for (ExitPoint exitPoint : exitPoints) {
            if ((int) exitPoint.getX() == x && (int) exitPoint.getY() == y) {
                removePoint.add(exitPoint);
            }
            if (exitPoint.getMapAreaID() == mapID){
                exists = true;
                break;
            }
        }
        if (removePoint.isEmpty()) {
            if (!exists) {
                ExitPoint newExit = new ExitPoint();
                newExit.setLocation(x, y);
                newExit.setMapAreaID(mapID);
                exitPoints.add(newExit);
            }
        } else {
            exitPoints.remove(removePoint.get(0));
        }
    }

    public void toggleSpawnPoint(int x, int y, int miscID, int teamID){
        List<SpawnPoint> removePoint = new ArrayList<>();
        List<SpawnPoint> spawnList;
        if (miscID == 0){
            spawnList = battleStartPos;
        }
        else {
            spawnList = enemySpawns;
        }
        for (SpawnPoint spawnPoint : spawnList) {

            if ((int) spawnPoint.getX() == x && (int) spawnPoint.getY() == y) {
                removePoint.add(spawnPoint);
                break;
            }
        }
        if (removePoint.isEmpty()) {
            spawnList.add(new SpawnPoint(x, y, teamID));
        } else {
            spawnList.remove(removePoint.get(0));
        }
    }

    public void setupEmptyMapArea(int width, int height, int id){
        this.width = width;
        this.height = height;
        this.id = id;
        this.enemySpawns = new ArrayList<>();
        this.battleStartPos = new ArrayList<>();
        this.exitPoints = new ArrayList<>();
        this.enemyTypes = new ArrayList<>();
        mapTiles = new MapTile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapTiles[y][x] = new MapTile(0, -1, false);
            }
        }
    }

    // Getter Functions

    public List<NPC> getNpcList() { return npcList; }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyAI getEnemyAi() { return enemyAi; }

    public Battle getCurrentBattle() {
        return currentBattle;
    }

    public List<SpawnPoint> getBattleStartPos() {
        return battleStartPos;
    }

    public List<SpawnPoint> getEnemySpawns() { return enemySpawns; }

    public List<BattleCharacter>[] getTeams(){ return teams; }

    public int getId() {
        return id;
    }

    public List<ExitPoint> getExitPoints() {
        return exitPoints;
    }

    public List<Integer> getEnemyTypes() {
        return enemyTypes;
    }

    public List<Point> getTileNeighbors(Point p) {
        List<Point> neighbors = new ArrayList<>();
        for (Direction.Dir dir : Direction.Dir.values()) {
            Point neighborPoint = direction.translatePoint(dir, new Point(p));
            BattleCharacter bc = getBattleCharacterAt(neighborPoint);
            if(!getCollisionAt(neighborPoint) && (bc == null || bc.equals(player))){
                neighbors.add(neighborPoint);
            }
        }
        return neighbors;
    }

    public BattleCharacter getBattleCharacterAt(Point p) {
        for (List<BattleCharacter> team : teams) {
            for (BattleCharacter battlecharacter : team) {
                if (battlecharacter.getPos().equals(p)) { return battlecharacter; }
            }
        }
        return null;
    }

    public Boolean getCollisionAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) { return true; }
        return mapTiles[y][x].getCollision();
    }

    public boolean getCollisionAt(Point p){
        return getCollisionAt((int)p.getX(), (int)p.getY());
    }

    public NPC getNPCAt(Point p){
        for (NPC npc : npcList) {
            if (npc.getPos().equals(p)){ return npc; }
        }
        return null;
    }

    public ExitPoint getExitPointAtLocation(double x, double y){
        for (ExitPoint exitPoint : exitPoints) {
            if ((int)exitPoint.getX() == (int)x && (int)exitPoint.getY() == (int)y) {
                return exitPoint;
            }
        }
        return null;
    }

    public ExitPoint getExitPointByMapID(int id){
        for (ExitPoint exitPoint : exitPoints) {
            if (exitPoint.getMapAreaID() == id) {
                return exitPoint;
            }
        }
        return null;
    }

    public MapTile getMapTile(int x, int y) { return mapTiles[y][x]; }

    // Setters

    public void setName(final String mapName) {
        name = mapName;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setNpcList(List<NPC> list){
        npcList = list;
    }

    public void setPlayer(Player player) {
        if(this.player == null) {
            this.player = player;
            teams[0].add(player);
        }
    }

    public void addMapListener(final MapListener mapListener) {
        mapListeners.add(mapListener);
    }

    private void notifyListeners(int i){
        for (MapListener mapListener : mapListeners) {
            mapListener.mapChanged(i);
        }
    }
}
