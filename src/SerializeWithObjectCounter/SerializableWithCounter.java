package SerializeWithObjectCounter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Classe da estendere per rendere serializzabile e contabile un'altra classe
 * Uso tipico:
 *      Genera una classe sottoclassando  ClasseDaSerializzareEContare.
 *          ClasseDaSerializzareEContare extends SerializableWithCounter { ... }
 *      Crea l'oggetto o un insieme/lista ecc. di oggetti ClasseDaSerializzareEContare
 *          "ListaDiOggetti&lt;ClasseDaSerializzareEContare&gt; = new ..."
 *
 *      Usa gli oggetti/lista ecc. nel tuo programma come meglio credi
 *
 *      Crea un oggetto della PublishAction ed implementa il metodo publish come meglio credi
 *      Crea un oggetto SerializableEnclosure e mettici le info necessarie.
 *          SerializableEnclosure ser = new SerializableEnclosure(numOggetti, Oggetto/list da serializzare, PublishActionObject);
 *
 *      Serializza quest'ultimo oggetto (ser).
 *
 *      Verrà salvato ogni oggetto ClasseDaSerializzareEContare e viene chiamato per ciascun salvataggio
 *      il metodo publish(...) dell'oggetto PublishActionObject
 *
 */
public class SerializableWithCounter implements Serializable {

    // Riferimento per contare l'oggetto quando viene salvato o letto
    private final static transient SerializableEnclosure.ObjectCounterInternalClass counter = SerializableEnclosure.ObjectCounterInternalClass.getInstance();

    /**
     * Incremento il contatore per ogni azione di lettura dell'oggetto. C'è un effetto collaterale in quanto il contatore
     * viene sempre incrementato per l'azione di ripristino dell'oggetto elementare. Questo effetto è eliminato dalla
     * classe enclosure che resetta i contatori ad ogni lettura e scrittura. In questo modo, l'incremento del contatore
     * rappresenta effettivamente la lettura all'interno del processo di ripristino.
     * Il contatore è inaccessibile alla classe derivata.
     * @param aInputStream  Stream di lettura
     * @throws ClassNotFoundException The ClassNotFoundException of the readObject method
     * @throws IOException The IOException of the readObject method
     */
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        aInputStream.defaultReadObject();
        counter.incrementCounter();
    }

    /**
     * Incremento il contatore per ogni azione di scrittura dell'oggetto. C'è un effetto collaterale in quanto il contatore
     * viene sempre incrementato per l'azione di salvataggio dell'oggetto elementare. Questo effetto è eliminato dalla
     * classe enclosure che resetta i contatori ad ogni lettura e scrittura. In questo modo, l'incremento del contatore
     * rappresenta effettivamente la lettura all'interno del processo di ripristino.
     * Il contatore è inaccessibile alla classe derivata.
     * @param aOutputStream Stream di scrittura
     * @throws IOException The IOException of the readObject method
     */
    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.defaultWriteObject();
        counter.incrementCounter();
    }
}
