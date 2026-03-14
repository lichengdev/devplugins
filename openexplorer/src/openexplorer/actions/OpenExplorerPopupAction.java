package openexplorer.actions;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

public class OpenExplorerPopupAction extends AbstractOpenExplorerAction implements IObjectActionDelegate
{
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart)
    {
        this.window = targetPart.getSite().getWorkbenchWindow();
        this.shell = targetPart.getSite().getShell();
    }
}
