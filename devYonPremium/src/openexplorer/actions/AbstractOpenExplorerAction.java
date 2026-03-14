package openexplorer.actions;

import java.io.IOException;
import openexplorer.Activator;
import openexplorer.util.Messages;
import openexplorer.util.OperatingSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractOpenExplorerAction implements IActionDelegate, IPropertyChangeListener {
    protected IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    protected Shell shell;
    protected ISelection currentSelection;
    protected String systemBrowser;

    public AbstractOpenExplorerAction() {
        this.systemBrowser = OperatingSystem.INSTANCE.getSystemBrowser();
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (OperatingSystem.INSTANCE.isLinux()) {
            this.systemBrowser = OperatingSystem.INSTANCE.getSystemBrowser();
        }

    }

    public void run(IAction action) {
        if (this.currentSelection != null && !this.currentSelection.isEmpty()) {
            if (this.currentSelection instanceof ITreeSelection) {
                ITreeSelection treeSelection = (ITreeSelection)this.currentSelection;
                TreePath[] paths = treeSelection.getPaths();

                for(int i = 0; i < paths.length; ++i) {
                    TreePath path = paths[i];
                    IResource resource = null;
                    Object segment = path.getLastSegment();
                    if (segment instanceof IResource) {
                        resource = (IResource)segment;
                    } else if (segment instanceof IJavaElement) {
                        resource = ((IJavaElement)segment).getResource();
                    }

                    if (resource != null) {
                        String browser = this.systemBrowser;
                        String location = resource.getLocation().toOSString();
                        if (resource instanceof IFile) {
                            location = ((IFile)resource).getParent().getLocation().toOSString();
                            if (OperatingSystem.INSTANCE.isWindows()) {
                                browser = this.systemBrowser + " /select,";
                                location = ((IFile)resource).getLocation().toOSString();
                            }
                        }

                        this.openInBrowser(browser, location);
                    }
                }
            } else if (this.currentSelection instanceof ITextSelection || this.currentSelection instanceof IStructuredSelection) {
                IEditorPart editor = this.window.getActivePage().getActiveEditor();
                if (editor != null) {
                    IFile current_editing_file = (IFile)editor.getEditorInput().getAdapter(IFile.class);
                    String browser = this.systemBrowser;
                    String location = current_editing_file.getParent().getLocation().toOSString();
                    if (OperatingSystem.INSTANCE.isWindows()) {
                        browser = this.systemBrowser + " /select,";
                        location = current_editing_file.getLocation().toOSString();
                    }

                    this.openInBrowser(browser, location);
                }
            }

        }
    }

    protected void openInBrowser(String browser, String location) {
        try {
            if (OperatingSystem.INSTANCE.isWindows()) {
                Runtime.getRuntime().exec(browser + " \"" + location + "\"");
            } else {
                Runtime.getRuntime().exec(new String[]{browser, location});
            }
        } catch (IOException e) {
            MessageDialog.openError(this.shell, Messages.OpenExploer_Error, Messages.Cant_Open + " \"" + location + "\"");
            e.printStackTrace();
        }

    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.currentSelection = selection;
    }
}
