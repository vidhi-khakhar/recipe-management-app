package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    @Test
    void testUnitEnumValues() {
        Unit[] values = Unit.values();
        assertEquals(7, values.length);
        assertNotNull(Unit.valueOf("G"));
        assertNotNull(Unit.valueOf("KG"));
        assertNotNull(Unit.valueOf("ML"));
        assertNotNull(Unit.valueOf("L"));
        assertNotNull(Unit.valueOf("TSP"));
        assertNotNull(Unit.valueOf("TBSP"));
        assertNotNull(Unit.valueOf("PCS"));
    }

    @Test
    void testUnitName() {
        assertEquals("G", Unit.G.name());
        assertEquals("KG", Unit.KG.name());
        assertEquals("ML", Unit.ML.name());
        assertEquals("L", Unit.L.name());
        assertEquals("TSP", Unit.TSP.name());
        assertEquals("TBSP", Unit.TBSP.name());
        assertEquals("PCS", Unit.PCS.name());
    }

    @Test
    void testUnitOrdinal() {
        assertEquals(0, Unit.G.ordinal());
        assertEquals(1, Unit.KG.ordinal());
        assertEquals(2, Unit.ML.ordinal());
        assertEquals(3, Unit.L.ordinal());
        assertEquals(4, Unit.TSP.ordinal());
        assertEquals(5, Unit.TBSP.ordinal());
        assertEquals(6, Unit.PCS.ordinal());
    }

    @Test
    void testUnitValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Unit.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> Unit.valueOf("g"));
        assertThrows(IllegalArgumentException.class, () -> Unit.valueOf(""));
    }

    @Test
    void testUnitValueOfNull() {
        assertThrows(NullPointerException.class, () -> Unit.valueOf(null));
    }

    @Test
    void testUnitCompareTo() {
        assertTrue(Unit.G.compareTo(Unit.KG) < 0);
        assertTrue(Unit.KG.compareTo(Unit.G) > 0);
        assertEquals(0, Unit.G.compareTo(Unit.G));
    }
}
