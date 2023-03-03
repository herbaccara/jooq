package herbaccara.jooq

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.SelectConditionStep
import org.jooq.SelectWhereStep
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.function.Consumer

abstract class JooqRepository<R : Record, T : TableImpl<R>, ID>(
    protected val dsl: DSLContext,
    protected val table: T,
    protected val idField: TableField<R, ID>
) {
    open fun deleteAllByIdInBatch(vararg id: ID) {
        id.map {
            dsl.deleteFrom(table)
                .where(
                    idField.eq(it)
                )
        }.apply {
            dsl.batch(this).execute()
        }
    }

    open fun deleteAllById(vararg id: ID) {
        dsl.deleteFrom(table)
            .where(
                idField.`in`(*id)
            )
            .execute()
    }

    open fun deleteAll() {
        dsl.deleteFrom(table).execute()
    }

    open fun deleteById(id: ID) {
        deleteAllById(id)
    }

    open fun existsByIx(id: ID): Boolean {
        return findById(id) != null
    }

    open fun findById(id: ID): R? {
        return dsl
            .selectFrom(table)
            .where(
                idField.eq(id)
            )
            .fetchOne()
    }

    @JvmOverloads
    open fun findAll(block: Consumer<SelectWhereStep<R>> = Consumer { }): Result<R> {
        return dsl
            .selectFrom(table)
            .also(block::accept)
            .fetch()
    }

    @JvmOverloads
    open fun page(pageable: Pageable, block: Consumer<SelectConditionStep<R>> = Consumer { }): Page<R> {
        val query = dsl
            .selectFrom(table)
            .where(
                DSL.noCondition()
            )
            .also(block::accept)

        return Pagination.ofPage(dsl, query, pageable) { it }
    }

    @JvmOverloads
    open fun slice(pageable: Pageable, block: Consumer<SelectConditionStep<R>> = Consumer { }): Slice<R> {
        val query = dsl
            .selectFrom(table)
            .where(
                DSL.noCondition()
            )
            .also(block::accept)

        return Pagination.ofSlice(query, pageable) { it }
    }
}
