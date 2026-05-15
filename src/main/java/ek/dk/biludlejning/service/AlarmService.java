package ek.dk.biludlejning.service;

import ek.dk.biludlejning.model.AlarmRule;
import org.springframework.stereotype.Service;

@Service
public class AlarmService {

    public boolean evaluate(Double currentValue, AlarmRule rule){
        if((rule == null) || (rule.getOperator() == null) || (rule.getCompareValue() == null) || (currentValue == null)){
            return false;
        }

        return switch (rule.getOperator()){
            case GREATER_THAN -> currentValue > rule.getCompareValue();
            case LESS_THAN -> currentValue < rule.getCompareValue();
            case EQUALS -> Double.compare(currentValue, rule.getCompareValue()) == 0;
        };
    }
}
