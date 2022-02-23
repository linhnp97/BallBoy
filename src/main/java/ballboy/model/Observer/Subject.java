package ballboy.model.Observer;

/**
 * Inteface for classes that needs to store and update observers.
 */
public interface Subject{
    public void registerObserver(Observer o);
    public void notifyObservers();
}