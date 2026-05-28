// プレイヤーのステータスと行動を管理するクラス
public class Player {
    private String name;
    private int hp;
    private int atk;

    public Player(String name, int hp, int atk) {
        this.name = name;
        this.hp = hp;
        this.atk = atk;
    }

    public String attack(Enemy enemy) {
        double r = Math.random();
        if (r < 0.1) {
            return "👉 しかし 攻撃は外れてしまった！";
        } else if (r > 0.9) {
            int damage = this.atk * 3;
            enemy.receiveDamage(damage);
            return "🔥 会心の一撃！！！\n💥 " + enemy.getName() + " に " + damage + " の大ダメージ！";
        } else {
            int damage = this.atk;
            enemy.receiveDamage(damage);
            return "💥 " + enemy.getName() + " に " + damage + " のダメージ！";
        }
    }

    public void receiveDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0)
            this.hp = 0;
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }
}