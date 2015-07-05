package gastarter;

/**
 * @author Andrew
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Triple<A, B, C> {
    public final A begin;
    public final B step;
    public final C end;
    public Triple(A a, B b, C c) { this.begin = a; this.step = b; this.end = c; }
}

