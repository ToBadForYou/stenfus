package visual;

import logic.GameObject;
import logic.World;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a JPanel, it is shown on the rightside in the world editor, it is used to adjust the MapArea
 * for example level, which enemy types it should contain and so on. It communicates with the WorldEditorComponent
 * for which settings are selected, for example tile ID, Object ID, should it has collision and so on
 */

public class WorldEditorOption extends JPanel
{
    private static final Font WORLD_EDITOR_TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final int EDITOR_WIDTH = 300;
    private WorldEditorComponent worldEdit;
    private JComboBox<Integer> mapAreas = null, exitID = null; //Skapas i seperata funktioner
    private List<JCheckBox> mapEnemyTypes = new ArrayList<>();
    private String[] interactableCharacters = null; //Skapas i seperata funktioner
    private Integer[] mapAreaIDs = null; //Skapas i seperata funktioner

    public WorldEditorOption(World world, WorldEditorComponent worldEdit) {
    	this.worldEdit = worldEdit;

	BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
	setLayout(boxLayout);

	setUpAreas(world);
	setUpNPCs();

	JLabel worldName = new JLabel("World: " + world.getName());
	worldName.setFont(WORLD_EDITOR_TITLE_FONT);
	worldName.setAlignmentX(Component.CENTER_ALIGNMENT);

	JLabel questIDLabel = new JLabel("Quest IDs:");
	questIDLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	JLabel mapEnemiesLabel = new JLabel("Map Enemy Types:");
	mapEnemiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	add(worldName);
	add(setUpMapAreas(world));
	add(setUpTilesObjects());
	add(setUpMisc());
	add(setUpTeamID());
	add(setUpExitMapArea());
	add(setUpInteractableCharacters());
	add(questIDLabel);
	add(setUpQuestID());
	add(setUpMapAreaName());
	add(setUpMapAreaLevel());
	add(mapEnemiesLabel);
	add(setUpMapEnemies());
	add(setUpOptions(world));
    }

