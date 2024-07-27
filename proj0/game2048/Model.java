package game2048;

import java.util.Formatter;  // 格式化输出
import java.util.Observable; // 观察者模式



/** The state of a game of 2048.
 *  @author wqs111
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


    /** 辅助方法：检查第一个方块是否在边界上，否则将其一定至边界
     *
     */
    public void fillEdge(Side side) {
        return;
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

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        //////////////////////////////////////////////
        // i do
        // 例如 2 4 2 2，向左合并：从左向右依次检测本行右侧第一个 □ 是否与其相同，相同合并；
        // 反之对下一个 □ 进行相同操作
        // 当合并发生（画面变化）时 计分 并 将 changed 置为 true
        board.setViewingPerspective(side);

        if (true) {
            for (int x = 0; x < board.size(); x += 1) {
                for (int y = board.size()-1; y >= 0; y -= 1) {
                    Tile edge = board.tile(x, board.size()-1);

                    if (edge == null && y > 0) {  // 将边界补齐
                        for (int k = y-1; k >= 0; k -= 1) {
                            if (board.tile(x, k) != null) {
                                Tile tt = board.tile(x, k);
                                board.move(x, y, tt);
                                changed = true;
                                break;
                            }
                        }
                    }

                    // 找下一个非空值
                    for (int p = y-1; p >= 0; p -= 1) {
                        if (board.tile(x, p) != null) { // x for test
                            Tile next = board.tile(x, p);
                            Tile target = board.tile(x, y);
                            if (target != null && next.value() == target.value()) {
                                board.move(x, y, next);
                                score += target.value() * 2;
                                changed = true;
                                break;
                            } else if (target == null){
                                board.move(x, y, next);
                                changed = true;
                            } else {
                                board.move(x, y-1, next);
                                changed = true;
                                break;
                            }

                        }
                    }

                }
            }
        }
        board.setViewingPerspective(Side.NORTH);

        /////////////////////////////////////////////////
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

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
        // i do
        for (int i = 0; i < b.size(); i += 1) {
            for (int j = 0; j < b.size(); j += 1) {
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
        // i do
        for (int i = 0; i < b.size(); i += 1) {
            for (int j = 0; j < b.size(); j += 1) {
                // 注意空格为null无法与数值比较
                if (b.tile(i, j) != null && b.tile(i, j).value() == MAX_PIECE) {
                    return true;
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
        // i do
        if (Model.emptySpaceExists(b)) {
            return true;
        } else {
            for (int i = 0; i < b.size(); i += 1) {
                for (int j = 0; j < b.size(); j += 1) {
                    for (int ni = -1; ni < 2; ni += 1) {
                        for (int nj = -1; nj < 2; nj += 1) {
                            int x = i + ni, y = j + nj;
                            if (x < 0 || x >= b.size() || y < 0 || y >= b.size()) {
                                continue;
                            } else if ((ni == -1 && nj == -1) || (ni == -1 && nj == 1) || (ni == 1 && nj == -1) ||
                                        (ni == 1 && nj == 1) || ni == 0 && nj == 0) {
                                continue;
                            } else {
                                if (b.tile(x, y).value() == b.tile(i, j).value()) {
                                    return true;
                                }
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
}
