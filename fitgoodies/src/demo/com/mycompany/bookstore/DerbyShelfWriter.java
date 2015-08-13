/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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


package com.mycompany.bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DerbyShelfWriter implements ShelfWriter {
	private Connection connection;

	private void connect() {
		connection = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			connection = DriverManager.getConnection("jdbc:derby:demo-output/db;create=true");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void disconnect() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				connection = null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createTable() {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT tablename "
					+ "FROM sys.systables "
					+ "WHERE tablename='STORE'");

			if (!resultSet.next()) {
				statement.execute("CREATE TABLE STORE ("
						+ "ID INT PRIMARY KEY, "
						+ "ISBN VARCHAR(15), "
						+ "TITLE VARCHAR(80), "
						+ "AUTHOR VARCHAR(50), "
						+ "PRICE FLOAT)");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(statement);
		}
	}

	private void closeStatement(final Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Bookshelf load() {
		return null;
	}

	@Override
	public void write(final Bookshelf shelf) {
		PreparedStatement statement = null;
		connect();

		try {
			createTable();

			Statement st2 = connection.createStatement();
			st2.execute("DELETE FROM STORE");
			closeStatement(st2);

			statement = connection.prepareStatement("INSERT INTO STORE"
					+ "(ID, ISBN, TITLE, AUTHOR, PRICE) VALUES (?, ?, ?, ?, ?)");

			for (int i = 0; i < shelf.bookCount(); ++i) {
				statement.setInt(1, i);
				Book b = shelf.get(i);
				statement.setString(2, b.getIsbn().stripped());
				statement.setString(3, b.getTitle().toString());
				statement.setString(4, b.getAuthor());
				statement.setFloat(5, b.getPrice());

				statement.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(statement);
			disconnect();
		}
	}
}