    public JScrollPane setUpQuestID(){
	JPanel questPanel = new JPanel();
	questPanel.setLayout(new BoxLayout(questPanel, BoxLayout.Y_AXIS));

	JScrollPane scrollPanel = new JScrollPane(questPanel);
	scrollPanel.setHorizontalScrollBar(null);
	scrollPanel.setMaximumSize(new Dimension(EDITOR_WIDTH - 10, 90));

	for (int i = 0; i < World.getQuestsSize(); i++) {
	    JCheckBox quest = new JCheckBox(World.getQuest(i).getName());
	    final int finalI = i;
	    quest.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent event) {
		    JCheckBox item = (JCheckBox)event.getItem();
		    worldEdit.setQuest(finalI, item.isSelected());
		}
	    });
	    questPanel.add(quest);
	}

	return scrollPanel;
    }

    public JPanel setUpMapAreaName(){
	JPanel mapNamePanel = new JPanel();
	mapNamePanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 30));

	JLabel mapNameLabel = new JLabel("MapArea Name:");

	JTextField mapName = new JTextField();
	mapName.setText("");
	mapName.setPreferredSize(new Dimension(100, 25));
	mapName.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));

	mapName.getDocument().addDocumentListener(new DocumentListener() {
	    @Override
	    public void removeUpdate(DocumentEvent arg0) {
	    }

	    @Override
	    public void insertUpdate(DocumentEvent arg0) {
		worldEdit.setMapName(mapName.getText());
	    }

	    @Override
	    public void changedUpdate(DocumentEvent arg0) {
	    }
	});

	mapNamePanel.add(mapNameLabel);
	mapNamePanel.add(mapName);
	return mapNamePanel;
    }

    public void refreshEnemiesBox(){
	for (JCheckBox box: mapEnemyTypes) {
	    box.setSelected(false);
	}

	List<Integer> mapEnemies = worldEdit.getEnemyTypes();
	for (int mapEnemy : mapEnemies) {
	    JCheckBox enemyBox = mapEnemyTypes.get(mapEnemy);
	    enemyBox.setSelected(true);
	}
    }

    public JPanel setUpMapAreas(World world) {
	JPanel mapAreasPanel = new JPanel();
	mapAreasPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 30));

	JLabel mapAreasID = new JLabel("MapArea ID:");

	mapAreas = new JComboBox<>(mapAreaIDs);
	mapAreas.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	ActionListener actionListener5 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			worldEdit.setCurrentMap(world.getMapArea(mapAreas.getSelectedIndex()));
			refreshEnemiesBox();
			worldEdit.repaint();
		}
	};
	mapAreas.addActionListener(actionListener5);

	mapAreasPanel.add(mapAreasID);
	mapAreasPanel.add(mapAreas);

	return mapAreasPanel;
	}


    public JPanel setUpOptions(World world){
	JPanel optionsPanel = new JPanel();
	optionsPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 35));

	JButton addNewMapArea = new JButton("Add New Empty MapArea");
	ActionListener actionListener6 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			world.addEmptyMapArea();
			mapAreas.addItem(world.size()-1);
			exitID.addItem(world.size()-1);
		}
	};
	addNewMapArea.addActionListener(actionListener6);

	JButton finishButton = new JButton("Finish");
	ActionListener actionListener3 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			worldEdit.finishEditor();
		}
	};
	finishButton.addActionListener(actionListener3);

	optionsPanel.add(addNewMapArea);
	optionsPanel.add(finishButton);

	return optionsPanel;
    }

    public JPanel setUpTilesObjects(){
    	JPanel tileObjectPanel = new JPanel();
	tileObjectPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 65));

	JLabel mapTile = new JLabel("MapTile:");

	JComboBox<String> tiles = setUpTiles();
	tiles.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	ActionListener actionListener1 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			worldEdit.setTile(tiles.getSelectedIndex());
		}
	};
	tiles.addActionListener(actionListener1);

	JLabel object = new JLabel("Object:");

	JComboBox<String> objects = setUpObjects();
	objects.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	ActionListener actionListener2 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			int index = objects.getSelectedIndex();
			if (objects.getSelectedItem() == "None"){index = -1;}
			worldEdit.setObject(index);
		}
	};
	objects.addActionListener(actionListener2);

	JCheckBox collision = new JCheckBox("Collision");
	collision.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent event) {
			JCheckBox item = (JCheckBox)event.getItem();
			worldEdit.setCollision(item.isSelected());
		}
	});

	tileObjectPanel.add(mapTile);
	tileObjectPanel.add(tiles);
	tileObjectPanel.add(object);
	tileObjectPanel.add(objects);
	tileObjectPanel.add(collision);

    	return tileObjectPanel;
    }

    public JPanel setUpMisc(){
	JPanel miscPanel = new JPanel();
	miscPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 35));

	JLabel miscLabel = new JLabel("Misc:");

	String[] misc = new String[] {"Battle SpawnPoint", "Mob SpawnPoint", "ExitPoint","NPC"};
	JComboBox<String> miscs = new JComboBox<>(misc);
	miscs.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	miscs.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent event) {
			worldEdit.setMisc(miscs.getSelectedIndex());
		}
	});
	miscPanel.add(miscLabel);
	miscPanel.add(miscs);

	return miscPanel;
    }

    public JPanel setUpTeamID(){
	JPanel teamIDPanel = new JPanel();
	teamIDPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 30));

	JLabel teamIDLabel = new JLabel("Team ID:");

	JComboBox<Integer> teamIDs = new JComboBox<>(new Integer[]{0, 1,2,3,4});
	teamIDs.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	ActionListener actionListener7 = new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
		worldEdit.setTeamID((int)teamIDs.getSelectedItem());
	    }
	};
	teamIDs.addActionListener(actionListener7);
	teamIDPanel.add(teamIDLabel);
	teamIDPanel.add(teamIDs);
	return teamIDPanel;
    }

    public JPanel setUpExitMapArea(){
	JPanel exitMapAreaPanel = new JPanel();
	exitMapAreaPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 30));

	JLabel exitLabel = new JLabel("Exit MapArea ID:");

	exitID = new JComboBox<>(mapAreaIDs);
	exitID.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	ActionListener actionListener7 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			worldEdit.setMiscValue(exitID.getSelectedIndex());
		}
	};
	exitID.addActionListener(actionListener7);
	exitMapAreaPanel.add(exitLabel);
	exitMapAreaPanel.add(exitID);
	return exitMapAreaPanel;
    }

    public JPanel setUpInteractableCharacters(){
	JPanel npcPanel = new JPanel();
	npcPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 30));

	JLabel npcLabel = new JLabel("NPC ID:");

	JComboBox<String> npcs = new JComboBox<>(interactableCharacters);
	npcs.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));
	ActionListener actionListener7 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {

		}
	};
	npcs.addActionListener(actionListener7);

	npcPanel.add(npcLabel);
	npcPanel.add(npcs);
	return npcPanel;
    }

    public JPanel setUpMapAreaLevel(){
	JPanel mapAreaLevelPanel = new JPanel();
	mapAreaLevelPanel.setMaximumSize(new Dimension(EDITOR_WIDTH, 30));

	JLabel level = new JLabel("MapArea Level:");

	NumberFormat format = NumberFormat.getInstance();
	format.setGroupingUsed(false);//Remove comma from number greater than 4 digit
	NumberFormatter sleepFormatter = new NumberFormatter(format);
	sleepFormatter.setValueClass(Integer.class);
	sleepFormatter.setMinimum(0);
	sleepFormatter.setMaximum(3600);
	sleepFormatter.setAllowsInvalid(false);
	sleepFormatter.setCommitsOnValidEdit(true);

	JFormattedTextField mapLevel = new JFormattedTextField(sleepFormatter);
	mapLevel.setText("1");
	mapLevel.setPreferredSize(new Dimension(50, 25));
	mapLevel.setMaximumSize(new Dimension(EDITOR_WIDTH, 25));

	mapLevel.getDocument().addDocumentListener(new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent arg0) {
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			worldEdit.setLevel((Integer.parseInt(mapLevel.getText())));
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
		}
	});

	mapAreaLevelPanel.add(level);
	mapAreaLevelPanel.add(mapLevel);
	return mapAreaLevelPanel;
    }

    public JScrollPane setUpMapEnemies(){
	JPanel enemiesPanel = new JPanel();
	enemiesPanel.setLayout(new BoxLayout(enemiesPanel, BoxLayout.Y_AXIS));

	JScrollPane scrollPanel = new JScrollPane(enemiesPanel);
	scrollPanel.setHorizontalScrollBar(null);
	scrollPanel.setMaximumSize(new Dimension(EDITOR_WIDTH - 10, 90));

	int enemyAmount = GameObject.OBJECT_DATA_HANDLER.getDataLength("enemies");
	for (int i = 0; i < enemyAmount; i++) {
	    JCheckBox enemyType = new JCheckBox((String)GameObject.OBJECT_DATA_HANDLER.getData(i, "enemies")[0]);
	    final int finalI = i;
	    enemyType.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent event) {
		    JCheckBox item = (JCheckBox)event.getItem();
		    worldEdit.toggleEnemyType(finalI, item.isSelected());
		}
	    });
	    enemiesPanel.add(enemyType);
	    mapEnemyTypes.add(enemyType);
	}
	refreshEnemiesBox();

	return scrollPanel;
    }


    public void setUpNPCs(){
	int npcAmount = GameObject.OBJECT_DATA_HANDLER.getDataLength("npc");
	String[] npcs = new String[npcAmount];
	for (int i = 0; i < npcAmount; i++) {
		npcs[i] = (String)GameObject.OBJECT_DATA_HANDLER.getData(i, "npc")[0];
	}

    	interactableCharacters = npcs;
    }

    public void setUpAreas(World world){
    	int mapAmount = world.size();
	Integer[] mapAreaIDs2 = new Integer[mapAmount];
	for (int i = 0; i < mapAmount; i++) {
		mapAreaIDs2[i] = (world.getMapArea(i).getId());
	}
	mapAreaIDs = mapAreaIDs2;
    }

    public JComboBox<String> setUpTiles(){
	int tileAmount = GameObject.OBJECT_DATA_HANDLER.getDataLength("tiles");
	String[] tilePaths = new String[tileAmount];
	for (int i = 0; i < tileAmount; i++) {
		tilePaths[i] = GameObject.OBJECT_DATA_HANDLER.getImageName(i, "tiles");
	}
	return new JComboBox<>(tilePaths);
    }

    public JComboBox<String> setUpObjects(){
	int objectAmount = GameObject.OBJECT_DATA_HANDLER.getDataLength("static");
	String[] objectPaths = new String[objectAmount+1];
	for (int i = 0; i < objectAmount; i++) {
		objectPaths[i] = GameObject.OBJECT_DATA_HANDLER.getImageName(i, "static");
	}
	objectPaths[objectAmount] = "None";
	return new JComboBox<>(objectPaths);
    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(EDITOR_WIDTH, 300);
    }
}
