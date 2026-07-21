package org.roly.personalaccountant.desktop;

import atlantafx.base.theme.PrimerLight;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.roly.personalaccountant.view.rs.dto.MonthDetailResult;
import org.roly.personalaccountant.view.rs.dto.MonthDetailResult.DayResult;
import org.roly.personalaccountant.view.rs.dto.MonthSummaryResult;

/**
 * Minimal JavaFX desktop prototype (approach 2: REST client).
 *
 * <p>Shows one month's detail — budgets and the daily allocation list — fetched from the
 * running Spring Boot server over HTTP. Styled with AtlantaFX. This is a proof of the
 * integration/threading/look-and-feel, not a full port of the web UI.
 *
 * <p>Run: {@code ./mvnw -Pdesktop javafx:run} (server must be up on :8080).
 * Override base URL with {@code -Dpa.api.baseUrl=http://host:port}.
 */
public class DesktopApp extends Application {

    private final String baseUrl = System.getProperty("pa.api.baseUrl", "http://localhost:8080");
    private final ApiClient api = new ApiClient(baseUrl);
    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "pa-api");
        thread.setDaemon(true);
        return thread;
    });

    private final ComboBox<MonthSummaryResult> monthPicker = new ComboBox<>();
    private final VBox content = new VBox(12);
    private final Label status = new Label();

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        monthPicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(MonthSummaryResult month) {
                return month == null ? "" : month.expenseName() + "  (" + month.yearMonth() + ")";
            }

            @Override
            public MonthSummaryResult fromString(String string) {
                return null;
            }
        });
        monthPicker.setOnAction(event -> {
            MonthSummaryResult selected = monthPicker.getValue();
            if (selected != null) {
                loadDetail(selected.yearMonth());
            }
        });

        Button reload = new Button("Reload months");
        reload.setOnAction(event -> loadMonths());

        HBox top = new HBox(10, new Label("Month:"), monthPicker, reload);
        top.setAlignment(Pos.CENTER_LEFT);

        content.setPadding(new Insets(4));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);

        VBox root = new VBox(12, top, new Separator(), scroll, status);
        root.setPadding(new Insets(16));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        stage.setScene(new Scene(root, 780, 660));
        stage.setTitle("Personal Accountant — Desktop (" + baseUrl + ")");
        stage.show();

        loadMonths();
    }

    private void loadMonths() {
        status.setText("Loading months from " + baseUrl + " ...");
        runAsync(api::listMonths, months -> {
            monthPicker.getItems().setAll(months);
            if (months.isEmpty()) {
                status.setText("No months found. Is the server running and does it have data?");
                return;
            }
            monthPicker.getSelectionModel().selectLast();
            loadDetail(monthPicker.getValue().yearMonth());
        });
    }

    private void loadDetail(String yearMonth) {
        status.setText("Loading " + yearMonth + " ...");
        runAsync(() -> api.getMonthDetail(yearMonth), detail -> {
            if (detail == null) {
                status.setText("Month " + yearMonth + " not found.");
                return;
            }
            render(detail);
            status.setText("Loaded " + detail.expenseName() + " from " + baseUrl);
        });
    }

    private void render(MonthDetailResult month) {
        content.getChildren().clear();

        Label title = new Label(month.expenseName() + "  ·  " + month.yearMonth());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox cash = new HBox(24,
                metric("Cash total", month.cashTotal()),
                metric("Cash left", month.cashLeft()));

        VBox budgets = new VBox(10,
                budgetBar("Fixed Budget", month.fixedExpenseTotal(), month.fixedBudget()),
                budgetBar("Leisure Budget", month.leisureExpenseTotal(), month.leisureBudget()),
                budgetBar("Saving Budget", month.savingExpenseTotal(), month.savingBudget()));
        budgets.setPadding(new Insets(8, 0, 8, 0));

        TableView<DayResult> table = buildDayTable(month.days());

        content.getChildren().addAll(
                title,
                cash,
                new Separator(),
                sectionLabel("Budgets"),
                budgets,
                new Separator(),
                sectionLabel("Daily allocation"),
                table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private VBox metric(String label, double value) {
        Label caption = new Label(label);
        Label amount = new Label(String.format("%.2f", value));
        amount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return new VBox(2, caption, amount);
    }

    /**
     * Mirrors the web UI's budget-bar behavior: full and red when spent >= budget (including
     * a zero budget with spending), proportional green otherwise.
     */
    private VBox budgetBar(String label, double spent, double budget) {
        boolean over = spent > budget;
        double ratio = budget > 0 ? Math.min(1.0, spent / budget) : (spent > 0 ? 1.0 : 0.0);
        double left = budget - spent;

        ProgressBar bar = new ProgressBar(ratio);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + (over ? "#e5484d" : "#2da44e") + ";");

        Label caption = new Label(String.format("%s — spent %.2f of %.2f  (%.2f left)",
                label, spent, budget, left));
        return new VBox(3, caption, bar);
    }

    private TableView<DayResult> buildDayTable(List<DayResult> days) {
        TableView<DayResult> table = new TableView<>();

        TableColumn<DayResult, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().date().toString()));

        TableColumn<DayResult, String> weekend = new TableColumn<>("Weekend");
        weekend.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().weekend() ? "Yes" : ""));

        TableColumn<DayResult, String> allocation = new TableColumn<>("Allocation");
        allocation.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f", cell.getValue().maxAllocation())));

        TableColumn<DayResult, String> spent = new TableColumn<>("Spent");
        spent.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f", cell.getValue().expenditure())));

        TableColumn<DayResult, String> done = new TableColumn<>("Done");
        done.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().dayDone() ? "\u2713" : ""));

        table.getColumns().add(date);
        table.getColumns().add(weekend);
        table.getColumns().add(allocation);
        table.getColumns().add(spent);
        table.getColumns().add(done);
        table.getItems().setAll(days);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    private <T> void runAsync(Callable<T> task, Consumer<T> onSuccess) {
        executor.submit(() -> {
            try {
                T result = task.call();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception ex) {
                Platform.runLater(() -> status.setText("Error: " + ex.getMessage()));
            }
        });
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
