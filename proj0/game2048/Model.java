package game2048;

import static org.junit.Assert.fail;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        System.out.println("before: ");
        printlnForTest();


        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        /* 
         * NORTH: 右邊
         * SOUTH: 左邊
         * 
         * (未測試)
         * WEST: 上面
         * EAST: 下面
         */
        int addScore = 0;
        switch (side) {
            case NORTH:
                changed = northAction();
                break;
            case SOUTH:
                changed = southAction();
                break;
            case WEST:
                changed = westAction();
                break;
            case EAST:
                changed = eastAction();
                break;
            default:
                break;
        }

        System.out.println("after: ");
        printlnForTest();

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }


    private boolean northAction() {
        int bSize = this.board.size();
        int score = 0;
        boolean change = false;

        for(int row = 0; row < bSize; row++) {

            int currentCol = bSize - 1;
            while (currentCol >= 0) {
                if (tile(row, currentCol) == null) {
                    int nextOneValue = nextOneIndexForNorth(currentCol - 1, row);
                    if (nextOneValue == -1) break;

                    int nextTwoValue = nextOneIndexForNorth(nextOneValue - 1, row);

                    if (nextTwoValue == -1) {
                        this.board.move(row, currentCol, tile(row, nextOneValue));
                        change = true;
                        break;
                    } else {
                        if (tile(row, nextOneValue).value() == tile(row, nextTwoValue).value()) {
                            this.board.move(row, currentCol, tile(row, nextOneValue));
                            this.board.move(row, currentCol, tile(row, nextTwoValue));
                            score += tile(row, currentCol).value();
                            change = true;
                        } else {
                            this.board.move(row, currentCol, tile(row, nextOneValue));
                            this.board.move(row, currentCol - 1, tile(row, nextTwoValue));
                            change = true;
                        }
                    }
                } else {
                    int nextOneValue = nextOneIndexForNorth(currentCol - 1, row);
                    if (nextOneValue == -1) break;

                    if (tile(row, currentCol).value() == tile(row, nextOneValue).value()) {
                        this.board.move(row, currentCol, tile(row, nextOneValue));
                        score += tile(row, currentCol).value();
                        change = true;
                    } else {
                        this.board.move(row, currentCol - 1, tile(row, nextOneValue));
                        change = true;
                    }

                }

                currentCol -= 1;
            }
            System.out.println("row: " + row);
            printlnForTest();
        }

        this.score += score;
        return change;
    }

    private boolean southAction() {
        int bSize = this.board.size();
        int score = 0;
        boolean change = false;

        for(int row = 0; row < bSize; row++) {

            int currentCol = 0;
            while (currentCol < bSize) {
                if (tile(row, currentCol) == null) {
                    int nextOneValue = nextOneIndexForSouth(currentCol + 1, row);
                    if (nextOneValue == -1) break;

                    int nextTwoValue = nextOneIndexForSouth(nextOneValue + 1, row);

                    if (nextTwoValue == -1) {
                        this.board.move(row, currentCol, tile(row, nextOneValue));
                        change = true;
                        break;
                    } else {
                        if (tile(row, nextOneValue).value() == tile(row, nextTwoValue).value()) {
                            this.board.move(row, currentCol, tile(row, nextOneValue));
                            this.board.move(row, currentCol, tile(row, nextTwoValue));
                            score += tile(row, currentCol).value();
                            change = true;
                        } else {
                            this.board.move(row, currentCol, tile(row, nextOneValue));
                            this.board.move(row, currentCol + 1, tile(row, nextTwoValue));
                            change = true;
                        }
                    }
                } else {
                    int nextOneValue = nextOneIndexForSouth(currentCol + 1, row);
                    if (nextOneValue == -1) break;

                    if (tile(row, currentCol).value() == tile(row, nextOneValue).value()) {
                        this.board.move(row, currentCol, tile(row, nextOneValue));
                        score += tile(row, currentCol).value();
                        change = true;
                    } else {
                        this.board.move(row, currentCol + 1, tile(row, nextOneValue));
                        change = true;
                    }

                }

                currentCol += 1;
            }
            // System.out.println("row: " + row);
            // printlnForTest();
        }

        this.score += score;
        return change;
    }

    private boolean westAction() {
        int bSize = this.board.size();
        int score = 0;
        boolean change = false;

        for(int col = 0; col < bSize; col++) {

            int currentRow = 0;
            while (currentRow < bSize) {
                if (tile(currentRow, col) == null) {
                    int nextOneValue = nextOneIndexForWest(currentRow + 1, col);
                    if (nextOneValue == -1) break;

                    int nextTwoValue = nextOneIndexForWest(nextOneValue + 1, col);

                    if (nextTwoValue == -1) {
                        this.board.move(currentRow, col, tile(nextOneValue, col));
                        change = true;
                        break;
                    } else {
                        if (tile(nextOneValue, col).value() == tile(nextTwoValue, col).value()) {
                            this.board.move(currentRow, col, tile(nextOneValue, col));
                            this.board.move(currentRow, col, tile(nextTwoValue, col));
                            score += tile(currentRow, col).value();
                            change = true;
                        } else {
                            this.board.move(currentRow, col, tile(nextOneValue, col));
                            this.board.move(currentRow + 1, col, tile(nextTwoValue, col));
                            change = true;
                        }
                    }
                } else {
                    int nextOneValue = nextOneIndexForWest(currentRow + 1, col);
                    if (nextOneValue == -1) break;

                    if (tile(currentRow, col).value() == tile(nextOneValue, col).value()) {
                        this.board.move(currentRow, col, tile(nextOneValue, col));
                        score += tile(currentRow, col).value();
                        change = true;
                    } else {
                        this.board.move(currentRow + 1, col, tile(nextOneValue, col));
                        change = true;
                    }

                }

                currentRow += 1;
            }
            // System.out.println("row: " + row);
            // printlnForTest();
        }

        this.score += score;
        return change;
    }

    private boolean eastAction() {
        int bSize = this.board.size();
        int score = 0;
        boolean change = false;

        for(int col = 0; col < bSize; col++) {

            int currentRow = bSize - 1;
            while (currentRow >= 0) {
                if (tile(currentRow, col) == null) {
                    int nextOneValue = nextOneIndexForEast(currentRow - 1, col);
                    if (nextOneValue == -1) break;

                    int nextTwoValue = nextOneIndexForEast(nextOneValue - 1, col);

                    if (nextTwoValue == -1) {
                        this.board.move(currentRow, col, tile(nextOneValue, col));
                        change = true;
                        break;
                    } else {
                        if (tile(nextOneValue, col).value() == tile(nextTwoValue, col).value()) {
                            this.board.move(currentRow, col, tile(nextOneValue, col));
                            this.board.move(currentRow, col, tile(nextTwoValue, col));
                            score += tile(currentRow, col).value();
                            change = true;
                        } else {
                            this.board.move(currentRow, col, tile(nextOneValue, col));
                            this.board.move(currentRow - 1, col, tile(nextTwoValue, col));
                            change = true;
                        }
                    }
                } else {
                    int nextOneValue = nextOneIndexForEast(currentRow - 1, col);
                    if (nextOneValue == -1) break;

                    if (tile(currentRow, col).value() == tile(nextOneValue, col).value()) {
                        this.board.move(currentRow, col, tile(nextOneValue, col));
                        score += tile(currentRow, col).value();
                        change = true;
                    } else {
                        this.board.move(currentRow - 1, col, tile(nextOneValue, col));
                        change = true;
                    }

                }

                currentRow -= 1;
            }
            System.out.println("row: " + col);
            printlnForTest();
        }

        this.score += score;
        return change;
    }


    private int nextOneIndexForNorth(int start, int row) {
        for(int i = start; i >= 0; i--) {
            if (tile(row, i) != null) {
                return i; 
            }
        }
        return -1;
    }

    private int nextOneIndexForSouth(int start, int row) {
        for(int i = start; i < this.board.size(); i++) {
            if (tile(row, i) != null) {
                return i; 
            }
        }
        return -1;
    }

    private int nextOneIndexForWest(int start, int col) {
        for(int i = start; i < this.board.size(); i++) {
            if (tile(i, col) != null) {
                return i; 
            }
        }
        return -1;
    }

    private int nextOneIndexForEast(int start, int col) {
        for(int i = start; i >= 0; i--) {
            if (tile(i, col) != null) {
                return i; 
            }
        }
        return -1;
    }


    // private Board collapseList(Board b, Side) {

    // }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) != null) {
                    if (b.tile(i, j).value() == MAX_PIECE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (emptySpaceExists(b)) return true;

        int[][] direction = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1}
        }; 
        
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                for(int x = 0; x < direction.length; x++) {
                    if (b.tile(i, j) != null) {
                        int adjacent_i = i + direction[x][0];
                        int adjacent_j = j + direction[x][1];
                        if ((0 <= adjacent_i && adjacent_i < b.size()) && (0 <= adjacent_j && adjacent_j < b.size())) {
                            if(b.tile(i, j).value() == b.tile(adjacent_i, adjacent_j).value()){
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }


    private void printlnForTest() {
        for(int i = 0; i < this.board.size(); i++) {
            for(int j = 0; j < this.board.size(); j++) {
                if (tile(i, j) == null) {
                    System.out.print("|" + i + " " + j + "  ");
                } else {
                    System.out.print("|" + i + " " + j + " " + tile(i, j).value());
                }
            }
            System.out.println("");
        }
    }
}
