package Controller;
/* $Id$
 *
 * Revisions:
 *   $Log: Lane.java,v $
 *   Revision 1.52  2003/02/20 20:27:45  ???
 *   Fouls disables.
 *
 *   Revision 1.51  2003/02/20 20:01:32  ???
 *   Added things.
 *
 *   Revision 1.50  2003/02/20 19:53:52  ???
 *   Added foul support.  Still need to update laneview and test this.
 *
 *   Revision 1.49  2003/02/20 11:18:22  ???
 *   Works beautifully.
 *
 *   Revision 1.48  2003/02/20 04:10:58  ???
 *   Score reporting code should be good.
 *
 *   Revision 1.47  2003/02/17 00:25:28  ???
 *   Added disbale controls for View objects.
 *
 *   Revision 1.46  2003/02/17 00:20:47  ???
 *   fix for event when game ends
 *
 *   Revision 1.43  2003/02/17 00:09:42  ???
 *   fix for event when game ends
 *
 *   Revision 1.42  2003/02/17 00:03:34  ???
 *   Bug fixed
 *
 *   Revision 1.41  2003/02/16 23:59:49  ???
 *   Reporting of sorts.
 *
 *   Revision 1.40  2003/02/16 23:44:33  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.39  2003/02/16 23:43:08  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.38  2003/02/16 23:41:05  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.37  2003/02/16 23:00:26  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.36  2003/02/16 21:31:04  ???
 *   Score logging.
 *
 *   Revision 1.35  2003/02/09 21:38:00  ???
 *   Added lots of comments
 *
 *   Revision 1.34  2003/02/06 00:27:46  ???
 *   Fixed a race condition
 *
 *   Revision 1.33  2003/02/05 11:16:34  ???
 *   Boom-Shacka-Lacka!!!
 *
 *   Revision 1.32  2003/02/05 01:15:19  ???
 *   Real close now.  Honest.
 *
 *   Revision 1.31  2003/02/04 22:02:04  ???
 *   Still not quite working...
 *
 *   Revision 1.30  2003/02/04 13:33:04  ???
 *   Lane may very well work now.
 *
 *   Revision 1.29  2003/02/02 23:57:27  ???
 *   fix on pinsetter hack
 *
 *   Revision 1.28  2003/02/02 23:49:48  ???
 *   Pinsetter generates an event when all pins are reset
 *
 *   Revision 1.27  2003/02/02 23:26:32  ???
 *   ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 *
 *   Revision 1.26  2003/02/02 23:11:42  ???
 *   parties can now play more than 1 game on a lane, and lanes are properly released after games
 *
 *   Revision 1.25  2003/02/02 22:52:19  ???
 *   Lane compiles
 *
 *   Revision 1.24  2003/02/02 22:50:10  ???
 *   Lane compiles
 *
 *   Revision 1.23  2003/02/02 22:47:34  ???
 *   More observering.
 *
 *   Revision 1.22  2003/02/02 22:15:40  ???
 *   Add accessor for pinsetter.
 *
 *   Revision 1.21  2003/02/02 21:59:20  ???
 *   added conditions for the party choosing to play another game
 *
 *   Revision 1.20  2003/02/02 21:51:54  ???
 *   LaneEvent may very well be observer method.
 *
 *   Revision 1.19  2003/02/02 20:28:59  ???
 *   fixed sleep thread bug in lane
 *
 *   Revision 1.18  2003/02/02 18:18:51  ???
 *   more changes. just need to fix scoring.
 *
 *   Revision 1.17  2003/02/02 17:47:02  ???
 *   Things are pretty close to working now...
 *
 *   Revision 1.16  2003/01/30 22:09:32  ???
 *   Worked on scoring.
 *
 *   Revision 1.15  2003/01/30 21:45:08  ???
 *   Fixed speling of received in Lane.
 *
 *   Revision 1.14  2003/01/30 21:29:30  ???
 *   Fixed some MVC stuff
 *
 *   Revision 1.13  2003/01/30 03:45:26  ???
 *   *** empty log message ***
 *
 *   Revision 1.12  2003/01/26 23:16:10  ???
 *   Improved thread handeling in lane/controldesk
 *
 *   Revision 1.11  2003/01/26 22:34:44  ???
 *   Total rewrite of lane and pinsetter for R2's observer model
 *   Added Lane/Pinsetter Observer
 *   Rewrite of scoring algorythm in lane
 *
 *   Revision 1.10  2003/01/26 20:44:05  ???
 *   small changes
 *
 * 
 */

