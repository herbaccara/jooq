package herbaccara.jooq

import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.data.domain.*

class Pagination {

    companion object {

        @JvmStatic
        fun sortFields(sort: Sort, dsl: DSLContext): List<SortField<Any>> {
            return sortFields(sort, dsl.configuration().dialect())
        }

        @JvmStatic
        fun sortFields(sort: Sort, dialect: SQLDialect): List<SortField<Any>> {
            if (sort.isEmpty) return emptyList()

            val quote = when (dialect) {
                SQLDialect.MYSQL, SQLDialect.H2 -> "`"
                else -> "\""
            }

            return sort.map { s ->
                val field = DSL.field("${quote}${s.property}$quote")
                val sortField = if (s.direction == Sort.Direction.ASC) field.asc() else field.desc()
                when (s.nullHandling) {
                    Sort.NullHandling.NULLS_FIRST -> sortField.nullsFirst()
                    Sort.NullHandling.NULLS_LAST -> sortField.nullsLast()
                    else -> sortField
                }
            }.toList()
        }

        @JvmStatic
        fun <R : Record, E> ofSlice(
            dsl: DSLContext,
            query: SelectConditionStep<R>,
            pageable: Pageable,
            mapper: (record: R) -> E
        ): Slice<E> {
            val content = query
                .orderBy(sortFields(pageable.sort, dsl))
                .limit(pageable.offset, pageable.pageSize + 1)
                .map(mapper)

            val hasNext = content.size > pageable.pageSize

            return SliceImpl(content.take(pageable.pageSize), pageable, hasNext)
        }

        @JvmStatic
        fun <R : Record, E> ofPage(
            dsl: DSLContext,
            query: SelectConditionStep<R>,
            pageable: Pageable,
            mapper: (record: R) -> E
        ): Page<E> {
            val sql = query.sql

            val content = query
                .orderBy(sortFields(pageable.sort, dsl))
                .limit(pageable.offset, pageable.pageSize)
                .map(mapper)

            val total = if (content.size < pageable.pageSize) {
                content.size
            } else {
                dsl
                    .select(DSL.count())
                    .from("( $sql )")
                    .single()
                    .value1()
            }

            return PageImpl(content, pageable, total.toLong())
        }
    }
}
