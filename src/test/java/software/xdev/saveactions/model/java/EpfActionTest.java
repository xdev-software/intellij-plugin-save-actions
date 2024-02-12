package software.xdev.saveactions.model.java;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import software.xdev.saveactions.model.Action;


class EpfActionTest
{
	@Test
	void should_epf_action_have_no_duplicate_action()
	{
		final List<Action> actions = EpfAction.stream().map(EpfAction::getAction).collect(toList());
		assertThat(actions).doesNotHaveDuplicates();
	}
	
	@Test
	void should_java_processor_have_valid_type_and_inspection()
	{
		EpfAction.stream().forEach(epfAction -> assertThat(epfAction.getAction().getType()).isNotNull());
		EpfAction.stream().forEach(epfAction -> assertThat(epfAction.getEpfKeys()).isNotEmpty());
	}
}
