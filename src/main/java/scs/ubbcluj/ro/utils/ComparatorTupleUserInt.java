package scs.ubbcluj.ro.utils;

import scs.ubbcluj.ro.domain.User;

import java.util.Comparator;
import java.util.Objects;

public class ComparatorTupleUserInt implements Comparator<Tuple<User, Integer>> {


    /**
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     */
    @Override
    public int compare(Tuple<User, Integer> o1, Tuple<User, Integer> o2) {
        if(o1.getRight() > o2.getRight()) return -1;
        if(Objects.equals(o1.getRight(), o2.getRight())) return 0;
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
