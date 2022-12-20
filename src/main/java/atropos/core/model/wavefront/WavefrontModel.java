package atropos.core.model.wavefront;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.core.DisplayList;
import atropos.core.math.Vector3f;

public class WavefrontModel {
	
	ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	ArrayList<Vector3f> texCoords = new ArrayList<Vector3f>();
	ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	
	HashMap<String, Material> materialMapping = new HashMap<String, Material>();
	HashMap<String, MaterialGroup> materialGroupMapping = new HashMap<String, MaterialGroup>();
	
	MaterialGroup currentMaterialGroup;
	Material currentMaterial;
	String currentGroup;
	
	DisplayList list;
	
	public WavefrontModel(GL2 gl,String fileName) {
		try {
			File file = new File(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			// new begin
			MaterialGroup materialGroup  = materialGroupMapping.get("(null)");
			
			if(materialGroup == null) {
				Material material = new Material();

				material.setAmbient(0.4f, 0.4f, 0.4f);
				material.setDiffuse(0.75f, 0.9f, 0.7f);
				material.setSpecular(0.1f, 0.1f, 0.1f);
				material.setShininess(1.0f);
				
				materialGroup = new MaterialGroup(material);
				
				materialGroup.setVertices(vertices);
				materialGroup.setTexCoords(texCoords);
				materialGroup.setNormals(normals);
				materialGroupMapping.put("(null)", materialGroup);
				
				
			}
			currentMaterialGroup = materialGroup;
			
			//new end
			
			while(reader.ready()) {
				String line = reader.readLine();
				
				// on some systems the line is null when the end
				// of the file is reached.
				if(line == null) break;
				
				
				
				
				
				if(line.startsWith("v ")) {
					Vector3f vertex = parseVertex(line);
					vertices.add(vertex);
				} else if(line.startsWith("vt")) {
					Vector3f texCoord = parseVertexTexture(line);
					texCoords.add(texCoord);
				} else if(line.startsWith("vn")) {
					Vector3f normal = parseVertexNormal(line);
					normals.add(normal);
				} else if(line.startsWith("f")) {
//					if(currentGroup!=null && currentGroup.equals("hood_swing") || currentGroup.equals("door_ext_s")
//							|| currentGroup.equals("door_int_s")|| currentGroup.equals("door_g_swi"))
//						continue;
					

					
					Face face = parseFace(line);
					
				
					
					currentMaterialGroup.add(face);
				} else if(line.startsWith("mtllib")) {
					parseMaterialLibrary(gl, line, file);
					System.out.println("mtllib");
				} else if(line.startsWith("usemtl")) {
					parseUseMaterial(line);
					
				} else if(line.startsWith("g")) {
					StringTokenizer tokenizer = new StringTokenizer(line, " ");
					tokenizer.nextToken();
					currentGroup = tokenizer.nextToken();
				} else if(line.startsWith("s")) {
					
				}
 				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		System.out.println(materialMapping.size());
	}
	
	public void draw(GL2 gl) {

		list.draw(gl);
	}
	
	public void construct(GL2 gl) {
			list = new DisplayList(gl);
			list.begin(gl);

			System.out.println("draw");
			Set<String> materialGroupKeySet = materialGroupMapping.keySet();
			
			for(String key : materialGroupKeySet) {
				MaterialGroup materialGroup = materialGroupMapping.get(key);
				System.out.println("key: "+key);
				materialGroup.draw(gl);
			}
			
		list.end(gl);
	}
	
	public void parseUseMaterial(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "v" token from the vertex definition
		tokenizer.nextToken();
		
		String materialName = tokenizer.nextToken();
		
		MaterialGroup materialGroup = materialGroupMapping.get(materialName);
		
		if(materialGroup == null) {
			materialGroup = new MaterialGroup(materialMapping.get(materialName));
			materialGroup.setVertices(vertices);
			materialGroup.setTexCoords(texCoords);
			materialGroup.setNormals(normals);
			materialGroupMapping.put(materialName, materialGroup);
		}
		
		currentMaterialGroup = materialGroup;
	}
	
	public Vector3f parseVertex(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "v" token from the vertex definition
		tokenizer.nextToken();
		
		int componentCount = tokenizer.countTokens();
		
		// check wether three components follow after
		// the "v" command
		if(componentCount != 3) {
			System.err.println("Only three dimensional vertices are supported.");
		}
		
		// parse the components
		float x = Float.parseFloat(tokenizer.nextToken());
		float y = Float.parseFloat(tokenizer.nextToken());
		float z = Float.parseFloat(tokenizer.nextToken());
		
		// create a new Vector3f to store the point
		Vector3f vertex = new Vector3f(x, y, z);
		return vertex;	
	}
	
	// TODO: support "vt u v [w]"
	public Vector3f parseVertexTexture(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "vt" token from the texture coordinate definition
		tokenizer.nextToken();
		
		int componentCount = tokenizer.countTokens();
		
		// check wether two components follow after
		// the "vt" command
		if(componentCount != 2) {
			//System.err.println("Only two dimensional texture coordinates are supported.");
		}
		
		// parse the components
		float s = Float.parseFloat(tokenizer.nextToken());
		float t = Float.parseFloat(tokenizer.nextToken());
		
		// create a new Vector3f to store the texture coordinate
		Vector3f textureCoord = new Vector3f(s, t, 0.0f);
		return textureCoord;	
	}
	
	public Vector3f parseVertexNormal(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "vn" token from the vertex normal definition
		tokenizer.nextToken();
		
		int componentCount = tokenizer.countTokens();
		
		// check wether three components follow after
		// the "vn" command
		if(componentCount != 3) {
			System.err.println("Only three dimensional normals are supported.");
		}
		
		// parse the components
		float x = Float.parseFloat(tokenizer.nextToken());
		float y = Float.parseFloat(tokenizer.nextToken());
		float z = Float.parseFloat(tokenizer.nextToken());
		
		// create a new Vector3f to store the normal
		Vector3f normal = new Vector3f(x, y, z);
		return normal;	
	}
	
	public Face parseFace(String line) {
		StringTokenizer faceVertices = new StringTokenizer(line, " ");
		//System.out.println(line);
		// remove the "f" token from the face definition
		faceVertices.nextToken();
		
		int faceCount = faceVertices.countTokens();
		
		// check wether three components follow after
		// the "f" command. currently we only support triangles.
		if(faceCount != 3) {
			System.err.println("Only triangles are supported.");
			return null;
		}
		
		// create a new face and fill it
		Face face = new Face();
		
		for(int i=0; i < faceCount; i++) {
			String point= faceVertices.nextToken();
			//StringTokenizer parts = new StringTokenizer(faceVertices.nextToken(), "/");

			//int partCount = parts.countTokens();
			//System.out.println("num tokens" + partCount);
			
			//TODO: f 8//9
			// wird nicht erkannt sondern nur "8" und "9" also 2 tokens
			
			String[] parts = point.split("/");
		
//			int v = Integer.parseInt(parts[0]);
//			int t = Integer.parseInt(parts[1]);
//			int n = Integer.parseInt(parts[2]);

			
			Vector3f vertex = null;
			if(!parts[0].equals("")) {
				int v = Integer.parseInt(parts[0]);
				vertex = vertices.get(v - 1);
			}
			
			
			Vector3f texCoord = null;
			if(!parts[1].equals("")) {
				int t = Integer.parseInt(parts[1]);
				texCoord = texCoords.get(t - 1);
			}
			
			
			int n = Integer.parseInt(parts[2]);
			
			
			
			
			
			
			Vector3f normal = normals.get(n - 1);
			
			FaceVertex faceVertex = new FaceVertex(vertex, texCoord, normal);
			face.add(faceVertex);
		}
		return face;
	}
	
	public void parseMaterialLibrary(GL2 gl, String line2, File file) {
		StringTokenizer tokenizer = new StringTokenizer(line2, " ");
		
		// remove the "mtllib" token
		tokenizer.nextToken();
		System.out.println("trigger" + file);
		int componentCount = tokenizer.countTokens();
		
		// parse the components
		String mtlLibName = tokenizer.nextToken();

		String fileName = file.getParent() + File.separator + mtlLibName;

		File mtlFile = new File(fileName);
		
		System.out.println(fileName);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mtlFile)));
			
			while(reader.ready()) {
				String line = reader.readLine().trim();
				//System.out.println(line);
				// on some systems the line is null when the end
				// of the file is reached.
				if(line == null) break;
				
				if(line.startsWith("newmtl")) {
					StringTokenizer tok = new StringTokenizer(line, " ");
					System.out.println(line);
					// remove the "newmtl" token
					tok.nextToken();
					
					String materialName = tok.nextToken();
					//if(activeMaterial!= null) return;
					currentMaterial = new Material();
					materialMapping.put(materialName, currentMaterial);
				} else if(line.startsWith("Ka")) {
					System.out.println(line);
					parseAmbientColor(line);
				} else if(line.startsWith("Kd")) {
					parseDiffuseColor(line);
				} else if(line.startsWith("Ks")) {
					parseSpecularColor(line);
				} else if(line.startsWith("d")) {
					// transparency
				} else if(line.startsWith("Tr")) {
					// transparency
				} else if(line.startsWith("Ns")) {
					parseShininess(line);
				} else if(line.startsWith("illum")) {
					// later
				} else if(line.startsWith("map_Ka")) {
					parseTextureMap(gl,line, file);
					System.out.println(line);
				} 
				else if(line.startsWith("map_Kd")) {
					parseTextureMap(gl,line, file);
					System.out.println("parse tex");
				}
 				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void parseAmbientColor(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "Ka" token
		tokenizer.nextToken();
		
		float r = Float.parseFloat(tokenizer.nextToken());
		float g = Float.parseFloat(tokenizer.nextToken());
		float b = Float.parseFloat(tokenizer.nextToken());
		
		currentMaterial.setAmbient(r, g, b);
	}
	
	public void parseDiffuseColor(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "Ka" token
		tokenizer.nextToken();
		
		float r = Float.parseFloat(tokenizer.nextToken());
		float g = Float.parseFloat(tokenizer.nextToken());
		float b = Float.parseFloat(tokenizer.nextToken());
		
		currentMaterial.setDiffuse(r, g, b);
	}
	
	public void parseSpecularColor(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "Ka" token
		tokenizer.nextToken();
		
		float r = Float.parseFloat(tokenizer.nextToken());
		float g = Float.parseFloat(tokenizer.nextToken());
		float b = Float.parseFloat(tokenizer.nextToken());
		
		currentMaterial.setSpecular(r, g, b);
	}
	
	public void parseShininess(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove the "Ka" token
		tokenizer.nextToken();
		
		float s = Float.parseFloat(tokenizer.nextToken());
		
		currentMaterial.setShininess(s);
	}
	
	public void parseTextureMap(GL2 gl,String line, File file) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// remove 
		tokenizer.nextToken();
		
		if(tokenizer.countTokens() == 0)
			return;
			
		String textureName = tokenizer.nextToken();
		
		String fileName = file.getParent() + File.separator + textureName;

		Texture texture = load(gl, fileName);
		currentMaterial.setTexture(texture);
	}
	
	 Texture load (GL2 gl,String filename)
	   {
		 System.out.println(filename);
	      Texture texture = null;

	      try
	      {
	          // Create an OpenGL texture from the specified file. Do not create
	          // mipmaps.

	          texture = TextureIO.newTexture (new File (filename), false);

	          // Use the NEAREST magnification function when the pixel being
	          // textured maps to an area less than or equal to one texture
	          // element (texel).

	          texture.setTexParameteri (gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

	          // Use the NEAREST minification function when the pixel being
	          // textured maps to an area greater than one texel.

	          texture.setTexParameteri (gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	          texture.setTexParameterf(gl,GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
	          texture.setTexParameterf(gl,GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
	          //texture.setTexParameteri(GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
	          
	          
	      }
	      catch (Exception e)
	      {
	          System.out.println ("error loading texture from "+filename);
	      }

	      return texture;
	   }

}
