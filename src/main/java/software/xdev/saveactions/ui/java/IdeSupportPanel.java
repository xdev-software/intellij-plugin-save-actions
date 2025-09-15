package software.xdev.saveactions.ui.java;

import static com.intellij.openapi.ui.TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.WEST;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileTextField;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;

import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.ui.Configuration;


/**
 * @author markiewb
 */
public class IdeSupportPanel
{
	private static final String BUTTON = "Reset";
	private static final String EXTENSION = "epf";
	private static final String LABEL = "Use external Eclipse configuration file (.epf)";
	private static final String TITLE = "Eclipse Support";
	
	private TextFieldWithBrowseButton path;
	
	public JPanel getPanel(final String configurationPath)
	{
		final JPanel panel = new JPanel();
		if(!SaveActionsServiceManager.getService().isJavaAvailable())
		{
			return panel;
		}
		
		panel.setBorder(IdeBorderFactory.createTitledBorder(TITLE));
		panel.setLayout(new BorderLayout());
		
		final JBLabel label = this.getLabel();
		this.path = this.getPath(configurationPath);
		final JButton reset = this.getResetButton(this.path);
		
		panel.add(label, WEST);
		panel.add(this.path, CENTER);
		panel.add(reset, EAST);
		
		panel.setMaximumSize(new Dimension(Configuration.BOX_LAYOUT_MAX_WIDTH, Configuration.BOX_LAYOUT_MAX_HEIGHT));
		
		return panel;
	}
	
	@NotNull
	private JBLabel getLabel()
	{
		final JBLabel label = new JBLabel();
		label.setText(LABEL);
		label.setLabelFor(this.path);
		return label;
	}
	
	@NotNull
	private TextFieldWithBrowseButton getPath(final String configurationPath)
	{
		final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(EXTENSION);
		final FileTextField field = FileChooserFactory.getInstance().createFileTextField(descriptor, null);
		field.getField().setEnabled(false);
		field.getField().setText(configurationPath);
		final TextFieldWithBrowseButton resultPath = new TextFieldWithBrowseButton(field.getField());
		resultPath.addBrowseFolderListener(null, descriptor, TEXT_FIELD_WHOLE_TEXT);
		return resultPath;
	}
	
	@NotNull
	private JButton getResetButton(final TextFieldWithBrowseButton path)
	{
		final JButton reset = new JButton(BUTTON);
		reset.addActionListener(e -> path.setText(""));
		return reset;
	}
	
	public String getPath()
	{
		return this.path == null ? null : this.path.getText();
	}
	
	public void setPath(final String configurationPath)
	{
		if(this.path != null)
		{
			this.path.setText(configurationPath);
		}
	}
}
