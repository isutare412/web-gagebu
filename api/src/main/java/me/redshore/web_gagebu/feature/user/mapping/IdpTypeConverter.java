package me.redshore.web_gagebu.feature.user.mapping;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.redshore.web_gagebu.feature.user.domain.IdpType;

@Converter(autoApply = true)
public class IdpTypeConverter implements AttributeConverter<IdpType, String> {

    @Override
    public String convertToDatabaseColumn(IdpType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public IdpType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return IdpType.valueOf(dbData);
    }
}