import Model.*;
import View.EndGamePrompt;
import View.EndGameReport;

import java.util.*;

public class Lane extends Thread implements Observer {
    private static final int LASTFRAME = 9;
    private Party party;
    private Pinsetter setter;
    private HashMap scores;
    private Vector subscribers;

    private boolean gameIsHalted;

    private boolean partyAssigned;
    private boolean gameFinished;
    private Iterator bowlerIterator;
    private int ball;
    private int bowlIndex;
    private int frameNumber;
    private boolean tenthFrameStrike;

    private int[] curScores;
    private int[][] cumulScores;
    private boolean canThrowAgain;

    private int[][] finalScores;
    private int gameNumber;

    private Bowler currentThrower;            // = the thrower who just took a throw

    /**
     * Lane()
     * <p>
     * Constructs a new lane and starts its thread
     *
     * @pre none
     * @post a new lane has been created and its thered is executing
     */
    public Lane() {
        setter = new Pinsetter();
        scores = new HashMap();
        subscribers = new Vector();

        gameIsHalted = false;
        partyAssigned = false;

        gameNumber = 0;

        setter.subscribe((Observer) this);

        this.start();
    }

    /**
     * run()
     * <p>
     * entry point for execution of this lane
     */
    public void run() {

        while (true) {
            if (partyAssigned && !gameFinished) {    // we have a party on this lane,
                // so next bowler can take a throw

                while (gameIsHalted) {    //lane sleeps if maintenance halts the game
                    waitTen();
                }

                if (bowlerIterator.hasNext()) {    //some bowlers haven't bowled the current frame yet
                    currentThrower = (Bowler) bowlerIterator.next();
                    bowlFrame();
                } else {
                    nextFrame();
                }
            } else if (partyAssigned && gameFinished) {
                endGame();
            }

            waitTen();
        }
    }

    /**
     * recordScores()
     * <p>
     * Adds the current bowler's score from the current game to the score history file.
     */
    private void recordScore() {
        finalScores[bowlIndex][gameNumber] = cumulScores[bowlIndex][9];
        try {
            Date date = new Date();
            String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
            ScoreHistoryFile.addScore(currentThrower.getNick(), dateString, new Integer(cumulScores[bowlIndex][9]).toString());
        } catch (Exception e) {
            System.err.println("Exception in addScore. " + e);
        }
    }

    /**
     * bowlFrame()
     * <p>
     * simulates a bowler bowling one frame, then tells the pinsetter to reset for the next bowler
     * records the score of the bowler if it is the last frame
     */
    private void bowlFrame() {
        canThrowAgain = true;
        tenthFrameStrike = false;
        ball = 0;
        while (canThrowAgain) {
            setter.ballThrown();        // simulate the thrower's ball hiting
            ball++;
        }
        if (frameNumber == LASTFRAME) {
            recordScore();
        }

        setter.reset();
        bowlIndex++;
    }

    /**
     * waitForMaintenance()
     * <p>
     * puts the lane to sleep for 10 milliseconds.
     */
    private void waitTen() {
        try {
            sleep(10);
        } catch (Exception e) {
        }
    }

    /**
     * nextFrame()
     * <p>
     * advances the game to the next frame, moves back to the start of the bowler list.
     * sets the game to finished if it is called on the last frame.
     */
    private void nextFrame() {
        frameNumber++;
        resetBowlerIterator();
        bowlIndex = 0;
        if (frameNumber > LASTFRAME) {
            gameFinished = true;
            gameNumber++;
        }
    }

