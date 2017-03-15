package model;

import java.util.HashMap;

public class Graph {
	private Vertex[] vertices;
	private DescriptorMetaData descriptorMetaData = null;
	private HashMap<String, Integer> indicesOfVertices;

	public Graph(int numberOfVertices) {
		vertices = new Vertex[numberOfVertices];
		indicesOfVertices = new HashMap<>();
	}

	public void setDescriptorMetaData(DescriptorMetaData descriptorMetaData) {
		this.descriptorMetaData = descriptorMetaData;
	}

	public DescriptorMetaData getDescriptorsMetaData() {
		return descriptorMetaData;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}

	public HashMap<String, Integer> getIndicesOfVertices() {
		return indicesOfVertices;
	}

	public void setIndicesToVertices() {
		for (int i = 0; i < vertices.length; i++) {
			indicesOfVertices.put(vertices[i].getId(), i);
			vertices[i].setIndexInGraph(i);
		}
	}
}
