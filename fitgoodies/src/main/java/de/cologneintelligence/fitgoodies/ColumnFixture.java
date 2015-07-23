package de.cologneintelligence.fitgoodies;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.util.FixtureTools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColumnFixture extends Fixture {
	// FIXME: make this private again
	protected String[] columnParameters;
	public TypeAdapter[] columnBindings;

	private boolean hasExecuted = false;

	// Traversal ////////////////////////////////

	/**
	 * Replacement of {@code doRows(Parse)} which resolves question marks
	 * in the first row and calls fit.ColumnFixture.doRows(Parse).
	 * <p/>
	 * Question marks represent method calls,
	 * so getValue() and getValue? are equivalent.
	 *
	 * @param rows rows to be processed
	 */

	@Override
	protected void doRows(final Parse rows) {
		columnParameters = FixtureTools.extractColumnParameters(rows);
		bind(rows.parts);
		super.doRows(rows.more);
	}

	protected void doRow(Parse row) {
		hasExecuted = false;
		try {
			reset();
			super.doRow(row);
			if (!hasExecuted) {
				execute();
			}
		} catch (Exception e) {
			exception(row.leaf(), e);
		}
	}

	// FIXME
	protected void doCell_old(Parse cell, int column) {
		TypeAdapter a = columnBindings[column];
		try {
			String text = cell.text();
			if (text.equals("")) {
				check(cell, a);
			} else if (a == null) {
				ignore(cell);
			} else if (a.field != null) {
				a.set(a.parse(text));
			} else if (a.method != null) {
				check(cell, a);
			}
		} catch (Exception e) {
			exception(cell, e);
		}
	}


	/**
	 * Replacement of {@code doCell} which resolves cross-references
	 * before calling the original {@code doCell} method of fit.
	 *
	 * @param cell   the cell to check
	 * @param column the selected column
	 * @see Fixture#doCell(Parse, int) fit.Fixture.doCell(Parse, int)
	 */
	@Override
	protected void doCell(final Parse cell, final int column) {
		TypeAdapter a = columnBindings[column];

		setCellParameter(null);
		if (column < columnParameters.length) {
			setCellParameter(columnParameters[column]);
		}

		if (a == null) {
			ignore(cell);
		} else {
			final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
			a = FixtureTools.processCell(cell, a, this, helper);

			try {
				String text = cell.text();
				if (text.equals("")) {
					check(cell, a);
				} else if (a == null) {
					ignore(cell);
				} else if (a.field != null) {
					a.set(a.parse(text));
				} else if (a.method != null) {
					check(cell, a);
				}
			} catch (Exception e) {
				exception(cell, e);
			}
		}
	}


	public void check(Parse cell, TypeAdapter a) {
		if (!hasExecuted) {
			try {
				execute();
			} catch (Exception e) {
				exception(cell, e);
			}
			hasExecuted = true;
		}
		super.check(cell, a);
	}

	public void reset() throws Exception {
		// about to process first cell of row
	}

	public void execute() throws Exception {
		// about to process first method call of row
	}

	// Utility //////////////////////////////////

	private Pattern methodPattern = Pattern.compile("(.*)(?:\\(\\)|\\?)");

	protected void bind(Parse heads) {
		columnBindings = new TypeAdapter[heads.size()];
		for (int i = 0; heads != null; i++, heads = heads.more) {
			String name = heads.text();

			try {
				String parameter = null;
				if (i < columnParameters.length) {
					parameter = columnParameters[i];
				}

				if (name.equals("")) {
					columnBindings[i] = null;
				} else {
					Matcher matcher = methodPattern.matcher(name);

					if (matcher.find()) {
						columnBindings[i] = bindMethod(matcher.group(1), parameter);
					} else {
						columnBindings[i] = bindField(name, parameter);
					}
				}
			} catch (Exception e) {
				exception(heads, e);
			}
		}

	}

	protected TypeAdapter bindMethod(final String name, final String parameter)
			throws Exception {
		final TypeAdapterHelper taHelper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
		TypeAdapter ta = TypeAdapter.on(this, this, getTargetClass().getMethod(FitUtils.camel(name), new Class[]{}));
		return FixtureTools.rebindTypeAdapter(ta, parameter, taHelper);
	}

	protected TypeAdapter bindField(final String name, final String parameter)
			throws Exception {
		final TypeAdapterHelper taHelper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
		TypeAdapter ta = TypeAdapter.on(this, this, getTargetClass().getField(FitUtils.camel(name)));
		return FixtureTools.rebindTypeAdapter(ta, parameter, taHelper);
	}

	protected Class<?> getTargetClass() {
		return getClass();
	}
}
