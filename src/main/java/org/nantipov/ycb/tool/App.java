package org.nantipov.ycb.tool;

import com.google.common.io.Resources;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.fusesource.jansi.Ansi.ansi;

public class App {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        parseArgsAndLaunchCommand(args);
        AnsiConsole.systemUninstall();
        System.out.println();
    }

    private static void parseArgsAndLaunchCommand(String[] args) {
        if (args.length > 0) {
            String commandName = args[0];

            switch (commandName) {
                case "init":
                    if (args.length < 2) {
                        System.out.println(ansi().render("@|red Mandatory project name argument is missed.|@"));
                        return;
                    }
                    runInitCommand(args[1]);
                    break;
                case "update":
                    runUpdateCommand();
                    break;
                default:
                    printHelp();
                    break;
            }
        } else {
            runUpdateCommand();
        }
    }

    private static void runInitCommand(String projectName) {
        InitCommand.execute(projectName);
    }

    private static void runUpdateCommand() {
        UpdateCommand.execute();
    }

    private static void printHelp() {
        try {
            System.out.println(
                    ansi().render(Resources.toString(Resources.getResource("help.txt"), Charset.defaultCharset()))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
