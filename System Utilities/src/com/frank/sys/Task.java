/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Task.java is PROPRIETARY/CONFIDENTIAL built in 2:28:45 PM, Feb 13, 2015.
 * Use is subject to license terms.
 */
package com.frank.sys;

/**
 * A controlled runnable task.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public abstract class Task implements Runnable
{
	/**
	 * The status of the playing thread.
	 * <p>
	 * </p>
	 * 
	 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
	 * @version 1.0.0
	 */
	public static enum Status
	{
		/**
		 * The status indicates the task is before started.
		 */
		Init,
		/**
		 * The status indicates the task is running.
		 */
		Run,
		/**
		 * The status indicates the task is paused.
		 */
		Pause,
		/**
		 * The status indicates the task is stopped.
		 */
		Stop,
		/**
		 * The status indicates the task is interrupted by an error.
		 */
		Error
	}

	/**
	 * The current task status.
	 */
	protected Status	status		= Status.Init;
	/**
	 * The milliseconds to sleep when the task is paused.
	 */
	protected long		sleepTime	= 40;
	/**
	 * The exception ({@linkplain Throwable}) catched in the task.
	 */
	protected Throwable	throwable;

	/**
	 * The action when the task is started.
	 * <p>
	 * In this period, the {@link #status} values is already
	 * {@linkplain Status#Run}.
	 * </p>
	 * 
	 * @throws Throwable
	 *             any throwable occurs in the task
	 */
	abstract protected void taskStarted() throws Throwable;

	/**
	 * The action when the task is paused.
	 * <p>
	 * No need to change the {@link #status} values, the value will be changed
	 * before this method.
	 * </p>
	 * 
	 * @throws Throwable
	 *             any throwable occurs in the task
	 */
	protected void taskPaused() throws Throwable
	{
		Thread.sleep(sleepTime);
	}

	/**
	 * The action when the task is resumed.
	 * <p>
	 * No need to change the {@link #status} values, the value will be changed
	 * before this method.
	 * </p>
	 * 
	 * @throws Throwable
	 *             any throwable occurs in the task
	 */
	abstract protected void taskResumed() throws Throwable;

	/**
	 * The action when the task is running.
	 * <p>
	 * No need to change the {@link #status} values, the value will be changed
	 * before this method.
	 * </p>
	 * 
	 * @throws Throwable
	 *             any throwable occurs in the task
	 */
	abstract protected void taskRunning() throws Throwable;

	/**
	 * The action when the task is stopped.
	 * <p>
	 * No need to change the {@link #status} values, the value will be changed
	 * before this method.
	 * </p>
	 * 
	 * @throws Throwable
	 *             any throwable occurs in the task
	 */
	abstract protected void taskStopped() throws Throwable;

	/**
	 * The action when the task is forced to stop.
	 */
	abstract protected void taskForcedStop();

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			while (status != Status.Stop)
			{
				switch (status)
				{
				case Init:
					status = Status.Run;
					taskStarted();
					break;
				case Pause:
					taskPaused();
					break;
				case Run:
					taskRunning();
					break;
				default:
				case Stop:
					taskStopped();
					break;
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			throwable = e;
			status = Status.Error;
		}
	}

	/**
	 * Pause the current task.
	 */
	public void pause()
	{
		if (status == Status.Run)
			status = Status.Pause;
	}

	/**
	 * Resume the current task.
	 */
	public void resume()
	{
		if (status == Status.Pause)
		{
			status = Status.Run;
			try
			{
				taskResumed();
			}
			catch (Throwable e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Toggle the status of the current task of {@link Status#Run} /
	 * {@link Status#Pause}.
	 */
	public void toggle()
	{
		switch (status)
		{
		case Error:
		case Init:
		case Stop:
		default:
			break;
		case Pause:
			resume();
			break;
		case Run:
			pause();
			break;
		}
	}

	/**
	 * Teriminate the current task.
	 */
	public void terminate()
	{
		status = Status.Stop;
	}
}
