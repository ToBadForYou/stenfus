package resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.GsonFireBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of the data of a specific type, for example enemies, npcs etc, it loads in the data and creates SaveData objects which are put in a
 * list, it has methods to return the requested data for the given ID when called upon
 */

public class ObjectData
{
    private String type;
    private List<SaveData> savedData;

    public ObjectData(final String type) {
        savedData = new ArrayList<>();
	this.type = type;
	loadData();
    }

    private void loadData(){
	GsonFireBuilder fireBuilder = new GsonFireBuilder();
	TextResource dataSaver = new TextResource();
	GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();
	Gson gson = gsonBuilder.create();
	savedData = gson.fromJson(dataSaver.getJSONData("objects", type), new TypeToken<List<SaveData>>() {}.getType());
    }

    public void addData(SaveData newData){
        int newID = newData.getId();
        SaveData duplicate = null;
	for (SaveData data: savedData) {
	    if (data.getId() == newID){
		duplicate = data;
	    }
	}
	if (duplicate != null){
	    savedData.remove(duplicate);
	}
        savedData.add(newData);
    }

    public void saveData(){
	TextResource dataSaver = new TextResource();
	Gson gson = new Gson();
	String dataJSON = gson.toJson(savedData);
	dataSaver.saveJSONData("objects", type, dataJSON);
    }

    public String getImagePath(int id){
        return (String)savedData.get(id).getImageData()[0];
    }

    public double[] getImageOffset(int id){
        Object[] imageData = savedData.get(id).getImageData();
	return new double[] {(double)imageData[1], (double)imageData[2]};
    }
    public double[] getImageSize(int id){
	Object[] imageData = savedData.get(id).getImageData();
	return new double[] {(double)imageData[3], (double)imageData[4]};
    }

    public Object[] getData(int id){
	return savedData.get(id).getData();
    }

    public int size(){
        return savedData.size();
    }
}
