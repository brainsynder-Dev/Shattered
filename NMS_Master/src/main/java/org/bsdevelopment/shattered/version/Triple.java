package org.bsdevelopment.shattered.version;

public final class Triple<L, M, R> {
    public L left;
    public M middle;
    public R right;

    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new Triple(left, middle, right);
    }

    public Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public Triple setLeft(L left) {
        this.left = left;
        return this;
    }
    public Triple setMiddle(M middle) {
        this.middle = middle;
        return this;
    }
    public Triple setRight(R right) {
        this.right = right;
        return this;
    }

    public L getLeft() {
        return this.left;
    }
    public M getMiddle() {
        return this.middle;
    }
    public R getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "left=" + left +
                ", middle=" + middle +
                ", right=" + right +
                '}';
    }
}