package atropos.core.model.md2;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {

	private DataInputStream dataInputStream = null;
	
	public LittleEndianDataInputStream(InputStream in) {
		super(in);
		dataInputStream = new DataInputStream(in);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return dataInputStream.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readByte();
	}

	@Override
	public char readChar() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readChar();
	}

	@Override
	public double readDouble() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readDouble();
	}

	@Override
	public float readFloat() throws IOException {
		// TODO Auto-generated method stub
		//return dataInputStream.readFloat();
		 return Float.intBitsToFloat(readInt());
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		dataInputStream.readFully(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		dataInputStream.read(b, off, len);
	}

	@Override
	public int readInt() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();

		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();

		return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
	}

	@Override
	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readLine();
	}

	@Override
	public long readLong() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readLong();
	}

	@Override
	public short readShort() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();

		if ((ch1 | ch2) < 0)
			throw new EOFException();

		return (short)((ch2 << 8) + (ch1 << 0));
	}

	@Override
	public String readUTF() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readUTF();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readUnsignedByte();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.readUnsignedShort();
	}

	@Override
	public int skipBytes(int n) throws IOException {
		// TODO Auto-generated method stub
		return dataInputStream.skipBytes(n);
	}



}
