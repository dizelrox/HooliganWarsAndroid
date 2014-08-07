package com.example.dizelrox.hooliganwarsandroid.Logic;


import java.io.Serializable;


public abstract class Item implements Serializable,Comparable
{
	/**
	 * String variable used as item name.
	 */
    private String name;

    private String smallColor;

    private String smallBW;

    private String big;

    private boolean available;

    public boolean inUse = false;

    public abstract Type getItemType();

    public abstract void setItemType(Type value);
    /**
     * The constructor of the class used to initialize all the fields of the object.
     * @param name receives String variable represents item's name.
     * @param available receives boolean variable defining whether current item is unlocked.
     * @param itemFileName is a String variable represents current item's icon file name.
     */
    public Item(String name,boolean available,String itemFileName) //disabled PRICE
    {
        String smallColorIcon = itemFileName+"_big";
        String smallBWIcon = itemFileName+"_big_bw";
        String bigIcon = itemFileName+"_big";
        //setPrice(price);  //disabled PRICE
        setName(name);
        setSmallColorIcon(smallColorIcon);
        setSmallBWIcon(smallBWIcon);
        setBigIcon(bigIcon);
        setAvalible(available);
    }

    @Override
    public int compareTo(Object t)
    {
        return this.getItemType().getIndex()-((Item) t).getItemType().getIndex();
    }

    public String getSmallIcon()
    {
        if ( available )
        {
            return getSmallColorIcon();
        } 
        else
        {
            return getSmallBWIcon();
        }
    }
    /**
     * Method used to get item's availability.
     * @return boolean expression defining whether the item is available or not.
     */
    public boolean isAvalible()
    {
        return available;
    }
    /**
     * Method used to set item's availability.
     * @param val boolean variable defines whether the item is available or not.
     */
    public void setAvalible(boolean val)
    {
        this.available = val;
    }
   
    private String getSmallColorIcon()
    {
        String redIcon = new String("default_big_image");
        if (inUse)
        {
            return redIcon;
        }
        else
        return smallColor;
    }
    
    private String getSmallBWIcon()
    {
        return smallBW;
    }

    public String getBigIcon()
    {

            return big;
    }

    public void setSmallColorIcon(String val)
    {
        this.smallColor = val;
    }

    public void setSmallBWIcon(String val)
    {
        this.smallBW = val;
    }

    public void setBigIcon(String val)
    {
        
        this.big = val;
    }
    /**
     * Method used to get item's name.
     * @return String parameter containing item's name. 
     */
    public String getName()
    {
        return name;
    }
    /**
     * Method used to set item's name.
     * @param val receives String variable containing items desired name.
     */
    public void setName(String val)
    {
        this.name = val;
    }
    
    /**
     * Method same as getName()
     * @return string variable holding item's name.
     */
    public String toString()
    {
        return getName();
    }
}
