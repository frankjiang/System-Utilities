/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * MessagesProvider.java is PROPRIETARY/CONFIDENTIAL built in 10:32:29 PM, Mar
 * 23, 2014.
 * Use is subject to license terms.
 */
package com.frank.sys;

import java.util.Hashtable;

/**
 * The {@linkplain Messages} provider.
 * <p>
 * The message provider which can generate the messages instance according to
 * the bundle.
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class MessagesProvider
{
	/**
	 * The messages hash table.
	 */
	protected static final Hashtable<String, Messages>	table	= new Hashtable<String, Messages>();
	/**
	 * The singleton of the messages provider.
	 */
	protected volatile static MessagesProvider			instance;

	/**
	 * Construct an instance of <tt>MessagesProvider</tt>.
	 */
	protected MessagesProvider()
	{
	}

	/**
	 * Returns the bounded messages.
	 * <p>
	 * The key string is case insensitive.
	 * </p>
	 * @param key
	 *            the key of bundle
	 * @return the messages instance or <code>null</code> if no resource bundle
	 *         for the specified base name can be found
	 */
	public synchronized Messages provide(String key)
	{
		key = key.toLowerCase();
		Messages messages = table.get(key);
		if (messages == null)
		{
			try
			{
				messages = new Messages(key);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
			table.put(key, messages);
		}
		return table.get(key.toLowerCase());
	}

	/**
	 * Returns the bounded messages.
	 * <p>
	 * The key string is case insensitive.
	 * </p>
	 * 
	 * @param key
	 *            the key of bundle
	 * @return the messages instance or <code>null</code> if no resource bundle
	 *         for the specified base name can be found
	 */
	public static Messages getMessages(String key)
	{
		return getInstance().provide(key);
	}

	/**
	 * Returns the instance of the messages provider.
	 * 
	 * @return the instance of the messages provider
	 */
	public static synchronized MessagesProvider getInstance()
	{
		if (instance == null)
			instance = new MessagesProvider();
		return instance;
	}
}
