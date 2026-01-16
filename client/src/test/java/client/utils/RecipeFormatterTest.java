package client.utils;

import client.RecipeFormatter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipeFormatterTest {

    @Test
    public void englishLanguageTest() {
        String englishInput1 = "en";
        String englishInput2 = "EN";
        String englishInput3 = " en ";
        assertEquals("English", RecipeFormatter.getFullRecipeLanguage(englishInput1));
        assertEquals("English", RecipeFormatter.getFullRecipeLanguage(englishInput2));
        assertEquals("English", RecipeFormatter.getFullRecipeLanguage(englishInput3));
    }

    @Test
    public void dutchLanguageTest() {
        String dutchInput1 = "nl";
        String dutchInput2 = "NL";
        String dutchInput3 = " nl ";
        assertEquals("Nederlands", RecipeFormatter.getFullRecipeLanguage(dutchInput1));
        assertEquals("Nederlands", RecipeFormatter.getFullRecipeLanguage(dutchInput2));
        assertEquals("Nederlands", RecipeFormatter.getFullRecipeLanguage(dutchInput3));
    }

    @Test
    public void spanishLanguageTest() {
        String spanishInput1 = "es";
        String spanishInput2 = "ES";
        String spanishInput3 = " es ";
        assertEquals("Españiol", RecipeFormatter.getFullRecipeLanguage(spanishInput1));
        assertEquals("Españiol", RecipeFormatter.getFullRecipeLanguage(spanishInput2));
        assertEquals("Españiol", RecipeFormatter.getFullRecipeLanguage(spanishInput3));
    }

    @Test
    public void romanianLanguageTest() {
        String romanianInput1 = "ro";
        String romanianInput2 = "RO";
        String romanianInput3 = " ro ";
        assertEquals("română", RecipeFormatter.getFullRecipeLanguage(romanianInput1));
        assertEquals("română", RecipeFormatter.getFullRecipeLanguage(romanianInput2));
        assertEquals("română", RecipeFormatter.getFullRecipeLanguage(romanianInput3));
    }

    @Test
    public void emptyLanguageTest() {
        String emptyInput1 = "";
        String emptyInput2 = "   ";
        assertEquals("No language found", RecipeFormatter.getFullRecipeLanguage(emptyInput1));
        assertEquals("No language found", RecipeFormatter.getFullRecipeLanguage(emptyInput2));
    }

    @Test
    public void unknownLanguageTest() {
        String unknownInput = "fr";
        assertEquals("No language found", RecipeFormatter.getFullRecipeLanguage(unknownInput));
    }

    @Test
    public void nullLanguageTest() {
        String input = null;

        try {
            RecipeFormatter.getFullRecipeLanguage(input);
        } catch (NullPointerException e) {

        }
    }
}
