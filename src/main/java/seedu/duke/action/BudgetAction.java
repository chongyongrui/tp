package seedu.duke.action;

import seedu.duke.Ui;
import seedu.duke.exception.GlobalInvalidMonthYearException;
import seedu.duke.model.Budget;
import seedu.duke.model.Expense;
import seedu.duke.util.Commons;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

//@@author chongyongrui
/**
 * Contains methods related to budget function
 */
public class BudgetAction {
    private static BudgetUIResponse budgetUi;
    private ArrayList<Budget> budgets;

    public BudgetAction(ArrayList<Budget> budgets, Ui ui) {
        this.budgets = budgets;
        budgetUi = new BudgetUIResponse(ui);
    }

    public ArrayList<Budget> getBudgets() {
        return this.budgets;
    }

    /**
     * Creates a budget of name and limit determined by user input
     *
     * @param budgetName  the name of the budget that the user wants to create
     * @param budgetLimit the monetary limit of the budget
     */
    public void addBudget(String budgetName, double budgetLimit) {
        // Check if there are any duplicate budgets
        if (validateBudget(budgetName, budgets)) {
            budgetUi.printBudgetNameUsed();
            return;
        } else if (budgetLimit < 0) {
            budgetUi.printBudgetLimitNegative();
            return;
        }

        Budget budget = new Budget(budgetName, budgetLimit);
        budgets.add(budget);

        budgetUi.printBudgetAddSuccessful(budget, budgets.size());

    }

    /**
     * Deletes a budget from the budget list
     *
     * @param budgetName the name of the budget to delete
     */
    public void deleteBudget(String budgetName, ArrayList<Expense> expenses) {
        Budget budget = getBudget(budgetName);
        if (budget == null) {
            budgetUi.printBudgetDoesNotExist();
            return;
        }

        budgets.remove(budget);
        budgetUi.printBudgetDelSuccessful(budget, budgets.size());
    }

    /**
     * Modifies the budget limit of a chose budget
     *
     * @param budgetName  the budget to modify the budget limit for
     * @param budgetLimit the new budget limit
     */

    public void setBudget(String budgetName, double budgetLimit) {
        Budget budget = getBudget(budgetName);
        if (budget == null) {
            budgetUi.printBudgetDoesNotExist();
            return;
        } else if (budgetLimit < 0) {
            budgetUi.printBudgetLimitNegative();
            return;
        }

        budget.setAmount(budgetLimit);

        budgetUi.printBudgetSetSuccessful(budget, budgets.size());
    }

    /**
     * Finds if a budget contains the keywords input by user
     *
     * @param keyword the word the user wants to check for
     */
    public void findBudget(String keyword) {
        ArrayList<Budget> foundBudgets = new ArrayList<>();
        for (Budget budget : budgets) {

            if (budget.getName().contains(keyword)) {
                foundBudgets.add(budget);
            }
        }

        if (foundBudgets.isEmpty()) {
            budgetUi.printBudgetDoesNotExist();
        } else {
            // budgetUi.printListBudgets(foundBudgets);
        }
        foundBudgets.clear();
    }


    /**
     * Checks if a certain budget exists
     *
     * @param budgetName budget name to check for if it has been used
     */
    private Budget getBudget(String budgetName) {
        for (Budget budget : budgets) {
            if (budget.getName().equals(budgetName)) {
                return budget;
            }
        }

        return null;
    }

    /**
     * Prints all the details of all budgets in the list
     * @throws GlobalInvalidMonthYearException
     */
    public void printBudgets(int month, int year, ArrayList<Expense> expenses) throws GlobalInvalidMonthYearException {
        double[] budgetsExpenseTotal = new double[budgets.size()];

        // Check if month and year is valid
        LocalDate endDate = Commons.isValidMonthYear(month, year);
        LocalDate startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());

        // Used to format the printing
        int longestBudgetName = 0;

        int i = 0;
        for (Budget b : budgets) {
            String category = b.getName();

            if (category.length() > longestBudgetName) {
                longestBudgetName = category.length();
            }

            ArrayList<Expense> filteredExpenses = ExpenseAction.filterExpensesByCategory(expenses, category);
            filteredExpenses = ExpenseAction.filterExpensesByDate(filteredExpenses, startDate, endDate);

            budgetsExpenseTotal[i] = ExpenseAction.getTotalExpenses(filteredExpenses);
            i++;
        }
        
        budgetUi.printListBudgets(budgets, budgetsExpenseTotal, month, year, longestBudgetName);
    }
    
    /**
     * Prints user instructions on how to use budget commands
     */
    public void budgetHelp() {
        budgetUi.printBudgetCommands();
    }

    // /**
    //  * Prints a message about budgets that are close to the limit upon the initialisation of Duke
    //  */
    // public static void summaryBudget(ArrayList<Expense> expenses, ArrayList<Budget> budgets) {
    //     int count = 0;

    //     for (Budget budget : budgets) {
    //         double amountSpent = ExpenseUIResponse.findTotalRelatedExpenses(expenses, budget.getName());
    //         double ratio = amountSpent / budget.getAmount();
    //         if (ratio >= 0.75) {
    //             count += 1;
    //             System.out.println(count + ". Warning: " + budget.getName() + " budget:");
    //             printBudgetDetailBar(ratio);
    //         }
    //     }
    //     if (count == 0 && budgets.size() != 0) {
    //         System.out.println("Good Job! There are no budgets that are close to its limit!");
    //     }
    // }

    /**
     * Checks if a certain budget name already exists
     *
     * @param budgetName budget name to check for if it has been used
     */
    protected static boolean validateBudget(String budgetName, ArrayList<Budget> budgetList) {
        for (Budget budget : budgetList) {
            if (budget.getName().equals(budgetName)) {
                return true;
            }
        }
        return false;
    }

    // /**
    //  * Prints the budget progress bar
    //  *
    //  * @param ratio the percentage of what is spent compared to the budget limit
    //  */
    // public static void printBudgetDetailBar(double ratio) {
    //     int numberOfBlocks = 0;
    //     if ((int) ratio >= 1) {
    //         numberOfBlocks = 40;
    //     } else {
    //         numberOfBlocks = (int) (ratio * 40);
    //     }
    //     int excess = (int) ratio;
    //     int i = 0;
    //     int numberOfBlanks = 40 - numberOfBlocks;
    //     while (i < numberOfBlocks) {
    //         if (excess == 0) {
    //             System.out.print("█");
    //         } else {
    //             System.out.print(Constants.ANSI_RED + "█" + Constants.ANSI_RESET);
    //         }
    //         i++;
    //     }
    //     i = 0;
    //     while (i < numberOfBlanks) {
    //         System.out.print("░");
    //         i++;
    //     }
    //     System.out.println(" ");
    //     if (ratio >= 1) {
    //         System.out.println("You have exceeded the budget limit by " + (ratio - 1) * 100 + "%!");
    //     } else {
    //         System.out.println(ratio * 100 + "% of your budget has been spent!");
    //     }
    // }
}
