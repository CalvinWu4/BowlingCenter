package Controller;/* ControlDeskObserver.java
 *
 *  Version
 *  $Id$
 * 
 *  Revisions:
 * 		$Log$
 * 
 */

import Model.ControlDeskEvent;

/**
 * Interface for classes that observe control desk events
 *
 */

public interface ControlDeskObserver {

	public void receiveControlDeskEvent(ControlDeskEvent ce);

}
