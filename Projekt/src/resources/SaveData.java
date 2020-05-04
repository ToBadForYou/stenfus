package resources;

/**
 * This class contains the data loaded from the .txt file
 * The data field is flexible and differs from each type while
 * id and imageData fields always have same structure
 */
public class SaveData
{
    private int id;
    private Object[] imageData;
    private Object[] data;

    public SaveData(final int id, final Object[] imageData, final Object[] data) {
	this.id = id;
	this.imageData = imageData;
	this.data = data;
    }

    public int getId() {
	return id;
    }

    public Object[] getData(){
        return data;
    }

    public Object[] getImageData() {
	return imageData;
    }
}
