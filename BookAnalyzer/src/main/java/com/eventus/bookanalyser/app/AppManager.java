package com.eventus.bookanalyser.app;

import java.util.Scanner;

public class AppManager {

    public static void main(String[] args) {

        BookAnalyzer bookAnalyzer = new BookAnalyzer("ZING", 200);
        String dataLog = "Start !!!";

        System.out.println("I am App Manager.");

        // Using Scanner for Getting Input from User
        Scanner in = new Scanner(System.in);

        if (dataLog.isEmpty() || dataLog.isBlank()) {
            throw new IllegalArgumentException();
        }

        while (!dataLog.equalsIgnoreCase("exit!")) {
            System.out.println("Enter input:");
            //System.out.println(dataLog);
            dataLog = in.nextLine();
            bookAnalyzer.run(dataLog);
            System.out.println("**********************************");
        }
    }

}
