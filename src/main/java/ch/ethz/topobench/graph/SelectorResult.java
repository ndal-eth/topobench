package ch.ethz.topobench.graph;


public class SelectorResult<T> {

    private final T result;
    private final String[] remainingArgs;

    public SelectorResult(T result, String[] remainingArgs) {
        this.result = result;
        this.remainingArgs = remainingArgs;
    }

    public T getResult() {
        return result;
    }

    public String[] getRemainingArgs() {
        return remainingArgs;
    }

}
