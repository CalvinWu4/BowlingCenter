package Controller;

/**
 * Created by Brandon on 5/7/2017.
 */
public class NormThrowState implements ScoreState{

    ScoreContext context;
    int[] curScore;
    int[][] cumulScores;
    int bowlIndex;
    int current;

    public NormThrowState(ScoreContext context) {
        this.context = context;
        context.setState(this);
    }

    public void handle(int[] curScore, int i, int[][] cumulScores, int bowlIndex, int current) {
        //We're dealing with a normal throw, add it and be on our way.
        if (i % 2 == 0 && i < 18) {
            if (i / 2 == 0) {
                //First frame, first ball.  Set his cumul score to the first ball
                if (curScore[i] != -2) {
                    cumulScores[bowlIndex][i / 2] += curScore[i];
                }
            } else if (i / 2 != 9) {
                //add his last frame's cumul to this ball, make it this frame's cumul.
                if (curScore[i] != -2) {
                    cumulScores[bowlIndex][i / 2] += cumulScores[bowlIndex][i / 2 - 1] + curScore[i];
                } else {
                    cumulScores[bowlIndex][i / 2] += cumulScores[bowlIndex][i / 2 - 1];
                }
            }
        } else if (i < 18) {
            if (curScore[i] != -1 && i > 2) {
                if (curScore[i] != -2) {
                    cumulScores[bowlIndex][i / 2] += curScore[i];
                }
            }
        }
        if (i / 2 == 9) {
            if (i == 18) {
                cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
            }
            if (curScore[i] != -2) {
                cumulScores[bowlIndex][9] += curScore[i];
            }
        } else if (i / 2 == 10) {
            if (curScore[i] != -2) {
                cumulScores[bowlIndex][9] += curScore[i];
            }
        }
        this.curScore = curScore;
        this.cumulScores = cumulScores;
        this.bowlIndex = bowlIndex;
        this.current = current;
    }

    public int[] getCurScore(){
        return curScore;
    }

    public int[][] getCumulScores() {
        return cumulScores;
    }

    public int getBowlIndex(){
        return bowlIndex;
    }

    public int getCurrent(){
        return current;
    }
}
