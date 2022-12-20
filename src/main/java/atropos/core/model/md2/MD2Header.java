package atropos.core.model.md2;


/**
 * This is the structure of an MD2 Header file
 * @author trigger
 *
 */
public class MD2Header {
	
	public int ident;				// magic number: "IDP2"
	public int version;				// version: 8
	public int skinWidth;			// texture width
	public int skinHeight;			// texture height
	public int frameSize;			// size in bytes of a frame
	public int numSkins;			// number of skins
	public int numVertices;			// number of vertices per frame
	public int numTexCoords;		// number of texture coordinates
	public int numTriangles;		// number of triangles
	public int numGLCommands;		// number of opengl commands
	public int numFrames;			// number of frames
	public int offsetSkins;			// offset skin data
	public int offsetTexCoords;		// offset texture coordinate data
	public int offsetTriangles;		// offset triangle data
	public int offsetFrames;		// offset frame data
	public int offsetGLCommands;	// offset opengl command data
	public int offsetEnd;			// offset end of file
	
	@Override
	public String toString() {
		return super.toString() + "[ident=" + this.ident +
		                          ", version=" + this.version + 
		                          ", skinWidth=" + this.skinWidth +
		                          ", skinHeight=" + this.skinHeight +
		                          ", frameSize=" + this.frameSize +
		                          ", numSkins=" + this.numSkins +
		                          ", numVertices=" + this.numVertices +
		                          ", numTexCoords=" + this.numTexCoords +
		                          ", numTriangles=" + this.numTriangles +
		                          ", numGLCommands=" + this.numGLCommands +
		                          ", numFrames=" + this.numFrames +
		                          ", offsetSkins=" + this.offsetSkins +
		                          ", offsetTexCoords=" + this.offsetTexCoords +
		                          ", offsetTriangles=" + this.offsetTriangles +
		                          ", offsetFrames=" + this.offsetFrames +
		                          ", offsetGLCommands=" + this.offsetGLCommands +
		                          ", offsetEnd=" + this.offsetEnd +
		                          "]";
	}
}
