package atropos.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

import ibxm.IBXM;
import ibxm.Instrument;
import ibxm.Module;

public class IBXMPlayer {
	
	private IBXM ibxm;
	private Module module;
	private volatile boolean playing;
	private Thread playThread;
	private int samplePos;
	
	private static final int SAMPLE_RATE = 48000;
	
	public IBXMPlayer() {
		System.out.println("IBXM " + IBXM.VERSION);
	}
	
	public void setInterpolation(int interpolation) {
		//if( ibxm != null ) 
			ibxm.setInterpolation( interpolation );
	}
	
	public void loadModule( File modFile ) throws IOException {		
		byte[] moduleData = new byte[ ( int ) modFile.length() ];
		FileInputStream inputStream = new FileInputStream( modFile );
		int offset = 0;
		while( offset < moduleData.length ) {
			int len = inputStream.read( moduleData, offset, moduleData.length - offset );
			if( len < 0 ) throw new IOException( "Unexpected end of file." );
			offset += len;
		}
		inputStream.close();
		module = new Module( moduleData );
		ibxm = new IBXM( module, SAMPLE_RATE );
	}
	
	public void play() {
		if( ibxm != null ) {
			playing = true;
			playThread = new Thread( new Runnable() {
				public void run() {
					int[] mixBuf = new int[ ibxm.getMixBufferLength() ];
					byte[] outBuf = new byte[ mixBuf.length * 4 ];
					AudioFormat audioFormat = null;
					SourceDataLine audioLine = null;
					try {
						audioFormat = new AudioFormat( SAMPLE_RATE, 16, 2, true, true );
						audioLine = AudioSystem.getSourceDataLine( audioFormat );
						audioLine.open();
						audioLine.start();
						while( playing ) {
							int count = ibxm.getAudio(mixBuf);
							int outIdx = 0;
							for( int mixIdx = 0, mixEnd = count * 2; mixIdx < mixEnd; mixIdx++ ) {
								int ampl = mixBuf[ mixIdx ];
								if( ampl > 32767 ) ampl = 32767;
								if( ampl < -32768 ) ampl = -32768;
								outBuf[ outIdx++ ] = ( byte ) ( ampl >> 8 );
								outBuf[ outIdx++ ] = ( byte ) ampl;
							}
							audioLine.write( outBuf, 0, outIdx );
						}
						audioLine.drain();
					} catch( Exception e ) {
						System.out.println(e.getMessage());
					} finally {
						if( audioLine != null && audioLine.isOpen() ) audioLine.close();
					}	
				}
			} );
			playThread.start();
		}
	}
	
	public void stop() {
		playing = false;
		try {
			if( playThread != null ) playThread.join();
		} catch( InterruptedException e ) {
		}
	}
	
}
