package software.xdev.saveactions.processors;

import software.xdev.saveactions.model.Action;
import org.junit.jupiter.api.Test;
import software.xdev.saveactions.processors.GlobalProcessor;
import software.xdev.saveactions.processors.Processor;

import java.util.List;

import static software.xdev.saveactions.model.ActionType.global;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalProcessorTest {

    @Test
    void should_processor_have_no_duplicate_action() {
        List<Action> actions = GlobalProcessor.stream().map(Processor::getAction).collect(toList());
        assertThat(actions).doesNotHaveDuplicates();
    }

    @Test
    void should_processor_have_valid_type_and_inspection() {
        GlobalProcessor.stream()
                .forEach(processor -> assertThat(processor.getAction().getType()).isEqualTo(global));
        GlobalProcessor.stream()
                .forEach(processor -> assertThat(((GlobalProcessor) processor).getCommand()).isNotNull());
    }

    @Test
    void should_action_have_java_processor() {
        Action.stream(global).forEach(action -> assertThat(GlobalProcessor.getProcessorForAction(action)).isNotEmpty());
    }

}
