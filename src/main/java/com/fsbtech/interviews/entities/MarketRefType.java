package com.fsbtech.interviews.entities;

import java.io.Serializable;

public class MarketRefType implements Serializable
{
    private final Integer marketRefId;
    private final String marketRefName;

    public String toString() {
        StringBuilder sb  = new StringBuilder();
        sb.append(marketRefId).append(",").append(marketRefName);
        return sb.toString();
    }

    public MarketRefType(Integer marketRefId, String marketRefName)
    {
        this.marketRefId = marketRefId;
        this.marketRefName = marketRefName;
    }

    public Integer getMarketRefId() 
    {
        return marketRefId;
    }

    public String getMarketRefName() 
    {
        return marketRefName;
    }
}
