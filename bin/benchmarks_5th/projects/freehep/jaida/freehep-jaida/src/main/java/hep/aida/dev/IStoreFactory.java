package hep.aida.dev;

/**
 * An interface to a factory that create IStore.
 */

public interface IStoreFactory
{
    String description();
    boolean supportsType(String type);
    IStore createStore();
}
