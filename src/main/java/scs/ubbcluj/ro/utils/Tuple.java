package scs.ubbcluj.ro.utils;

import java.util.Objects;

public class Tuple<E1, E2> {
    private E1 left;
    private E2 right;

    public Tuple(E1 left, E2 right) {
        this.left = left;
        this.right = right;
    }

    public E1 getLeft() {
        return left;
    }

    public void setLeft(E1 left) {
        this.left = left;
    }

    public E2 getRight() {
        return right;
    }

    public void setRight(E2 right) {
        this.right = right;
    }


    @Override
    public String toString() {
        return "( "+left+", "+right+" )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(getLeft(), tuple.getLeft()) && Objects.equals(getRight(), tuple.getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeft(), getRight());
    }
}
