package org.example.billproject.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.billproject.domain.model.BillStatus;

@Converter(autoApply = true)
public class BillStatusConverter implements AttributeConverter<BillStatus, String> {
    @Override
    public String convertToDatabaseColumn(BillStatus attribute) {
        return attribute == null ? null : attribute.getOracleCode();
    }

    @Override
    public BillStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : BillStatus.fromOracleCode(dbData);
    }
}