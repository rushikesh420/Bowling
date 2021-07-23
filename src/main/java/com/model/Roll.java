package com.model;

public class Roll {
    public int score;
    public Roll(){

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Roll(Roll other) {
        score = other.getScore();
    }
}
