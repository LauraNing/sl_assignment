package com.lning.connect_four;

/**
 * Created by lning on 4/26/17.
 */

public class GameBoard {

    private static GameBoard INSTANCE = null;
    private static int numCols = 0;
    private static int numRows = 0;
    private static int[][] cells;  //0: empty; 1: player 1st; 2: player 2nd;
    private boolean hasWinner = false;
    public enum Turn {FIRST, SECOND};
    public static Turn turn;

    private GameBoard(int width, int height) {
        numRows = height;
        numCols = width;
        cells = new int[numCols][numRows];
        reset();
    }

    public static GameBoard getInstance(int numColumns, int numRows) {
        if(INSTANCE == null) {
            try {
                INSTANCE = new GameBoard(numColumns, numRows);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    public void reset() {
        hasWinner = false;
        turn = Turn.FIRST;
        for (int col = 0; col < numCols; col++) {
            for (int row = 0; row < numRows; row++) {
                cells[col][row] = 0;
            }
        }
    }

    public boolean hasWinner() {
        return hasWinner;
    }

    public int lastAvailableRow(int col) {
        for (int row = numRows - 1; row >= 0; row--) {
            if (cells[col][row] == 0) {
                return row;
            }
        }
        return -1;
    }

    public void fillCell(int col, int row) {
        if(turn == Turn.FIRST) {
            cells[col][row] = 1;
        }
        else {
            cells[col][row] = 2;
        }
    }

    public void toggleTurn() {
        if (turn == Turn.FIRST) {
            turn = Turn.SECOND;
        } else {
            turn = Turn.FIRST;
        }
    }

    public Turn getCurrentTurn() {
        return turn;
    }

    public Turn playerOfCell(int col, int row) {
        if( cells[col][row] == 1 )
            return Turn.FIRST;
        if( cells[col][row] == 2 )
            return Turn.SECOND;
        else
            return null;
    }

    public boolean checkForWin() {
        for (int col = 0; col < numCols; col++) {
            if (isContiguous(turn, 0, 1, col, 0, 0) || isContiguous(turn, 1, 1, col, 0, 0) || isContiguous(turn, -1, 1, col, 0, 0)) {
                hasWinner = true;
                return true;
            }
            if (isContiguous(turn, 1, -1, col, numRows-1, 0) || isContiguous(turn, -1, -1, col, numRows-1, 0)) {
                hasWinner = true;
                return true;
            }
        }
        for (int row = 0; row < numRows; row++) {
            if (isContiguous(turn, 1, 0, 0, row, 0) || isContiguous(turn, 1, 1, 0, row, 0) || isContiguous(turn, -1, 1, numCols - 1, row, 0)) {
                hasWinner = true;
                return true;
            }
        }
        return false;
    }

    private boolean isContiguous(Turn player, int dirX, int dirY, int col, int row, int count) {
        if (count >= 4) {
            return true;
        }
        if (col < 0 || col >= numCols || row < 0 || row >= numRows) {
            return false;
        }
        if ( playerOfCell(col, row) == player) {
            return isContiguous(player, dirX, dirY, col + dirX, row + dirY, count + 1);
        } else {
            return isContiguous(player, dirX, dirY, col + dirX, row + dirY, 0);
        }
    }

    public int getCells(int x, int y) {
        return cells[x][y];
    }

}
