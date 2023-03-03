package herbaccara.jooq

import org.h2.Driver
import org.jooq.SQLDialect
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

    private val id = table.field("id", Int::class.java)
    private val title = table.field("title", String::class.java)

    private val baseQuery = dsl
        .select(
            id,
            title
        )
        .from(table)
        .where(
            DSL.noCondition()
        )

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

        assertEquals(22, page.totalElements)
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
