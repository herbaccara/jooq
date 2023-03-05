package herbaccara.jooq

import org.jooq.*
import org.jooq.SQLDialect.*
import org.jooq.impl.DSL
import org.springframework.data.domain.*
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.NullHandling

class Pagination {

    companion object {

        @JvmStatic
        fun sortFields(sort: Sort, dsl: DSLContext): List<SortField<Any>> {
            return sortFields(sort, dsl.configuration().dialect())
        }

        @JvmStatic
        fun sortFields(sort: Sort, dialect: SQLDialect): List<SortField<Any>> {
            val quote = when (dialect) {
                MYSQL, MARIADB, H2 -> "`"
                else -> "\""
            }
            return sortFields(sort, quote)
        }

        @JvmStatic
        fun sortFields(sort: Sort, quote: String): List<SortField<Any>> {
            if (sort.isEmpty) return emptyList()

            return sort.map { s ->
                val field = DSL.field("${quote}${s.property}$quote")
                val sortField = if (s.direction == Direction.ASC) field.asc() else field.desc()
                when (s.nullHandling) {
                    NullHandling.NULLS_FIRST -> sortField.nullsFirst()
                    NullHandling.NULLS_LAST -> sortField.nullsLast()
                    else -> sortField
                }
            }.toList()
        }

        // https://blog.jooq.org/faster-sql-paging-with-jooq-using-the-seek-method/
        @JvmStatic
        fun <R : Record, E> ofSeek(
            dsl: DSLContext,
            query: SelectConditionStep<R>,
            sort: Sort,
            limit: Int,
            seekValues: List<Any?>,
            mapper: (record: R) -> E
        ): Seek<E> {
            val sortFields = sortFields(sort, dsl)

            if (seekValues.isNotEmpty()) {
                if (sortFields.size != seekValues.size) {
                    throw IllegalArgumentException("Size of seekValues and Sort do not match.")
                }
            }

            val content = query
                .orderBy(sortFields)
                .apply {
                    if (seekValues.isNotEmpty()) {
                        seek(*seekValues.toTypedArray())
                    }
                }
                .limit(limit + 1)
                .map(mapper)

            val hasNext = content.size > limit

            return Seek(hasNext, content.take(limit))
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
            val total = dsl.fetchCount(query)

            val content = if (total == 0) {
                emptyList()
            } else {
                query
                    .orderBy(sortFields(pageable.sort, dsl))
                    .limit(pageable.offset, pageable.pageSize)
                    .map(mapper)
            }

            return PageImpl(content, pageable, total.toLong())
        }
    }
}
