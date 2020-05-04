package logic;

import resources.TextResource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.GsonFireBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is suposed to be edited and run to generate a new Quest to our txt file.
 */

public class GenerateQuest
{
    public static void main(String[] args) {


        TextResource resourceHandler = new TextResource();
        GsonFireBuilder fireBuilder = new GsonFireBuilder();

        fireBuilder.enableHooks(KillObjective.class);

        GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
        Gson gson = gsonBuilder.create();

        String questData = resourceHandler.getJSONData("objects", "quests");
        List<Quest> questList = gson.fromJson(questData, new TypeToken<List<Quest>>() {}.getType());
        KillObjective[] obj = new KillObjective[]{new KillObjective(10, "Kill dregon", 0)};
        Quest quest = new Quest(0, new ArrayList<>(), obj, 100, 100,  new Integer[] {0},"Dregon kil", questList.size());
        questList.add(quest);

        String dataJSON = gson.toJson(questList);
        resourceHandler.saveJSONData("objects", "quests", dataJSON);
    }
}