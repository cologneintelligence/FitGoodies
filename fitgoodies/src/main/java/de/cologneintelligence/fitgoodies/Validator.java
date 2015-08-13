/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.checker.Checker;
import de.cologneintelligence.fitgoodies.checker.EqualityChecker;
import de.cologneintelligence.fitgoodies.references.CellProcessor;
import de.cologneintelligence.fitgoodies.references.CellProcessorProvider;
import de.cologneintelligence.fitgoodies.references.CellProcessorProviderHelper;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandlerFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;

import java.util.LinkedList;
import java.util.List;

public class Validator {

	private final List<CellProcessorProvider> cellProcessorProviders;

	public Validator() {
		cellProcessorProviders = DependencyManager.getOrCreate(CellProcessorProviderHelper.class)
				.getProviders();
	}

	public String preProcess(Parse cell) {
		return preProcess(cell.text().trim());
	}

	public String preProcess(String cellText) {
		return new PreProcessorStep().run(cellText).getProcessedText();
	}

	public void process(Parse cell, Counts counts, ValueReceiver valueReceiver, String cellParameter, TypeHandlerFactory typeHandlerFactory) {

		PreProcessorStep preProcessorStep = new PreProcessorStep().run(cell.text().trim());

		ComparatorStep comparatorStep = new ComparatorStep().run(preProcessorStep, cell, counts,
				valueReceiver, typeHandlerFactory, cellParameter);

		new PostProcessorStep().run(preProcessorStep, comparatorStep);
	}

	private class PreProcessorStep {
		private Checker checkRoutine;
		private List<CellProcessor> applicableProcessors;
		private String processedText;

		public PreProcessorStep run(String cellText) {
			checkRoutine = null;
			applicableProcessors = new LinkedList<>();

			processedText = cellText;
			for (CellProcessorProvider provider : cellProcessorProviders) {
				if (provider.canProcess(processedText)) {
					CellProcessor processor = provider.create(processedText);

					processedText = processor.preprocess();

					if (checkRoutine == null && processor.replacesCheckRoutine()) {
						checkRoutine = processor.getChecker();
					}

					applicableProcessors.add(processor);
				}
			}

			return this;
		}

		public Checker getCheckRoutine() {
			return checkRoutine;
		}

		public List<CellProcessor> getApplicableProcessors() {
			return applicableProcessors;
		}

		public String getProcessedText() {
			return processedText;
		}
	}

	private static class ComparatorStep {
		private TypeHandler handler;
		private Object result;

		public ComparatorStep run(PreProcessorStep preProcessorStep, Parse cell, Counts counts, ValueReceiver valueReceiver, TypeHandlerFactory typeHandlerFactory, String cellParameter) {
			Checker checkRoutine = preProcessorStep.getCheckRoutine();
			String cellText = preProcessorStep.getProcessedText();

			if (checkRoutine == null) {
				checkRoutine = new EqualityChecker();
			}

			handler = typeHandlerFactory.getHandler(valueReceiver.getType(), cellParameter);
			result = checkRoutine.check(cell, counts, cellText, valueReceiver, handler);
			return this;
		}

		public TypeHandler getHandler() {
			return handler;
		}

		public Object getResult() {
			return result;
		}
	}

	private static class PostProcessorStep {
		public void run(PreProcessorStep preProcessorStep, ComparatorStep comparatorStep) {
			List<CellProcessor> applicableProcessors = preProcessorStep.getApplicableProcessors();
			for (int i = applicableProcessors.size() - 1; i >= 0; i--) {
				CellProcessor applicableProcessor = applicableProcessors.get(i);
				applicableProcessor.postprocess(comparatorStep.getResult(), comparatorStep.getHandler());
			}
		}
	}
}
