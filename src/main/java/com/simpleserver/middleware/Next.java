package com.simpleserver.middleware;

@FunctionalInterface
public interface Next {
    public void execute();
}
