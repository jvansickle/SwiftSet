package com.michaeloles.swiftset.SortingGroups;

import com.michaeloles.swiftset.SortingCategory;
import com.michaeloles.swiftset.SortingGroup;
import com.michaeloles.swiftset.SortingGroups.Grip;

import java.io.Serializable;

public class LatMovementPatterns extends SortingGroup implements Serializable {
    public LatMovementPatterns(){
        this.setName("Movement Patterns");
        SortingCategory horizontalpull = new SortingCategory("Horizontal Pull", "Movement", "Horizontal Pull");
        horizontalpull.addNewOptions(new Angle());
        this.addOption(horizontalpull);
        SortingCategory verticalpull = new SortingCategory("Vertical Pull","Movement","Vertical Pull");
        verticalpull.addNewOptions(new Grip());
        this.addOption(verticalpull);
        this.addOption(new SortingCategory("Pullover Variation", "Movement", "Pullover"));
        SortingCategory pullup = new SortingCategory("Pullup Variations", "Movement", "Pullup");
        pullup.addNewOptions(new Grip());
        this.addOption(pullup);
    }
}