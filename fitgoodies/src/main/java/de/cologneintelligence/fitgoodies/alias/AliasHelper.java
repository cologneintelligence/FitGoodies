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


package de.cologneintelligence.fitgoodies.alias;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage aliases.
 *
 * @author jwierum
 * @version $Id$
 */
public final class AliasHelper {
    private final Map<String, String> classMapper = new HashMap<String, String>();

    public AliasHelper() {
        initDefaultMappings();
    }

    private void initDefaultMappings() {
        classMapper.put("fitgoodies.ActionFixture", "de.cologneintelligence.fitgoodies.ActionFixture");
        classMapper.put("fitgoodies.ColumnFixture", "de.cologneintelligence.fitgoodies.ColumnFixture");
        classMapper.put("fitgoodies.RowFixture", "de.cologneintelligence.fitgoodies.RowFixture");

        classMapper.put("fitgoodies.file.FileFixture", "de.cologneintelligence.fitgoodies.file.FileFixture");
        classMapper.put("fitgoodies.runners.RunFixture", "de.cologneintelligence.fitgoodies.runners.RunFixture");
        classMapper.put("fitgoodies.selenium.SeleniumFixture", "de.cologneintelligence.fitgoodies.selenium.SeleniumFixture");
        classMapper.put("fitgoodies.selenium.SetupFixture", "de.cologneintelligence.fitgoodies.selenium.SetupFixture");
        classMapper.put("fitgoodies.references.SetupFixture", "de.cologneintelligence.fitgoodies.references.SetupFixture");
        classMapper.put("fitgoodies.parsers.SetupFixture", "de.cologneintelligence.fitgoodies.parsers.SetupFixture");
        classMapper.put("fitgoodies.mail.SetupFixture", "de.cologneintelligence.fitgoodies.mail.SetupFixture");
        classMapper.put("fitgoodies.date.SetupFixture", "de.cologneintelligence.fitgoodies.date.SetupFixture");
        classMapper.put("fitgoodies.database.SetupFixture", "de.cologneintelligence.fitgoodies.database.SetupFixture");
        classMapper.put("fitgoodies.adapters.SetupFixture", "de.cologneintelligence.fitgoodies.adapters.SetupFixture");

        classMapper.put("fitgoodies.database.ResultSetFixture", "de.cologneintelligence.fitgoodies.database.ResultSetFixture");
        classMapper.put("fitgoodies.database.TableFixture", "de.cologneintelligence.fitgoodies.database.TableFixture");

        classMapper.put("fitgoodies.file.CSVFileRecordFixture", "de.cologneintelligence.fitgoodies.file.CSVFileRecordFixture");
        classMapper.put("fitgoodies.file.DelimiterFileRecordFixture", "de.cologneintelligence.fitgoodies.file.DelimiterFileRecordFixture");
        classMapper.put("fitgoodies.file.FixedLengthFileRecordFixture", "de.cologneintelligence.fitgoodies.file.FixedLengthFileRecordFixture");
        classMapper.put("fitgoodies.file.XMLFileFixture", "de.cologneintelligence.fitgoodies.file.XMLFileFixture");
        classMapper.put("fitgoodies.log4j.LogFixture", "de.cologneintelligence.fitgoodies.log4j.LogFixture");
        classMapper.put("fitgoodies.log4j.SetupFixture", "de.cologneintelligence.fitgoodies.log4j.SetupFixture");
        classMapper.put("fitgoodies.mail.MailFixture", "de.cologneintelligence.fitgoodies.mail.MailFixture");
        classMapper.put("fitgoodies.alias.SetupFixture", "de.cologneintelligence.fitgoodies.alias.SetupFixture");
    }

    /**
     * Looks up a class in the alias map. If <code>className</code> exists as
     * an alias, it is resolved and returned. If the alias doesn't exist, the
     * original String is returned.
     *
     * @param className class name to look up
     * @return resolved name of the alias
     */
    public String getClazz(final String className) {
        String realName = className;

        while (classMapper.containsKey(realName)) {
            realName = classMapper.get(realName);
        }
        return realName;
    }

    /**
     * Registers a new alias <code>alias</code> for <code>className</code>.
     *
     * @param alias alias to use
     * @param className class the alias maps on
     */
    public void register(final String alias, final String className) {
        classMapper.put(alias, className);
    }
}
