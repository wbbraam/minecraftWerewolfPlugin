package hoeve.plugins.werewolf.game.helpers;

/**
 * Created by DeStilleGast 14-4-2020
 */
public class Tuple <A, B> {

    private A first;
    private B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
