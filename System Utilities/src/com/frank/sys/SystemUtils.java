/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * SystemUtils.java is built in 2013-2-14.
 */
package com.frank.sys;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * The system utilities.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.1
 */
public class SystemUtils
{
	/**
	 * The default buffer size.
	 * <p>
	 * 8192 is always used in all kinds of IO affairs.
	 * </p>
	 */
	public static final int	BUFFER_SIZE	= 8192;

	/**
	 * Returns the texts in the system clip board.
	 * 
	 * @return clip board text
	 */
	public static String getSysClipboardText()
	{
		String ret = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipTf = sysClip.getContents(null);
		if (clipTf != null)
			// Check if the data in the clip board is text
			if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor))
				try
				{
					ret = (String) clipTf
							.getTransferData(DataFlavor.stringFlavor);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		return ret;
	}

	/**
	 * Write the specified text to system clip board.
	 * 
	 * @param writeMe
	 *            the specified text to write
	 */
	public static void setSysClipboardText(String writeMe)
	{
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(writeMe);
		clip.setContents(tText, null);
	}

	/**
	 * Get image form system clip board.
	 * 
	 * @return the image
	 * @throws Exception
	 */
	public static Image getImageFromClipboard() throws Exception
	{
		Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = sysc.getContents(null);
		if (cc == null)
			return null;
		else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor))
			return (Image) cc.getTransferData(DataFlavor.imageFlavor);
		return null;
	}

	/**
	 * Write the specified image to system clip board.
	 * 
	 * @param image
	 *            the specified image to write
	 */
	public static void setClipboardImage(final Image image)
	{
		Transferable trans = new Transferable()
		{
			public DataFlavor[] getTransferDataFlavors()
			{
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException
			{
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}
		};
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(trans, null);
	}

	/**
	 * Load all the files accepted by the specified file filter under the
	 * specified directory into the specified <code>File</code> collection.
	 * 
	 * @param files
	 *            the specified <code>File</code> collection
	 * @param file
	 *            the specified file or directory
	 * @param filter
	 *            the specified file filter, if <tt>null</tt> than accept all
	 *            the file types.
	 */
	public static void loadFiles(Collection<File> files, File file,
			FileFilter filter)
	{
		if (file.isDirectory())
		{
			File[] list = file.listFiles(filter);
			for (File f : list)
				loadFiles(files, f, filter);
		}
		else if (filter == null || filter.accept(file))
			files.add(file);
	}

	/**
	 * Read the specified text input stream and fill the content to the
	 * specified appendable.
	 * <p>
	 * The input stream will be read to the end and will be closed after read.
	 * </p>
	 * 
	 * @param in
	 *            the input stream to read
	 * @param cs
	 *            the character set of the input stream, <code>null</code> if
	 *            use environment character set
	 * @param appendable
	 *            the appendable to append
	 * @param capacity
	 *            the capacity of the buffer
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static void read(InputStream in, Charset cs, Appendable appendable,
			int capacity) throws IOException
	{
		InputStreamReader isr = new InputStreamReader(in,
				cs == null ? Charset.defaultCharset() : cs);
		CharBuffer cbuf = CharBuffer.allocate(capacity);
		int r = 0;
		while (r > -1)
		{
			cbuf.clear();
			appendable.append(cbuf, 0, r);
			r = isr.read(cbuf);
		}
		isr.close();
	}

	/**
	 * Read the specified file and fill the content to the specified appendable.
	 * 
	 * @param file
	 *            the file to read
	 * @param cs
	 *            the character set of the input stream, <code>null</code> if
	 *            use environment character set
	 * @param appendable
	 *            the appendable to append
	 * @param capacity
	 *            the capacity of the buffer
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static void read(File file, Charset cs, Appendable appendable,
			int capacity) throws IOException
	{
		read(new java.io.FileInputStream(file), cs, appendable, capacity);
	}

	/**
	 * Read the specified file and fill the content to the specified appendable.
	 * 
	 * @param filename
	 *            the name of the file to read
	 * @param cs
	 *            the character set of the input stream, <code>null</code> if
	 *            use environment character set
	 * @param appendable
	 *            the appendable to append
	 * @param capacity
	 *            the capacity of the buffer
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static void read(String filename, Charset cs, Appendable appendable,
			int capacity) throws IOException
	{
		read(new java.io.FileInputStream(filename), cs, appendable, capacity);
	}

	/**
	 * Read the specified file and returns the file content.
	 * 
	 * @param filename
	 *            the name of the file to read
	 * @param cs
	 *            the character set of the input stream, <code>null</code> if
	 *            use environment character set
	 * @param appendable
	 *            the appendable to append
	 * @param capacity
	 *            the capacity of the buffer
	 * @return the file content
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static String read(String filename, Charset cs, int capacity)
			throws IOException
	{
		StringBuilder sb = new StringBuilder();
		read(filename, cs, sb, capacity);
		return sb.toString();
	}

	/**
	 * Read the specified file and returns the file content.
	 * 
	 * @param file
	 *            the file to read
	 * @param cs
	 *            the character set of the input stream, <code>null</code> if
	 *            use environment character set
	 * @param capacity
	 *            the capacity of the buffer
	 * @return the file content
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static String read(File file, Charset cs, int capacity)
			throws IOException
	{
		StringBuilder sb = new StringBuilder();
		read(file, cs, sb, capacity);
		return sb.toString();
	}

	/**
	 * Read and returns the content of the input stream.
	 * <p>
	 * The input stream will be read to the end and will be closed after read.
	 * </p>
	 * 
	 * @param in
	 *            the input stream to read
	 * @param cs
	 *            the character set of the input stream, <code>null</code> if
	 *            use environment character set
	 * @param capacity
	 *            the capacity of the buffer
	 * @return the file content
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static String readString(InputStream in, Charset cs,
			Appendable appendable, int capacity) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		read(in, cs, appendable, capacity);
		return sb.toString();
	}

	/**
	 * Write one object to the specified file.
	 * 
	 * @param file
	 *            the specified file
	 * @param obj
	 *            the object to write
	 * @throws IOException
	 *             if IO error occurs
	 */
	public static void writeObject(File file, Object obj) throws IOException
	{
		PathUtils.createFileIfNotExist(file);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				file));
		oos.writeObject(obj);
		oos.close();
	}

	/**
	 * Read an object from the specified file.
	 * 
	 * @param file
	 *            the specified to read
	 * @return the read object
	 * @throws IOException
	 *             if IO error occurs
	 * @throws ClassNotFoundException
	 *             if the class is not found
	 */
	public static Object readObject(File file) throws IOException,
			ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}
}
