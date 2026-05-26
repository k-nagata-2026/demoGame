// プレイヤーのステータスと行動を管理するクラス
public class Player {
    private String name;
    private int hp;
    private int atk;

    // コンストラクタ（初期設定）
    public Player(String name, int hp, int atk) {
        this.name = name;
        this.hp = hp;
        this.atk = atk;
    }

    // ⚔️ 敵に攻撃するメソッド
    public void attack(Enemy enemy) {
        double r = Math.random(); // 0.0〜1.0のランダムな数

        if (r < 0.1) {
            // 10%の確率でミス
            System.out.println("👉 " + this.name + " の攻撃！ しかし攻撃は外れた！");
        } else if (r > 0.9) {
            // 10%の確率で会心の一撃（3倍ダメージ）
            int damage = this.atk * 3;
            System.out.println("🔥 " + this.name + " の攻撃！ 会心の一撃！！！");
            System.out.println("💥 " + enemy.getName() + " に " + damage + " の大ダメージ！");
            enemy.receiveDamage(damage);
        } else {
            // 通常攻撃
            int damage = this.atk;
            System.out.println("⚔️ " + this.name + " の攻撃！");
            System.out.println("💥 " + enemy.getName() + " に " + damage + " のダメージ！");
            enemy.receiveDamage(damage);
        }
    }

    // ダメージを受けるメソッド
    public void receiveDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    // 生きているか確認するメソッド（HPが0より大きければtrue）
    public boolean isAlive() {
        return this.hp > 0;
    }

    // ゲッター（外部から値を読み取る用）
    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }
}