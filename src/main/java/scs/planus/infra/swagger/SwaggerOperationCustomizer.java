package scs.planus.infra.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalTime;

@Component
public class SwaggerOperationCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Schema<LocalTime> timeSchema = new Schema<>();
        timeSchema.example("23:30");
        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, timeSchema);
        return operation;
    }
}
