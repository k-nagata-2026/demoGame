import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// メインのゲーム画面を管理するクラス
public class CommandBattleGame extends JFrame {

    private Player player;
    private Enemy enemy;
    private int defeatedCount = 0; // 倒した敵の数

    // GUIコンポーネント（画面の部品）
    private JTextArea logArea;
    private JLabel playerStatusLabel;
    private JLabel enemyStatusLabel;
    private JLabel playerIconLabel; // 🌟 【追加】プレイヤーのアイコン用ラベル
    private JLabel enemyIconLabel; // 🌟 【追加】敵のアイコン用ラベル
    private JButton attackButton;
    private JProgressBar playerHPBar;
    private JProgressBar enemyHPBar;

    // 🚀 メインメソッド
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CommandBattleGame().setVisible(true);
        });
    }

    // コンストラクタ（画面の初期設定）
    public CommandBattleGame() {
        // プレイヤーの作成
        player = new Player("あなた", 150, 15);

        // ウィンドウの基本設定
        setTitle("ターン制コマンドバトルゲーム");
        setSize(550, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. キャラクター表示エリア（上部を左右分割） ---
        JPanel characterPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        characterPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 【左側：プレイヤーパネルの構築】
        JPanel playerPanel = new JPanel(new BorderLayout(0, 5));
        playerStatusLabel = new JLabel("", JLabel.CENTER);
        playerIconLabel = new JLabel("", JLabel.CENTER);
        setPlayerIcon("character01.png");
        playerIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        playerIconLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        playerHPBar = new JProgressBar();
        playerHPBar.setForeground(Color.GREEN);
        playerPanel.add(playerStatusLabel, BorderLayout.NORTH);
        playerPanel.add(playerIconLabel, BorderLayout.CENTER);
        playerPanel.add(playerHPBar, BorderLayout.SOUTH);

        // 【右側：敵パネルの構築】
        JPanel enemyPanel = new JPanel(new BorderLayout(0, 5));
        enemyStatusLabel = new JLabel("", JLabel.CENTER);
        enemyIconLabel = new JLabel("👾", JLabel.CENTER);
        enemyIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        enemyIconLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        enemyHPBar = new JProgressBar();
        enemyHPBar.setForeground(Color.RED);
        enemyPanel.add(enemyStatusLabel, BorderLayout.NORTH);
        enemyPanel.add(enemyIconLabel, BorderLayout.CENTER);
        enemyPanel.add(enemyHPBar, BorderLayout.SOUTH);

        characterPanel.add(playerPanel);
        characterPanel.add(enemyPanel);
        add(characterPanel, BorderLayout.NORTH);

        // --- 2. ログエリア（中央） ---
        // 💡 ここでlogAreaが実体化（new）されます！
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("MS Gothic", Font.PLAIN, 16));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // --- 3. コマンドエリア（下部） ---
        JPanel commandPanel = new JPanel();
        attackButton = new JButton("たたかう");
        attackButton.addActionListener(e -> handleTurn());
        commandPanel.add(attackButton);
        add(commandPanel, BorderLayout.SOUTH);

        // 🌟 【ここに移動！】
        // ログエリアや各種ラベルが「すべて完全に準備できてから」敵を生み出す
        spawnNextEnemy();

        // 初期ステータスの反映と開始ログ
        updateStatus();
        log("ーーー 冒険が始まった！ ーーー");
    }

    // ⚔️ 「たたかう」ボタンが押された時の処理
    private void handleTurn() {
        if (!player.isAlive() || !enemy.isAlive())
            return;

        // 【1. プレイヤーのターン】
        log("\n⚔️ " + player.getName() + " のターン！");
        String playerResult = player.attack(enemy);
        log(playerResult);
        updateStatus();

        if (!enemy.isAlive()) {
            log("🎉 " + enemy.getName() + " を倒した！");
            defeatedCount++;

            if (defeatedCount > 3) {
                log("\n👑 ボスを倒した！ GAME CLEAR!!!");
                enemyIconLabel.setText("💀"); // ボス死亡アイコン
                attackButton.setEnabled(false);
            } else {
                spawnNextEnemy();
            }
            return;
        }

        // 【2. 敵のターン】
        log("\n👾 " + enemy.getName() + " の攻撃！");
        String enemyResult = enemy.attack(player);
        log(enemyResult);
        updateStatus();

        if (!player.isAlive()) {
            log("\n💀 あなたは力尽きてしまった… GAME OVER");
            playerIconLabel.setText("👻"); // プレイヤー死亡アイコン
            attackButton.setEnabled(false);
        }
    }

    // 👾 次の敵を生成して、ステータスやアイコンを切り替えるメソッド
    private void spawnNextEnemy() {
        if (defeatedCount == 3) {
            enemy = new Enemy("ボス", 200, 25);
            setEnemyIcon("character03.png"); // 🌟 3匹倒したらボスのアイコン（悪魔）に切り替え！
            log("\nボスが現れた！！！");
        } else {
            int enemyNum = defeatedCount + 1;
            enemy = new Enemy("敵" + enemyNum, 40, 10);
            setEnemyIcon("character02.png"); // 🌟 雑魚敵のアイコンにリセット
            log("\n👾 「敵" + enemyNum + "」が飛び出してきた！");
        }

        enemyHPBar.setMaximum(enemy.getHp());
        updateStatus();
    }

    // 📊 画面の表示数値を最新にするメソッド
    private void updateStatus() {
        playerHPBar.setMaximum(150);
        playerHPBar.setValue(player.getHp());
        enemyHPBar.setValue(enemy.getHp());

        playerStatusLabel.setText(player.getName() + " (HP: " + player.getHp() + " / 150)");
        enemyStatusLabel.setText(enemy.getName() + " (HP: " + enemy.getHp() + ")");
    }

    // 📝 ログエリアにテキストを追加するメソッド
    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // 🌟 プレイヤーの画像をセットするメソッド
    private void setPlayerIcon(String fileName) {
        ImageIcon icon = new ImageIcon(fileName);
        // 画像を60x60ピクセルに滑らかに縮小リサイズする処理
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);

        playerIconLabel.setIcon(new ImageIcon(resizedImg));
        playerIconLabel.setText(""); // 元の絵文字を消す
    }

    // 🌟 敵の画像をセットするメソッド
    private void setEnemyIcon(String fileName) {
        ImageIcon icon = new ImageIcon(fileName);
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);

        enemyIconLabel.setIcon(new ImageIcon(resizedImg));
        enemyIconLabel.setText(""); // 元の絵文字を消す
    }
}
