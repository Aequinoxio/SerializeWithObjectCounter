package SerializeWithObjectCounter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Enclosure class that read and/or write serielizable object while counting them
 */
public class SerializableEnclosure implements Serializable {
    private long numOfItems;              // Per salvarli
    private Object objectToBeSerialized;    // Oggetto da salvare

    // Contatore interno
    private final transient static ObjectCounterInternalClass counter = ObjectCounterInternalClass.getInstance();

    ///////// Metodi ////////

    /**
     * Costructor, all set to 0
     */
    public SerializableEnclosure() {
        numOfItems=0;
        counter.resetAll();
    }

    /**
     * Constructor
     * @param numOfItems The maximum number of items in main object to be counted
     */
    public SerializableEnclosure(long numOfItems) {
        this.numOfItems = numOfItems;
        counter.resetAll();
        counter.setNumObjects(numOfItems);
    }

    /**
     * Constructor
     * @param numOfItems The maximum number of items in main object to be counted
     * @param objectToBeSerialized The object to be serialized whose items will be counted
     */
    public SerializableEnclosure(long numOfItems, Object objectToBeSerialized) {
        this.numOfItems = numOfItems;
        this.objectToBeSerialized = objectToBeSerialized;
        counter.resetAll();
        counter.setNumObjects(numOfItems);
    }

    /**
     * Constructor
     * @param numOfItems The maximum number of items in main object to be counted
     * @param objectToBeSerialized The object to be serialized whose items will be counted
     * @param publishAction The action that will be called every time an item is read or written
     */
    public SerializableEnclosure(long numOfItems, Object objectToBeSerialized, PublishAction publishAction) {
        this.numOfItems = numOfItems;
        this.objectToBeSerialized = objectToBeSerialized;
        counter.resetAll();
        counter.setNumObjects(numOfItems);
        counter.setPublishAction(publishAction);
    }


    /**
     * Get the number of items of the object serialized/serializable
     * @return The number of items of the serialized/serializable object
     */
    public long getNumOfItems() {
        return numOfItems;
    }

    /**
     * Set the number of items of the object serialized/serializable
     * @param numOfItems The number of items of the serialized/serializable object
     */
    public void setNumOfItems(long numOfItems) {
        this.numOfItems = numOfItems;
    }

    /**
     * Get the whole object serialized/serializable
     * @return The whole object serialized/serializable
     */
    public Object getObject() {
        return objectToBeSerialized;
    }

    /**
     * Set the whole object serialized/serializable
     * @param objectToBeSerialized The whole object serialized/serializable
     */
    public void setObject(Object objectToBeSerialized) {
        this.objectToBeSerialized = objectToBeSerialized;
    }

    public void setObjectPublishAction(PublishAction publishAction) {
        counter.setPublishAction(publishAction);
    }

    //////////// Metodi per la serializzazione /////////////////
    private void readObject( ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        this.numOfItems = aInputStream.readLong();
        counter.resetAll();
        counter.setNumObjects(numOfItems);
        // Probabilemnte è più efficiente sui grandi numeri
        // counter.resetCounterSetMaxObj(numOfObjects); // Azzero il contatore ed imposto il numero max di oggetti
        aInputStream.defaultReadObject();
    }

    private void writeObject( ObjectOutputStream aOutputStream) throws IOException {
        counter.resetAll();
        counter.setNumObjects(numOfItems);
        // counter.resetCounterSetMaxObj(numOfObjects); // probabilmente è più efficiente ma ha impliciti effetti collaterali, per chiarezza è stato splittato
        aOutputStream.writeLong(numOfItems);
        aOutputStream.defaultWriteObject();
    }


    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////// Singleton privato per tenere traccia del numero di oggetti  /////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * Singleton per la gestione del contatore. Non va usato da solo
     */
    static class ObjectCounterInternalClass{
        final transient private static ObjectCounterInternalClass ourInstance = new ObjectCounterInternalClass() ;
        volatile transient private static PublishAction publishAction;

        private volatile transient long counter=0;
        private volatile transient long numObjects=0;

        ////////////////////////
        /**
         * private constructor for Singleton
         */
        private ObjectCounterInternalClass() {

        }
        ////////////////////////


        long getNumObjects() {
            return this.numObjects;
        }

        void setNumObjects(long numObjects) {
            this.numObjects = numObjects;
        }

        long getCounter() {
            return this.counter;
        }

        void setCounter(long counter) {
            this.counter = counter;
        }

        /**
         * Trying to be thread safe
         */
        synchronized void incrementCounter(){
            this.counter++;
            publish();
        }

        /**
         * Reset the counter and the max number of objects
         */
        void resetAll(){
            this.counter=0;
            this.numObjects=0;
        }


        /**
         * Set the counter and the maximun mumber of objects
         * @param counter       The counter
         * @param numObjects    The maximum number of objects
         */
        void setAll(long counter, long numObjects){
            this.counter=counter;
            this.numObjects=numObjects;
        }


        /**
         * Set the publih action that is called every increment of counter
         * @param publishAction Action to be called
         */
        void setPublishAction(PublishAction publishAction){
            ObjectCounterInternalClass.publishAction = publishAction;
        }


        /**
         * Call the publish method of the publishAction
         */
        private void publish() {
            if (publishAction !=null) {
                publishAction.publish(counter, numObjects);
            }
            //System.out.println(String.format("Oggetto n. %d di %d",getCounter(), getNumObjects()));
        }


        /**
         * Return the instance of the Singleton
         * @return The singleton
         */
        static ObjectCounterInternalClass getInstance() {
            return ourInstance;
        }
    }

}