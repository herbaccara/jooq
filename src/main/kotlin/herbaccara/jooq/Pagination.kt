package herbaccara.jooq

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.impl.DSL
import org.springframework.data.domain.*

class Pagination {

    companion object {

        @JvmStatic
        fun <R : Record, E> ofSlice(
            query: SelectLimitStep<R>,
            pageable: Pageable,
            mapper: (record: R) -> E
        ): Slice<E> {
            val content = query
                .limit(pageable.offset, pageable.pageSize + 1)
                .map(mapper)

            val hasNext = content.size > pageable.pageSize

            return SliceImpl(content.take(pageable.pageSize), pageable, hasNext)
        }

        @JvmStatic
        fun <R : Record, E> ofPage(
            dsl: DSLContext,
            query: SelectLimitStep<R>,
            pageable: Pageable,
            mapper: (record: R) -> E
        ): Page<E> {
            val sql = query.sql

            val content = query
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
