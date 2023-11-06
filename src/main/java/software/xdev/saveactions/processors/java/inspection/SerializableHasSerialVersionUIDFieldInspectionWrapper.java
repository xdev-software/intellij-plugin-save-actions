package software.xdev.saveactions.processors.java.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.diagnostic.Logger;
import software.xdev.saveactions.core.service.SaveActionsService;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class changes package between intellij versions.
 *
 * @see com.intellij.codeInspection.SerializableHasSerialVersionUidFieldInspection
 */
public class SerializableHasSerialVersionUIDFieldInspectionWrapper {

    private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);

    private SerializableHasSerialVersionUIDFieldInspectionWrapper() {
    }

    public static LocalInspectionTool get() {
        return Arrays.stream(SerializableClass.values())
                .map(SerializableClass::getInspectionInstance)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot find inspection tool SerializableHasSerialVersionUIDFieldInspection"));
    }

    private enum SerializableClass {
        CLASS_NAME_INTELLIJ_2021_3("com.intellij.codeInspection.SerializableHasSerialVersionUidFieldInspection",
                "software.xdev.saveactions.processors.java.inspection.CustomSerializableHasSerialVersionUidFieldInspection");

        /**
         * Field className: Inspection class provided by IDE
         */
        private final String className;

        /**
         * Field targetClass: Inspection class to run. Needed to apply wrapper class for Idea 2021.3 and up.
         *
         * @see CustomSerializableHasSerialVersionUidFieldInspection
         */
        private final String targetClass;

        SerializableClass(String className, String targetClass) {
            this.className = className;
            this.targetClass = targetClass;
        }

        public LocalInspectionTool getInspectionInstance() {
            try {
                Class.forName(className).asSubclass(LocalInspectionTool.class);
                Class<? extends LocalInspectionTool> targetInspectionClass =
                        Class.forName(targetClass).asSubclass(LocalInspectionTool.class);
                LOGGER.info(String.format("Found serial version uid class %s", targetInspectionClass.getName()));
                return targetInspectionClass.cast(targetInspectionClass.getDeclaredConstructor().newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException
                     | InvocationTargetException e) {
                return null;
            }
        }
    }
}
