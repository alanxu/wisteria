package me.alanx.wisteria.utils.cor;

public abstract class Responsibility<I, O> {
    private Responsibility<I, O> nxt;

    public boolean hasNext() {
        return this.nxt == null;
    }

    public void setNext(Responsibility<I, O> nxt) {
        this.nxt = nxt;
    }

    public void handle(I input, O output) {
        this.handle_before_next(input, output);
        this.nxt.handle(input, output);
        this.handle_after_next(input, output);
    }

    protected abstract void handle_before_next(I input, O output);
    protected abstract void handle_after_next(I input, O output);
}
