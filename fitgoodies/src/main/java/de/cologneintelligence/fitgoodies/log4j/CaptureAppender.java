/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.log4j;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Appender which caches all appended log entries.
 * It copies the filters from a given parent Appender
 *
 * @author jwierum
 * @version $Id$
 */
public final class CaptureAppender extends AppenderSkeleton {
	private final List<LoggingEvent> events = new LinkedList<LoggingEvent>();
	private final Appender parent;

	private CaptureAppender(final Appender parent) {
		this.parent = parent;
		name = getAppenderNameFor(parent.getName());
	}

	/**
	 * Does nothing - the parent's filters are used.
	 */
	@Override public void addFilter(final Filter newFilter) { }

	/**
	 * Does nothing - the parent's filters are used.
	 */
	@Override public void clearFilters() { }

	/**
	 * Returns the parent's filters.
	 */
	@Override
	public Filter getFilter() {
		return parent.getFilter();
	}

	/**
	 * Returns <code>false</code>, layouts are not supported.
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * Does nothing.
	 */
	@Override public void close() { }

	/**
	 * Sets the parent's filter and appends the event
	 * <code>event</code> to the cache.
	 * @param event event to save
	 */
	@Override
	public synchronized void doAppend(final LoggingEvent event) {
		headFilter = parent.getFilter();
		super.doAppend(event);
	}

	/**
	 * Appends the event <code>event</code> to the cache.
	 * @param event event to save
	 */
	@Override
	public void append(final LoggingEvent event) {
		events.add(event);
	}

	/**
	 * Returns all saved events.
	 * @return all saved events.
	 */
	public LoggingEvent[] getAllEvents() {
		return events.toArray(new LoggingEvent[]{});
	}

	/**
	 * Generates a new appender which bases on <code>parentAppender</code>.
	 * @param parentAppender appender to use as a template
	 * @return the new appender
	 */
	public static CaptureAppender newAppenderFrom(final Appender parentAppender) {
		return new CaptureAppender(parentAppender);
	}

	/**
	 * Does nothing, the name is generated automatically.
	 */
	@Override
	public void setName(final String name) { }

	/**
	 * Generates the name of the capture appender which belongs to
	 * the given <code>baseName</code>.
	 * @param baseName name of the appender to capture
	 * @return the generated name
	 */
	public static String getAppenderNameFor(final String baseName) {
		return baseName + "-fitgoodiescapture";
	}

	/**
	 * Deletes all saved items.
	 */
	public void clear() {
		events.clear();
	}
}
