package herbaccara.jooq;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;

/**
 * Spring specific {@link DAOImpl} override.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public abstract class AbstractSpringDAOImpl<R extends UpdatableRecord<R>, P, T> extends DAOImpl<R, P, T> {

    protected AbstractSpringDAOImpl(Table<R> table, Class<P> type) {
        super(table, type);
    }

    protected AbstractSpringDAOImpl(Table<R> table, Class<P> type, Configuration configuration) {
        super(table, type, configuration);
    }

    @Override
    public long count() {
        return super.count();
    }

    @Override
    public boolean exists(P object) {
        return super.exists(object);
    }

    @Override
    public boolean existsById(T id) {
        return super.existsById(id);
    }

    @Override
    public <Z> List<P> fetch(Field<Z> field, Collection<? extends Z> values) {
        return super.fetch(field, values);
    }

    @Override
    public <Z> List<P> fetch(Field<Z> field, Z... values) {
        return super.fetch(field, values);
    }

    @Override
    public <Z> P fetchOne(Field<Z> field, Z value) {
        return super.fetchOne(field, value);
    }

    @Override
    public <Z> Optional<P> fetchOptional(Field<Z> field, Z value) {
        return super.fetchOptional(field, value);
    }

    @Override
    public <Z> List<P> fetchRange(Field<Z> field, Z lowerInclusive, Z upperInclusive) {
        return super.fetchRange(field, lowerInclusive, upperInclusive);
    }

    @Override
    public List<P> findAll() {
        return super.findAll();
    }

    @Override
    public P findById(T id) {
        return super.findById(id);
    }

    @Override
    public Optional<P> findOptionalById(T id) {
        return super.findOptionalById(id);
    }

    @Override
    public void insert(Collection<P> objs) {
        super.insert(objs);
    }

    @Override
    public void insert(P obj) {
        super.insert(obj);
    }

    @Override
    public void insert(P... objs) {
        super.insert(objs);
    }

    @Override
    public void update(Collection<P> objs) {
        super.update(objs);
    }

    @Override
    public void update(P obj) {
        super.update(obj);
    }

    @Override
    public void update(P... objs) {
        super.update(objs);
    }

    @Override
    public void merge(Collection<P> objs) {
        super.merge(objs);
    }

    @Override
    public void merge(P obj) {
        super.merge(obj);
    }

    @Override
    public void merge(P... objs) {
        super.merge(objs);
    }

    @Override
    public void delete(Collection<P> objs) {
        super.delete(objs);
    }

    @Override
    public void delete(P obj) {
        super.delete(obj);
    }

    @Override
    public void delete(P... objs) {
        super.delete(objs);
    }

    @Override
    public void deleteById(Collection<T> ids) {
        super.deleteById(ids);
    }

    @Override
    public void deleteById(T id) {
        super.deleteById(id);
    }

    @Override
    public void deleteById(T... ids) {
        super.deleteById(ids);
    }
}
