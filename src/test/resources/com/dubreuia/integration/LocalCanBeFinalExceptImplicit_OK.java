package com.dubreuia.integration;

public class Class {

    private void method() {
        final String canBeFinal = "";
        String cannotBeFinal = "";
        cannotBeFinal = "";
        try (Resource r = new Resource()) {

        }
    }

    class Resource implements AutoCloseable {

        @Override
        public void close() {

        }

    }

}
