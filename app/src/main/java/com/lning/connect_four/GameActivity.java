package com.lning.connect_four;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    private final static String TAG = "Connect4";

    public final static int NUM_ROWS = 6;
    public final static int NUM_COLS = 7;

    public final static String ONE_PLAYER = "ONE_PLAYER";
    public final static String TWO_PLAYER = "TWO_PLAYERS";

    private ImageView[][] cellViews;
    private View boardView;
    private static GameBoard board;
    private static String playerSet = TWO_PLAYER;  //default: 2 players' game

    private class ViewHolder {
        public TextView winnerText;
        public ImageView turnIndicatorImageView;
    }

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = GameBoard.getInstance(NUM_COLS, NUM_ROWS);
        boardView = findViewById(R.id.game_board);
        buildCells();

        boardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP: {
                        int col = colAtX(motionEvent.getX());
                        if (col != -1)
                            drop(col);
                    }
                }
                return true;
            }
        });
        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        Button playerButton = (Button) findViewById((R.id.player_button));
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPlayerSet();
            }
        });
        initPlayerButton();

        viewHolder = new ViewHolder();
        viewHolder.turnIndicatorImageView = (ImageView) findViewById(R.id.turn_indicator_image_view);
        viewHolder.turnIndicatorImageView.setImageResource(resourceForTurn());
        viewHolder.winnerText = (TextView) findViewById(R.id.winner_text);
        viewHolder.winnerText.setVisibility(View.GONE);

    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildCells() {
        cellViews = new ImageView[NUM_ROWS][NUM_COLS];
        for (int r=0; r<NUM_ROWS; r++) {
            ViewGroup row = (ViewGroup) ((ViewGroup) boardView).getChildAt(r);
            row.setClipChildren(false);
            for (int c=0; c<NUM_COLS; c++) {
                ImageView imageView = (ImageView) row.getChildAt(c);
                imageView.setImageResource(android.R.color.transparent);
                if(board.playerOfCell(c,r) == GameBoard.Turn.FIRST)
                    imageView.setImageResource(R.drawable.red);
                else if(board.playerOfCell(c,r) == GameBoard.Turn.SECOND)
                    imageView.setImageResource(R.drawable.yellow);
                cellViews[r][c] = imageView;
            }
        }
    }

    private void drop(int col) {
        if (board.hasWinner())
            return;
        int row = board.lastAvailableRow(col);
        if (row == -1)
            return;
        final ImageView cell = cellViews[row][col];
        float move = -(cell.getHeight() * row + cell.getHeight() + 15);
        cell.setY(move);
        cell.setImageResource(resourceForTurn());
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, Math.abs(move));
        anim.setDuration(850);
        anim.setFillAfter(true);
        cell.startAnimation(anim);
        board.fillCell(col, row);
        if (board.checkForWin()) {
            win();
        } else {
            changeTurn();
        }
    }

    private void win() {
        int color = board.turn == GameBoard.Turn.FIRST ? getResources().getColor(R.color.primary_player) : getResources().getColor(R.color.secondary_player);
        viewHolder.winnerText.setTextColor(color);
        viewHolder.winnerText.setVisibility(View.VISIBLE);
    }

    private void changeTurn() {
        board.toggleTurn();
        viewHolder.turnIndicatorImageView.setImageResource(resourceForTurn());

        // AI used
        if(playerSet == ONE_PLAYER && GameBoard.Turn.SECOND == board.getCurrentTurn()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drop(minimax());
                }
            },1000);
        }
    }

    private int colAtX(float x) {
        float colWidth = cellViews[0][0].getWidth();
        int col = (int) x / (int) colWidth;
        if (col < 0 || col > NUM_ROWS)
            return -1;
        return col;
    }
    private int resourceForTurn() {
        switch (board.turn) {
            case FIRST:
                return R.drawable.red;
            case SECOND:
                return R.drawable.yellow;
        }
        return R.drawable.red;
    }

    private void reset() {
        board.reset();
        viewHolder.winnerText.setVisibility(View.GONE);
        viewHolder.turnIndicatorImageView.setImageResource(resourceForTurn());
        for (int r=0; r<NUM_ROWS; r++) {
            for (int c=0; c<NUM_COLS; c++) {
                cellViews[r][c].setImageResource(android.R.color.transparent);
            }
        }
    }

    private void initPlayerButton () {
        Button playerButton = (Button) findViewById((R.id.player_button));
        if( playerSet == TWO_PLAYER ) {
            playerButton.setText(ONE_PLAYER);
        }
        else {
            playerButton.setText(TWO_PLAYER);
        }
    }
    private void switchPlayerSet() {
        Button playerButton = (Button) findViewById((R.id.player_button));
        if( playerSet == TWO_PLAYER ) {
            playerSet = ONE_PLAYER;
            playerButton.setText(TWO_PLAYER);
        }
        else {
            playerSet = TWO_PLAYER;
            playerButton.setText(ONE_PLAYER);
        }
    }

    public int minimax()
    {
        int[][] slots = new int[NUM_COLS][NUM_ROWS];
        for (int i=0; i < NUM_COLS; i++) {
            for (int j=0; j < NUM_ROWS; j++) {
                slots[i][j] = board.getCells(i,j);
            }
        }
        MiniMaxTree tree = new MiniMaxTree(slots, 0);
        return tree.getX();
    }
}
