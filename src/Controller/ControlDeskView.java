package Controller;

/* ControlDeskView.java
 *
 *  Version:
 *		$Id$
 *
 *  Revisions:
 * 		$Log$
 *
 */

import Model.ControlDeskEvent;
import View.AddPartyView;
import View.LaneStatusView;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.*;


/**
 * Class representing the control desk.
 */
public class ControlDeskView implements ActionListener, Observer {

    private ControlDesk controlDesk;
    private int maxMembers;
    private JList partyList;
    private JButton addParty;
    private JButton assign;
    private JButton finished;
    private JFrame win;

    ControlDeskView(ControlDesk controlDesk, int maxMembers) {
        this.controlDesk = controlDesk;
        this.maxMembers = maxMembers;
        partyList = getPartyList();

        /* Buttons */
        addParty = new JButton("Add Party");
        addParty.addActionListener(this);

        assign = new JButton("Assign Lanes");
        assign.addActionListener(this);

        finished = new JButton("Finished");
        finished.addActionListener(this);

        /* Inner Panels */
        JPanel addPartyPanel = getAddPartyPanel(addParty);
        JPanel finishedPanel = getFinishedPanel(finished);

        /* Outer Panels */
        JPanel controlsPanel = getControlsPanel(addPartyPanel, finishedPanel);
        JPanel laneStatusPanel = getLaneStatusPanel(controlDesk, controlDesk.getNumLanes());
        JPanel partyPanel = getPartyPanel(partyList);

        /* Main panel */
        JPanel colPanel = getColPanel(controlsPanel, laneStatusPanel, partyPanel);

        /* Window */
        win = getWin(colPanel);
        win.setVisible(true);
    }

    /**
     * Handler for actionEvents
     *
     * @param e the ActionEvent that triggered the handler
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addParty)) {
            new AddPartyView(this, maxMembers);
        } else if (e.getSource().equals(assign)) {
            controlDesk.assignLane();
        } else if (e.getSource().equals(finished)) {
            win.setVisible(false);
            System.exit(0);
        }
    }

    /**
     * Receive a new party from andPartyView.
     *
     * @param addPartyView the AddPartyView that is providing a new party
     */
    public void updateAddParty(AddPartyView addPartyView) {
        controlDesk.addPartyQueue(addPartyView.getParty());
    }

    /**
     * Receive a broadcast from a ControlDesk
     * @param o
     * @param arg the ControlDeskEvent that triggered the handler
     */
    public void update(Observable o, Object arg) {
        if (arg instanceof ControlDeskEvent) {
            ControlDeskEvent ce = (ControlDeskEvent) arg;
            partyList.setListData(ce.getPartyQueue());
        }
    }

    /**
     * getPartyList
     *
     * @return party list
     */
    private JList getPartyList() {
        Vector empty = new Vector();
        empty.add("(Empty)");
        JList newPartyList = new JList(empty);
        newPartyList.setFixedCellWidth(120);
        newPartyList.setVisibleRowCount(10);
        return newPartyList;
    }

    /**
     * getWin
     *
     * @param colPanel column panel
     * @return JFrame window
     */
    private JFrame getWin(JPanel colPanel) {
        JFrame newWin = new JFrame("Control Desk");
        newWin.getContentPane().setLayout(new BorderLayout());
        ((JPanel) newWin.getContentPane()).setOpaque(false);

        /* Close program when this window closes */
        newWin.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Center Window on Screen
        Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
        newWin.setLocation(
                ((screenSize.width) / 2) - ((newWin.getSize().width) / 2),
                ((screenSize.height) / 2) - ((newWin.getSize().height) / 2));

        newWin.getContentPane().add("Center", colPanel);
        newWin.pack();
        return newWin;
    }

    /**
     * addPartyPanel
     *
     * @param addParty addParty button
     * @return copy of JPanel addPartyPanel
     */
    private JPanel getAddPartyPanel(JButton addParty) {
        JPanel addPartyPanel = new JPanel();
        addPartyPanel.setLayout(new FlowLayout());
        addPartyPanel.add(addParty);
        return addPartyPanel;
    }

    /**
     * getFinishedPanel
     *
     * @param finishedButton finished JButton
     * @return copy of finishedPanel JPanel
     */
    private JPanel getFinishedPanel(JButton finishedButton) {
        JPanel finishedPanel = new JPanel();
        finishedPanel.setLayout(new FlowLayout());
        finishedPanel.add(finishedButton);
        return finishedPanel;
    }

    /**
     * getControlsPanel
     *
     * @param addPartyPanel addPartyPanel JPanel
     * @param finishedPanel finishedPanel JPanel
     * @return copy of controlsPanel JPanel
     */
    private JPanel getControlsPanel(JPanel addPartyPanel, JPanel finishedPanel) {
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(3, 1));
        controlsPanel.setBorder(new TitledBorder("Controls"));
        controlsPanel.add(finishedPanel);
        controlsPanel.add(addPartyPanel);
        return controlsPanel;
    }

    /**
     * getLaneStatusPanel
     *
     * @param controlDesk ControlDesk
     * @param numLanes    int number of lanes
     * @return copy of LaneStatusPanel JPanel
     */
    private JPanel getLaneStatusPanel(ControlDesk controlDesk, int numLanes) {
        JPanel laneStatusPanel = new JPanel();
        laneStatusPanel.setLayout(new GridLayout(numLanes, 1));
        laneStatusPanel.setBorder(new TitledBorder("Lane Status"));

        Iterator it = controlDesk.getLanes().iterator();
        int laneCount = 0;
        while (it.hasNext()) {
            Lane curLane = (Lane) it.next();
            addLanePanel(laneStatusPanel, laneCount, curLane);
            laneCount += 1;
        }
        return laneStatusPanel;
    }

    /**
     * addLanePanel
     *
     * @param laneStatusPanel laneStatusPanel JPanel
     * @param laneCount       int lane count
     * @param curLane         current Lane
     */
    private void addLanePanel(JPanel laneStatusPanel, int laneCount, Lane curLane) {
        LaneStatusView laneStat = new LaneStatusView(curLane, (laneCount + 1));
        curLane.subscribe(laneStat);
        curLane.getPinsetter().subscribe(laneStat);
        JPanel lanePanel = laneStat.showLane();
        lanePanel.setBorder(new TitledBorder("Lane" + laneCount + 1));
        laneStatusPanel.add(lanePanel);
    }

    /**
     * getPartyPanel
     *
     * @param partyList partyList JList
     * @return copy of partyPanel JPanel
     */
    private JPanel getPartyPanel(JList partyList) {
        JPanel partyPanel = new JPanel();
        partyPanel.setLayout(new FlowLayout());
        partyPanel.setBorder(new TitledBorder("Party Queue"));

        JScrollPane partyPane = new JScrollPane(partyList);
        partyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        partyPanel.add(partyPane);
        return partyPanel;
    }

    /**
     * getColPanel
     *
     * @param controlsPanel   controls JPanel
     * @param laneStatusPanel lane Status JPanel
     * @param partyPanel      party JPanel
     * @return copy of ColumnPanel JPanel
     */
    private JPanel getColPanel(JPanel controlsPanel, JPanel laneStatusPanel, JPanel partyPanel) {
        JPanel colPanel = new JPanel();
        colPanel.setLayout(new BorderLayout());
        colPanel.add(controlsPanel, "East");
        colPanel.add(laneStatusPanel, "Center");
        colPanel.add(partyPanel, "West");
        return colPanel;
    }
}
