package org.example;

public class Main {
    public static void main(String[] args) {
        int threadCount = 5;
        int calculationLength = 30;
        int updateDelayMs = 150;

        SimpleProgressBarSimulation sim = new SimpleProgressBarSimulation(
                threadCount,
                calculationLength,
                updateDelayMs
        );

        sim.start();
    }
}