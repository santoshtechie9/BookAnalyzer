package com.eventus.bookanalyser.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class BookAnalyzerTest {

    List<String> input;
    BookAnalyzer bookAnalyzer;

    @BeforeEach
    public void before() {
        BookAnalyzer bookAnalyzer = new BookAnalyzer("ZING", 1);
        input = new ArrayList<>();
        input.add("28800538 A b S 44.26 100");
        input.add("28800562 A c B 44.10 100");
        input.add("28800744 R b 100");
        input.add("28800758 A d B 44.18 157");
        input.add("28800773 A e S 44.38 100");
        input.add("28800796 R d 157");
        input.add("28800812 A f B 44.18 157");
        input.add("28800974 A g S 44.27 100");
        input.add("28800975 R e 100");
        input.add("28812071 R f 100");
        input.add("28813129 A h B 43.68 50");
        input.add("28813300 R f 57");
        input.add("28813830 A i S 44.18 100");
        input.add("28814087 A j S 44.18 1000");
        input.add("28814834 R c 100");
        input.add("28814864 A k B 44.09 100");
        input.add("28815774 R k 100");
        input.add("28815804 A l B 44.07 175");
        input.add("28815937 R j 1000");
        input.add("28816245 A m S 44.22 100");
    }


    @Test
    void run() {
        for (String dataLog : input) {
            bookAnalyzer.run(dataLog);
        }
    }

    @Test
    void testTargetSizeOneFileInput() {
        bookAnalyzer = new BookAnalyzer("ZING", 200);
        try {
            String file = "C:\\Users\\santo\\Downloads\\test1.txt";
            PrintStream stream = new PrintStream(file);
            System.setOut(stream);
            FileInputStream fis = new FileInputStream("C:\\Users\\santo\\Downloads\\book_analyzer.in");
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                //System.out.println(sc.nextLine());
                bookAnalyzer.run(sc.nextLine());
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void hasValidNumberOfFields() {
    }
}