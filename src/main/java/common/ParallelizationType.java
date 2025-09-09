package common;

public enum ParallelizationType {
    SEQUENTIAL,
    PARALLEL,
    FORK_JOIN,
    VIRTUAL_PER_ROW,
    VIRTUAL_PER_CHUNK
    ;

    public static ParallelizationType fromString(String string) {
        return ParallelizationType.valueOf(string.toUpperCase());
    }
}
