package Controller;

/**
 * Created by Brandon on 5/7/2017.
 */
public class ScoreContext {

    private ScoreState state;

    public ScoreContext(){}

    public void setState(ScoreState state){
        this.state = state;
    }

    public ScoreState getState(){
        return state;
    }
}
