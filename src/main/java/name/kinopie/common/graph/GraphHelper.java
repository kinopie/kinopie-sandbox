package name.kinopie.common.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.graph.Graph;
import com.google.common.graph.MutableGraph;

public class GraphHelper {

	public <N> List<N> getConnectedNodes(Graph<N> graph, @SuppressWarnings("unchecked") N... nodes) {
		List<N> nodeList = Arrays.asList(nodes);
		return getConnectedNodes(graph, nodeList);
	}

	public <N> List<N> getConnectedNodes(Graph<N> graph, List<N> nodeList) {
		return getConnectedNodes(graph, nodeList, new ArrayList<N>());
	}

	private <N> List<N> getConnectedNodes(Graph<N> graph, List<N> nodeList, List<N> connectedNodes) {
		if (nodeList.isEmpty()) {
			return Collections.emptyList();
		}
		N node = nodeList.get(0);
		if (graph.nodes().contains(node)) {
			Set<N> successors = graph.successors(node);
			if (successors.isEmpty()) {
				connectedNodes.add(node);
				return connectedNodes;
			}
			List<N> subList = nodeList.subList(1, nodeList.size());
			if (subList.isEmpty()) {
				return Collections.emptyList();
			}
			connectedNodes.add(node);
			return getConnectedNodes(graph, subList, connectedNodes);
		}
		return Collections.emptyList();
	}

	public <N> boolean isConnected(Graph<N> graph, @SuppressWarnings("unchecked") N... nodes) {
		List<N> nodeList = Arrays.asList(nodes);
		return isConnected(graph, nodeList);
	}

	public <N> boolean isConnected(Graph<N> graph, List<N> nodeList) {
		if (nodeList.isEmpty()) {
			return false;
		}
		N node = nodeList.get(0);
		if (graph.nodes().contains(node)) {
			Set<N> successors = graph.successors(node);
			if (successors.isEmpty()) {
				return true;
			}
			List<N> subList = nodeList.subList(1, nodeList.size());
			if (subList.isEmpty()) {
				return false;
			}
			return isConnected(graph, subList);
		}
		return false;
	}

	public <N> void fill(MutableGraph<N> mutableGraph, @SuppressWarnings("unchecked") N... nodes) {
		List<N> nodeList = Arrays.asList(nodes);
		fill(mutableGraph, nodeList);
	}

	public <N> void fill(MutableGraph<N> mutableGraph, List<N> nodeList) {
		N prev = null;
		for (N node : nodeList) {
			mutableGraph.addNode(node);
			if (prev != null) {
				mutableGraph.putEdge(prev, node);
			}
			prev = node;
		}
	}
}
