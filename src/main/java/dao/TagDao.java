package dao;

import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }


    public boolean isReceiptTagged(int receiptID, String tagName) {
        List<TagsRecord> tagRecord = dsl.selectFrom(TAGS)
                .where(TAGS.RECEIPT_ID.eq(receiptID).and(TAGS.TAG.eq(tagName)))
                .fetch();
        return (tagRecord.size() > 0);
    }

    public void untagReceipt(int receiptID, String tagName) {
        dsl.delete(TAGS)
                .where(TAGS.RECEIPT_ID.eq(receiptID))
                .and(TAGS.TAG.eq(tagName))
                .execute();
    }

    public void tagReceipt(int receiptID, String tagName) {
        dsl.insertInto(TAGS, TAGS.RECEIPT_ID, TAGS.TAG)
                .values(receiptID, tagName)
                .execute();
    }

    public List<Integer> getReceiptIDsFromTag(String tagName) {
        return dsl.selectFrom(TAGS)
                .where(TAGS.TAG.eq(tagName))
                .fetch()
                .getValues(TAGS.RECEIPT_ID);

    }

}
