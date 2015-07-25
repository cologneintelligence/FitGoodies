package de.cologneintelligence.fitgoodies;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandlerFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class ColumnFixture extends Fixture {

	// FIXME: make this private again
	protected String[] columnParameters;
	public ValueReceiver[] columnBindings;

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
		columnParameters = extractColumnParameters(rows);
		bind(rows.parts);
		super.doRows(rows.more);
	}

	@Override
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
		ValueReceiver a = columnBindings[column];

		setCurrentCellParameter(null);
		if (column < columnParameters.length) {
			setCurrentCellParameter(columnParameters[column]);
		}

		if (a == null) {
			ignore(cell);
		} else {
			a = processCell(cell, a);

			try {
				String text = cell.text();
				if (text.equals("")) {
					check(cell, a);
				} else if (a == null) {
					ignore(cell);
				} else {
					TypeHandlerFactory factory = DependencyManager.getOrCreate(TypeHandlerFactory.class);
					TypeHandler handler = factory.getHandler(a.getType(), getCellParameter());

					if (a.canSet()) {
						a.set(this, handler.parse(text));
					} else {
						check(cell, a);
					}
				}
			} catch (Exception e) {
				exception(cell, e);
			}
		}
	}


	public void check(Parse cell, ValueReceiver valueReceiver) {
		if (!hasExecuted) {
			try {
				execute();
			} catch (Exception e) {
				exception(cell, e);
			}
			hasExecuted = true;
		}
		super.check(cell, valueReceiver);
	}

	public void reset() throws Exception {
		// about to process first cell of row
	}

	public void execute() throws Exception {
		// about to process first method call of row
	}

	// Utility //////////////////////////////////

	protected void bind(Parse heads) {
		columnBindings = new ValueReceiver[heads.size()];
		for (int i = 0; heads != null; i++, heads = heads.more) {
			String name = heads.text();

			try {
				columnBindings[i] = createReceiver(this, name);
			} catch (Exception e) {
				exception(heads, e);
			}
		}
	}
}
