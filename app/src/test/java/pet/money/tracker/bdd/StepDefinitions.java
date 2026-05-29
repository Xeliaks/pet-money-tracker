package pet.money.tracker.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.service.TransactionService;

/** Cucumber step definitions for the Transaction Management feature. */
public class StepDefinitions {

    private TransactionService service;
    private List<Transaction> lastResult;

    @Before
    public void setUp() {
        service = new TransactionService(new InMemoryStorageProvider());
        lastResult = null;
    }

    @Given("the tracker is empty")
    public void theTrackerIsEmpty() {
        assertThat(service.findAll()).isEmpty();
    }

    @Given("I have added a {string} expense of {double} in {string} on {string}")
    public void iHaveAddedAnExpense(String title, double amount, String category, String date) {
        service.add(title, BigDecimal.valueOf(amount),
                Category.fromString(category), LocalDate.parse(date), "");
    }

    @When("I add a {string} expense of {double} in {string} on {string}")
    public void iAddAnExpense(String title, double amount, String category, String date) {
        service.add(title, BigDecimal.valueOf(amount),
                Category.fromString(category), LocalDate.parse(date), "");
    }

    @When("I list all transactions")
    public void iListAllTransactions() {
        lastResult = service.findAll();
    }

    @When("I search for {string}")
    public void iSearchFor(String keyword) {
        lastResult = service.search(keyword);
    }

    @When("I filter by category {string}")
    public void iFilterByCategory(String category) {
        lastResult = service.filterByCategory(Category.fromString(category));
    }

    @When("I delete the transaction with title {string}")
    public void iDeleteTheTransactionWithTitle(String title) {
        Transaction target = service.findAll().stream()
                .filter(t -> t.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No transaction with title: " + title));
        service.delete(target.getId());
    }

    @Then("the tracker contains {int} transaction(s)")
    public void theTrackerContains(int count) {
        assertThat(service.findAll()).hasSize(count);
    }

    @Then("the result contains {int} transaction(s)")
    public void theResultContains(int count) {
        assertThat(lastResult).hasSize(count);
    }
}