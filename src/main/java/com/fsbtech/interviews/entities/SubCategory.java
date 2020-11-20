package com.fsbtech.interviews.entities;

import java.io.Serializable;

public class SubCategory implements Serializable
{
    private final Integer id;
    private final String ref;
    private final com.fsbtech.interviews.entities.Category category;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(ref).append(",").append(category.toString());
        return sb.toString();
    }

    public SubCategory(Integer id, String ref, com.fsbtech.interviews.entities.Category category)
    {
        this.id = id;
        this.ref = ref;
        this.category = category;
    }

    public Integer getId()
    {
        return id;
    }

    public String getRef()
    {
        return ref;
    }

    public com.fsbtech.interviews.entities.Category getCategory()
    {
        return category;
    }
}
