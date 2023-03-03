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

abstract class JooqRepository<R : Record, T : TableImpl<R>, ID>(
    private val dsl: DSLContext,
    private val table: T,
    private val idField: TableField<R, ID>
) {
    fun deleteAllByIdInBatch(vararg id: ID) {
        id.map {
            dsl.deleteFrom(table)
                .where(
                    idField.eq(it)
                )
        }.apply {
            dsl.batch(this).execute()
        }
    }

    fun deleteAllById(vararg id: ID) {
        dsl.deleteFrom(table)
            .where(
                idField.`in`(*id)
            )
            .execute()
    }

    fun deleteAll() {
        dsl.deleteFrom(table).execute()
    }

    fun deleteById(id: ID) {
        deleteAllById(id)
    }

    fun existsByIx(id: ID): Boolean {
        return findById(id) != null
    }

    fun findById(id: ID): R? {
        return dsl
            .selectFrom(table)
            .where(
                idField.eq(id)
            )
            .fetchOne()
    }

    @JvmOverloads
    fun findAll(block: (query: SelectWhereStep<R>) -> Unit = {}): Result<R> {
        return dsl
            .selectFrom(table)
            .also(block)
            .fetch()
    }

    @JvmOverloads
    fun page(pageable: Pageable, block: (query: SelectConditionStep<R>) -> Unit = {}): Page<R> {
        val query = dsl
            .selectFrom(table)
            .where(
                DSL.noCondition()
            )
            .also(block)

        return Pagination.ofPage(dsl, query, pageable) { it }
    }

    @JvmOverloads
    fun slice(pageable: Pageable, block: (query: SelectConditionStep<R>) -> Unit = {}): Slice<R> {
        val query = dsl
            .selectFrom(table)
            .where(
                DSL.noCondition()
            )
            .also(block)

        return Pagination.ofSlice(query, pageable) { it }
    }
}
