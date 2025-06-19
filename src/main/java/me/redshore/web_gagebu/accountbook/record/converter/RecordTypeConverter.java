package me.redshore.web_gagebu.accountbook.record.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.redshore.web_gagebu.accountbook.record.RecordType;

@Converter(autoApply = true)
public class RecordTypeConverter implements AttributeConverter<RecordType, String> {

    @Override
    public String convertToDatabaseColumn(RecordType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public RecordType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return RecordType.valueOf(dbData);
    }

}
