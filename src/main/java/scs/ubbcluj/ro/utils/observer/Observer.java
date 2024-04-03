package scs.ubbcluj.ro.utils.observer;

import scs.ubbcluj.ro.utils.event.Event;

public interface Observer<E extends Event>{
    void update(E e);
}
