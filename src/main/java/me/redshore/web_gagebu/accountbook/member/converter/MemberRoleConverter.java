package me.redshore.web_gagebu.accountbook.member.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.redshore.web_gagebu.accountbook.member.MemberRole;

@Converter(autoApply = true)
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {

    @Override
    public String convertToDatabaseColumn(MemberRole attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public MemberRole convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return MemberRole.valueOf(dbData);
    }
}
