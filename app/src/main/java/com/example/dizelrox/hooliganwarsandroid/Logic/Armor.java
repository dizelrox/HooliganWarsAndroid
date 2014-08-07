package com.example.dizelrox.hooliganwarsandroid.Logic;

import java.io.Serializable;

/**
 * Class that extents {@linkplain Item} and defines Armor objects used in the game.
 * Here are all the properties of armors and all the methods that can be used on them.
 * @author Dima Ryskin
 * @author Bogdan Kirylyuk.
 * @see {@linkplain Item}
 */
public class Armor extends Item implements Serializable
{
	/**
	 * Double value defines items defense value in format 0.x.
	 */
    private double defenceValue;
    /**
     * {@linkplain Type} object defines item's type.
     */
    private Type itemType;
    /**
     * <h2>The constructor of the class.</h2>
     * Method used to create instance of the class initializing all its properties.
     * @param name receives String value that will defines item's name.
     * @param available boolean variable defining current item's availibility.
     * @param itemType {@linkplain Type} object defining item's type.
     * @param defenceValue Double value representing current armor's defense in the following range 0.1-0.9 
     * @param itemName String variable defining items icon file name.
     */
    public Armor(String name, boolean available, Type itemType, double defenceValue,String itemName)
    {
        super(name, available,itemName);

        setItemType(itemType);
        setDefenceValue(defenceValue);

    }
    /**
     * Method used to get current armor's defense value.
     * @return Double value represents items defense rate.
     */
    public double getDefenceValue()
    {
        return defenceValue;
    }
    /**
     * Method used to set current armor's defense value.
     * @param val receives double variable that will be set as armor's defense rate.
     */
    public void setDefenceValue(double val)
    {
        this.defenceValue = val;
    }
    /**
     * Method used to get current item's {@linkplain Type}.
     * @return {@linkplain Type} object containing information about current item's type.
     */
    @Override
    public Type getItemType()
    {
        return this.itemType;
    }
    /**
     * Method used to set current item's {@linkplain Type}.
     * @param value {@linkplain Type} object containing information about current item's type.
     */
    @Override
    public void setItemType(Type value)
    {
        try
        {
            if (value != Type.WEAPON)
            {
                this.itemType = value;
            } else
            {
                throw new Exception("Armor cannot be a weapon");
            }
        } catch (Exception exception)
        {
            System.err.println(exception);
        }

    }

}
