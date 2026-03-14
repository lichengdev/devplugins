package openexplorer.actions;

import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import openexplorer.util.Messages;
import org.eclipse.ui.IEditorPart;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.PropertyChangeEvent;
import openexplorer.Activator;
import openexplorer.util.OperatingSystem;
import org.eclipse.ui.PlatformUI;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IActionDelegate;

public abstract class AbstractOpenExplorerAction implements IActionDelegate, IPropertyChangeListener
{
    protected IWorkbenchWindow window;
    protected Shell shell;
    protected ISelection currentSelection;
    protected String systemBrowser;
    
    public AbstractOpenExplorerAction()
    {
        this.window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        this.systemBrowser = OperatingSystem.INSTANCE.getSystemBrowser();
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener((IPropertyChangeListener) this);
    }
    
    public void propertyChange(final PropertyChangeEvent event)
    {
        if (OperatingSystem.INSTANCE.isLinux())
        {
            this.systemBrowser = OperatingSystem.INSTANCE.getSystemBrowser();
        }
    }
    
    public void run(final IAction action)
    {
        if (this.currentSelection == null || this.currentSelection.isEmpty())
        {
            return;
        }
        if (this.currentSelection instanceof ITreeSelection)
        {
            final ITreeSelection treeSelection = (ITreeSelection) this.currentSelection;
            final TreePath[] paths = treeSelection.getPaths();
            for (int i = 0; i < paths.length; ++i)
            {
                final TreePath path = paths[i];
                IResource resource = null;
                final Object segment = path.getLastSegment();
                if (segment instanceof IResource)
                {
                    resource = (IResource) segment;
                }
                else if (segment instanceof IJavaElement)
                {
                    resource = ((IJavaElement) segment).getResource();
                }
                if (resource != null)
                {
                    String browser = this.systemBrowser;
                    String location = resource.getLocation().toOSString();
                    if (resource instanceof IFile)
                    {
                        location = ((IFile) resource).getParent().getLocation().toOSString();
                        if (OperatingSystem.INSTANCE.isWindows())
                        {
                            browser = String.valueOf(this.systemBrowser) + " /select,";
                            location = ((IFile) resource).getLocation().toOSString();
                        }
                    }
                    this.openInBrowser(browser, location);
                }
            }
        }
        else if (this.currentSelection instanceof ITextSelection || this.currentSelection instanceof IStructuredSelection)
        {
            final IEditorPart editor = this.window.getActivePage().getActiveEditor();
            if (editor != null)
            {
                final IFile current_editing_file = (IFile) editor.getEditorInput().getAdapter((Class) IFile.class);
                String browser2 = this.systemBrowser;
                String location2 = current_editing_file.getParent().getLocation().toOSString();
                if (OperatingSystem.INSTANCE.isWindows())
                {
                    browser2 = String.valueOf(this.systemBrowser) + " /select,";
                    location2 = current_editing_file.getLocation().toOSString();
                }
                this.openInBrowser(browser2, location2);
            }
        }
    }
    
    protected void openInBrowser(final String browser, final String location)
    {
        try
        {
            if (OperatingSystem.INSTANCE.isWindows())
            {
                Runtime.getRuntime().exec(String.valueOf(browser) + " \"" + location + "\"");
            }
            else
            {
                Runtime.getRuntime().exec(new String[]{browser, location});
            }
            MDEConsoleFactory.console("本城正在帮你打开：" + location);
            System.err.println("本城正在帮你打开：" + location);
        }
        catch (final IOException e)
        {
            MessageDialog.openError(this.shell, Messages.OpenExploer_Error, String.valueOf(Messages.Cant_Open) + " \"" + location + "\"");
            e.printStackTrace();
        }
    }
    
    public void selectionChanged(final IAction action, final ISelection selection)
    {
        this.currentSelection = selection;
    }
}
