package com.example.v0_12;

public class Player {
    String sName, sScore;

    public Player(String sName, String sScore) {
        this.sName = sName;
        this.sScore = sScore;
    }

    public String getName() {
        return sName;
    }

    public void setName(String name) {
        this.sName = name;
    }

    public String getScore() {
        return sScore;
    }

    public void setScore(String score) {
        this.sScore = score;
    }
}
