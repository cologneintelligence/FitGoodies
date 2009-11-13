/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies;
import fitgoodies.util.FixtureToolsParserTest;
import fitgoodies.util.FixtureToolsTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * $Id$
 * @author jwierum
 */

public final class FitGoodiesTests {
	private FitGoodiesTests() {
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for fittools package");
		//$JUnit-BEGIN$
		suite.addTestSuite(FixtureToolsTest.class);
		suite.addTestSuite(FixtureToolsParserTest.class);
		suite.addTestSuite(fitgoodies.parsers.SetupFixtureTest.class);
		suite.addTestSuite(fitgoodies.ColumnFixtueTest.class);
		suite.addTestSuite(fitgoodies.RowFixtureTest.class);
		suite.addTestSuite(ScientificDoubleTest.class);

		suite.addTestSuite(fitgoodies.adapters.AbstractTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.adapters.TypeAdapterHelperTest.class);
		suite.addTestSuite(fitgoodies.adapters.SetupFixtureTest.class);
		suite.addTestSuite(fitgoodies.adapters.StringBuilderTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.adapters.StringBufferTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.adapters.StringTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.adapters.CachingTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.adapters.ArrayTypeAdapterTest.class);

		suite.addTestSuite(fitgoodies.alias.AliasHelperTest.class);
		suite.addTestSuite(fitgoodies.alias.SetupFixtureTest.class);

		suite.addTestSuite(fitgoodies.database.TableFixtureTest.class);
		suite.addTestSuite(fitgoodies.database.SetupHelperTest.class);
		suite.addTestSuite(fitgoodies.database.SetupFixtureTest.class);

		suite.addTestSuite(fitgoodies.date.SetupHelperTest.class);
		suite.addTestSuite(fitgoodies.date.SetupFixtureTest.class);

		suite.addTestSuite(fitgoodies.references.CrossReferenceHelperTest.class);
		suite.addTestSuite(fitgoodies.references.CrossReferenceTest.class);
		suite.addTestSuite(fitgoodies.references.ProcessorsTest.class);
		suite.addTestSuite(fitgoodies.references.SetupFixtureTest.class);

		suite.addTestSuite(fitgoodies.references.processors.NamespaceHashMapTest.class);
		suite.addTestSuite(fitgoodies.references.processors.EmptyCrossReferenceProcessorTest.class);
		suite.addTestSuite(
				fitgoodies.references.processors.StorageCrossReferenceProcessorTest.class);
		suite.addTestSuite(
				fitgoodies.references.processors.FileFixtureCrossReferenceProcessorTest.class);
		suite.addTestSuite(
				fitgoodies.references.processors.PropertyCrossReferenceProcessorTest.class);

		suite.addTestSuite(
				fitgoodies.references.processors.AbstractCrossReferenceProcessorTest.class);

		suite.addTestSuite(fitgoodies.file.FilterDirectoryIteratorTest.class);
		suite.addTestSuite(fitgoodies.file.FileSelectorTest.class);
		suite.addTestSuite(fitgoodies.file.FileFixtureTest.class);
		suite.addTestSuite(fitgoodies.file.FileFixtureHelperTest.class);
		suite.addTestSuite(fitgoodies.file.FileInformationTest.class);
		suite.addTestSuite(fitgoodies.file.RecursiveFileSelectorTest.class);
		suite.addTestSuite(fitgoodies.file.IteratorHelperTest.class);
		suite.addTestSuite(fitgoodies.file.AbstractFilereaderFixtureTest.class);
		suite.addTestSuite(fitgoodies.file.XMLFileFixtureTest.class);
		suite.addTestSuite(fitgoodies.file.AbstractFileRecordReaderFixtureTest.class);
		suite.addTestSuite(fitgoodies.file.FixedLengthFileRecordFixtureTest.class);
		suite.addTestSuite(fitgoodies.file.AbstractDirectoryHelperTest.class);
		suite.addTestSuite(fitgoodies.file.SimpleRegexFilterTest.class);

		suite.addTestSuite(fitgoodies.file.readers.DelimiterRecordReaderTest.class);
		suite.addTestSuite(fitgoodies.file.readers.FixedLengthRecordReaderTest.class);
		suite.addTestSuite(fitgoodies.file.readers.CSVRecordReaderTest.class);

		suite.addTestSuite(fitgoodies.log4j.CaptureAppenderTest.class);
		suite.addTestSuite(fitgoodies.log4j.LogHelperTest.class);
		suite.addTestSuite(fitgoodies.log4j.SetupFixtureTest.class);
		suite.addTestSuite(fitgoodies.log4j.LogFixtureTest.class);
		suite.addTestSuite(fitgoodies.log4j.CellArgumentParserImplTest.class);
		suite.addTestSuite(fitgoodies.log4j.CellArgumentParserFactoryImplTest.class);
		suite.addTestSuite(fitgoodies.log4j.LogEventAnalyzerFactoryImplTest.class);
		suite.addTestSuite(fitgoodies.log4j.LogEventAnalyzerTest.class);

		suite.addTestSuite(fitgoodies.mail.MailFixtureTest.class);
		suite.addTestSuite(fitgoodies.mail.SetupFixtureTest.class);
		suite.addTestSuite(fitgoodies.mail.SetupHelperTest.class);
		suite.addTestSuite(fitgoodies.mail.JavaMailMailTest.class);

		suite.addTestSuite(fitgoodies.dynamic.DynamicObjectFactoryTest.class);
		suite.addTestSuite(fitgoodies.dynamic.ResultSetWrapperTest.class);

		suite.addTestSuite(fitgoodies.parsers.ParserHelperTest.class);
		suite.addTestSuite(fitgoodies.parsers.BigDecimalParserTest.class);
		suite.addTestSuite(fitgoodies.parsers.BigIntegerParserTest.class);
		suite.addTestSuite(fitgoodies.adapters.DateTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.adapters.SQLDateTypeAdapterTest.class);
		suite.addTestSuite(fitgoodies.parsers.ObjectParserTest.class);
		suite.addTestSuite(fitgoodies.parsers.ScientificDoubleParserTest.class);

		suite.addTestSuite(fitgoodies.runners.DirectoryRunnerTest.class);
		suite.addTestSuite(fitgoodies.runners.FitResultTableTest.class);
		suite.addTestSuite(fitgoodies.runners.FileNameComperatorTest.class);
		suite.addTestSuite(fitgoodies.runners.FileCountTest.class);
		suite.addTestSuite(fitgoodies.runners.RunnerHelperTest.class);
		suite.addTestSuite(fitgoodies.runners.RunFixtureTest.class);
		suite.addTestSuite(fitgoodies.runners.FitParseResultTest.class);

		suite.addTestSuite(fitgoodies.util.WaitForResultTest.class);

		//$JUnit-END$
		return suite;
	}

}
