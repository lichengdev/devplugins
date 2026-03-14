package openexplorer.actions;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenExplorerAction extends AbstractOpenExplorerAction implements IWorkbenchWindowActionDelegate
{
    public void init(final IWorkbenchWindow window)
    {
        this.window = window;
        this.shell = this.window.getShell();
    }
    
    public void dispose()
    {
    }
}
