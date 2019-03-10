package name.kinopie.common.graph;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

public class GraphHelperTest {

	private GraphHelper graphHelper;
	private MutableGraph<String> graph;
	private List<String> nodes;

	@Before
	public void setUp() throws Exception {
		graphHelper = new GraphHelper();
		graph = GraphBuilder.directed().build();
		nodes = Arrays.asList("A", "B", "C");
		graphHelper.fill(graph, nodes);
	}

	@After
	public void tearDown() throws Exception {
		graphHelper = null;
		graph = null;
		nodes = null;
	}

	@Test
	public void testIsConnected_1() {
		Assert.assertTrue(graphHelper.isConnected(graph, "C", "D"));
	}

	@Test
	public void testGetConnectedNodes_1() {
		List<String> expected = Arrays.asList("C");
		List<String> actual = graphHelper.getConnectedNodes(graph, "C", "D");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_2() {
		Assert.assertTrue(graphHelper.isConnected(graph, "B", "C", "D"));
	}

	@Test
	public void testGetConnectedNodes_2() {
		List<String> expected = Arrays.asList("B", "C");
		List<String> actual = graphHelper.getConnectedNodes(graph, "B", "C", "D");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_3() {
		Assert.assertTrue(graphHelper.isConnected(graph, "A", "B", "C"));
	}

	@Test
	public void testGetConnectedNodes_3() {
		List<String> expected = Arrays.asList("A", "B", "C");
		List<String> actual = graphHelper.getConnectedNodes(graph, "A", "B", "C");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_4() {
		Assert.assertFalse(graphHelper.isConnected(graph, "E", "C", "D"));
	}

	@Test
	public void testGetConnectedNodes_4() {
		List<String> expected = Arrays.asList();
		List<String> actual = graphHelper.getConnectedNodes(graph, "E", "C", "D");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_5() {
		Assert.assertFalse(graphHelper.isConnected(graph));
	}

	@Test
	public void testGetConnectedNodes_5() {
		List<String> expected = Arrays.asList();
		List<String> actual = graphHelper.getConnectedNodes(graph);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_6() {
		Assert.assertFalse(graphHelper.isConnected(graph, "A"));
	}

	@Test
	public void testGetConnectedNodes_6() {
		List<String> expected = Arrays.asList();
		List<String> actual = graphHelper.getConnectedNodes(graph, "A");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_7() {
		Assert.assertFalse(graphHelper.isConnected(graph, "B"));
	}

	@Test
	public void testGetConnectedNodes_7() {
		List<String> expected = Arrays.asList();
		List<String> actual = graphHelper.getConnectedNodes(graph, "B");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_8() {
		Assert.assertFalse(graphHelper.isConnected(graph, "A", "B"));
	}

	@Test
	public void testGetConnectedNodes_8() {
		List<String> expected = Arrays.asList();
		List<String> actual = graphHelper.getConnectedNodes(graph, "A", "B");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIsConnected_9() {
		Assert.assertTrue(graphHelper.isConnected(graph, "B", "C"));
	}

	@Test
	public void testGetConnectedNodes_9() {
		List<String> expected = Arrays.asList("B", "C");
		List<String> actual = graphHelper.getConnectedNodes(graph, "B", "C");
		Assert.assertEquals(expected, actual);
	}
}
