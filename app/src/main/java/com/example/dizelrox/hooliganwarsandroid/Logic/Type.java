package com.example.dizelrox.hooliganwarsandroid.Logic;

/**
 * Class that defines 5 different states of an item and 5 different body parts of a player.
 * @author Dima Ryskin
 * @author Bogdan Kirylyuk
 *
 */
public enum Type {

    HEAD(0),

    CHEST(1),

    STOMACH(2),

    LEGS(3),

    WEAPON(4);

    private final int index;
    
    Type(int index)
    {
        this.index=index;
    }
    /**
     * Method used to get index of current type.
     * @return integer variable representing its index in enum {@linkplain com.example.dizelrox.hooliganwarsandroid.Logic.Type}.
     */
    public int getIndex () {
        return index;
    }
    /**
     * Method used to get {@linkplain com.example.dizelrox.hooliganwarsandroid.Logic.Type} by index.
     * @param index receives integer variable between 0-4
     * @return {@linkplain com.example.dizelrox.hooliganwarsandroid.Logic.Type}.
     */
    public static Type getType (int index)
    {
        switch (index)
        {
            case (0):
                return HEAD;
            case (1):
                return CHEST;
            case (2):
                return STOMACH;
            case (3):
                return LEGS;
            case (4):
                return WEAPON;
                  
        }
        return null;
    }


}

