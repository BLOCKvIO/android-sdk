package io.blockv.common.internal.repository

interface Mapper<T> {

  fun model(data: Map<String, Any>): T

  fun db(data: T): Table.Row

  val table: Table

  class Table(val name: String, val columns: List<Field>) {

    class Row(val id: String, val values: List<Value>) {
      class Builder(val id: String) {
        val list = ArrayList<Value>()

        fun addValue(value: Value): Builder {
          list.add(value)
          return this
        }

        fun addValue(name: String, value: Any): Builder {
          list.add(
            Value(
              name,
              when (value::class) {
                String::class -> "'$value'"
                Int::class,
                Double::class,
                Float::class,
                Long::class -> "$value"
                Boolean::class -> "${if (value == true) 1 else 0}"
                else -> "'$value'"
              }
            )
          )
          return this
        }

        fun build(): Row {
          return Row(id, list)
        }
      }
    }

    class Value(val name: String, val value: String)

    class Field(val name: String, val type: Type)

    override fun hashCode(): Int {
      return toString().hashCode()
    }

    override fun toString(): String {
      var out = "{table:$name, columns:["
      columns.sortedBy { it.name }.forEach {
        out += "[${it.name},${it.type.name}],"
      }
      out += "]}"
      return out
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false
      return other.toString() == toString()
    }

    enum class Type {
      BOOLEAN,
      STRING,
      INTEGER,
      FLOAT,
      DOUBLE,
      LONG
    }

    class Builder(val name: String) {
      val list = ArrayList<Field>()
      fun addField(field: Field): Builder {
        list.add(field)
        return this
      }

      fun addField(name: String, type: Type): Builder {
        list.add(Field(name, type))
        return this
      }

      fun build(): Table {
        return Table(name, list)
      }
    }
  }
}