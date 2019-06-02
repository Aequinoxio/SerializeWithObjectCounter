package SerializeWithObjectCounter;

/**
 * Interfaccia da implementare per pubblicare il contatore
 */
public interface PublishAction {

    /**
     * Publish the read/written item counter
     * @param counter Counter of the item read/written
     * @param maxObjects THe maximum number of items to be read/write
     */
    void publish(long counter, long maxObjects);
}
