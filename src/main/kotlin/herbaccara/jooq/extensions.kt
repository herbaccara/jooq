@file:JvmName("JooqUtils")

package herbaccara.jooq

import org.jooq.UpdatableRecord
import org.jooq.impl.DAOImpl
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.page(pageable: Pageable): Page<P> {
    val query = ctx().selectFrom(table).where(DSL.noCondition())
    return Pagination.ofPage(ctx(), query, pageable) { it.into(type) }
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.slice(pageable: Pageable): Slice<P> {
    val query = ctx().selectFrom(table).where(DSL.noCondition())
    return Pagination.ofSlice(query, pageable) { it.into(type) }
}
