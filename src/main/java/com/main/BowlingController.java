package com.main;

import com.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
@CrossOrigin(origins = "*")
@SuppressWarnings("all")
public class BowlingController {
    private static final Logger log = LoggerFactory.getLogger(BowlingController.class);
    private BowlingRepository bowlingRepository;

    public BowlingController() {
        bowlingRepository = new BowlingRepository();
    }

    @PostMapping("/bowling/newGame/{gameName}")
    Response newGame(@PathVariable String gameName) {
        Response response = new Response();
        Game game = bowlingRepository.findByGameName(gameName);
        log.info("Creating New Game");
        if (game != null) {
            response.setStatus(Status.ERROR);
            response.setMessage("gameName already in use");
            log.info("Game Name Used");
            return response;
        } else {
            Frame frame = new Frame();
            game = new Game();
            game.getFrames().add(frame);
            game.setGameName(gameName);
            game.setGameStatus(GameStatus.ACTIVE);
            response.setStatus(Status.SUCCESS);
            response.setGameName(gameName);
            response.setMessage("New Game Created");
            bowlingRepository.save(game);
            log.info("Game Created: " +gameName);
            return response;
        }
    }

    @PostMapping("/bowling/roll/{gameName}/{roll}")
    Response roll(@PathVariable String gameName, @PathVariable int roll) {
        log.info("Roll for the Game: " + gameName);

        Response response = new Response();
        Game game = bowlingRepository.findByGameName(gameName);
        if (game == null) {
            log.info("Given Game invlaid");
            response.setStatus(Status.GID_INVALID);
            response.setMessage("Invalid game name");
            return response;
        }
        log.info("Roll!!");
        Game nGame = new Game(game);
        game = makeRoll(nGame, roll);
        if (game == null) {
            log.info("Invalid Roll");
            response.setStatus(Status.ROLL_INVALID);
            return response;
        } else {
            log.info("Saving Roll");
            bowlingRepository.save(game);
        }
        response.setGameName(gameName);
        response.setStatus(Status.SUCCESS);

        return response;
    }

    //Returns the Game ScoreBoard for the given game, -1 for inclomplete
    @GetMapping("/bowling/scoreboard/{gameName}")
    ScoreboardResponse getScoreboard(@PathVariable String gameName) {
        log.info("GET game details.");
        Game game = bowlingRepository.findByGameName(gameName);

        ScoreboardResponse response = new ScoreboardResponse();
        if (game == null) {
            response.setStatus(Status.GID_INVALID);
            response.setMessage("Invalid game name");
            log.info("Invalid Game: " + gameName);
            return response;
        }
        Scoreboard scoreBoard = buildScoreBoardString(game);
        int rollsRemaining = rollsRemaining(game);
        log.info("Roll Remaining: " + rollsRemaining);
        int currentFrame = getCurrentFrameNumber(game);

        response.setScoreboard(scoreBoard);
        response.setCurrentFrame(currentFrame);
        response.setRollsRemaining(rollsRemaining);
        response.setStatus(Status.SUCCESS);
        if (game.getGameStatus().equals(GameStatus.COMPLETE)) {
            response.setGameComplete(true);
        } else {
            response.setGameComplete(false);
        }
        log.info("Getting Game details for:" + gameName);
        return response;
    }

    private int rollsRemaining(Game game) {
        log.info("Calculating Remaining Roll");
        if (game.getGameStatus().equals(GameStatus.COMPLETE)) {
            return 0;
        }
        int currFrameNum = getCurrentFrameNumber(game);
        if (currFrameNum == 0) return Constants.ROLLS_PER_FRAME;
        Frame currFrame = game.getFrames().get(currFrameNum - 1);

        if (currFrameNum == Constants.NUM_FRAMES) {
            if (currFrame.getRolls().size() == 0) {
                return Constants.ROLLS_PER_FRAME;
            } else if (currFrame.getRolls().size() == Constants.ROLLS_PER_FRAME) {
                return 1;
            } else {
                if (currFrame.getRolls().get(0).getScore() == Constants.NUM_PINS) {
                    return Constants.ROLLS_PER_FRAME;
                } else {
                    return Constants.ROLLS_PER_FRAME - 1;
                }
            }
        } else {
            return Constants.ROLLS_PER_FRAME - currFrame.getRolls().size();
        }
    }

    private int getCurrentFrameNumber(Game game) {
        if (game.getGameStatus().equals(GameStatus.COMPLETE)) return -1;
        log.info("Getting Current Frame");
        return game.getFrames().size();
    }

