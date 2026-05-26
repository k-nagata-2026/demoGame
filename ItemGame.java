import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// JPanel（画面の部品）をベースに、タイマー機能とキーボード機能を実装
public class ItemGame extends JPanel implements ActionListener, KeyListener {

    // 画面のサイズ
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    // プレイヤーのデータ（X座標、Y座標、サイズ）
    private int playerX = 180;
    private final int playerY = 320;
    private final int playerSize = 30;

    // アイテムのデータ（X座標、Y座標、サイズ）
    private int itemX = 200;
    private int itemY = 0;
    private final int itemSize = 20;

    // ゲームの状態を管理する変数
    private boolean isTimeUp = false; // 時間切れフラグ
    private int timeCount = 0; // 時間計測用のカウンター
    private int score = 0; // スコアを記録する変数

    // コンストラクタ（ゲームの初期設定）
    public ItemGame() {
        // 20ミリ秒ごとに画面を更新するタイマーをスタート
        Timer timer = new Timer(20, this);
        timer.start();
    }

    // 🎨 画面を描画するメソッド（自動で呼び出される）
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景を黒で塗りつぶす
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (!isTimeUp) {
            // プレイヤーを描画（青い四角）
            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, playerSize, playerSize);

            // アイテムを描画（緑の四角）
            g.setColor(Color.GREEN);
            g.fillRect(itemX, itemY, itemSize, itemSize);

            // 残り時間の表示
            int remainingTime = 30 - (timeCount / 50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("TIME: " + remainingTime, 15, 30);

            // 現在のスコアの表示
            g.drawString("SCORE: " + score, 280, 30);

        } else {
            // 30秒経った時のリザルト画面表示
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("TIME UP!", 135, 160);

            g.setColor(Color.WHITE);
            g.drawString("FINAL SCORE: " + score, 85, 230);
        }
    }

    // 🕒 タイマーによって20ミリ秒ごとに実行される処理
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isTimeUp)
            return; // 時間切れなら処理をしない

        // アイテムを下に落とす
        itemY += 6;

        // アイテムが一番下（画面外）まで落ちたら、上に戻してX座標をランダムにする
        if (itemY > HEIGHT) {
            itemY = 0;
            itemX = (int) (Math.random() * (WIDTH - itemSize));
        }

        // 当たり判定（プレイヤーがアイテムをキャッチしたかどうかの判定）
        if (itemX < playerX + playerSize &&
                itemX + itemSize > playerX &&
                itemY < playerY + playerSize &&
                itemY + itemSize > playerY) {

            score++; // スコアを1増やす

            // キャッチしたアイテムを上に戻して再配置する
            itemY = 0;
            itemX = (int) (Math.random() * (WIDTH - itemSize));
        }

        // 時間のカウントアップ（20ms × 1000回 ＝ 20秒）
        timeCount++;
        if (timeCount >= 1000) {
            isTimeUp = true;
        }

        // 画面を最新の状態に描き直す
        repaint();
    }

    // ⌨️ キーボードが押されたときの処理
    @Override
    public void keyPressed(KeyEvent e) {
        if (isTimeUp)
            return; // 終了後は動かせないようにする

        int key = e.getKeyCode();

        // 左矢印キーが押されたら左に移動（画面外に出ないように制限）
        if (key == KeyEvent.VK_LEFT) {
            playerX -= 15;
            if (playerX < 0)
                playerX = 0;
        }
        // 右矢印キーが押されたら右に移動
        if (key == KeyEvent.VK_RIGHT) {
            playerX += 15;
            if (playerX > WIDTH - playerSize)
                playerX = WIDTH - playerSize;
        }
    }

    // インターフェースの実装上、記述が必要なメソッド
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // 🚀 メインメソッド
    public static void main(String[] args) {
        // ゲームを表示する「窓（フレーム）」を作る
        JFrame frame = new JFrame("アイテムキャッチゲーム");

        // 自分のクラスのインスタンス（実体）を生み出す
        ItemGame game = new ItemGame();

        frame.add(game); // 窓にゲーム画面を貼り付ける
        frame.setSize(WIDTH, HEIGHT + 30); // ウィンドウの枠線分、縦を少し大きく設定
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ✕ボタンでプログラムを終了する設定
        frame.setLocationRelativeTo(null); // 画面の真ん中に表示
        frame.setVisible(true); // 窓を表示する

        // 窓にキーボード入力を感知するセンサー（KeyListener）を取り付ける
        frame.addKeyListener(game);
    }
}