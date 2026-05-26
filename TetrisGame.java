import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TetrisGame extends JPanel implements ActionListener, KeyListener {

    // マス目の設定（10マス × 20マス）
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int CELL_SIZE = 25; // 1マスのサイズ（ピクセル）

    // 画面全体のサイズ
    private static final int WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final int HEIGHT = BOARD_HEIGHT * CELL_SIZE;

    // ゲーム盤面を管理する2次元配列（0:空、1:ブロックあり）
    private int[][] grid = new int[BOARD_HEIGHT][BOARD_WIDTH];

    // ブロック（テトリミノ）の形状パターン（4種類に厳選）
    private final int[][][] SHAPES = {
            { { 1, 1, 1, 1 } }, // I字（棒）
            { { 1, 1 }, { 1, 1 } }, // 四角
            { { 0, 1, 0 }, { 1, 1, 1 } }, // T字
            { { 1, 0, 0 }, { 1, 1, 1 } } // L字
    };

    // 現在落下中のブロックのデータ
    private int[][] currentPiece;
    private int currentX;
    private int currentY;

    // ゲームの状態管理
    private boolean isGameOver = false;
    private int score = 0;

    // コンストラクタ
    public TetrisGame() {
        // 400ミリ秒ごとに自動で下に落ちるタイマー
        Timer timer = new Timer(400, this);
        timer.start();

        // 最初のブロックを生み出す
        spawnPiece();
    }

    // 🧱 新しいブロックを画面最上部に登場させるメソッド
    private void spawnPiece() {
        int index = (int) (Math.random() * SHAPES.length);
        currentPiece = SHAPES[index];

        // 画面の横中央に配置
        currentX = BOARD_WIDTH / 2 - currentPiece[0].length / 2;
        currentY = 0;

        // 登場した瞬間から衝突している場合はゲームオーバー
        if (checkCollision(currentX, currentY, currentPiece)) {
            isGameOver = true;
        }
    }

    // 💥 衝突判定メソッド（移動・回転先が壁や他のブロックと重なるならtrueを返す）
    private boolean checkCollision(int nextX, int nextY, int[][] piece) {
        for (int r = 0; r < piece.length; r++) {
            for (int c = 0; c < piece[r].length; c++) {
                if (piece[r][c] != 0) {
                    int boardX = nextX + c;
                    int boardY = nextY + r;

                    // 壁・床の突き抜けチェック
                    if (boardX < 0 || boardX >= BOARD_WIDTH || boardY >= BOARD_HEIGHT) {
                        return true;
                    }
                    // すでに固定されているブロックとの衝突チェック
                    if (boardY >= 0 && grid[boardY][boardX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 📌 落ちきったブロックを盤面（配列）に固定するメソッド
    private void lockPiece() {
        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {
                if (currentPiece[r][c] != 0) {
                    if (currentY + r >= 0) {
                        grid[currentY + r][currentX + c] = 1;
                    }
                }
            }
        }
    }

    // ✨ 横一列が揃ったら消去して、上のブロックを下に詰めるメソッド
    private void clearLines() {
        for (int r = BOARD_HEIGHT - 1; r >= 0; r--) {
            boolean isFull = true;
            for (int c = 0; c < BOARD_WIDTH; c++) {
                if (grid[r][c] == 0) {
                    isFull = false;
                    break;
                }
            }
            // 横一列がすべて埋まっていた場合
            if (isFull) {
                // その行より上にあるすべての行を1つ下にずらす
                for (int y = r; y > 0; y--) {
                    for (int x = 0; x < BOARD_WIDTH; x++) {
                        grid[y][x] = grid[y - 1][x];
                    }
                }
                // 一番上の行を空（0）にする
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    grid[0][x] = 0;
                }
                score += 100; // スコア獲得
                r++; // 行が下にずれたので、同じ行をもう一度判定するためにインデックスを調整
            }
        }
    }

    // 🔄 ブロックを90度回転させるメソッド（2次元配列の行列入れ替え）
    private void rotatePiece() {
        int r = currentPiece.length;
        int c = currentPiece[0].length;
        int[][] rotated = new int[c][r];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                rotated[j][r - 1 - i] = currentPiece[i][j];
            }
        }

        // 回転した結果、壁や他のブロックにぶつからない場合のみ採用する
        if (!checkCollision(currentX, currentY, rotated)) {
            currentPiece = rotated;
        }
    }

    // 🎨 画面描画
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景（黒）
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 1. すでに固定された盤面のブロックを描画（グレー）
        for (int r = 0; r < BOARD_HEIGHT; r++) {
            for (int c = 0; c < BOARD_WIDTH; c++) {
                if (grid[r][c] != 0) {
                    g.setColor(Color.GRAY);
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                }
            }
        }

        // 2. 現在落下中のブロックを描画（オレンジ）
        if (!isGameOver) {
            g.setColor(Color.ORANGE);
            for (int r = 0; r < currentPiece.length; r++) {
                for (int c = 0; c < currentPiece[r].length; c++) {
                    if (currentPiece[r][c] != 0) {
                        g.fillRect((currentX + c) * CELL_SIZE, (currentY + r) * CELL_SIZE, CELL_SIZE - 1,
                                CELL_SIZE - 1);
                    }
                }
            }
        }

        // 3. スコア表示
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("SCORE: " + score, 10, 25);

        // 4. ゲームオーバー表示
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 40, HEIGHT / 2);
        }
    }

    // 🕒 タイマーによって定期的に実行される自動落下処理
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver)
            return;

        // 下に1マス進める。進めない（衝突する）場合は床か他のブロックに激突したとみなす
        if (!checkCollision(currentX, currentY + 1, currentPiece)) {
            currentY++;
        } else {
            lockPiece(); // 盤面に固定
            clearLines(); // 揃った行を消去
            spawnPiece(); // 次のブロックを生成
        }
        repaint();
    }

    // ⌨️ キーボード操作
    @Override
    public void keyPressed(KeyEvent e) {
        if (isGameOver)
            return;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            if (!checkCollision(currentX - 1, currentY, currentPiece))
                currentX--;
        }
        if (key == KeyEvent.VK_RIGHT) {
            if (!checkCollision(currentX + 1, currentY, currentPiece))
                currentX++;
        }
        if (key == KeyEvent.VK_DOWN) {
            // 下キーで高速落下
            if (!checkCollision(currentX, currentY + 1, currentPiece))
                currentY++;
        }
        if (key == KeyEvent.VK_UP) {
            // 上キーで回転
            rotatePiece();
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // 🚀 メインメソッド
    public static void main(String[] args) {
        JFrame frame = new JFrame("簡易テトリス");
        TetrisGame game = new TetrisGame();

        frame.add(game);
        frame.setSize(WIDTH + 16, HEIGHT + 39); // ウィンドウの枠線分を考慮したサイズ
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addKeyListener(game);
    }
}