    //makes a roll. return null if roll is invalid if value greater then 10
    private Game makeRoll(Game game, int roll) {
        if (roll > 10 || roll < 0) return null;
        if (game.getGameStatus().equals(GameStatus.COMPLETE)) return null;

        int currFrameNum = getCurrentFrameNumber(game);

        Roll newRoll = new Roll();
        newRoll.setScore(roll);
        game.getFrames().get(currFrameNum - 1).getRolls().add(newRoll);
        int pinsKnockedOver = 0;
        for (Roll a : game.getFrames().get(currFrameNum - 1).getRolls()) {
            pinsKnockedOver += a.getScore();
        }

        if (currFrameNum != Constants.NUM_FRAMES) {
            //roll greater then 10
            if (pinsKnockedOver > Constants.NUM_PINS) {
                return null;
            }
            //add next frame in 2 cases (strike or completed 2 rolls.
            if (game.getFrames().get(currFrameNum - 1).getRolls().size() == Constants.ROLLS_PER_FRAME ||
                    game.getFrames().get(currFrameNum - 1).getRolls().get(0).getScore() == Constants.NUM_PINS) {
                Frame frame = new Frame();
                game.getFrames().add(frame);
            }
        } else {
            boolean firstRollStrike = false;
            if (game.getFrames().get(currFrameNum - 1).getRolls().get(0).getScore() == Constants.NUM_PINS) {
                firstRollStrike = true;
            }
            if (game.getFrames().get(currFrameNum - 1).getRolls().size() == Constants.ROLLS_PER_FRAME + 1) {
                if (firstRollStrike) {
                    //roll greater than 10, for example in this frame: Strike, 5, 9
                    if (pinsKnockedOver > 2 * Constants.NUM_PINS) {
                        return null;
                    }
                }
                game.setGameStatus(GameStatus.COMPLETE);
            } else if (game.getFrames().get(currFrameNum - 1).getRolls().size() == Constants.ROLLS_PER_FRAME) {
                //roll greater
                if (!firstRollStrike && pinsKnockedOver > Constants.NUM_PINS) {
                    return null;
                }
                if (pinsKnockedOver < Constants.NUM_PINS || (firstRollStrike && pinsKnockedOver == 2 * Constants.NUM_PINS)) {
                    game.setGameStatus(GameStatus.COMPLETE);
                }
            }
        }
        return game;
    }

    /*    returns a representation of the scoreboard, with
        * X to represent a strike
        * / to represent a spare
        * - to represent no pins knocked down, and two rolls per frame
     */
    private Scoreboard buildScoreBoardString(Game game) {
        Scoreboard result = new Scoreboard();
        log.info("Generating ScoreBoard.");
        int count = 0;
        int cumulativeScore = 0;
        for (count = 0; count < game.getFrames().size(); count++) {
            Frame cFrame = game.getFrames().get(count);

            if (cFrame.getRolls().size() == 0) {
                break;
            }
            int frameScore = 0;
            String moves = "";
            if (count == Constants.NUM_FRAMES - 1) {
                for (int i = 0; i < cFrame.getRolls().size(); i++) {
                    Roll cRoll = cFrame.getRolls().get(i);
                    if (frameScore >= 0)
                        frameScore += cRoll.getScore();
                    if (cRoll.getScore() == Constants.NUM_PINS) {
                        moves += "X";
                        if (cFrame.getRolls().size() < Constants.ROLLS_PER_FRAME + 1) {
                            frameScore = -1;
                        }
                    } else if ((frameScore == Constants.NUM_PINS && i == 1)) {
                        moves += "/";
                        if (cFrame.getRolls().size() < Constants.ROLLS_PER_FRAME + 1) {
                            frameScore = -1;
                        }
                    } else if ((frameScore == 2 * Constants.NUM_PINS && i == Constants.ROLLS_PER_FRAME)) {
                        moves += "/";
                    } else {
                        moves += cRoll.getScore();
                    }
                }
                if (frameScore < 0 || cumulativeScore < 0) {
                    result.addFrame(count + 1, moves);
                } else {
                    cumulativeScore += frameScore;
                    result.addFrame(count + 1, cumulativeScore, moves);
                }
                return result;
            }
            for (int i = 0; i < cFrame.getRolls().size(); i++) {
                Roll cRoll = cFrame.getRolls().get(i);
                frameScore += cRoll.getScore();
                if (frameScore == Constants.NUM_PINS) {
                    if (i == 0) {
                        moves += "X";
                        if (count < game.getFrames().size() - 1) {
                            Frame nextFrame = game.getFrames().get(count + 1);
                            if (nextFrame.getRolls().size() > 1) {
                                frameScore += nextFrame.getRolls().get(0).getScore();
                                frameScore += nextFrame.getRolls().get(1).getScore();
                                break;
                            } else if (nextFrame.getRolls().size() == 1) {
                                frameScore += nextFrame.getRolls().get(0).getScore();
                                if (count < game.getFrames().size() - 2) {
                                    Frame twoFrames = game.getFrames().get(count + 2);
                                    if (twoFrames.getRolls().size() > 0) {
                                        frameScore += twoFrames.getRolls().get(0).getScore();
                                        break;
                                    }
                                }
                            }
                            frameScore = -1;
                        }

                    } else {
                        moves += "/";
                        if (count < game.getFrames().size() - 1) {
                            Frame nextFrame = game.getFrames().get(count + 1);
                            if (nextFrame.getRolls().size() > 0) {
                                frameScore += nextFrame.getRolls().get(0).getScore();
                            } else {
                                frameScore = -1;
                            }
                        }
                    }
                    break;
                } else {
                    if (cRoll.getScore() == 0) {
                        moves += "-";
                    } else {
                        moves += cRoll.getScore();
                    }
                }
            }
            if (frameScore < 0 || cumulativeScore < 0) {
                cumulativeScore = -1;
                result.addFrame(count + 1, moves);
            } else {
                cumulativeScore += frameScore;
                result.addFrame(count + 1, cumulativeScore, moves);
            }
        }
        for (count = count + 1; count <= Constants.NUM_FRAMES; count++) {
            result.addFrame(count, "");
        }
        return result;
    }

}

