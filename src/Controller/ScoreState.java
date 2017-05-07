package Controller;

/**
 * Created by Brandon on 5/7/2017.
 */
public interface ScoreState {

    public void handle(int[] curScore, int i, int[][] cumulScores, int bowlIndex, int current);
    public int[] getCurScore();
    public int[][] getCumulScores();
    public int getBowlIndex();
    public int getCurrent();
}
