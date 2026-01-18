package client;

import client.model.ShoppingItem;
import client.model.ShoppingList;
import java.util.List;

/**
 * Formats a shopping list for export
 */
public class ShoppingListFormatter {

    private final ShoppingList shoppingList;

    /**
     * Creates a new formatter.
     *
     * @param shoppingList the shopping list to format
     */
    public ShoppingListFormatter(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    /**
     * Formats the shopping list as plaintext
     *
     * @return the formatted shopping list
     */
    public String formatPlaintext() {
        StringBuilder text = new StringBuilder();
        text.append("Shopping List\n");
        text.append("==============\n\n");

        List<ShoppingItem> items = shoppingList.getShoppingItems();
        if (items.isEmpty()) {
            text.append("No items in shopping list.\n");
            return text.toString();
        }

        text.append("Items:\n");
        for (ShoppingItem item : items) {
            text.append("  - ").append(item.toString()).append("\n");
        }

        return text.toString();
    }

    /**
     * Formats the shopping list as markdown
     *
     * @return the formatted shopping list
     */
    public String formatMarkdown() {
        StringBuilder text = new StringBuilder();
        text.append("# Shopping List\n\n");

        List<ShoppingItem> items = shoppingList.getShoppingItems();
        if (items.isEmpty()) {
            text.append("No items in shopping list.\n");
            return text.toString();
        }

        text.append("| Item | Amount | Source |\n");
        text.append("|------|--------|--------|\n");

        for (ShoppingItem item : items) {
            String name = item.getName();
            String amount =
                item.getAmount() > 0
                    ? String.format("%.1f %s", item.getAmount(), item.getUnit())
                    : item.getUnit();
            String source =
                item.getSourceRecipeName() != null
                    ? item.getSourceRecipeName()
                    : "-";
            text
                .append("| ")
                .append(name)
                .append(" | ")
                .append(amount)
                .append(" | ")
                .append(source)
                .append(" |\n");
        }

        return text.toString();
    }
}
