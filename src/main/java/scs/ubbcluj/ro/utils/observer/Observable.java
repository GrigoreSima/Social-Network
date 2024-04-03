package scs.ubbcluj.ro.utils.observer;


import scs.ubbcluj.ro.utils.event.Event;

public interface Observable<E extends Event>{
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
