package com.michaeloles.swiftset;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Oles on 5/29/2017.
 * A sorting group is a method in which exercises can be narrowed down.
 * Ex: Push/Pull/Legs, Muscle Group, Equipment used
 * @
 */
public class SortingGroup implements Serializable{

    public String name;
    //All of the sorting groups that cant be used after this sorting group is used
    //Ex: You can't sort by Upper/Lower after Muscle Group is called
    private ArrayList<Class> cantFollow = new ArrayList<>();
    //All of the categories that can be chosen from this sorting group
    //Ex: Muscle Groups categories: Chest, Triceps, Quads, Lats...
    public ArrayList<SortingCategory> categories = new ArrayList<>();
    //Is the user allowed to select more than one category from this group at the same time
    //Ex: can select both dumbbell and barbell exercises but not incline and decline exercises at same time
    public boolean isMultiChoice = false;

    //used to keep track of which ones to highlight in green to show the user they were added based on their selections
    public boolean isOriginal = false;

    //Holds the id of the drawable icon for this group
    public int groupIcon = R.drawable.ic_movement;

    public SortingGroup(){
        this("");
    }

    public SortingGroup(String n){
        name = n;
        categories = new ArrayList<>();
        cantFollow = new ArrayList<>();
    }

    //add a new category that this group can be divided into
    protected void addOption(SortingCategory option) {
        categories.add(option);
    }

    //add a new category that this group can be divided into
    protected void addCantFollow(Class cf) {
        cantFollow.add(cf);
    }

    //Getters and Setters
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public ArrayList<SortingCategory> getCategories() {return categories;}

    //Returns a list of strings of the categories names
    public String[] getCategoryNames() {
        String[] values = new String[categories.size()];
        for(int i=0; i<values.length;i++){
            values[i] = categories.get(i).getName();
        }
        return values;
    }

    public ArrayList<Class> getCantFollow() {return cantFollow;}

    public int getGroupIcon() {return groupIcon;}
}
