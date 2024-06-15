import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BlackboxTest {

    private TextToGraph textToGraph;

    @Before
    public void setUp() {
        textToGraph = new TextToGraph();
        textToGraph.readTxt("./2.txt");

    }

    @Test
    public void testGenerateNewTextWithBridgeWords() {

        String inputText = "to into the secrets of space";
        String expectedOutput = "to venture into the secrets of space";

        assertEquals(expectedOutput, textToGraph.generateNewText(inputText));
    }

    @Test
    public void testGenerateNewTextWithoutBridgeWords() {

        String inputText = "wyc to wonders of space";
        String expectedOutput = "wyc to wonders of space";

        assertEquals(expectedOutput, textToGraph.generateNewText(inputText));
    }

    @Test
    public void testGenerateNewTextWithMultipleBridgeWords() {

        String inputText = "to the of space";
        String generatedText = textToGraph.generateNewText(inputText);

        // 检查生成的新文本是否包含桥接词
        assertTrue(generatedText.equals("to embrace the secrets of space")
                || generatedText.equals("to embrace the wonders of space")
                || generatedText.equals("to uncover the secrets of space")
                || generatedText.equals("to uncover the of wonders space"));
    }

    @Test
    public void testGenerateNewTextWithEmptyInput() {
        TextToGraph textToGraph = new TextToGraph();

        String inputText = "";
        String expectedOutput = "";

        assertEquals(expectedOutput, textToGraph.generateNewText(inputText));
    }

    @Test
    public void testGenerateNewTextWithSingleWordInput() {
        String inputText = "the";
        String expectedOutput = "the";

        assertEquals(expectedOutput, textToGraph.generateNewText(inputText));
    }

    @Test
    public void testGenerateNewTextWithNonCharacters() {

        String inputText = "happyhappyhappy to you";
        String expectedOutput = "happyhappyhappy to you";

        assertEquals(expectedOutput, textToGraph.generateNewText(inputText));
    }

    @Test
    public void testGenerateNewTextWithMixedCaseInput() {
        String inputText = "to into the of happyhappy world";
        String generatedText = textToGraph.generateNewText(inputText);

        assertTrue(generatedText.equals("to venture into the wonders of happyhappy world")
                || generatedText.equals("to venture into the secrets of happyhappy world"));
    }
}
