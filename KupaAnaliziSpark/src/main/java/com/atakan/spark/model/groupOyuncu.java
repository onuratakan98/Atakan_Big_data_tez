package com.atakan.spark.model;

import java.io.Serializable;

/**
 * Created by Atakan on 14.06.2023.
 */
public class groupOyuncu implements Serializable {
    String playerName;
    int matchCount;

    public groupPlayer(String playerName, int matchCount) {
        this.playerName = playerName;
        this.matchCount = matchCount;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }
}





