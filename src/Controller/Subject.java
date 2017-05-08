package Controller;

import Model.Event;

import java.util.Observer;

/**
 * Created by Calvin on 5/7/2017.
 */
public interface Subject {

    void subscribe(Observer adding);

    void publish(Event event);
}
