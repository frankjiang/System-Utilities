/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * SystemUtils.java is built in 2013-2-14.
 */
package com.frank.sys;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

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
	public static final int		BUFFER_SIZE	= 8192;
	/**
	 * The header of BASE64 image.
	 */
	public static final String	BASE64IMAGE	= "base64image:";

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
					ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
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
	@SuppressWarnings("restriction")
	public static Image getImageFromClipboard() throws Exception
	{
		String osname = System.getProperties().getProperty("os.name").toLowerCase();
		if (osname.startsWith("mac"))
			;
		Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = sysc.getContents(null);
		if (cc == null)
			return null;
		else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor))
			return (Image) cc.getTransferData(DataFlavor.imageFlavor);
		else if (cc.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor()))
		{
			String code = (String) cc.getTransferData(DataFlavor.getTextPlainUnicodeFlavor());
			if (code.startsWith("base64image:"))
			{
				code = code.substring(BASE64IMAGE.length(), code.length());
				sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
				byte[] bytes = decoder.decodeBuffer(code);
				return ImageIO.read(new ByteArrayInputStream(bytes));
			}
		}
		return null;
	}

	/**
	 * Translate the {@link java.awt.Image Image} to
	 * {@link java.awt.image.BufferedImage BufferedImage}.
	 * 
	 * @param image
	 *            the {@link java.awt.Image Image} instance.
	 * @return {@link java.awt.image.BufferedImage BufferedImage} instance
	 */
	public static BufferedImage toBufferedImage(Image image)
	{
		if (image instanceof BufferedImage)
			return (BufferedImage) image;
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent Pixels
		// boolean hasAlpha = hasAlpha(image);
		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try
		{
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			/*
			 * if (hasAlpha) {
			 * transparency = Transparency.BITMASK;
			 * }
			 */
			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		}
		catch (HeadlessException e)
		{
			// The system does not have a screen
		}
		if (bimage == null)
		{
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			//int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			/*
			 * if (hasAlpha) {
			 * type = BufferedImage.TYPE_INT_ARGB;
			 * }
			 */
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	@SuppressWarnings("restriction")
	private static void setClipboardImageMac(Image image) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
		if (image instanceof RenderedImage)
			ImageIO.write((RenderedImage) image, "png", ios);
		else
			ImageIO.write(toBufferedImage(image), "png", ios);
		ios.flush();
		ios.close();
		byte[] bytes = baos.toByteArray();
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		final String code = encoder.encode(bytes);
		Transferable trans = new Transferable()
		{
			private String	base64code	= "base64image:" + code;
			private DataFlavor flavor = DataFlavor.stringFlavor;//DataFlavor.getTextPlainUnicodeFlavor();

			public DataFlavor[] getTransferDataFlavors()
			{
				return new DataFlavor[] { flavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return this.flavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
			{
				if (isDataFlavorSupported(flavor))
					return base64code;
				throw new UnsupportedFlavorException(flavor);
			}
		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}

	private static void setClipboardImageWindows(final Image image)
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

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
			{
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}
		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}

	/**
	 * Write the specified image to system clip board.
	 * 
	 * @param image
	 *            the specified image to write
	 */
	public static void setClipboardImage(final Image image)
	{
		String osname = System.getProperties().getProperty("os.name").toLowerCase();
		if (osname.startsWith("mac"))
			try
			{
				setClipboardImageMac(image);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e.getLocalizedMessage(), e);
			}
		else
			setClipboardImageWindows(image);
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
	public static void loadFiles(Collection<File> files, File file, FileFilter filter)
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
	public static void read(InputStream in, Charset cs, Appendable appendable, int capacity) throws IOException
	{
		InputStreamReader isr = new InputStreamReader(in, cs == null ? Charset.defaultCharset() : cs);
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
	public static void read(File file, Charset cs, Appendable appendable, int capacity) throws IOException
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
	public static void read(String filename, Charset cs, Appendable appendable, int capacity) throws IOException
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
	public static String read(String filename, Charset cs, int capacity) throws IOException
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
	public static String read(File file, Charset cs, int capacity) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		read(file, cs, sb, capacity);
		return sb.toString();
	}

	/**
	 * Read the specified file and returns the file content with default
	 * character set.
	 * 
	 * @param file
	 *            the specified file
	 * @return the file content
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static String read(File file) throws IOException
	{
		return read(file, Charset.defaultCharset(), 8192);
	}

	/**
	 * Read the specified file and returns the file content with default
	 * character set.
	 * 
	 * @param filename
	 *            the filename of the specified file
	 * @return the file content
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static String read(String filename) throws IOException
	{
		return read(new File(filename), Charset.defaultCharset(), 8192);
	}

	/**
	 * Read the specified file and fill the content to the specified appendable
	 * with default character set.
	 * 
	 * @param filename
	 *            the name of the file to read
	 * @param appendable
	 *            the appendable to append
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static void read(String filename, Appendable appendable) throws IOException
	{
		read(filename, Charset.defaultCharset(), appendable, 8192);
	}

	/**
	 * Read raw byte data from the specified input stream.
	 * 
	 * @param in
	 *            the specified input stream
	 * @param size
	 *            the size of the buffer
	 * @return the raw byte data
	 * @throws IOException
	 *             if any IO error occurs
	 */
	public static byte[] readRawBytes(InputStream in, int size) throws IOException
	{
		ArrayList<Byte> list = new ArrayList<Byte>();
		BufferedInputStream bis = new BufferedInputStream(in, size);
		byte[] b = new byte[size];
		int r = 0, i;
		while (r > -1)
		{
			for (i = 0; i < r; i++)
				list.add(b[i]);
			r = bis.read(b);
		}
		byte[] a = new byte[list.size()];
		i = 0;
		for (Byte e : list)
			a[i++] = e;
		return a;
	}

	/**
	 * Read raw byte data from the specified file.
	 * 
	 * @param file
	 *            the specified file
	 * @param size
	 *            the size of the buffer
	 * @return the raw byte data
	 * @throws IOException
	 *             if any IO error occurs
	 */
	public static byte[] readRawBytes(File file, int size) throws IOException
	{
		return readRawBytes(new FileInputStream(file), size);
	}

	/**
	 * Read raw byte data from the specified file.
	 * 
	 * @param filename
	 *            name of the specified file
	 * @param size
	 *            the size of the buffer
	 * @return the raw byte data
	 * @throws IOException
	 *             if any IO error occurs
	 */
	public static byte[] readRawBytes(String filename, int size) throws IOException
	{
		return readRawBytes(new FileInputStream(filename), size);
	}

	/**
	 * Read raw byte data from the specified input stream.
	 * 
	 * @param in
	 *            the specified input stream
	 * @return the raw byte data
	 * @throws IOException
	 *             if any IO error occurs
	 */
	public static byte[] readRawBytes(InputStream in) throws IOException
	{
		ArrayList<Byte> list = new ArrayList<Byte>();
		BufferedInputStream bis = new BufferedInputStream(in, 8192);
		byte[] b = new byte[8192];
		int r = 0, i;
		while (r > -1)
		{
			for (i = 0; i < r; i++)
				list.add(b[i]);
			r = bis.read(b);
		}
		byte[] a = new byte[list.size()];
		i = 0;
		for (Byte e : list)
			a[i++] = e;
		return a;
	}

	/**
	 * Read raw byte data from the specified file.
	 * 
	 * @param file
	 *            the specified file
	 * @return the raw byte data
	 * @throws IOException
	 *             if any IO error occurs
	 */
	public static byte[] readRawBytes(File file) throws IOException
	{
		return readRawBytes(file, 8192);
	}

	/**
	 * Read raw byte data from the specified file.
	 * 
	 * @param filename
	 *            name of the specified file
	 * @return the raw byte data
	 * @throws IOException
	 *             if any IO error occurs
	 */
	public static byte[] readRawBytes(String filename) throws IOException
	{
		return readRawBytes(filename, 8192);
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
		createFileIfNotExist(file);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
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
	public static Object readObject(File file) throws IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}

	/**
	 * Open the specified URL with the system default browser.
	 * 
	 * @param url
	 *            the URL to open
	 * @return <code>true</code> if the command is successfully sent to the
	 *         system, otherwise, <code>false</code>
	 */
	public static boolean openByBrower(String url)
	{
		Runtime run = Runtime.getRuntime();
		if (null == url || "".equals(url))
			return false;
		try
		{
			// Open the URL according the current system.
			final String OS_NAME = System.getProperty("os.name").toLowerCase();
			if (OS_NAME.indexOf("win") > -1)
				run.exec("rundll32.exe url.dll,FileProtocolHandler " + url);
			else if (OS_NAME.indexOf("mac") > -1)
				run.exec("open " + url);
			else if (OS_NAME.indexOf("nux") > -1 || OS_NAME.indexOf("nix") > -1)
			{
				String[] cmd = new String[2];
				cmd[0] = "firefox";
				cmd[1] = url;
				try
				{
					run.exec(cmd);
				}
				catch (IOException e)
				{
					cmd[0] = "xdg-open";
					run.exec(cmd);
				}
			}
			else
				return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Returns the file separator of the current system.
	 * 
	 * @return the file separator
	 */
	public static String getFileSeparator()
	{
		return System.getProperty("file.separator");//$NON-NLS-1$
	}

	/**
	 * Returns the directory of current project in the format of
	 * "Disk:/Directory/".
	 * 
	 * @return the directory of current project
	 */
	public static String getCurrentDirectory()
	{
		return System.getProperty("user.dir")//$NON-NLS-1$
				+ System.getProperty("file.separator");//$NON-NLS-1$
	}

	/**
	 * Create the specified file if it not exists.
	 * 
	 * @param file
	 *            the specified file
	 * @return true if the file exists or created successfully, false otherwise.
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public static boolean createFileIfNotExist(File file) throws IOException
	{
		if (file.exists())
			return true;
		else
		{
			File parent = file.getParentFile();
			if (parent != null && parent.mkdirs())
				return file.createNewFile();
			else
				return false;
		}
	}

	/**
	 * Translate the specified string to UNICODE presented code string.
	 * 
	 * @param s
	 *            the specified string
	 * @return UNICODE presented code string
	 */
	public static String toUnicodeSymbols(String s)
	{
		StringBuffer sb = new StringBuffer();
		char a[] = s.toCharArray();
		for (char c : a)
			if (c < 256)
				sb.append(c);
			else if (c < 4096)
				sb.append(String.format("\\u0%h", (int) c));//$NON-NLS-1$
			else
				sb.append(String.format("\\u%h", (int) c));//$NON-NLS-1$
		return sb.toString();
	}
}
