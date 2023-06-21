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

package software.xdev.saveactions.core.service.impl;

import software.xdev.saveactions.processors.BuildProcessor;
import software.xdev.saveactions.processors.GlobalProcessor;

import static software.xdev.saveactions.model.StorageFactory.DEFAULT;

/**
 * This ApplicationService implementation is used for all IDE flavors that are not handling JAVA.
 * <p/>
 * It is assigned as ExtensionPoint from inside plugin.xml. In terms of IDEs using Java this service is overridden
 * by the extended JAVA based version {@link SaveActionsJavaService}. Hence, it will not be loaded for Intellij IDEA,
 * Android Studio a.s.o.
 * <p/>
 * Services must be final classes as per definition. That is the reason to use an abstract class here.
 * <p/>
 *
 * @see AbstractSaveActionsService
 * @since 2.4.0
 */
public final class SaveActionsDefaultService extends AbstractSaveActionsService {

    public SaveActionsDefaultService() {
        super(DEFAULT);
        addProcessors(BuildProcessor.stream());
        addProcessors(GlobalProcessor.stream());
    }
}
