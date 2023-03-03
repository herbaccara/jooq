package herbaccara.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

import java.util.function.Consumer;

public abstract class JooqRepository<R extends Record, T extends TableImpl<R>, ID> {

    final protected DSLContext dsl;
    final protected T table;
    final protected TableField<R, ID> idField;

    public JooqRepository(final DSLContext dsl, final T table, final TableField<R, ID> idField) {
        this.dsl = dsl;
        this.table = table;
        this.idField = idField;
    }

    public @NotNull Result<R> findAll() {
        return findAll(query -> {});
    }

    public @NotNull Result<R> findAll(Consumer<SelectWhereStep<R>> consumer) {
        final SelectWhereStep<R> query = dsl.selectFrom(table);
        consumer.accept(query);
        return query.fetch();
    }
}