    /**
     * endGame()
     * <p>
     * ends a game on a lane, resets the lane.
     * After getting a play again/don't play again response from the EndGamePrompt,
     * the lane either starts another game with the same party or clears its current party
     */
    private void endGame() {
        EndGamePrompt egp = new EndGamePrompt(((Bowler) party.getMembers().get(0)).getNickName() + "'s Party");
        int result = egp.getResult();
        egp.distroy();
        egp = null;


        System.out.println("result was: " + result);

        // TODO: send record of scores to control desk
        if (result == 1) {                    // yes, want to play again
            resetScores();
            resetBowlerIterator();

        } else if (result == 2) {// no, dont want to play another game
            sendReports();
        }
    }

    /**
     * sendReports()
     * <p>
     * Creates and Emails the bowlers' new score reports to their email addresses.
     */
    private void sendReports() {
        Vector printVector;
        EndGameReport egr = new EndGameReport(((Bowler) party.getMembers().get(0)).getNickName() + "'s Party", party);
        printVector = egr.getResult();
        partyAssigned = false;
        Iterator scoreIt = party.getMembers().iterator();
        party = null;


        publish(lanePublish());

        int myIndex = 0;
        while (scoreIt.hasNext()) {
            Bowler thisBowler = (Bowler) scoreIt.next();
            ScoreReport sr = new ScoreReport(thisBowler, finalScores[myIndex++], gameNumber);
            sr.sendEmail(thisBowler.getEmail());
            Iterator printIt = printVector.iterator();
            while (printIt.hasNext()) {
                if (thisBowler.getNick() == (String) printIt.next()) {
                    System.out.println("Printing " + thisBowler.getNick());
                    sr.sendPrintout();
                }
            }

        }
    }


    public void update(Observable o, Object arg) {
        if (arg instanceof PinsetterEvent) {
            PinsetterEvent pe = (PinsetterEvent) arg;
            if (pe.pinsDownOnThisThrow() >= 0) {            // this is a real throw
                markScore(currentThrower, frameNumber + 1, pe.getThrowNumber(), pe.pinsDownOnThisThrow());

                // next logic handles the ?: what conditions dont allow them another throw?
                // handle the case of 10th frame first
                if (frameNumber == 9) {
                    if (pe.totalPinsDown() == 10) {
                        setter.resetPins();
                        if (pe.getThrowNumber() == 1) {
                            tenthFrameStrike = true;
                        }
                    }

                    if ((pe.totalPinsDown() != 10) && (pe.getThrowNumber() == 2 && tenthFrameStrike == false)) {
                        canThrowAgain = false;
                        //publish( lanePublish() );
                    }

                    if (pe.getThrowNumber() == 3) {
                        canThrowAgain = false;
                        //publish( lanePublish() );
                    }
                } else { // its not the 10th frame

                    if (pe.pinsDownOnThisThrow() == 10) {        // threw a strike
                        canThrowAgain = false;
                        //publish( lanePublish() );
                    } else if (pe.getThrowNumber() == 2) {
                        canThrowAgain = false;
                        //publish( lanePublish() );
                    } else if (pe.getThrowNumber() == 3)
                        System.out.println("I'm here...");
                }
            } else {                                //  this is not a real throw, probably a reset
            }
        }
    }

    /**
     * resetBowlerIterator()
     * <p>
     * sets the current bower iterator back to the first bowler
     *
     * @pre the party as been assigned
     * @post the iterator points to the first bowler in the party
     */
    private void resetBowlerIterator() {
        bowlerIterator = (party.getMembers()).iterator();
    }

    /**
     * resetScores()
     * <p>
     * resets the scoring mechanism, must be called before scoring starts
     *
     * @pre the party has been assigned
     * @post scoring system is initialized
     */
    private void resetScores() {
        Iterator bowlIt = (party.getMembers()).iterator();

        while (bowlIt.hasNext()) {
            int[] toPut = new int[25];
            for (int i = 0; i != 25; i++) {
                toPut[i] = -1;
            }
            scores.put(bowlIt.next(), toPut);
        }


        gameFinished = false;
        frameNumber = 0;
    }

