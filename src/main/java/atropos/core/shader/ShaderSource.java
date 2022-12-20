package atropos.core.shader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLOutput;


public class ShaderSource {

	private String[] shaderSource = null;
	
	private static String readAll2Array(File file) throws java.io.IOException{
	    byte[] buffer = new byte[(int) file.length()];
	    FileInputStream f = new FileInputStream(file);
	    f.read(buffer);
	    return new String(buffer);
	}
	
	public ShaderSource(File file) {
		System.out.println(file.getName());
		try {
			shaderSource = (new String(
					readAll2Array(file)
					)).split("\n");

			for( String line: shaderSource)
				System.out.println(line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i=0; i < shaderSource.length; i++) {
			shaderSource[i] += "\n";
		}
	}
	
	public ShaderSource(String source) {
		shaderSource = source.split("\n");
		
		for(int i=0; i < shaderSource.length; i++) {
			shaderSource[i] += "\n";
		}
	}
	
	public ShaderSource(String[] source) {
		shaderSource = source.clone();
		
		for(int i=0; i < shaderSource.length; i++) {
			shaderSource[i] += "\n";
		}
	}
	
	public ShaderSource(byte[] source) {
		shaderSource = new String(source).split("\n");
		
		for(int i=0; i < shaderSource.length; i++) {
			shaderSource[i] += "\n";
		}
	}
	
	public String[] getShaderSource() {
		return shaderSource;
	}
	
}
