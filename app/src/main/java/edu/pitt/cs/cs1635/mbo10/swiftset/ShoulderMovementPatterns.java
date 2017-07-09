package edu.pitt.cs.cs1635.mbo10.swiftset;

import java.io.Serializable;

public class ShoulderMovementPatterns extends SortingGroup implements Serializable {
    public ShoulderMovementPatterns(){
        this.setName("Movement Patterns");
        this.addOption(new SortingCategory("Overhead Press", "Movement", "Press"));
        this.addOption( new SortingCategory("Front Raise","Movement","Front"));
        this.addOption(new SortingCategory("Side Raise", "Movement", "Side"));
    }
}