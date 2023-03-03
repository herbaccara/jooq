package herbaccara.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

import java.util.function.Consumer;

public class JooqRepository2<R extends Record, T extends TableImpl<R>, ID> {

    final protected DSLContext dsl;
    final protected T table;
    final protected TableField<R, ID> idField;

    public JooqRepository2(final DSLContext dsl, final T table, final TableField<R, ID> idField) {
        this.dsl = dsl;
        this.table = table;
        this.idField = idField;
    }

    public @NotNull Result<R> findAll() {
        return findAll(query -> {
        });
    }

    public @NotNull Result<R> findAll(final Consumer<SelectWhereStep<R>> block) {
        final SelectWhereStep<R> query = dsl
                .selectFrom(table);
        block.accept(query);
        return query.fetch();
    }
}
