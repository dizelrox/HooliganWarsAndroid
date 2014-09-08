package com.example.dizelrox.hooliganwarsandroid.Logic;


import java.io.Serializable;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;



public class Player implements Serializable
{

    private String loginToDatabase;

    private String shopIcon;

    private String battleIcon;

    private int strengthFactor;

    private double defenceFactor;

    private TreeSet<Item> items = new TreeSet<Item>();
    private String name;

    private int health;

    private Type defenceArea;

    private Type attackArea;

    private String consoleText;

    private boolean[] currentArmors;

    private boolean[] currentWeapons;


    public Player(String name) //Player constructor
    {
        int randomIcon = (int) (Math.random() * 8 + 1);
        shopIcon = "warrior_shop_"+randomIcon;
        battleIcon = "warrior_battle_"+randomIcon;
        int rndStrength = (int) (Math.random() * 3 + 1); //Generate strength factor
        setStrengthFactor(rndStrength);
        double rndDefence = (double) (Math.random() * 0.3); //Generate defence factor
        setDefenceFactor(rndDefence);
        setName(name);
        setHealth(100);
        currentArmors = new boolean[]
        {
            true,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,

        };
        currentWeapons = new boolean[]
        {
            true,
            true,
            true,
            false,
            false,
            true,
            false,
            false
        };
    }

    public void setLoginToDatabase(String login)
    {
        this.loginToDatabase = login;
    }

    public String getLoginToDatabase()
    {
        return loginToDatabase;
    }

    public boolean[] getCurrentWeaponsArray()
    {
        return this.currentWeapons;
    }

    public boolean[] getCurrentArmorsArray()
    {
        return this.currentArmors;
    }

    public void updatePlayerItemsByCurrentGame(Armor[] armArray, Weapon[] wepArray)
    {
        int i = 0;
        for (Armor armorSingleItem : armArray)
        {
            currentArmors[i++] = armorSingleItem.isAvalible();
        }
        i = 0;
        for (Weapon weaponSingleItem : wepArray)
        {
            currentWeapons[i++] = weaponSingleItem.isAvalible();
        }
    }

