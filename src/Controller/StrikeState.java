package Controller;

/**
 * Created by Brandon on 5/7/2017.
 */
public class StrikeState implements ScoreState{

    ScoreContext context;
    int[] curScore;
    int[][] cumulScores;
    int bowlIndex;
    int current;


    public StrikeState(ScoreContext context){
        this.context = context;
        context.setState(this);
    }

    public void handle(int[] curScore, int i, int[][] cumulScores, int bowlIndex, int current){
        int strikeballs = 0;
        //This ball is the first ball, and was a strike.
        //If we can get 2 balls after it, good add them to cumul.
        if (curScore[i + 2] != -1) {
            strikeballs = 1;
            if (curScore[i + 3] != -1) {
                //Still got em.
                strikeballs = 2;
            } else if (curScore[i + 4] != -1) {
                //Ok, got it.
                strikeballs = 2;
            }
        }
        if (strikeballs == 2) {
            //Add up the strike.
            //Add the next two balls to the current cumulscore.
            cumulScores[bowlIndex][i / 2] += 10;
            if (curScore[i + 1] != -1) {
                cumulScores[bowlIndex][i / 2] += curScore[i + 1] + cumulScores[bowlIndex][(i / 2) - 1];
                if (curScore[i + 2] != -1) {
                    if (curScore[i + 2] != -2) {
                        cumulScores[bowlIndex][(i / 2)] += curScore[i + 2];
                    }
                } else {
                    if (curScore[i + 3] != -2) {
                        cumulScores[bowlIndex][(i / 2)] += curScore[i + 3];
                    }
                }
            } else {
                if (i / 2 > 0) {
                    cumulScores[bowlIndex][i / 2] += curScore[i + 2] + cumulScores[bowlIndex][(i / 2) - 1];
                } else {
                    cumulScores[bowlIndex][i / 2] += curScore[i + 2];
                }
                if (curScore[i + 3] != -1) {
                    if (curScore[i + 3] != -2) {
                        cumulScores[bowlIndex][(i / 2)] += curScore[i + 3];
                    }
                } else {
                    cumulScores[bowlIndex][(i / 2)] += curScore[i + 4];
                }
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
