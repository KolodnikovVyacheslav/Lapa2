import Evaluator.Evaluator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @Test
    void testArithmetic() {
        Evaluator evaluator = new Evaluator();
        assertEquals(10.0, evaluator.evaluate("2 + 3 * 4 - 4"), 1e-6);
    }

    @Test
    void testBrackets() {
        Evaluator evaluator = new Evaluator();
        assertEquals(20.0, evaluator.evaluate("(2 + 3) * 4"), 1e-6);
    }

    @Test
    void testFunctions() {
        Evaluator evaluator = new Evaluator();
        assertEquals(0.0, evaluator.evaluate("sin(0)"), 1e-6);
        assertEquals(1.0, evaluator.evaluate("cos(0)"), 1e-6);
        assertEquals(5.0, evaluator.evaluate("sqrt(25)"), 1e-6);
    }

    @Test
    void testInvalidSyntax() {
        Evaluator evaluator = new Evaluator();
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("2 + * 3"));
    }

    @Test
    void testDivisionByZero() {
        Evaluator evaluator = new Evaluator();
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("4 / 0"));
    }

    @Test
    void testVariable() {
        Evaluator evaluator = new Evaluator();
        double result = evaluator.evaluate("3 * 2 + 1");
        assertEquals(7.0, result, 1e-6);
    }
}