    public String getStats()
    {
        String output = String.format("%s\n\nDEFENCE:\n", this.getName());

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext())
        {
            Item singleItem = iterator.next();
            if (((Object) singleItem).getClass().equals(Armor.class))
            {
                output += String.format("%s: %d%%\n", singleItem.getItemType().toString(), (int) (((Armor) singleItem).getDefenceValue() * 100));
            } else
            {
                output += String.format("\nATTACK: ");
                output += String.format("%d\n", ((Weapon) singleItem).getDamageValue() + getStrengthFactor());
            }

        }
        return output;

    }


    public String getCurrentTimeStamp()
    {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public String getConsoleText()
    {
        return consoleText;
    }

    /**
     * Method used only by the bot character to generate random attack and
     * defence area.
     *
     * @return {@linkplain Type} object containing information about specific
     * body part.
     */
    public Type getRandomAttackArea()
    {

        Type type = Type.getType((short) (Math.random() * 4));
        return type;
    }

    /**
     * Method used to get an {@linkplain Item} from specific body part.
     *
     * @param area receives {@linkplain Type} defining the body part we looking
     * for items on.
     * @return {@linkplain Item} that is currently on the desired body part if
     * any.
     */
    public Item getItem(Type area)
    {
        Iterator<Item> iterator = items.iterator();
        while(iterator.hasNext()) {
            Item singleItem = iterator.next();
            if(singleItem.getItemType() == area)             
                return singleItem;
        }

        return null;    
    }

    /**
     * Method used to wear an item.
     *
     * @param item receives an {@linkplain Item} to wear.
     */
    public void setItem(Item item)
    {
        item.inUse = true;
        items.add(item);
    }

    /**
     * Method used to take item off.
     *
     * @param itemType receives a {@linkplain Type} which defines body part
     * where we want to take the item off.
     */
    public void setItemNull(Type itemType)
    {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext())
        {
            Item singleItem = iterator.next();
            if (singleItem.getItemType() == itemType)
            {
                iterator.remove();
            }
        }
    }

    /**
     * Method used to get defense factor of the player.
     *
     * @return double value representing the defense factor.
     */
    public double getDefenceFactor()
    {
        return defenceFactor;
    }

    /**
     * Method used to set the defense factor of a player.
     *
     * @param val receives double value to be set as defense factor.
     */
    public void setDefenceFactor(double val)
    {
        this.defenceFactor = val;
    }

    /**
     * Method used to get current player's health.
     *
     * @return integer value representing current health of a player.
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * Method used to set player's current health.
     *
     * @param val receives integer value.
     */
    public void setHealth(int val)
    {
        this.health = val;
    }

    /**
     * Method used to get player's name.
     *
     * @return String variable containing player's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Method used to set player's Name
     *
     * @param val receives String variable to be set as player's name.
     */
    public void setName(String val)
    {
        this.name = val;
    }

    /**
     * Method used to get player's strength factor.
     *
     * @return integer value representing player's strength factor.
     */
    public int getStrengthFactor()
    {
        return strengthFactor;
    }

    /**
     * Method used to set player's strength factor.
     *
     * @param val receives integer value.
     */
    public void setStrengthFactor(int val)
    {
        this.strengthFactor = val;
    }

    /**
     * Method used to get player's full information.
     *
     * @return String variable.
     */
    public String toString()
    {
        return String.format("player name is %s\nplayer strength factor is %d\nplayer def factor is %.1f\n"
                + "player helmet is %s\n"
                + "player armor is %s\n"
                + "player belt is %s\n"
                + "player legs is %s\n"
                + "player weapon is %s\n"
                + "player health is %d\n", getName(), getStrengthFactor(), getDefenceFactor(),
                getItem(Type.HEAD), 
                getItem(Type.CHEST), 
                getItem(Type.STOMACH),
                getItem(Type.LEGS),
                getItem(Type.WEAPON),
                getHealth());
    }

    /**
     * Method used to throw hit on your opponent.
     *
     * @param weapon receives instance of class {@linkplain Weapon} defining
     * with which weapon to perform the hit.
     * @param strFactor receives player's strength factor as part of the damage
     * calculation.
     * @param attackArea receives {@linkplain Type} with defines desired attack
     * area.
     * @param target receives instance of class {@linkplain com.example.dizelrox.hooliganwarsandroid.Logic.Player} which is the
     * target of the attack.
     */
    public void throwHit(Weapon weapon, int strFactor, Type attackArea, Player target)
    {
        int damage = (weapon != null) ? weapon.getDamageValue() + strFactor : strFactor;
        System.err.printf("Thrown Damage: %d\n", damage);
        target.getHit(damage, attackArea, weapon);

    }

    /**
     * Method called automatically when player is getting a hit.
     *
     * @param damage receives integer value representing the damage calculated
     * by opponent's weapon and strength factor.
     * @param attackArea receives {@linkplain Type} which is the area where you
     * are getting hit.
     * @param weapon receives instance of {@linkplain Weapon} which is the
     * weapon you are being hit with.
     */
    public void getHit(int damage, Type attackArea, Weapon weapon)
    {
        double rawDamage; // damage thrown by the attacker
        rawDamage = damage - damage * getDefenceFactor(); //subtract the defence factor (random for each match) from the damage received
        System.err.printf("Got Damage: %.1f\n", rawDamage);

        if (getItem(attackArea) != null)
        {
            rawDamage -= rawDamage * ((Armor) getItem(attackArea)).getDefenceValue();
        }

        if (attackArea == getDefenceArea()) //is area attacked the same as defended?
        {
            if ((int) rawDamage / 2 > getHealth())
            {
                health = 0;
            } else
            {
                health -= (int) rawDamage / 2;
            }

            consoleText = String.format(" been hit with %s in the %s and successfully blocked the attack. Got %d points of damage!\n",
                   ((weapon != null) ? weapon.getName() : "bare hands"), attackArea.toString(), (int) (rawDamage / 2));

        } else
        {
            if ((int) rawDamage > getHealth())
            {
                health = 0;
            } else
            {
                health -= (int) rawDamage;
            }

            consoleText = String.format(" been hit with %s in the %s and couldn't properly block it. Got %d points of damage!\n",
                  ((weapon != null) ? weapon.getName() : "bare hands"), attackArea.toString(), (int) rawDamage);
        }
    }


    public String getPlayerIcon()
    {
        return shopIcon;
    }

    public String getBattleIcon()
    {
        return battleIcon;
    }


    public Type getAttackArea()
    {
        return this.attackArea;
    }


    public void setAttackArea(Type area)
    {
        attackArea = area;
    }


    public Type getDefenceArea()
    {
        return defenceArea;
    }


    public void setDefenceArea(Type area)
    {
        defenceArea = area;
    }

}
