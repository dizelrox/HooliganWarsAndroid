package com.example.dizelrox.hooliganwarsandroid.Logic;

import java.io.Serializable;

/**
 * Class that extents {@linkplain Item} and defines Weapon objects used in the game.
 * Here are all the properties of armors and all the methods that can be used on them.
 * @author Dima Ryskin
 * @author Bogdan Kirylyuk.
 * @see {@linkplain Item}
 */
public class Weapon extends Item implements Serializable
{
	/**
	 * Integer value defines items damage value.
	 */
    private int damageValue;
    /**
     * {@linkplain Type} object defines item's type.
     */
    private final Type itemType = Type.WEAPON;

    /**
     * <h2>The constructor of the class.</h2>
     * Method used to create instance of the class initializing all its properties.
     * @param name receives String value that will defines item's name.
     * @param available boolean variable defining current item's availibility.
     * @param damageValue integer value representing current weapon's damage. 
     * @param itemName String variable defining items icon file name.
     */
    public Weapon(String name,boolean available,int damageValue,String itemName)
    {
        super(name,available,itemName);
        setDamageValue(damageValue);
    }
    /**
     * Method used to get current weapon's damage value.
     * @return integer variable defining weapon's damage.
     */
    public int getDamageValue()
    {
        return damageValue;
    }
    /**
     * Method used to set item's damage value.
     * @param val receives integer defining weapon's damage rate.
     */
    public void setDamageValue(int val)
    {
        this.damageValue = val;
    }
    /**
     * Method used to get item's {@linkplain Type}.
     * @return {@linkplain Type} describing weapon's type.
     */
    public Type getItemType()
    {
        return itemType;
    }
    /**
     * Method without implementation because weapon's type is always "weapon" and can't be changed.
     */
    public void setItemType(Type value)
    {
        //final type cannot be set
    }
}
