package software.xdev.saveactions.processors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static software.xdev.saveactions.model.ActionType.build;

import java.util.List;

import org.junit.jupiter.api.Test;

import software.xdev.saveactions.model.Action;


class BuildProcessorTest
{
	@Test
	void should_processor_have_no_duplicate_action()
	{
		final List<Action> actions = BuildProcessor.stream().map(Processor::getAction).collect(toList());
		assertThat(actions).doesNotHaveDuplicates();
	}
	
	@Test
	void should_processor_have_valid_type_and_inspection()
	{
		BuildProcessor.stream()
			.forEach(processor -> assertThat(processor.getAction().getType()).isEqualTo(build));
		BuildProcessor.stream()
			.forEach(processor -> assertThat(((BuildProcessor)processor).getCommand()).isNotNull());
	}
	
	@Test
	void should_action_have_java_processor()
	{
		Action.stream(build).forEach(action -> assertThat(BuildProcessor.getProcessorForAction(action)).isNotEmpty());
	}
}
