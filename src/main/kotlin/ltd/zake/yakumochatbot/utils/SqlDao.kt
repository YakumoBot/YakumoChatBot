package ltd.zake.yakumochatbot.utils

import java.sql.ResultSet
import java.sql.Statement

class SqlDao {
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

    /**
     * 插入一段信息
     */
    fun writeSqlData(statement: Statement, table: String, vararg args: Any) {
        var sb = args.contentToString().replace("[", "").replace("]", "")
        statement.executeUpdate("INSERT  INTO ${table} VALUES ($sb)")
    }

    /**
     * 更新指定列的指定行的信息
     */
    fun updateDate(statement: Statement, table: String, list: String, value: String, where: String) {
        statement.executeUpdate("UPDATE ${table} SET ${list} = ${value} WHERE ${where} ")
    }

    /**
     * 更新指定列的信息
     */
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
