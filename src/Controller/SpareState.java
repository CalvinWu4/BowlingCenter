package Controller;


/**
 * Created by Brandon on 5/7/2017.
 */
public class SpareState implements ScoreState{

    ScoreContext context;
    int[] curScore;
    int[][] cumulScores;
    int bowlIndex;
    int current;

    public SpareState(ScoreContext context){
        this.context = context;
        context.setState(this);
    }

    public void handle(int[] curScore, int i, int[][] cumulScores, int bowlIndex, int current) {
        //This ball was a the second of a spare.
        //Also, we're not on the current ball.
        //Add the next ball to the ith one in cumul.
        cumulScores[bowlIndex][(i / 2)] += curScore[i + 1] + curScore[i];

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
