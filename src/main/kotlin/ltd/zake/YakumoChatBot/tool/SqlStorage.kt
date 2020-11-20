package ltd.zake.YakumoChatBot.tool

import java.sql.ResultSet
import java.sql.Statement

open class SqlStorage {
    fun readSqlDate(statement: Statement, table: String): ResultSet {
        return statement.executeQuery("SELECT * FROM ${table}")
    }

    fun readSqlDate(statement: Statement, list: String, table: String): ResultSet {
        return statement.executeQuery("SELECT ${list} FROM ${table}")
    }

    fun writeSqlDate(statement: Statement, table: String, value: String) {
        var VALUE = stringBulider(value.toString())
        statement.executeUpdate("INSERT  INTO ${table} VALUES (${VALUE})")
    }


    fun stringBulider(string: String): String {
        var output: String = string
        output.replace("select", "")
        output.replace("insert", "")
        output.replace("update", "")
        output.replace("delete", "")
        output.replace("drop", "")
        output.replace("truncate", "")
        output.replace("declare", "")
        return output
    }

}
