package Controller;

import Model.Event;

import java.util.Iterator;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by Calvin on 5/7/2017.
 */
public abstract class Subject extends Thread{
    Vector subscribers;

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
     * publish
     *
     * Method that publishes an event to subscribers
     *
     * @param event Event that is to be published
     */

    void publish(Event event) {
        if (subscribers.size() > 0) {
            Iterator eventIterator = subscribers.iterator();

            while (eventIterator.hasNext()) {
                ((Observer) eventIterator.next()).update(null, event);
            }
        }
    }
}
