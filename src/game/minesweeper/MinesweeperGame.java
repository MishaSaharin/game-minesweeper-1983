package game.minesweeper;

import com.javarush.engine.cell.*;

import java.util.*;

public class MinesweeperGame extends Game {
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private static final String MINE = "\ud83d\udca5";
    private static final String FLAG = "\ud83d\udea7";
    private static final int SIDE = 9;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int countMinesOnField;
    private int countFlags;
    private int score;

    private void createGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                int x = 4;
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    gameField[j][i] = new GameObject(i, j, true);
                    setCellColor(j, i, Color.PURPLE);
                    setCellValue(j, i, "");
                    countMinesOnField++;
                } else {
                    gameField[j][i] = new GameObject(i, j, false);
                    setCellColor(j, i, Color.PURPLE);
                    setCellValue(j, i, "");
                }
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x].isMine) {
                    continue;
                }
                List<GameObject> lgo = getNeighbors(gameField[y][x]);
                for (int i = 0; i < lgo.size(); i++) {
                    GameObject object1 = lgo.get(i);
                    if (object1.isMine) {
                        gameField[y][x].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped) {
        } else {
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            if (!gameField[y][x].isMine) {

            }
            score += 5;
            setScore(score);
            setCellColor(x, y, Color.GREEN);
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            } else if (gameField[y][x].countMineNeighbors == 0) {
                setCellValue(gameField[y][x].x, gameField[y][x].y, "");
                List<GameObject> neighbors = getNeighbors(gameField[y][x]);
                for (GameObject neighbor : neighbors) {
                    if (!neighbor.isOpen) {
                        openTile(neighbor.x, neighbor.y);
                    }
                }
            } else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
        }
        if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine) {
            win();
        }
    }

    private void markTile(int x, int y) {
        if (isGameStopped) return;
        if (gameField[y][x].isOpen || (countFlags == 0 && !gameField[y][x].isFlag)) return;
        else if (gameField[y][x].isFlag) {
            countFlags++;
            gameField[y][x].isFlag = false;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.PURPLE);
        } else {
            countFlags--;
            gameField[y][x].isFlag = true;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.WHITE);
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.YELLOW, "ти програв", Color.BLUE, 70);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.YELLOW, "ти виграв", Color.BLUE, 70);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }

    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }
}
