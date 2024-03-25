package scs.planus.global.util.slackAlarm;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import scs.planus.global.util.logTracker.entity.ExceptionLog;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class SlackAlarmGenerator {
    private final Slack slackClient;
    private final String url;

    public SlackAlarmGenerator(@Value("${slack.webhook.url}") String url) {
        this.url = url;
        this.slackClient = Slack.getInstance();
    }

    @Async
    public void sendExceptionLog(ExceptionLog exceptionLog){
        try {
            Payload payload = Payload.builder()
                    .text(":warning: 알수없는 서버 예외 발생!")
                    .attachments(
                            List.of(generateSlackAttachment(exceptionLog))
                    ).build();

            slackClient.send(url, payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Attachment generateSlackAttachment(ExceptionLog exceptionLog) {
        return Attachment.builder()
                .color("ff0000")
                .title(exceptionLog.getCreatedAt() + " 예외 발생")
                .fields(List.of(
                                generateSlackField("[Lod ID] ", exceptionLog.getId().toString(), true),
                                generateSlackField("[Request] ", exceptionLog.getMetaData().getHttpMethod() + "  " + exceptionLog.getMetaData().getRequestURI(), true),
                                generateSlackField("[Class Name] ", exceptionLog.getMetaData().getClassName(), false),
                                generateSlackField("[Method Name] ", exceptionLog.getMetaData().getMethodName(), true),
                                generateSlackField("[Line Number] ", exceptionLog.getMetaData().getLineNumber().toString() + " line", true),
                                generateSlackField("[Exception Type] ", exceptionLog.getExceptionData().getExceptionType(), true),
                                generateSlackField("[Message] ", exceptionLog.getExceptionData().getMessage(), true)
                        )
                )
                .build();
    }

    private Field generateSlackField(String title, String value, boolean ShortEnough) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(ShortEnough)
                .build();
    }
}
