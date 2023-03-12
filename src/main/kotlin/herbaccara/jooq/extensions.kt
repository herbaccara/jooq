@file:JvmName("JooqUtils")

package herbaccara.jooq

import org.jooq.*
import org.jooq.impl.DAOImpl
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.function.Consumer

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.page(pageable: Pageable): Page<P> {
    val query = ctx().selectFrom(table).where(DSL.noCondition())
    return Pagination.ofPage(ctx(), query, pageable) { it.into(type) }
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.slice(pageable: Pageable): Slice<P> {
    val query = ctx().selectFrom(table).where(DSL.noCondition())
    return Pagination.ofSlice(ctx(), query, pageable) { it.into(type) }
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.findOne(block: Consumer<SelectWhereStep<R>>): R? {
    return ctx().selectFrom(table).also(block::accept).fetchOne()
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.find(block: Consumer<SelectWhereStep<R>>): Result<R> {
    return ctx().selectFrom(table).also(block::accept).fetch()
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.count(block: Consumer<SelectJoinStep<Record1<Int>>>): Long {
    return ctx().selectCount().from(table).also(block::accept).fetchSingle(0, Long::class.java)!!
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.update(id: T, block: Consumer<UpdateSetFirstStep<R>>): Int {
    val pk = table.primaryKey?.fieldsArray ?: return 0
    val query = ctx().update(table)
        .also(block::accept)
        .setNull(DSL.noField())
        .where(
            if (pk.size == 1) {
                @Suppress("UNCHECKED_CAST")
                (pk[0] as Field<Any>).equal(pk[0].dataType.convert(id))
            } else {
                DSL.row(*pk).equal(id as Record)
            }
        )
    if (query.isExecutable.not()) return 0
    return query.execute()
}

fun <R : UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.delete(block: Consumer<DeleteUsingStep<R>>): Int {
    return ctx().deleteFrom(table).also(block::accept).execute()
}
