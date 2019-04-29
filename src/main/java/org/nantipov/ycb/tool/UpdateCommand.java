package org.nantipov.ycb.tool;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.fusesource.jansi.Ansi.ansi;

public class UpdateCommand implements Runnable {

    private final Path projectDir;
    private final ProjectUpdatingService updatingService;

    public UpdateCommand() {
        this.projectDir = Paths.get(".");
        this.updatingService = new ProjectUpdatingService(projectDir);
    }

    public static void execute() {
        UpdateCommand updateCommand = new UpdateCommand();
        updateCommand.run();
    }

    @Override
    public void run() {
        try {
            System.out.println("Updating the project structure...");
            updatingService.update();
            System.out.println(
                    ansi().render("@|green Done|@")
            );
        } catch (IOException e) {
            System.out.println(ansi().fgRed().a("Could not load and update project files: " + e.getMessage()));
        }
    }
}
