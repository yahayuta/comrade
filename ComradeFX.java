import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.Random;

public class ComradeFX extends Application {
    // Game variables (adapted from original applet)
    private int myX = (GAME_WIDTH - 20) / 2;
    private int myY = GAME_HEIGHT - 40;
    private int[] enemX = new int[3], enemY = new int[3];
    private int[] ammoX1 = new int[5], ammoY1 = new int[5], ammoX2 = new int[5], ammoY2 = new int[5];
    private boolean[] ammoCond = new boolean[5];
    private boolean[] enemCond = new boolean[3];
    private Image me, mig29, su27, f15, f16, euf, f18, f117, b2, b52, sea, snd, stp, wd;
    private Random rnd = new Random();
    private boolean MOVUP = false, MOVDOWN = false, MOVRIGHT = false, MOVLEFT = false;
    private int enemCount = 0;
    private int[] enmovX = new int[3], enmovY = new int[3];
    private int[] enemovtype = new int[3];
    private int enemtype;
    private int[] enemImgType = new int[3];
    // Enemy bullet variables
    private int[] enamX = new int[6], enamY = new int[6], enamXmov = new int[6], enamYmov = new int[6];
    private boolean[] enamCond = new boolean[6];
    // Explosion variables
    private int[] enexX = new int[3], enexY = new int[3], enexTimer = new int[3];
    private boolean[] enexCond = new boolean[3];
    private int[] myexX = new int[1], myexY = new int[1], myexTimer = new int[1];
    private boolean myexCond = false;
    private Image explosionImg;
    // Score variable
    private int score = 0;

    // Game display size
    private static final int GAME_WIDTH = 360;
    private static final int GAME_HEIGHT = 390;

