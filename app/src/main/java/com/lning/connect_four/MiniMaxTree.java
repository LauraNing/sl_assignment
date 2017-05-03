package com.lning.connect_four;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lning on 4/27/17.
 */

public class MiniMaxTree {

    private static final String TAG = "Connect4.Tree";
    private static final int maxDepth = 4;
    private int[][] nodes;
    public int value;
    private ArrayList<Integer> bestMoves;
    Slot prev = null;
    private int depth;
    private final int MaxRows = GameActivity.NUM_ROWS;
    private final int MaxCols = GameActivity.NUM_COLS;
    public final static int HUMAN_PLAYER = 1;
    public final static int AI_PLAYER = 2;

    public class Slot {
        public int i, j;
        public int iPlayer; //0: empty; 1: Human player; 2: AI player;
    }

    public MiniMaxTree(int[][] slots, int depth) {
        this.nodes = slots;
        this.bestMoves = new ArrayList<Integer>();
        this.depth = depth;
        this.value = getValue();

        if(depth < maxDepth)
        {
            Log.d(TAG, "depth = " + depth);
            prev = new Slot();
            ArrayList<Integer> possibilities = new ArrayList<Integer>();  // all the possible moves
            for(int i = 0; i < MaxCols; i++)
                if(slots[i][0] == 0)
                    possibilities.add(i);

            for(int i = 0; i < possibilities.size(); i++)
            {
                int row = lastAvailableRow(i);
                if( row != -1 ) {
                    fillSlot(i,row);
                }
                MiniMaxTree child = new MiniMaxTree(nodes, depth+1);
                //revert back the previous insert
                nodes[prev.i][prev.j] = 0;

                if(i == 0)
                {
                    // set initial value to the child value of the first column
                    bestMoves.add(possibilities.get(i));
                    value = child.value;
                }
                else if(depth % 2 == 0)
                {
                    if(value < child.value)
                    {
                        bestMoves.clear();
                        bestMoves.add(possibilities.get(i));
                        this.value = child.value;
                    }
                    else if(value == child.value)
                        bestMoves.add(possibilities.get(i));
                }
                else if(depth % 2 == 1)
                {
                    if(value > child.value)
                    {
                        bestMoves.clear();
                        bestMoves.add(possibilities.get(i));
                        this.value = child.value;
                    }
                    else if(value == child.value)
                        bestMoves.add(possibilities.get(i));
                }
            }
        }
        else
        {
            this.value = getValue();
        }
    }

    public int lastAvailableRow(int col) {
        for (int row = GameActivity.NUM_ROWS - 1; row >= 0; row--) {
            if (nodes[col][row] == 0) {
                return row;
            }
        }
        return -1;
    }

    public void fillSlot(int col, int row) {
        if(this.depth % 2 == 0)
            nodes[col][row] = AI_PLAYER;
        else
            nodes[col][row] = HUMAN_PLAYER;

        prev.i = col;
        prev.j = row;
        prev.iPlayer = nodes[col][row];

    }

    public int getValue()
    {
        int value = 0;
        int numAI = 0;
        int numHuman = 0;

        for(int j = 0; j < MaxRows; j++)
        {
            for(int i = 0; i < MaxCols; i++)
            {
                if(nodes[i][j] != 0 )
                {
                    if(nodes[i][j] == AI_PLAYER)
                    {
                        Log.d(TAG, "AI_PLAYER");
                        value += possibleConnections(i, j);
                        numAI ++;
                    }
                    else
                    {
                        Log.d(TAG, "HUMAN_PLAYER");
                        value -= possibleConnections(i, j);
                        numHuman++;
                    }
                }
            }
        }
        //Log.d(TAG, "AI_PLAYER: " + numAI + "HUMAN_PLAYER: " + numHuman);
        Log.d(TAG, "getValue() returns: " + value);
        return value;
    }

    public int possibleConnections(int i, int j)
    {
        int player = nodes[i][j];
        int value = 0;

        value += lineOfFour(i, j, 0, 1, player);
        value += lineOfFour(i, j, 1, -1, player);
        value += lineOfFour(i, j, 1, 0, player);
        value += lineOfFour(i, j, 1, 1, player );

        Log.d(TAG, "possibleConnections: " + i + " " + j + " " + value);
        return value;
    }

    private int lineOfFour( int x, int y, int i, int j, int player) {

        int k, col, row;
        int count = 0;
        for (k = -3; k< 4; k++) {
            col = x + k*i;
            row = y + k*j;
            if (col < 0 || col >= MaxCols || row < 0 || row >= MaxRows) {
                count = 0;
            }
            else if (nodes[col][row] == player) {
                count++;
            } else {
                count = 0;
            }

            if (count >= 4) {
                return 100;
            }
        }
        return 0;
    }

    public int getX()
    {
        int random = (int)(Math.random() * 100) % bestMoves.size();
        return bestMoves.get(random);
    }
}
