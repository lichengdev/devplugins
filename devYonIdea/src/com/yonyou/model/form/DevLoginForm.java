package com.yonyou.model.form;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.PwdUtil;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.util.NccdevUtil;
import com.yonyou.util.StringUtils;

public class DevLoginForm extends JDialog
{
    /** @date 2025年5月21日 */
    private static final long serialVersionUID = -8369242742386618123L;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField devuserText;
    private JTextField devpwdField;
    // private JPasswordField devpwdField;
    private JButton registerBtn;
    
    public DevLoginForm()
    {
        this.$$$setupUI$$$();
        this.setContentPane(this.contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);
        final NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
        final String devuser = configVO.getDevuser();
        final String key = "devpwd";
        if (StringUtils.isNotBlank((CharSequence) devuser))
        {
            this.devuserText.setText(devuser);
            String devpwd = null;
            final CredentialAttributes credentialAttributes = PwdUtil.createCredentialAttributes(key, devuser);
            final Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
            if (credentials != null)
            {
                devpwd = credentials.getPasswordAsString();
            }
            if (StringUtils.isNotBlank((CharSequence) devpwd))
            {
                this.devpwdField.setText(devpwd);
            }
        }
        this.buttonOK.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                DevLoginForm.this.onOK();
            }
        });
        this.buttonCancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                DevLoginForm.this.onCancel();
            }
        });
        this.registerBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                try
                {
                    NccdevUtil.toWeb(
                        "https://yonbip.diwork.com/iuap-uuas-user/fe-free/#/register?service=https://nccdev.yonyou.com&sysid=nccdev");
                }
                catch (final Exception exception)
                {
                    Messages.showErrorDialog(exception.getMessage(), "错误");
                }
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                DevLoginForm.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                DevLoginForm.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
    }
    
    private void onOK()
    {
        final String devuser = this.devuserText.getText();
        final String devpwd = this.devpwdField.getText();
        final NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
        configVO.setDevuser(devuser);
        final CredentialAttributes credentialAttributes = PwdUtil.createCredentialAttributes("devpwd", devuser);
        final Credentials credentials = new Credentials(devuser, devpwd);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
        configVO.setDevpwd("");
        final boolean connect = NccdevUtil.connect(devuser, devpwd);
        if (connect)
        {
            this.dispose();
        }
    }
    
    private void onCancel()
    {
        this.dispose();
    }
    
    public static void main(final String[] args)
    {
        final DevLoginForm dialog = new DevLoginForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
    
    public <T> T getComponent(final String componentName, final Class<T> clazz)
    {
        try
        {
            final Field declaredField = this.getClass().getDeclaredField(componentName);
            declaredField.setAccessible(true);
            return (T) declaredField.get(this);
        }
        catch (final Exception e)
        {
            Messages.showErrorDialog(e.getMessage(), "错误");
            return null;
        }
    }
    
    private void $$$setupUI$$$()
    {
        final JPanel contentPane = new JPanel();
        (this.contentPane = contentPane)
            .setLayout((LayoutManager) new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1, false, false));
        final JPanel comp = new JPanel();
        comp.setLayout((LayoutManager) new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
        contentPane.add(comp, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 1, (Dimension) null, (Dimension) null, (Dimension) null));
        comp.add((Component) new Spacer(),
            new GridConstraints(0, 1, 1, 1, 0, 1, 6, 1, (Dimension) null, (Dimension) null, (Dimension) null));
        final JPanel comp2 = new JPanel();
        comp2.setLayout((LayoutManager) new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        comp.add(comp2, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 3, (Dimension) null, (Dimension) null, (Dimension) null));
        final JButton button = new JButton();
        (this.buttonOK = button).setText("确定");
        comp2.add(button, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, (Dimension) null, (Dimension) null, (Dimension) null));
        final JButton button2 = new JButton();
        (this.buttonCancel = button2).setText("取消");
        comp2.add(button2, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, (Dimension) null, (Dimension) null, (Dimension) null));
        final JButton button3 = new JButton();
        (this.registerBtn = button3).setText("注册");
        comp.add(button3, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, (Dimension) null, (Dimension) null, (Dimension) null));
        final JPanel comp3 = new JPanel();
        comp3.setLayout((LayoutManager) new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        contentPane.add(comp3, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, (Dimension) null, (Dimension) null, (Dimension) null));
        final JLabel comp4 = new JLabel();
        comp4.setText("用户名：");
        comp3.add(comp4, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension) null, (Dimension) null, (Dimension) null));
        final JLabel comp5 = new JLabel();
        comp5.setText("密码：");
        comp3.add(comp5, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension) null, (Dimension) null, (Dimension) null));
        comp3.add(this.devuserText = new JTextField(),
            new GridConstraints(0, 1, 1, 1, 8, 1, 6, 0, (Dimension) null, new Dimension(150, -1), (Dimension) null));
        
        comp3.add(this.devpwdField = new JTextField(),
            new GridConstraints(1, 1, 1, 1, 8, 1, 6, 0, (Dimension) null, new Dimension(150, -1), (Dimension) null));
        
        // comp3.add(this.devpwdField = new JPasswordField(),
        // new GridConstraints(1, 1, 1, 1, 8, 1, 6, 0, (Dimension) null, new Dimension(150, -1), (Dimension)
        // null));
    }
}
