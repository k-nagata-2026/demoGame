// 敵のステータスと行動を管理するクラス
public class Enemy {
    private String name;
    private int hp;
    private int atk;

    // コンストラクタ（初期設定）
    public Enemy(String name, int hp, int atk) {
        this.name = name;
        this.hp = hp;
        this.atk = atk;
    }

    // ⚔️ プレイヤーに攻撃するメソッド
    public void attack(Player player) {
        double r = Math.random();

        if (r < 0.1) {
            System.out.println("👉 " + this.name + " の攻撃！ しかし攻撃は外れた！");
        } else if (r > 0.9) {
            int damage = this.atk * 3;
            System.out.println("😭 " + this.name + " の痛恨の一撃！！！");
            System.out.println("💥 " + player.getName() + " は " + damage + " のダメージを受けた！");
            player.receiveDamage(damage);
        } else {
            int damage = this.atk;
            System.out.println("⚔️ " + this.name + " の攻撃！");
            System.out.println("💥 " + player.getName() + " は " + damage + " のダメージを受けた！");
            player.receiveDamage(damage);
        }
    }

    // ダメージを受けるメソッド
    public void receiveDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    // 生きているか確認するメソッド
    public boolean isAlive() {
        return this.hp > 0;
    }

    // ゲッター
    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }
}
