package com.jay.sapapi.domain.converter;

import com.jay.sapapi.domain.MemberRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {

    @Override
    public String convertToDatabaseColumn(MemberRole role) {
        if (role == null) {
            return null;
        }
        return role.name();
    }

    @Override
    public MemberRole convertToEntityAttribute(String roleName) {
        if (roleName == null) {
            return null;
        }
        try {
            return MemberRole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
