package pet.money.tracker.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Deletes a transaction by its ID. */
@Command(name = "delete", description = "Delete a transaction by ID.", mixinStandardHelpOptions = true)
public class DeleteCommand implements Runnable {

    @Option(names = {"--id"}, required = true, description = "ID of the transaction to delete.")
    private String id;

    @Override
    public void run() {
        MainCommand.getTxService().delete(id);
        System.out.println("Deleted transaction " + id);
    }
}
