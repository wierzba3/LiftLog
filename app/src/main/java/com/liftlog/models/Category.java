package com.liftlog.models;

/**
 * Created by James Wierzba on 9/26/15.
 */
public class Category
{

    public Category()
    {

    }

    private long id;
    private String name;
    private boolean isNew;
    private boolean isModified;
    private boolean isDeleted;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isNew()
    {
        return isNew;
    }

    public void setNew(boolean isNew)
    {
        this.isNew = isNew;
    }

    public boolean isModified()
    {
        return isModified;
    }

    public void setModified(boolean isModified)
    {
        this.isModified = isModified;
    }

    public boolean isDeleted()
    {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString()
    {
        return name;
    }


    public static Category dummy;
    static
    {
        dummy = new Category();
        dummy.setId(-1l);
        dummy.setName("Uncategorized");
    }


}
