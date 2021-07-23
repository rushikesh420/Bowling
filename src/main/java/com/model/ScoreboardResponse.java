package com.model;

public class ScoreboardResponse extends Response {
    private int currentFrame;
    private int rollsRemaining;
    private boolean gameComplete;
    private Scoreboard scoreboard;

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getRollsRemaining() {
        return rollsRemaining;
    }

    public void setRollsRemaining(int rollsRemaining) {
        this.rollsRemaining = rollsRemaining;
    }

    public boolean isGameComplete() {
        return gameComplete;
    }

    public void setGameComplete(boolean gameComplete) {
        this.gameComplete = gameComplete;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
}
