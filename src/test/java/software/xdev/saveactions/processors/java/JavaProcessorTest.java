package software.xdev.saveactions.processors.java;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static software.xdev.saveactions.model.ActionType.java;

import java.util.List;

import org.junit.jupiter.api.Test;

import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.processors.Processor;


class JavaProcessorTest
{
	@Test
	void should_java_processor_have_no_duplicate_action()
	{
		final List<Action> actions = JavaProcessor.stream().map(Processor::getAction).collect(toList());
		assertThat(actions).doesNotHaveDuplicates();
	}
	
	@Test
	void should_java_processor_have_valid_type_and_inspection()
	{
		JavaProcessor.stream()
			.forEach(processor -> assertThat(processor.getAction().getType()).isEqualTo(java));
		JavaProcessor.stream()
			.forEach(processor -> assertThat(((JavaProcessor)processor).getInspection()).isNotNull());
	}
	
	@Test
	void should_java_action_have_java_processor()
	{
		Action.stream(java).forEach(action -> assertThat(JavaProcessor.getProcessorForAction(action)).isNotEmpty());
	}
}
