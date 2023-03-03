package herbaccara.jooq

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.impl.DSL
import org.springframework.data.domain.*

object Pagination {

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
    @JvmOverloads
    fun <R : Record, E> ofPage(
        dsl: DSLContext,
        query: SelectLimitStep<R>,
        pageable: Pageable,
        countQuery: SelectLimitStep<R>? = null,
        mapper: (record: R) -> E
    ): Page<E> {
        val sql = query.sql

        val content = query
            .limit(pageable.offset, pageable.pageSize)
            .map(mapper)

        val total = if (content.size < pageable.pageSize) {
            content.size
        } else {
            if (countQuery != null) {
                dsl.fetchCount(countQuery)
            } else {
                dsl
                    .select(DSL.count())
                    .from("( $sql )")
                    .single()
                    .value1()
            }
        }

        return PageImpl(content, pageable, total.toLong())
    }
}
