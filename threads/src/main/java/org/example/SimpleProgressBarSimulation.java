package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleProgressBarSimulation {

    private final int threadCount;
    private final int calculationLength;
    private final int updateDelayMs;
    private final List<SimpleProgressBar> progressBars;

    public SimpleProgressBarSimulation(int threadCount, int calculationLength, int updateDelayMs) {
        this.threadCount = threadCount;
        this.calculationLength = calculationLength;
        this.updateDelayMs = updateDelayMs;
        this.progressBars = new ArrayList<>();
    }

    public void start() {
        System.out.println("\n==================================================");
        System.out.println("MULTI-THREADED PROGRESS SIMULATION");
        System.out.printf("Threads: %d | Calculation length: %d | Update delay: %d ms\n",
                threadCount, calculationLength, updateDelayMs);
        System.out.println("==================================================\n");

        // Создаем и выводим начальные строки
        for (int i = 0; i < threadCount; i++) {
            SimpleProgressBar pb = new SimpleProgressBar(i + 1, calculationLength, updateDelayMs);
            progressBars.add(pb);
            System.out.println(pb.getEmptyLine());
        }

        // Запускаем все потоки
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (SimpleProgressBar pb : progressBars) {
            executor.submit(pb);
        }

        executor.shutdown();

        try {
            executor.awaitTermination(calculationLength * updateDelayMs + 5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n==================================================");
        System.out.println("ALL THREADS COMPLETED");
        System.out.println("==================================================");
    }

    private static class SimpleProgressBar implements Runnable {
        private final int threadNumber;
        private final int totalSteps;
        private final int delayMs;
        private int currentStep;
        private long startTime;
        private long threadId;

        public SimpleProgressBar(int threadNumber, int totalSteps, int delayMs) {
            this.threadNumber = threadNumber;
            this.totalSteps = totalSteps;
            this.delayMs = delayMs;
            this.currentStep = 0;
        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            threadId = Thread.currentThread().threadId();

            while (currentStep < totalSteps) {
                try {
                    Thread.sleep(delayMs);
                    currentStep++;
                    printProgress();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            printCompletion();
        }

        private synchronized void printProgress() {
            int percent = (currentStep * 100) / totalSteps;
            int filledLength = (currentStep * 40) / totalSteps;

            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < filledLength; i++) {
                bar.append("=");
            }
            for (int i = filledLength; i < 40; i++) {
                bar.append(" ");
            }

            // Сохраняем позицию курсора
            System.out.print("\r");

            // Ищем строку этого потока и обновляем
            for (int i = 0; i < threadNumber; i++) {
                System.out.print("\033[1A");
            }
            System.out.print("\r\033[2K");
            System.out.printf("Thread %-2d | ID: %-5d | [%s] %3d%%",
                    threadNumber, threadId, bar.toString(), percent);
            System.out.flush();

            // Возвращаемся обратно
            for (int i = 0; i < threadNumber; i++) {
                System.out.print("\033[1B");
            }
        }

        private synchronized void printCompletion() {
            long duration = System.currentTimeMillis() - startTime;

            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < 40; i++) {
                bar.append("=");
            }

            System.out.print("\r");
            for (int i = 0; i < threadNumber; i++) {
                System.out.print("\033[1A");
            }
            System.out.print("\r\033[2K");
            System.out.printf("Thread %-2d | ID: %-5d | [%s] 100%% | FINISHED in %d ms",
                    threadNumber, threadId, bar.toString(), duration);
            System.out.flush();

            for (int i = 0; i < threadNumber; i++) {
                System.out.print("\033[1B");
            }
            System.out.println();
        }

        public String getEmptyLine() {
            return String.format("Thread %-2d | ID: %-5s | [%s] %3s%%",
                    threadNumber, "?", "                                        ", "0");
        }
    }
}