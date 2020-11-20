package com.fsbtech.interviews.entities;

import java.io.Serializable;

public class Category implements Serializable
{
    private final Integer id;
    private final String ref;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(ref);
        return sb.toString();
    }

    public Category(Integer id, String ref)
    {
        this.id = id;
        this.ref = ref;
    }

    public Integer getId()
    {
        return id;
    }

    public String getRef()
    {
        return ref;
    }
}
