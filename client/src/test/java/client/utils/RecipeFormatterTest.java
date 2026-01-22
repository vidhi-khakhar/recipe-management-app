package client.utils;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import client.RecipeFormatter;

public class RecipeFormatterTest {

    // A simple ResourceBundle for testing language names
    private final ResourceBundle testBundle = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][]{
                    {"label.english", "English"},
                    {"label.dutch", "Nederlands"},
                    {"label.spanish", "Español"},
                    {"label.romanian", "română"}
            };
        }
    };

    @Test
    public void englishLanguageTest() {
        String[] inputs = {"en", "EN", " en "};
        for (String input : inputs) {
            assertEquals("English", RecipeFormatter.getFullRecipeLanguage(input, testBundle));
        }
    }

    @Test
    public void dutchLanguageTest() {
        String[] inputs = {"nl", "NL", " nl "};
        for (String input : inputs) {
            assertEquals("Nederlands", RecipeFormatter.getFullRecipeLanguage(input, testBundle));
        }
    }

    @Test
    public void spanishLanguageTest() {
        String[] inputs = {"es", "ES", " es "};
        for (String input : inputs) {
            assertEquals("Español", RecipeFormatter.getFullRecipeLanguage(input, testBundle));
        }
    }

    @Test
    public void romanianLanguageTest() {
        String[] inputs = {"ro", "RO", " ro "};
        for (String input : inputs) {
            assertEquals("română", RecipeFormatter.getFullRecipeLanguage(input, testBundle));
        }
    }

    @Test
    public void emptyLanguageTest() {
        String[] inputs = {"", "   "};
        for (String input : inputs) {
            assertEquals("", RecipeFormatter.getFullRecipeLanguage(input, testBundle));
        }
    }

    @Test
    public void unknownLanguageTest() {
        String unknownInput = "fr";
        assertEquals("fr", RecipeFormatter.getFullRecipeLanguage(unknownInput, testBundle));
    }

    @Test
    public void nullLanguageTest() {
        String input = null;
        assertEquals("", RecipeFormatter.getFullRecipeLanguage(input, testBundle));
    }
}
