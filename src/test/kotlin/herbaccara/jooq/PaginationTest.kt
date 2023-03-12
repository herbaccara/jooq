package herbaccara.jooq

import org.h2.Driver
import org.jooq.Record3
import org.jooq.SQLDialect
import org.jooq.SelectConditionStep
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import java.io.File

class PaginationTest {

    private val connection = Driver().connect("jdbc:h2:mem:test", null)
    private val dsl = DSL.using(connection, SQLDialect.H2).apply {
        execute(File("src/test/resources/db.sql").readText())
    }

    private val table = DSL.table("\"board\"")

    private val id = DSL.field("id", Int::class.java)
    private val title = DSL.field("title", String::class.java)
    private val like = DSL.field("`like`", Int::class.java)

    private val baseQuery = dsl
        .select(
            id,
            title,
            like
        )
        .from(table)
        .where(
            title.contains("게시")
        )

    @Test
    fun update() {
        val query = dsl
            .update(table)
            .set(title, "testttttt")
            .setNull(DSL.noField())
            .where(
                id.eq(1)
            )

        println("isExecutable : " + query.isExecutable)

        val execute = query
            .execute()

        println("execute : " + execute)
    }

    @Test
    fun seek() {
        val query: () -> SelectConditionStep<Record3<Int, String, Int>> = {
            dsl
                .select(
                    id,
                    title,
                    like
                )
                .from(table)
                .where(
                    DSL.noCondition()
                )
        }

        val sort = Sort.by(Order.desc("id"))
        val limit = 10

        val (hasNext, firstPage) = Pagination.ofSeek(dsl, query(), sort, limit, emptyList()) { it }
        if (hasNext) {
            var last = firstPage.lastOrNull()
            while (last != null) {
                val seekValues = listOf(last.get(id)) // id
                val (hasNext2, nextPage) = Pagination.ofSeek(dsl, query(), sort, limit, seekValues) { it }
                if (hasNext2.not()) {
                    println("다음 페이지 없음")
                    break
                }
                last = nextPage.lastOrNull()
            }
        } else {
            println("다음 페이지 없음 (첫 페이지에 모든 데이터 존재)")
        }
    }

    @Test
    fun sort() {
        val pageRequest = PageRequest.of(
            0,
            10,
            Sort.by(
                Order.asc("title"),
                Order.desc("id")
            )
        )
        val pageable = pageRequest.toOptional().get()

        val page = Pagination.ofPage(dsl, baseQuery, pageable) {
            it.value1() to it.value2()
        }

        assertEquals(22, page.totalElements)
        assertTrue(page.hasNext())
    }

    @Test
    fun ofPage() {
        val pageRequest = PageRequest.of(0, 10)
        val pageable = pageRequest.toOptional().get()

        val page = Pagination.ofPage(dsl, baseQuery, pageable) {
            it.value1() to it.value2()
        }

        assertEquals(24, page.totalElements)
        assertTrue(page.hasNext())
    }

    @Test
    fun ofSlice() {
        val pageRequest = PageRequest.of(0, 10)
        val pageable = pageRequest.toOptional().get()

        val slice = Pagination.ofSlice(dsl, baseQuery, pageable) {
            it.value1() to it.value2()
        }

        assertTrue(slice.hasNext())
    }
}
