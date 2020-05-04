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
    private List<NPC> npcs;
    public final transient static int TILESIZE = 64; //Statiskt konstant
    private static List<Quest> allQuests = loadQuests();

    public World(String name) {
        this.maps = new ArrayList<>();
        this.npcs = new ArrayList<>();
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
        setupNPCs(curMap, curMap.getId());
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
        setupNPCs(curMap, curMap.getId());

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
        setupNPCs(newMap, mapID);
    }

    public void setupNPCs(MapArea map, int mapID){
        List<NPC> mapNPCs = new ArrayList<>();
        for (NPC npc : npcs) {
            if(npc.getmapID() == mapID){ mapNPCs.add(npc); }
        }
        map.setNpcList(mapNPCs);
    }

    public void loadWorld(String mapData, String npcData) {
        GsonFireBuilder fireBuilder = new GsonFireBuilder();
        fireBuilder.enableHooks(MapTile.class);
        fireBuilder.enableHooks(MapArea.class);
        fireBuilder.enableHooks(AbstractNPC.class);

        GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
        Gson gson = gsonBuilder.create();

        maps = gson.fromJson(mapData, new TypeToken<List<MapArea>>() {}.getType());
        npcs = gson.fromJson(npcData, new TypeToken<List<QuestNPC>>() {}.getType());
    }

    public void saveWorld(){
        TextResource dataSaver = new TextResource();
        Gson gson = new Gson();
        String mapData = gson.toJson(maps);
        dataSaver.saveJSONData("world", name, mapData);

        String npcData = gson.toJson(npcs);
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
        return allQuests.get(id);
    }

    public static int getQuestsSize(){return allQuests.size();}

    public int size(){
        return maps.size();
    }

    public void addEmptyMapArea(){
        MapArea map = new MapArea();
        map.setupEmptyMapArea(15, 15, size());
        addMapArea(map);
    }


    public void toggleNPC(int mapID, int x, int y, int id, List<Integer> questIDs){
        boolean exists = false;
        NPC existingNPC = null;
        for (NPC npc : npcs) {
            if (npc.getId() == id){
                Point npcPos = (Point)npc.getPos();
                if (npc.getmapID() == mapID && (int) npcPos.getX() == x && (int) npcPos.getY() == y) {
                    existingNPC = npc;
                }
                exists = true;
                break;
            }
        }
        if (exists && existingNPC != null){
            npcs.remove(existingNPC);
        } else if (!exists){
            npcs.add(new QuestNPC(id, mapID, new Point(x, y), questIDs));
        }
        setupNPCs(getMapArea(mapID), mapID);
    }

    public List<NPC> getNpcs() {
        return npcs;
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
