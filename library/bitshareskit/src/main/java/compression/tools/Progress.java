package compression.tools;

public interface Progress {
    void setProgress(long inSize, long outSize);
}
