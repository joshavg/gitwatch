package de.joshavg.gitwatch.cli;

public class CliOut {

    public static void writeln() {
        System.out.println();
    }

    public static void writeln(String line) {
        System.out.println(line);
    }

    public static void writeln(String line, Object... args) {
        System.out.println(String.format(line, args));
    }

}
