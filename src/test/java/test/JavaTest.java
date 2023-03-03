package test;

import herbaccara.jooq.Pagination;
import kotlin.Pair;
import org.h2.Driver;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

public class JavaTest {

    @Test
    public void test() throws SQLException, IOException {
        final Connection connect = new Driver().connect("jdbc:h2:mem:test", null);
        final DSLContext dsl = DSL.using(connect, SQLDialect.H2);
        dsl.execute(new String(Files.readAllBytes(new File("src/test/resources/db.sql").toPath())));

        final Table<Record> table = DSL.table("\"board\"");
        final Field<Integer> id = table.field("id", Integer.class);
        final Field<String> title = table.field("title", String.class);

        final @NotNull SelectConditionStep<Record2<Integer, String>> query = dsl
                .select(
                        id,
                        title
                )
                .from(table)
                .where(
                        DSL.condition("1=1")
                );

        final PageRequest pageRequest = PageRequest.of(0, 10);
        final Pageable pageable = pageRequest.toOptional().get();

        final Page<Pair<Integer, String>> page = Pagination.ofPage(dsl, query, pageable, r -> {
            return new Pair<>(r.value1(), r.value2());
        });
        System.out.println();
    }
}
