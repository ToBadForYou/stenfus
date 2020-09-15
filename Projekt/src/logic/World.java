package logic;

import resources.TextResource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.GsonFireBuilder;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * This class represents the games world, it includes a list of all MapAreas, the player object and a list of NPCs and all
 * quests. It is also the world that handles the movement of the player between different MapAreas and the actual loading of the
 * player object.
 */

public class World implements MapListener, Serializable
{
    private List<MapArea> maps;
    private String name;
    private transient Player player = null; //Spelar objektet laddas in efter skapelsen av World
    private List<InteractableCharacter> interactableCharacters;
    /**
     * Statiskt konstant för "rut"storleken
     */
    public transient static final int TILE_SIZE = 64;
    private transient static final int DEFAULT_MAP_TILES = 15;
    private static final List<Quest> ALL_QUESTS = loadQuests();

    public World(String name) {
        this.maps = new ArrayList<>();
        this.interactableCharacters = new ArrayList<>();
        this.name = name;
    }

    public void addMapArea(MapArea mapArea){
        maps.add(mapArea);
    }

    public void reloadPlayer(){
        TextResource playerLoader = new TextResource();
        String playerData = playerLoader.getJSONData("playerdata", "1");

        GsonFireBuilder fireBuilder = new GsonFireBuilder();
        fireBuilder.enableHooks(Player.class);
        fireBuilder.enableHooks(AbstractItem.class);

        GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
        Gson gson = gsonBuilder.create();

        Player loadedPlayer = gson.fromJson(playerData, new TypeToken<Player>() {}.getType());
        MapArea curMap = getMapArea(loadedPlayer.getCurrentMap());
        curMap.despawnEnemies();
        if (curMap.getId() != player.getCurrentMap()) {
            curMap.addMapListener(this);
        }
        player.copyData(loadedPlayer);
        curMap.setPlayer(player);
        curMap.spawnEnemies();
        setUpNPCs(curMap, curMap.getId());
    }

    public MapArea loadPlayer(String playerData){
        GsonFireBuilder fireBuilder = new GsonFireBuilder();
        fireBuilder.enableHooks(Player.class);
        fireBuilder.enableHooks(AbstractItem.class);
        fireBuilder.enableHooks(EnumMap.class);
        fireBuilder.enableHooks(Armor.class);
        fireBuilder.enableHooks(Armor.Type.class);

        GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
        Gson gson = gsonBuilder.create();
        player = gson.fromJson(playerData, new TypeToken<Player>() {}.getType());
        MapArea curMap = getMapArea(player.getCurrentMap());
        curMap.addMapListener(this);
        curMap.setPlayer(player);
        curMap.spawnEnemies();
        setUpNPCs(curMap, curMap.getId());

        return curMap;
    }

    public void savePlayer(){
        TextResource dataSaver = new TextResource();
        Gson gson = new Gson();
        player.saveInventory();
        String dataJSON = gson.toJson(player);
        dataSaver.saveJSONData("playerdata", "1", dataJSON);
    }

    public void moveMapArea(int mapID){
        MapArea newMap = getMapArea(mapID);
        newMap.spawnEnemies();
        MapArea oldMap = getMapArea(player.getCurrentMap());
        oldMap.despawnEnemies();
        newMap.addMapListener(this);
        ExitPoint entryPoint = newMap.getExitPointByMapID(player.getCurrentMap());
        player.setCurrentMap(mapID);
        newMap.setPlayer(player);
        player.move(entryPoint);

        savePlayer();
        setUpNPCs(newMap, mapID);
    }

    public void setUpNPCs(MapArea map, int mapID){
        List<InteractableCharacter> mapInteractableCharacters = new ArrayList<>();
        for (InteractableCharacter interactableCharacter : interactableCharacters) {
            if(interactableCharacter.getMapID() == mapID){ mapInteractableCharacters.add(interactableCharacter); }
        }
        map.setInteractableCharacters(mapInteractableCharacters);
    }

    public void loadWorld(String mapData, String npcData) {
        GsonFireBuilder fireBuilder = new GsonFireBuilder();
        fireBuilder.enableHooks(MapTile.class);
        fireBuilder.enableHooks(MapArea.class);
        fireBuilder.enableHooks(AbstractInteractableCharacter.class);

        GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
        Gson gson = gsonBuilder.create();

        maps = gson.fromJson(mapData, new TypeToken<List<MapArea>>() {}.getType());
        interactableCharacters = gson.fromJson(npcData, new TypeToken<List<QuestGiver>>() {}.getType());
    }

    public void saveWorld(){
        TextResource dataSaver = new TextResource();
        Gson gson = new Gson();
        String mapData = gson.toJson(maps);
        dataSaver.saveJSONData("world", name, mapData);

        String npcData = gson.toJson(interactableCharacters);
        dataSaver.saveJSONData("worldnpcs", name, npcData);
    }

    private static List<Quest> loadQuests() {
        TextResource worldLoader = new TextResource();
        String jsonData = worldLoader.getJSONData("objects", "quests");
        GsonFireBuilder fireBuilder = new GsonFireBuilder();

        fireBuilder.enableHooks(KillObjective.class);

        GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonData, new TypeToken<List<Quest>>() {}.getType());
    }



    public MapArea getMapArea(int mapID){
        return maps.get(mapID);
    }

    public static Quest getQuest(int id){
        return ALL_QUESTS.get(id);
    }

    public static int getQuestsSize(){return ALL_QUESTS.size();}

    public int size(){
        return maps.size();
    }

    public void addEmptyMapArea(){
        MapArea map = new MapArea();
        map.setUpEmptyMapArea(DEFAULT_MAP_TILES, DEFAULT_MAP_TILES, size());
        addMapArea(map);
    }


    public void toggleNPC(int mapID, int x, int y, int id, List<Integer> questIDs){
        boolean exists = false;
        InteractableCharacter existingInteractableCharacter = null;
        for (InteractableCharacter interactableCharacter : interactableCharacters) {
            if (interactableCharacter.getID() == id){
                Point npcPos = (Point) interactableCharacter.getPos();
                if (interactableCharacter.getMapID() == mapID && (int) npcPos.getX() == x && (int) npcPos.getY() == y) {
                    existingInteractableCharacter = interactableCharacter;
                }
                exists = true;
                break;
            }
        }
        if (exists && existingInteractableCharacter != null){
            interactableCharacters.remove(existingInteractableCharacter);
        } else if (!exists){
            interactableCharacters.add(new QuestGiver(id, mapID, new Point(x, y), questIDs));
        }
        setUpNPCs(getMapArea(mapID), mapID);
    }

    @Override
    public void mapChanged(int i) {
        if (i > -1) {
            moveMapArea(i);
        } else if (i == -2){
            reloadPlayer();
        }
    }

    public String getName() {
        return name;
    }
}
