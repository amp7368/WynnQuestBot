package apple.questing.discord.pageable;

public interface Pageable {
    void forward();
    void backward();
    Long getId();
    long getLastUpdated();
    void top();
}
