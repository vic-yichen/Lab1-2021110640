import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WhiteboxTest {

    private TextToGraph textToGraph;

    @Before
    public void setUp() {
        textToGraph = new TextToGraph();
        textToGraph.readTxt("./2.txt");

    }

    @Test
    public void testNoWordInGraph() {
        String expected = "No \"wsy\" and \"hhh\" in the graph!";
        String actual = textToGraph.queryBridgeWords("wsy", "hhh");
        assertEquals("Expected no word in the graph message", expected, actual);
    }

    @Test
    public void testNoFirstWordInGraph() {
        String expected = "No \"wsy\" in the graph!";
        String actual = textToGraph.queryBridgeWords("wsy", "wyc");
        assertEquals("Expected no first word in the graph message", expected, actual);
    }

    @Test
    public void testNoSecondWordInGraph() {
        String expected = "No \"hhh\" in the graph!";
        String actual = textToGraph.queryBridgeWords("to", "hhh");
        assertEquals("Expected no second word in the graph message", expected, actual);
    }

    @Test
    public void testNoBridgeWords() {
        String expected = "No bridge words from \"explore\" to \"life\"!";
        String actual = textToGraph.queryBridgeWords("explore", "life");
        assertEquals("Expected no bridge words message", expected, actual);
    }

    @Test
    public void testSingleBridgeWord() {
        String expected = "The bridge words from \"new\" to \"and\" is: life.";
        String actual = textToGraph.queryBridgeWords("new", "and");
        assertEquals("Expected single bridge word message", expected, actual);
    }

    @Test
    public void testMultipleBridgeWords() {
        String expected = "The bridge words from \"to\" to \"the\" are: embrace, uncover.";
        String actual = textToGraph.queryBridgeWords("to", "the");
        assertEquals("Expected multiple bridge words message", expected, actual);
    }
}
