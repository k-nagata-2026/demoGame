import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

// ==========================================
// 1. メインのゲームクラス（ウィンドウの設定）
// ==========================================
public class RoguelikeGame extends JFrame {
    public static final int TILE_SIZE = 40;
    public static final int COLS = 10;
    public static final int ROWS = 10;

    private int[][] map = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 1, 1, 0, 1 },
            { 1, 0, 1, 0, 0, 0, 0, 1, 0, 1 },
            { 1, 0, 1, 1, 1, 1, 0, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 1, 0, 1, 0, 1 },
            { 1, 1, 1, 1, 0, 1, 0, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 1, 1, 1, 1, 2, 1 }, // 2: ゴール
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
    };

    private int playerX = 1;
    private int playerY = 1;

    // ★クラスの活用：敵（Enemy）をリストでまとめて管理する
    private ArrayList<Enemy> enemies = new ArrayList<>();

    public RoguelikeGame() {
        setTitle("Java Swing ロードクライク - クラス設計版");
        setSize(COLS * TILE_SIZE, ROWS * TILE_SIZE + 30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ★敵クラスのインスタンス（本物）を複数作成してリストに登録
        // 違う座標、違う色（赤・オレンジ）の敵を簡単に作れます！
        enemies.add(new Enemy(8, 1, Color.RED));
        enemies.add(new Enemy(4, 7, Color.ORANGE));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
            }
        });

        add(new GamePanel());
    }

    private void movePlayer(int keyCode) {
        int nextX = playerX;
        int nextY = playerY;

        if (keyCode == KeyEvent.VK_UP)
            nextY--;
        if (keyCode == KeyEvent.VK_DOWN)
            nextY++;
        if (keyCode == KeyEvent.VK_LEFT)
            nextX--;
        if (keyCode == KeyEvent.VK_RIGHT)
            nextX++;

        if (map[nextY][nextX] != 1) {
            playerX = nextX;
            playerY = nextY;

            // ★すべての敵を順番に動かす（拡張性アップ！）
            for (Enemy enemy : enemies) {
                enemy.moveRandom(map);
            }
        }

        // ゲームオーバー判定（すべての敵との衝突をチェック）
        checkGameOver();

        if (map[playerY][playerX] == 2) {
            JOptionPane.showMessageDialog(this, "🎉 ゴール！おめでとう！");
            System.exit(0);
        }

        repaint();
    }

    private void checkGameOver() {
        for (Enemy enemy : enemies) {
            // 敵クラスから getX(), getY() で座標を教えてもらう
            if (playerX == enemy.getX() && playerY == enemy.getY()) {
                JOptionPane.showMessageDialog(this, "👾 敵につかまりました！ゲームオーバー");
                System.exit(0);
            }
        }
    }

    // 描画パネルクラス
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // マップの描画
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLS; x++) {
                    if (map[y][x] == 1)
                        g.setColor(Color.DARK_GRAY);
                    else if (map[y][x] == 2)
                        g.setColor(Color.GREEN);
                    else
                        g.setColor(Color.LIGHT_GRAY);

                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }

            // ★すべての敵を描画する
            for (Enemy enemy : enemies) {
                g.setColor(enemy.getColor()); // 敵自身の持つ色を使う
                g.fillOval(enemy.getX() * TILE_SIZE + 5, enemy.getY() * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
            }

            // プレイヤーの描画
            g.setColor(Color.BLUE);
            g.fillOval(playerX * TILE_SIZE + 5, playerY * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
        }
    }

    public static final void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RoguelikeGame().setVisible(true);
        });
    }
}

// ==========================================
// 2. 独立した「敵（Enemy）クラス」
// ==========================================
class Enemy {
    // 外部から勝手に書き換えられないように private にする（カプセル化）
    private int x;
    private int y;
    private Color color;
    private Random random = new Random();

    // コンストラクタ（Java Bronzeの超重要テーマ！）
    public Enemy(int x, int y, Color color) {
        this.x = x; // 「this.」を使ってクラスの変数に代入
        this.y = y;
        this.color = color;
    }

    // 敵自身が自分で動くメソッド
    public void moveRandom(int[][] map) {
        int direction = random.nextInt(4);
        int nextX = this.x;
        int nextY = this.y;

        if (direction == 0)
            nextY--;
        if (direction == 1)
            nextY++;
        if (direction == 2)
            nextX--;
        if (direction == 3)
            nextX++;

        // 壁とゴールを避けて移動
        if (map[nextY][nextX] != 1 && map[nextY][nextX] != 2) {
            this.x = nextX;
            this.y = nextY;
        }
    }

    // 外部に安全にデータを渡すための getter メソッド
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Color getColor() {
        return this.color;
    }
}