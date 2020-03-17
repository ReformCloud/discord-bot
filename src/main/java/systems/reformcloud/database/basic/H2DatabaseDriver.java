/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 Pasqual Koschmieder.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package systems.reformcloud.database.basic;

import org.h2.Driver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.database.DatabaseDriver;
import systems.reformcloud.database.config.DatabaseConfig;
import systems.reformcloud.database.object.DatabaseObject;
import systems.reformcloud.database.object.DatabaseObjectToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A default implementation of a database driver
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class H2DatabaseDriver implements DatabaseDriver {

    private Connection connection;

    @Override
    public boolean connect(@NotNull DatabaseConfig config) {
        try {
            Driver.load();
            this.connection = DriverManager.getConnection(config.formatConnectionString());

            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public void createTable(@NotNull String table) {
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table
                + " (`key` TEXT, `value` LONGBLOB)")) {
            statement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteTable(@NotNull String table) {
        try (PreparedStatement statement = connection.prepareStatement("DROP TABLE IF EXISTS " + table)) {
            statement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void insert(@NotNull DatabaseObject object) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + object.getTable() + " (`key`, `value`) VALUES (?, ?)")) {
            statement.setString(1, object.getKey());
            statement.setBytes(2, object.serialize());

            statement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull DatabaseObject object) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE " + object.getTable() + " SET `value` = ? WHERE `key` = ?")) {
            statement.setBytes(1, object.serialize());
            statement.setString(2, object.getKey());

            statement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @Nullable
    public <T> T getOrDefault(@NotNull DatabaseObjectToken<T> databaseObjectToken, @Nullable T def) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT `value` FROM " + databaseObjectToken.getTable() + " WHERE `key` = ?")) {
            statement.setString(1, databaseObjectToken.getKey());

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return def;
            }

            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(resultSet.getBytes("value")))) {
                return databaseObjectToken.deserialize(stream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return def;
    }

    @Override
    public <T> void forEachInTable(@NotNull String table, @NotNull Function<byte[], T> mapper, @NotNull Consumer<T> handler) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                byte[] next = resultSet.getBytes("value");
                if (next != null) {
                    handler.accept(mapper.apply(next));
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteFromTable(@NotNull String table, @NotNull String key) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE `key` = ?")) {
            statement.setString(1, key);

            statement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }

            connection = null;
        }
    }
}
