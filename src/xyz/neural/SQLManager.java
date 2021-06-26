package xyz.neural;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.sql.*;
import java.util.*;

public class SQLManager {
    private static String file_path;
    private static Connection conn;

    public SQLManager() {
    }

    public static void closeConnection(){
        try {
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setup(String path) throws SQLException {
        SQLManager.file_path = "jdbc:sqlite:" + path;
        SQLManager.conn = DriverManager.getConnection(file_path, new SQLiteConfig().toProperties());
        Statement ps = conn.createStatement();

        ps.execute("CREATE TABLE IF NOT EXISTS weights ("
                + " id integer PRIMARY KEY,"
                + " value real,"
                + " neuron integer,"
                + " i integer"
                + ");"
        );
        ps.execute("CREATE TABLE IF NOT EXISTS neurons ("
                + "	id integer PRIMARY KEY,"
                + " layer integer,"
                + " i integer"
                + ");"
        );
        ps.execute("CREATE TABLE IF NOT EXISTS layers ("
                + "	id integer PRIMARY KEY,"
                + " network integer,"
                + " i integer"
                + ");"
        );
        ps.execute("CREATE TABLE IF NOT EXISTS neural ("
                + "	id integer PRIMARY KEY,"
                + " name text"
                + ");"
        );
    }
    /*
    LinkedHashMap<String, Object> search = new LinkedHashMap<>();
        search.put("name", "");
        LinkedList<LinkedList<Object>> rs = SQLManager.get(search, "cities", "id != -1");
     */

    public static LinkedList<LinkedList<Object>> get(LinkedHashMap<String, ?> hash, String table, String condition) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        LinkedList<LinkedList<Object>> els = new LinkedList<>();
        LinkedList<String> elements = new LinkedList<>(hash.keySet());
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            for (String el : elements) {
                sb.append(el).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" FROM ");
            sb.append(table);
            if (condition.length() > 0) {
                sb.append(" WHERE ").append(condition);
            }
            ps = SQLManager.conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                LinkedList<Object> lineResults = new LinkedList<>();
                for (String el : elements) {
                    Object o = hash.get(el);
                    if (o instanceof String) {
                        lineResults.add(rs.getString(el));
                    } else if (o instanceof Integer) {
                        lineResults.add(rs.getInt(el));
                    } else if (o instanceof Float) {
                        lineResults.add(rs.getFloat(el));
                    } else if (o instanceof Double) {
                        lineResults.add(rs.getDouble(el));
                    } else if (o instanceof Long) {
                        lineResults.add(rs.getLong(el));
                    }
                }
                els.add(lineResults);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps);
        }
        return els;
    }

    public static void insert(LinkedHashMap<String, ?> hash, String table) {
        PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(table).append("(");
            LinkedList<String> keys = new LinkedList<>(hash.keySet());
            for (String key : keys) {
                sb.append(key).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") VALUES(");
            for (String key : keys) {
                Object value = hash.get(key);
                if (value instanceof String) {
                    sb.append("\"").append(hash.get(key)).append("\",");
                } else {
                    sb.append(hash.get(key)).append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            ps = conn.prepareStatement(sb.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, ps);
        }
    }

    public static void update(LinkedHashMap<String, ?> hash, String table, String condition) {
        PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(table).append(" SET ");
            LinkedList<String> keys = new LinkedList<>(hash.keySet());
            for (String key : keys) {
                Object val = hash.get(key);
                if (val instanceof String) {
                    sb.append(key).append("=\"").append(hash.get(key)).append("\",");
                } else {
                    sb.append(key).append("=").append(hash.get(key)).append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" WHERE ").append(condition);
            ps = conn.prepareStatement(sb.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, ps);
        }
    }

    public static void delete(String table, String condition) {
        PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM ").append(table).append(" WHERE ").append(condition).append(";");
            ps = conn.prepareStatement(sb.toString());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, ps);
        }
    }

    private static void close(ResultSet rs, PreparedStatement ps) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