    /**
     * assignParty()
     * <p>
     * assigns a party to this lane
     *
     * @param theParty Party to be assigned
     * @pre none
     * @post the party has been assigned to the lane
     */
    public void assignParty(Party theParty) {
        party = theParty;
        resetBowlerIterator();
        partyAssigned = true;

        curScores = new int[party.getMembers().size()];
        cumulScores = new int[party.getMembers().size()][10];
        finalScores = new int[party.getMembers().size()][128]; //Hardcoding a max of 128 games, bite me.
        gameNumber = 0;

        resetScores();
    }

    /**
     * markScore()
     * <p>
     * Method that marks a bowlers score on the board.
     *
     * @param Cur   The current bowler
     * @param frame The frame that bowler is on
     * @param ball  The ball the bowler is on
     * @param score The bowler's score
     */
    private void markScore(Bowler Cur, int frame, int ball, int score) {
        int[] curScore;
        int index = ((frame - 1) * 2 + ball);

        curScore = (int[]) scores.get(Cur);


        curScore[index - 1] = score;
        scores.put(Cur, curScore);
        getScore(Cur, frame);
        publish(lanePublish());
    }

    /**
     * lanePublish()
     * <p>
     * Method that creates and returns a newly created laneEvent
     *
     * @return The new lane event
     */
    private LaneEvent lanePublish() {
        LaneEvent laneEvent = new LaneEvent(party, bowlIndex, currentThrower, cumulScores, scores, frameNumber + 1, curScores, ball, gameIsHalted);
        return laneEvent;
    }

    /**
     * getScore()
     * <p>
     * Method that calculates a bowlers score
     *
     * @param Cur   The bowler that is currently up
     * @param frame The frame the current bowler is on
     * @return The bowlers total score
     */
    private int getScore(Bowler Cur, int frame) {
        int[] curScore;
        int totalScore = 0;
        curScore = (int[]) scores.get(Cur);
        for (int i = 0; i != 10; i++) {
            cumulScores[bowlIndex][i] = 0;
        }
        int current = 2 * (frame - 1) + ball - 1;


        ScoreState state;
        ScoreContext context = new ScoreContext();

        for (int i = 0; i != current + 2; i++) {
            //Spare:
            if (i % 2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19) {
                state = new SpareState(context);
            }
            else if (i < current && i % 2 == 0 && curScore[i] == 10 && i < 18) {
                state = new StrikeState(context);
            }
            else{
                state = new NormThrowState(context);
            }

                context.getState().handle(curScore, i, cumulScores, bowlIndex, current);

                curScore = context.getState().getCurScore();
                cumulScores = context.getState().getCumulScores();
                current = context.getState().getCurrent();
                bowlIndex = context.getState().getBowlIndex();
            }
        return totalScore;
    }

    /**
     * isPartyAssigned()
     * <p>
     * checks if a party is assigned to this lane
     *
     * @return true if party assigned, false otherwise
     */
    public boolean isPartyAssigned() {
        return partyAssigned;
    }

    /**
     * isGameFinished
     *
     * @return true if the game is done, false otherwise
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     * subscribe
     * <p>
     * Method that will add a subscriber
     *
     * @param adding Observer that is to be added
     */

    public void subscribe(Observer adding) {
        subscribers.add(adding);
    }

    /**
     * unsubscribe
     * <p>
     * Method that unsubscribes an observer from this object
     *
     * @param removing The observer to be removed
     */

    public void unsubscribe(Observer removing) {
        subscribers.remove(removing);
    }

    /**
     * publish
     * <p>
     * Method that publishes an event to subscribers
     *
     * @param event Event that is to be published
     */

    public void publish(LaneEvent event) {
        if (subscribers.size() > 0) {
            Iterator eventIterator = subscribers.iterator();

            while (eventIterator.hasNext()) {
                ((Observer) eventIterator.next()).update(null, event);
            }
        }
    }

    /**
     * Accessor to get this Lane's pinsetter
     *
     * @return A reference to this lane's pinsetter
     */

    public Pinsetter getPinsetter() {
        return setter;
    }

    /**
     * Pause the execution of this game
     */
    public void pauseGame() {
        gameIsHalted = true;
        publish(lanePublish());
    }

    /**
     * Resume the execution of this game
     */
    public void unPauseGame() {
        gameIsHalted = false;
        publish(lanePublish());
    }
}
