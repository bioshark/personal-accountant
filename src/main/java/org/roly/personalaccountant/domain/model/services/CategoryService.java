package org.roly.personalaccountant.domain.model.services;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.lower;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.table;

import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private static final String CATEGORY = "category";
    private static final Table<Record> TABLE_CATEGORY = table(CATEGORY);
    private static final String NAME = "name";
    private static final Field<String> FIELD_NAME = field(NAME, String.class);
    private static final String ARCHIVED = "archived";
    private static final Field<Boolean> FIELD_ARCHIVE = field(ARCHIVED, Boolean.class);
    private final DSLContext dsl;

    @Autowired
    public CategoryService(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Active (non-archived) category names, alphabetical — feeds the datalist.
     */
    public List<String> listActive() {
        return dsl.select(FIELD_NAME)
                .from(TABLE_CATEGORY)
                .where(FIELD_ARCHIVE.isFalse())
                .orderBy(FIELD_NAME)
                .fetch(FIELD_NAME);
    }

    /**
     * Insert a new category if it isn't already present (case-insensitive), trimmed.
     */
    @Transactional
    public void addIfAbsent(String newCategory) {
        if (newCategory == null || newCategory.isBlank()) {
            return;
        }
        String name = newCategory.trim();
        boolean exists = dsl.fetchExists(
                selectOne().from(TABLE_CATEGORY)
                        .where(lower(FIELD_NAME).eq(name.toLowerCase())));

        if (!exists) {
            dsl.insertInto(TABLE_CATEGORY, field(NAME))
                    .values(name)
                    .execute();
        }
    }

    /**
     * Optional: hide a category without deleting history.
     */
    @Transactional
    public void archive(String name) {
        dsl.update(TABLE_CATEGORY)
                .set(FIELD_ARCHIVE, true)
                .where(FIELD_NAME.eq(name))
                .execute();
    }
}
