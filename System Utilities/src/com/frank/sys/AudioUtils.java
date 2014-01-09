/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * AudioUtils.java is built in 2013-2-14.
 */
package com.frank.sys;

import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * Audio utilities.<br>
 * It deals with the audio works, recalling Java<sup>TM</sup> sound interfaces
 * to play audio data.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class AudioUtils
{
	/**
	 * Play the specified audio synchronously.
	 * 
	 * @param audioFile
	 *            The file of specified audio.
	 */
	public static void playSynchronized(final File audioFile)
	{
		try
		{
			playSynchronized(AudioSystem.getAudioInputStream(audioFile));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Play the specified audio input stream synchronously.
	 * 
	 * @param ais
	 *            The specified audio input stream.
	 */
	public static void playSynchronized(InputStream ais)
	{
		try
		{
			playSynchronized(AudioSystem.getAudioInputStream(ais));
		}
		catch (Exception e)
		{
			
		}
	}

	/**
	 * Play the specified audio input stream synchronously.
	 * 
	 * @param ais
	 *            The specified audio input stream.
	 */
	public static void playSynchronized(final AudioInputStream ais)
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					AudioFormat format = ais.getFormat();
					int bufferSize = format.getFrameSize()
							* Math.round(format.getFrameRate() / 10);
					byte[] buffer = new byte[bufferSize];
					SourceDataLine line;
					DataLine.Info info = new DataLine.Info(
							SourceDataLine.class, format);
					line = (SourceDataLine) AudioSystem.getLine(info);
					line.open(format, bufferSize);
					// start the line
					line.start();
					// copy data to the line
					int numBytesRead = 0;
					while (numBytesRead != -1)
					{
						numBytesRead = ais.read(buffer, 0, buffer.length);
						if (numBytesRead != -1)
							line.write(buffer, 0, numBytesRead);
					}
					// wait until all data is played, then close the line
					line.drain();
					line.close();
				}
				catch (Throwable t)
				{
				}
			}
		}.start();
	}

	/**
	 * Play the specified audio input stream serially.
	 * 
	 * @param ais
	 *            The specified audio input stream.
	 */
	public static void play(AudioInputStream ais)
	{
		try
		{
			AudioFormat format = ais.getFormat();
			int bufferSize = format.getFrameSize()
					* Math.round(format.getFrameRate() / 10);
			byte[] buffer = new byte[bufferSize];
			SourceDataLine line;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, bufferSize);
			// start the line
			line.start();
			// copy data to the line
			int numBytesRead = 0;
			while (numBytesRead != -1)
			{
				numBytesRead = ais.read(buffer, 0, buffer.length);
				if (numBytesRead != -1)
					line.write(buffer, 0, numBytesRead);
			}
			// wait until all data is played, then close the line
			line.drain();
			line.close();
		}
		catch (Throwable t)
		{
		}
	}

	/**
	 * Play the specified audio file serially.
	 * 
	 * @param audioFile
	 *            The file of specified audio.
	 */
	public static void play(File audioFile)
	{
		try
		{
			play(AudioSystem.getAudioInputStream(audioFile));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
