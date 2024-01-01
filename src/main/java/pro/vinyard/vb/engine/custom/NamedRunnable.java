package pro.vinyard.vb.engine.custom;

public class NamedRunnable implements Runnable {

    private final String name;
    private final Runnable runnable;

    public NamedRunnable(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        this.runnable.run();
    }

    @Override
    public String toString() {
        return name;
    }
}