    // Map scrolling offset
    private int mapScrollY = MapData.getMapLength() * TILE_SIZE - GAME_HEIGHT;
    private static final int TILE_SIZE = 16; // Each map tile is 16x16 pixels
    private static final int MAP_SCROLL_SPEED = 1; // pixels per frame
    private static final int ENEMY_SPEED = 2; // vertical or horizontal enemy speed
    private static final int ENEMY_BULLET_SPEED = 2; // enemy bullet vertical speed (slower)

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        loadImages();
        me = mig29; // Default aircraft

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        // Key event handlers
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                drawGame(gc);
            }
        };
        timer.start();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Comrade FX");
        primaryStage.show();
        canvas.requestFocus();
    }

    private void loadImages() {
        // Adjusted file paths to use the resources directory
        mig29 = new Image("file:resources/mig29.gif");
        su27 = new Image("file:resources/su27.gif");
        f15 = new Image("file:resources/f15.gif");
        f16 = new Image("file:resources/f16.gif");
        euf = new Image("file:resources/euf.gif");
        f18 = new Image("file:resources/f18.gif");
        f117 = new Image("file:resources/f117.gif");
        b2 = new Image("file:resources/b2.gif");
        b52 = new Image("file:resources/b52.gif");
        sea = new Image("file:resources/sea.gif");
        snd = new Image("file:resources/snd.gif");
        stp = new Image("file:resources/stp.gif");
        wd = new Image("file:resources/wd.gif");
        explosionImg = new Image("file:resources/explosion.gif");
    }

    private void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) MOVRIGHT = true;
        if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) MOVLEFT = true;
        if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) MOVUP = true;
        if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) MOVDOWN = true;
        if (e.getCode() == KeyCode.SPACE) fireAmmo();
    }

    private void handleKeyReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) MOVRIGHT = false;
        if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) MOVLEFT = false;
        if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) MOVUP = false;
        if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) MOVDOWN = false;
    }

    private void updateGame() {
        updateMapScroll();
        // Simple movement logic (scaled for new size)
        int moveStep = 4;
        if (MOVUP) myY -= moveStep;
        if (MOVDOWN) myY += moveStep;
        if (MOVRIGHT) myX += moveStep;
        if (MOVLEFT) myX -= moveStep;
        if (myX < 0) myX = 0;
        if (myX > GAME_WIDTH - 20) myX = GAME_WIDTH - 20;
        if (myY < 0) myY = 0;
        if (myY > GAME_HEIGHT - 20) myY = GAME_HEIGHT - 20;
        // Update ammo positions
        for (int i = 0; i < 5; i++) {
            if (ammoCond[i]) {
                ammoY1[i] -= 2; // slower bullet speed
                ammoY2[i] -= 2;
                if (ammoY1[i] < 0 || ammoY2[i] < 0) ammoCond[i] = false;
            }
        }
        // Enemy logic
        makeEnemy();
        moveEnemy();
        enemyFire();
        moveEnemyBullets();
        checkCollisions();
        updateExplosions();
    }

    private void enemyFire() {
        for (int i = 0; i < 3; i++) {
            if (enemCond[i] && enemY[i] > 10 && !enamCond[i]) {
                enamX[i] = enemX[i] + 10;
                enamY[i] = enemY[i] + 25;
                enamXmov[i] = (myX + 10 - enamX[i]) / 35;
                enamYmov[i] = ENEMY_BULLET_SPEED;
                enamCond[i] = true;
            }
        }
    }

    private void moveEnemyBullets() {
        for (int i = 0; i < 6; i++) {
            if (enamCond[i]) {
                enamX[i] += enamXmov[i];
                enamY[i] += enamYmov[i]; // Use full speed, not divided
                if (enamX[i] > GAME_WIDTH - 2 || enamY[i] > GAME_HEIGHT - 5 || enamX[i] < 0 || enamY[i] < 0) {
                    enamCond[i] = false;
                }
            }
        }
    }

    private void checkCollisions() {
        // Player hit by enemy bullet
        if (!myexCond) {
            for (int i = 0; i < 6; i++) {
                if (enamCond[i]) {
                    if (enamX[i] > myX && enamX[i] < myX + 19 && enamY[i] > myY && enamY[i] < myY + 19) {
                        enamCond[i] = false;
                        myexX[0] = myX;
                        myexY[0] = myY;
                        myexTimer[0] = 0;
                        myexCond = true;
                    }
                }
            }
        }
        // Player bullet hits enemy
        for (int i = 0; i < 5; i++) {
            if (ammoCond[i]) {
                for (int j = 0; j < 3; j++) {
                    if (enemCond[j] && !enexCond[j]) {
                        if ((ammoX1[i] > enemX[j] && ammoX1[i] < enemX[j] + 20 && ammoY1[i] > enemY[j] && ammoY1[i] < enemY[j] + 25) ||
                            (ammoX2[i] > enemX[j] && ammoX2[i] < enemX[j] + 20 && ammoY2[i] > enemY[j] && ammoY2[i] < enemY[j] + 25)) {
                            enexX[j] = enemX[j];
                            enexY[j] = enemY[j];
                            enexTimer[j] = 0;
                            enexCond[j] = true;
                            enemCond[j] = false;
                            ammoCond[i] = false;
                            score += 100; // Add score for defeating enemy
                        }
                    }
                }
            }
        }
    }

    private void updateExplosions() {
        // Enemy explosions
        for (int i = 0; i < 3; i++) {
            if (enexCond[i]) {
                enexTimer[i]++;
                if (enexTimer[i] > 15) {
                    enexCond[i] = false;
                }
            }
        }
        // Player explosion
        if (myexCond) {
            myexTimer[0]++;
            if (myexTimer[0] > 20) {
                myexCond = false;
                myX = (GAME_WIDTH - 20) / 2;
                myY = GAME_HEIGHT - 40;
            }
        }
    }

    private void makeEnemy() {
        enemCount++;
        if ((!enemCond[0]) && (!enemCond[1]) && (!enemCond[2]) && (enemCount > 60)) {
            enemCount = 0;
            for (int i = 0; i < 3; i++) {
                if (!enemCond[i]) {
                    enemCond[i] = true;
                    enemImgType[i] = Math.abs(rnd.nextInt()) % 5;
                    enemtype = enemImgType[i];
                    enemovtype[i] = Math.abs(rnd.nextInt()) % 3; // 0: top, 1: left, 2: right
                    if (enemovtype[i] == 0) { // Spawn from top
                        enemY[i] = 0;
                        enemX[i] = rnd.nextInt(GAME_WIDTH - 20);
                        enmovX[i] = 0;
                        enmovY[i] = ENEMY_SPEED;
                    } else if (enemovtype[i] == 1) { // Spawn from left
                        enemX[i] = 0;
                        enemY[i] = rnd.nextInt(GAME_HEIGHT / 2);
                        enmovX[i] = ENEMY_SPEED;
                        enmovY[i] = 0;
                    } else { // Spawn from right
                        enemX[i] = GAME_WIDTH - 20;
                        enemY[i] = rnd.nextInt(GAME_HEIGHT / 2);
                        enmovX[i] = -ENEMY_SPEED;
                        enmovY[i] = 0;
                    }
                }
            }
        } else if (enemCount > 60) {
            enemCount = 0;
        }
    }

    private void moveEnemy() {
        for (int i = 0; i < 3; i++) {
            if (enemCond[i]) {
                enemX[i] += enmovX[i];
                enemY[i] += enmovY[i]; // Use full speed, not divided
                if ((enemY[i] > GAME_HEIGHT - 15) || (enemX[i] > GAME_WIDTH - 20) || (enemX[i] < 0) || (enemY[i] < 0)) {
                    enemCond[i] = false;
                }
            }
        }
    }

    private void drawMap(GraphicsContext gc) {
        // Calculate how many rows and columns fit on the screen
        int rowsOnScreen = (GAME_HEIGHT / TILE_SIZE) + 2;
        int colsOnScreen = (GAME_WIDTH / TILE_SIZE) + 2;
        int mapRows = MapData.getMapLength();
        int yOffset = mapScrollY % TILE_SIZE;
        int mapRowOffset = mapScrollY / TILE_SIZE;
        for (int row = 0; row < rowsOnScreen; row++) {
            int mapRow = mapRowOffset + row;
            if (mapRow >= mapRows) continue;
            for (int col = 0; col < colsOnScreen; col++) {
                int tile = MapData.MapDataReturn(mapRow, col);
                Image tileImg = null;
                switch (tile) {
                    case 0: tileImg = sea; break;
                    case 1: tileImg = snd; break;
                    case 2: tileImg = stp; break;
                    case 3: tileImg = wd; break;
                }
                if (tileImg != null) {
                    gc.drawImage(tileImg, col * TILE_SIZE, row * TILE_SIZE - yOffset);
                }
            }
        }
    }

    private void updateMapScroll() {
        // Scroll the map by MAP_SCROLL_SPEED pixels per frame
        mapScrollY -= MAP_SCROLL_SPEED;
        if (mapScrollY < 0) {
            mapScrollY = 0;
        }
    }

    private void drawGame(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        // Draw the scrolling map background
        drawMap(gc);
        // Draw score
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillText("Score: " + score, 10, 20);
        // Draw player
        if (!myexCond) {
            gc.drawImage(me, myX, myY);
        } else {
            gc.drawImage(explosionImg, myexX[0], myexY[0]);
        }
        // Draw ammo
        gc.setFill(javafx.scene.paint.Color.YELLOW);
        for (int i = 0; i < 5; i++) {
            if (ammoCond[i]) {
                gc.fillRect(ammoX1[i], ammoY1[i], 4, 20);
                gc.fillRect(ammoX2[i], ammoY2[i], 4, 20);
            }
        }
        // Draw enemy bullets
        gc.setFill(javafx.scene.paint.Color.PINK);
        for (int i = 0; i < 6; i++) {
            if (enamCond[i]) {
                gc.fillRect(enamX[i], enamY[i], 4, 10);
            }
        }
        // Draw enemies with images or explosions
        for (int i = 0; i < 3; i++) {
            if (enexCond[i]) {
                gc.drawImage(explosionImg, enexX[i], enexY[i]);
            } else if (enemCond[i]) {
                Image enemyImg = null;
                switch (enemImgType[i]) {
                    case 0: enemyImg = f15; break;
                    case 1: enemyImg = f16; break;
                    case 2: enemyImg = euf; break;
                    case 3: enemyImg = f18; break;
                    case 4: enemyImg = f117; break;
                }
                if (enemyImg != null) {
                    gc.drawImage(enemyImg, enemX[i], enemY[i]);
                } else {
                    gc.setFill(javafx.scene.paint.Color.RED);
                    gc.fillRect(enemX[i], enemY[i], 40, 50);
                }
            }
        }
        // TODO: Draw map, more effects, etc.
    }

    private void fireAmmo() {
        for (int i = 0; i < 5; i++) {
            if (!ammoCond[i]) {
                ammoCond[i] = true;
                ammoX1[i] = myX + 2;
                ammoX2[i] = myX + 16;
                ammoY1[i] = myY;
                ammoY2[i] = myY;
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
