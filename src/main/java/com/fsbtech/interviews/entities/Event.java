package com.fsbtech.interviews.entities;

import java.io.Serializable;
import java.util.Collection;

public class Event implements Serializable
{
    private final Integer id;
    private final String name;
    private final SubCategory subCategory;
    private final Collection<MarketRefType> marketRefTypes;
    private final Boolean completed;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(name).append(",").append(subCategory.toString()).append(",{");
        StringBuilder sb2 = new StringBuilder();
        if (null != marketRefTypes) {
            for (MarketRefType marketRefType : marketRefTypes) {
                if (sb2.toString().length() > 0)
                    sb2.append(",");
                sb2.append(marketRefType.getMarketRefName());
            }
        }
        sb.append(sb2).append("},").append(completed).append(";\n");
        return sb.toString();
    }

    public Event(Integer id, String name, SubCategory subCategory, Collection<MarketRefType> marketRefTypes, Boolean completed)
    {
        this.id = id;
        this.name = name;
        this.subCategory = subCategory;
        this.marketRefTypes = marketRefTypes;
        this.completed = completed;
    }

    public Integer getId() { return id; }

    public String getName() { return name; }

    public SubCategory getSubCategory() { return subCategory; }

    public Collection<MarketRefType> getMarketRefTypes() {
        System.out.println("Size of marketRefTypes is " + marketRefTypes.size());
        marketRefTypes.stream().forEach(System.out::println);
        return marketRefTypes;
    }

    public Boolean getCompleted() { return completed; }
}
