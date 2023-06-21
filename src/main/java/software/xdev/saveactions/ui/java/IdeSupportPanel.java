package software.xdev.saveactions.ui.java;

import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileTextField;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import software.xdev.saveactions.ui.Configuration;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static com.intellij.openapi.ui.TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.WEST;

/**
 * @author markiewb
 */
public class IdeSupportPanel {

    private static final String BUTTON = "Reset";
    private static final String EXTENSION = "epf";
    private static final String LABEL = "Use external Eclipse configuration file (.epf)";
    private static final String TITLE = "Eclipse Support";

    private TextFieldWithBrowseButton path;

    public JPanel getPanel(String configurationPath) {
        JPanel panel = new JPanel();
        if (!SaveActionsServiceManager.getService().isJavaAvailable()) {
            return panel;
        }

        panel.setBorder(IdeBorderFactory.createTitledBorder(TITLE));
        panel.setLayout(new BorderLayout());

        JBLabel label = getLabel();
        path = getPath(configurationPath);
        JButton reset = getResetButton(path);

        panel.add(label, WEST);
        panel.add(path, CENTER);
        panel.add(reset, EAST);

        panel.setMaximumSize(new Dimension(Configuration.BOX_LAYOUT_MAX_WIDTH, Configuration.BOX_LAYOUT_MAX_HEIGHT));

        return panel;
    }

    @NotNull
    private JBLabel getLabel() {
        JBLabel label = new JBLabel();
        label.setText(LABEL);
        label.setLabelFor(path);
        return label;
    }

    @NotNull
    private TextFieldWithBrowseButton getPath(String configurationPath) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(EXTENSION);
        FileTextField field = FileChooserFactory.getInstance().createFileTextField(descriptor, null);
        field.getField().setEnabled(false);
        field.getField().setText(configurationPath);
        TextFieldWithBrowseButton resultPath = new TextFieldWithBrowseButton(field.getField());
        resultPath.addBrowseFolderListener(null, null, null, descriptor, TEXT_FIELD_WHOLE_TEXT);
        return resultPath;
    }

    @NotNull
    private JButton getResetButton(TextFieldWithBrowseButton path) {
        JButton reset = new JButton(BUTTON);
        reset.addActionListener(e -> path.setText(""));
        return reset;
    }

    public String getPath() {
        return path == null ? null : path.getText();
    }

    public void setPath(String configurationPath) {
        if (path != null) {
            path.setText(configurationPath);
        }
    }

}
