package ltd.zake.YakumoChatBot.tool

import java.sql.ResultSet
import java.sql.Statement

open class SqlStorage {
    /**
     * 搜索全表
     */
    fun readSqlData(statement: Statement, table: String): ResultSet {
        return statement.executeQuery("SELECT * FROM ${table}")
    }

    /**
     * 搜索指定列
     */
    fun readSqlData(statement: Statement, list: String, table: String): ResultSet {
        return statement.executeQuery("SELECT ${list} FROM ${table}")
    }

    /**
     * 按位置搜索全表
     */
    fun readSqlData(statement: Statement, table: String, where: String, isWhere: Boolean = true): ResultSet {
        return statement.executeQuery("SELECT * FROM ${table} WHERE ${where}")
    }

    /**
     * 按位置搜索指定列
     */
    fun readSqlData(statement: Statement, list: String, table: String, where: String): ResultSet {
        return statement.executeQuery("SELECT ${list} FROM ${table} WHERE ${where}")
    }

    fun writeSqlData(statement: Statement, table: String, value: String) {
        var VALUE = stringBulider(value.toString())
        statement.executeUpdate("INSERT  INTO ${table} VALUES (${VALUE})")
    }

    fun updateDate(statement: Statement, table: String, list: String, value: String, where: String) {
        statement.executeUpdate("UPDATE ${table} SET ${list} = ${value} WHERE ${where} ")
    }

    fun updateDate(statement: Statement, table: String, list: String, value: String) {
        statement.executeUpdate("UPDATE ${table} SET ${list} = ${value}")
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
