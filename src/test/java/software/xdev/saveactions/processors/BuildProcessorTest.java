/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Alexandre DuBreuil
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package software.xdev.saveactions.processors;

import software.xdev.saveactions.model.Action;
import org.junit.jupiter.api.Test;
import software.xdev.saveactions.processors.BuildProcessor;
import software.xdev.saveactions.processors.Processor;

import java.util.List;

import static software.xdev.saveactions.model.ActionType.build;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class BuildProcessorTest {

    @Test
    void should_processor_have_no_duplicate_action() {
        List<Action> actions = BuildProcessor.stream().map(Processor::getAction).collect(toList());
        assertThat(actions).doesNotHaveDuplicates();
    }

    @Test
    void should_processor_have_valid_type_and_inspection() {
        BuildProcessor.stream()
                .forEach(processor -> assertThat(processor.getAction().getType()).isEqualTo(build));
        BuildProcessor.stream()
                .forEach(processor -> assertThat(((BuildProcessor) processor).getCommand()).isNotNull());
    }

    @Test
    void should_action_have_java_processor() {
        Action.stream(build).forEach(action -> assertThat(BuildProcessor.getProcessorForAction(action)).isNotEmpty());
    }

}
