package epam.lab.gymapp.config.aws.sqs;

import epam.lab.gymapp.dto.request.trainerWorkloadRequest.TrainerWorkloadRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageSender {

    @Value("${cloud.aws.queue.name}")
    String awsQueueName;

    @Autowired
    SqsTemplate sqsTemplate;


    @GetMapping("/aws/{message}")
    public void sendMessage(@PathVariable String message) {
        sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(awsQueueName).payload(message));
        System.out.println(message + " = sent successfully to the " + awsQueueName);
    }

    ;
}
