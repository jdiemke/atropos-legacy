package atropos.core.model.md2;

import java.util.Vector;

import atropos.core.math.Vector3f;

public class MD2Frame {
	
	public float scale;
	public float translate;
	public String name;
	
	public Vector<MD2Vertex> vertices = new Vector<MD2Vertex>();

}
