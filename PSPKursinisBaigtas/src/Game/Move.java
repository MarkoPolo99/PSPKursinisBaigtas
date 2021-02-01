package Game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
//Visa ejimo logika yra aprasyta sioje klaseje
public class Move {
    public final Reversi reversi;

    public Move(Reversi reversi) {
        this.reversi = reversi;
    }

    //ar ejimas yra imanomas
    boolean isThereValidMove(int color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isMoveValidInCoordinates(color, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    //ar ivestos koordinates yra galimos, jei ne vieta turbut jau uzimta
    public boolean isMoveValidInCoordinates(int color, int x, int y) {
        int opponent = 3 - color;
        if (coordinatesOutOfBounds(x, y)) return false;
        return checkCoordinatesInAllDirections(color, x, y, opponent);
    }
    //koordinaciu tikrinimas per visa lenta
    public boolean checkCoordinatesInAllDirections(int color, int x, int y, int opponent) {
        if (reversi.board[x][y] == 0) {
            for (int directionX = -1; directionX < 2; directionX++) {
                for (int directionY = -1; directionY < 2; directionY++) {
                    if (checkCoordinatesAfterFindingOpponent(x, y, directionX, directionY)) continue;
                    if (opponentPiecesFound(color, x, y, opponent, directionX, directionY)) return true;
                }
            }
        }
        return false;
    }
    //jei randame priesininko saskes ju koordinates pridedame
    public boolean opponentPiecesFound(int color, int x, int y, int opponent, int directionX, int directionY) {
        if (reversi.board[x + directionX][y + directionY] == opponent) {
            int tempX = x;
            int tempY = y;
            while (true) {
                tempX += directionX;
                tempY += directionY;
                if (pieceOutOfBounds(tempX, tempY)) break;
                if (reversi.board[tempX][tempY] == color) {
                    return true;
                }
            }
        }
        return false;
    }
    //ar saskes yra uz ribu
    public boolean pieceOutOfBounds(int tempX, int tempY) {
        return reversi.board[tempX][tempY] == 0 || tempX > 7 || tempY > 7;
    }
    //ar ivestos koordinates yra ribose apeinant priesininko saskes
    public boolean checkCoordinatesAfterFindingOpponent(int x, int y, int directionX, int directionY) {
        return x + directionX < 0 || x + directionX > 7 || y + directionY < 0 || y + directionY > 7;
    }
    //coordinates yra uz ribu
    public boolean coordinatesOutOfBounds(int x, int y) {
        return x < 0 || x > 7 || y < 0 || y > 7;
    }

    //Paprasome zaidejo koordinaciu
    void getMoveFromPlayer(int color) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        if (isThereValidMove(color)) {
            whichPlayerMoves(color);
            int x, y;
            System.out.print("Iveskite x koordinate: ");
            x = Integer.parseInt(bufferedReader.readLine());
            System.out.print("Iveskite y koordinate: ");
            y = Integer.parseInt(bufferedReader.readLine());
            checkPlayerGivenMove(color, x, y);
        } else {
            System.out.println(color == 1 ? "baltas zaidejas neturi galimu ejimu, praleidziama." : "juodas zaidejas neturi galimu ejimu, praleidziama.");
        }
    }
    //Patikriname zaidejo duotas koordinates
    public void checkPlayerGivenMove(int color, int x, int y) throws Exception {
        if (!isMoveValidInCoordinates(color, x, y)) {
            throw new Exception();
        } else {
            addMoveOnBoard(x, y, color);
        }
    }
    //Patikriname kurio zaidejo ejimas
    public void whichPlayerMoves(int color) {
        if (color == 1) System.out.println("Dabar yra  balto zaidejo ejimas");
        else System.out.println("Dabar yra juodo zaidejo ejimas");
    }

    //Pridedame ejima i lenta main
    void addMoveOnBoard(int x, int y, int color) {
        List<Point> endPoints = new ArrayList<>();
        reversi.board[x][y] = color;
        int opponent = 3 - color;
        checkAllDirections(x, y, color, endPoints, opponent);
        addPiecesOnBoard(x, y, color, endPoints);
    }
    //Patikriname ejima i visas puses
    public void checkAllDirections(int x, int y, int color, List<Point> endPoints, int opponent) {
        for (int directionX = -1; directionX < 2; directionX++) {
            for (int directionY = -1; directionY < 2; directionY++) {
                if (checkCoordinatesAfterFindingOpponent(x, y, directionX, directionY)) continue;
                opponentPieceWasFound(x, y, color, endPoints, opponent, directionX, directionY);
            }
        }
    }
    //
    public void addPiecesOnBoard(int x, int y, int color, List<Point> endPoints) {
        for (Point point : endPoints) {
            int directionX = point.x - x;
            int directionY = point.y - y;
            directionX = getDirection(x, directionX, point.x);
            directionY = getDirection(y, directionY, point.y);
            addToDirection(color, point, directionX, directionY, x, y);
        }
    }
    //Jei ejimas perlipa priesininka jo saske tampa zaidejo
    public void addToDirection(int color, Point point, int directionX, int directionY, int tempX, int tempY) {
        while (tempX != point.x || tempY != point.y) {
            tempX += directionX;
            tempY += directionY;
            reversi.board[tempX][tempY] = color;
        }
    }
    //Gauname i kuria puse reikia apversti saskes
    public int getDirection(int x, int direction, int y) {
        if (direction != 0) {
            if (y - x > 0) direction = 1;
            else direction = -1;
        }
        return direction;
    }

    // rastas priesininkas
    public void opponentPieceWasFound(int x, int y, int color, List<Point> endPoints, int opponent, int directionX, int directionY) {
        if (reversi.board[x + directionX][y + directionY] == opponent) {
            addToBoard(color, endPoints, directionX, directionY, x, y);
        }
    }
    //Pridedame i lenta ir apverciame
    public void addToBoard(int color, List<Point> endPoints, int directionX, int directionY, int tempX, int tempY) {
        while (true) {
            tempX += directionX;
            tempY += directionY;
            if (pieceOutOfBounds(tempX, tempY)) break;
            if (reversi.board[tempX][tempY] == color) {
                endPoints.add(new Point(tempX, tempY));
            }
        }
    }
}