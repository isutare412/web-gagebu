package me.redshore.web_gagebu.feature.user.mapping;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.redshore.web_gagebu.feature.user.domain.UserRole;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return UserRole.valueOf(dbData);
    }
}
