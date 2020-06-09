package org.cft.exception;

public class InitializeFailedException extends RuntimeException {
    public InitializeFailedException() {
        super("Не удалось иницилизировать объект");
    }
}
