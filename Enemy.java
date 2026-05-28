// 敵のステータスと行動を管理するクラス
public class Enemy {
    private String name;
    private int hp;
    private int atk;

    public Enemy(String name, int hp, int atk) {
        this.name = name;
        this.hp = hp;
        this.atk = atk;
    }

    public String attack(Player player) {
        double r = Math.random();
        if (r < 0.1) {
            return "👉 しかし 敵の攻撃は外れた！";
        } else if (r > 0.9) {
            int damage = this.atk * 3;
            player.receiveDamage(damage);
            return "😭 痛恨の一撃！！！\n💥 " + player.getName() + " は " + damage + " のダメージを受けた！";
        } else {
            int damage = this.atk;
            player.receiveDamage(damage);
            return "💥 " + player.getName() + " は " + damage + " のダメージを受けた！";
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
