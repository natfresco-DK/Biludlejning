package ek.dk.biludlejning.model;

public class AlarmRule {
    private String fieldName;
    private AlarmOperator operator;
    private Double compareValue;


    public AlarmRule() {
    }


    public AlarmRule(String fieldName, AlarmOperator operator, Double compareValue) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.compareValue = compareValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public AlarmOperator getOperator() {
        return operator;
    }

    public void setOperator(AlarmOperator operator) {
        this.operator = operator;
    }

    public Double getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(double compareValue) {
        this.compareValue = compareValue;
    }
}
