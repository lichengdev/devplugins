package com.yonyou.model.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.yonyou.model.listener.AddDbtypeListener;
import com.yonyou.model.listener.ApplyListener;
import com.yonyou.model.listener.AutoFormatListener;
import com.yonyou.model.listener.CancelListener;
import com.yonyou.model.listener.CopyDatabaseListener;
import com.yonyou.model.listener.CopyDbListener;
import com.yonyou.model.listener.DatabaseTypeListener;
import com.yonyou.model.listener.DatasourceListener;
import com.yonyou.model.listener.DelDatabaseListener;
import com.yonyou.model.listener.DriverClassNameListener;
import com.yonyou.model.listener.OKListener;
import com.yonyou.model.listener.OpenHomeListener;
import com.yonyou.model.listener.OpenSysConfigListener;
import com.yonyou.model.listener.PluginSettingActionListener;
import com.yonyou.model.listener.SelectHomeListener;
import com.yonyou.model.listener.SetDesignListener;
import com.yonyou.model.listener.TestConnectListener;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.DataSourceUtil;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.util.prop.DataSourceMeta;
import com.yonyou.util.prop.DriverInfo;

public class NCHomeConfigForm extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JButton buttonApply;
	private JLabel homeJLabel;
	private JTextField homePathText;
	private JButton buttonSelectHome;
	private JComboBox datasourceBox;
	private JComboBox databaseTypeBox;
	private JComboBox driverClassNameBox;
	private JTextField addressText;
	private JTextField portText;
	private JTextField dbNameText;
	private JTextField oidText;
	private JTextField userNameText;
//	private JTextField passwordText;
	private JPasswordField passwordText;
	private JComboBox baseDatabaseBox;
	private JButton buttonSetDesign;
	private JButton buttonTestConnect;
	private JButton buttonCopyDatabase;
	private JButton buttonDeleteDatabase;
	private JPanel datasourcePanel;
	private JPanel baseDatabasePanel;
	private JButton sysConfigAction;
	private JButton openHomeAction;
	private JButton copyDbButton;
	private JButton addDbtypeButton;
	private JTextField autoTextField;
	private JButton pluginSettingBtn;
	private JLabel tipLabel;
	private JTextField noteTextField;
	private Map<String, DriverInfo[]> driverForDatabaseMap;
	private Map<String, DataSourceMeta> dataSourceMetaMap;
	private boolean needUpdateDbLibrary;

	//
	private AnActionEvent evn;

	public AnActionEvent getEvn() {
		return evn;
	}

	public NCHomeConfigForm(AnActionEvent e) {
		this.evn = e;
		this.$$$setupUI$$$();
		this.driverForDatabaseMap = new HashMap<String, DriverInfo[]>();
		this.dataSourceMetaMap = new HashMap<String, DataSourceMeta>();
		this.needUpdateDbLibrary = false;
		this.createUIComponents();
		this.initComponent();
		this.initData();
		this.initHomeConfig();
	}

	public NCHomeConfigForm() {
		this.$$$setupUI$$$();
		this.driverForDatabaseMap = new HashMap<String, DriverInfo[]>();
		this.dataSourceMetaMap = new HashMap<String, DataSourceMeta>();
		this.needUpdateDbLibrary = false;
		this.createUIComponents();
		this.initComponent();
		this.initData();
		this.initHomeConfig();
	}

	private void initComponent() {
		this.setContentPane(this.contentPane);
		this.setModal(true);
		this.getRootPane().setDefaultButton(this.buttonOK);

		final int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		final int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setBounds((width - 800) / 2, (height - 600) / 2, 800, 600);

		if (this.pluginSettingBtn != null) {
			this.pluginSettingBtn.setIcon(IconLoader.getIcon("/icons/setup.svg"));
			this.pluginSettingBtn.setToolTipText("插件设置");
			this.pluginSettingBtn.addActionListener(new PluginSettingActionListener());

		}

		if (this.datasourceBox != null) {
			this.datasourceBox.addItemListener(new DatasourceListener(this));
		}
		if (this.databaseTypeBox != null) {
			this.databaseTypeBox.addItemListener(new DatabaseTypeListener(this));
		}
		if (this.driverClassNameBox != null) {
			this.driverClassNameBox.addItemListener(new DriverClassNameListener(this));

		}

		if (this.buttonSelectHome != null) {
			this.buttonSelectHome.addActionListener(new SelectHomeListener(this));

		}
		if (this.buttonSetDesign != null) {
			this.buttonSetDesign.addActionListener(new SetDesignListener(this));
		}
		if (this.buttonTestConnect != null) {
			this.buttonTestConnect.addActionListener(new TestConnectListener(this));
		}
		if (this.buttonCopyDatabase != null) {
			this.buttonCopyDatabase.addActionListener(new CopyDatabaseListener(this));
		}
		if (this.buttonDeleteDatabase != null) {
			this.buttonDeleteDatabase.addActionListener(new DelDatabaseListener(this));

		}
		if (this.buttonOK != null) {
			this.buttonOK.addActionListener(new OKListener(this));
		}
		if (this.buttonCancel != null) {
			this.buttonCancel.addActionListener(new CancelListener(this));
		}
		if (this.buttonApply != null) {
			this.buttonApply.addActionListener(new ApplyListener(this));

		}
		if (this.openHomeAction != null) {
			this.openHomeAction.addActionListener(new OpenHomeListener(this));
		}
		if (this.sysConfigAction != null) {
			this.sysConfigAction.addActionListener(new OpenSysConfigListener(this));

		}
		if (this.copyDbButton != null) {
			this.copyDbButton.addActionListener(new CopyDbListener(this));
		}
		if (this.addDbtypeButton != null) {
			this.addDbtypeButton.addActionListener(new AddDbtypeListener(this));

		}
		if (this.autoTextField != null) {
			this.autoTextField.addKeyListener(new AutoFormatListener(this));

		}

		this.setDefaultCloseOperation(0);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				NCHomeConfigForm.this.onCancel();
			}
		});
		this.contentPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				NCHomeConfigForm.this.onCancel();
			}
		}, KeyStroke.getKeyStroke(27, 0), 1);
	}

	private void initData() {
		final NCHomeConfigService service = NCHomeConfigService.getInstance();
		if (service != null) {
			final NCHomeConfigVO configVO = service.getState();
			if (configVO.getHomePath() != null && this.homePathText != null) {
				this.homePathText.setText(configVO.getHomePath());

			}

			if (this.tipLabel != null) {
				this.tipLabel.setText("");
			}
		}
	}

	private void initHomeConfig() {
		if (this.homePathText != null) {
			final String homePath = this.homePathText.getText();
			DataSourceUtil.initDataSourceConfig(this, null);
		}
	}

	private void onCancel() {
		this.dispose();

	}

	public static void main(final String[] args) {
		final NCHomeConfigForm dialog = new NCHomeConfigForm();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}

	public <T> T getComponent(final String componentName, final Class<T> clazz) {
		try {
			final Field declaredField = this.getClass().getDeclaredField(componentName);
			declaredField.setAccessible(true);
			return (T) declaredField.get(this);
		} catch (final Exception e) {
			Messages.showErrorDialog(e.getMessage(), "⚠️错误");

			return null;
		}
	}

	public Map<String, DriverInfo[]> getDriverForDatabaseMap() {
		return this.driverForDatabaseMap;
	}

	public Map<String, DataSourceMeta> getDataSourceMetaMap() {
		return this.dataSourceMetaMap;
	}

	public void setDriverForDatabaseMap(final Map<String, DriverInfo[]> driverForDatabaseMap) {
		this.driverForDatabaseMap = driverForDatabaseMap;
	}

	public void setDataSourceMetaMap(final Map<String, DataSourceMeta> dataSourceMetaMap) {
		this.dataSourceMetaMap = dataSourceMetaMap;
	}

	public boolean isNeedUpdateDbLibrary() {
		return this.needUpdateDbLibrary;
	}

	public void setNeedUpdateDbLibrary(final boolean needUpdateDbLibrary) {
		this.needUpdateDbLibrary = needUpdateDbLibrary;

	}

	private void createUIComponents() {
		if (this.tipLabel == null) {
			this.tipLabel = new JLabel();

		}
		if (this.homeJLabel == null) {
			this.homeJLabel = new JLabel();

		}
		if (this.noteTextField == null) {
			this.noteTextField = new JTextField();
		}
	}

	private void $$$setupUI$$$() {
		final JPanel contentPane = new JPanel();
		(this.contentPane = contentPane)
				.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1, false, false));
		final JPanel comp = new JPanel();
		comp.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
		contentPane.add(comp, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 1, null, null, null));
		final JPanel comp2 = new JPanel();
		comp2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
		comp.add(comp2, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 3, null, null, null));
		final JButton button = new JButton();
		(this.buttonOK = button).setText("确定");
		comp2.add(button, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null));
		final JButton button2 = new JButton();
		(this.buttonCancel = button2).setText("取消");
		comp2.add(button2, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null));
		final JButton button3 = new JButton();
		(this.buttonApply = button3).setText("应用");
		comp2.add(button3, new GridConstraints(0, 2, 1, 1, 0, 1, 3, 0, null, null, null));
		comp.add(new Spacer(), new GridConstraints(0, 1, 1, 1, 0, 1, 6, 1, null, null, null));
		final JButton button4 = new JButton();
		(this.pluginSettingBtn = button4).setText("");
		comp.add(button4, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, new Dimension(16, 16)));
		final JPanel comp3 = new JPanel();
		comp3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
		contentPane.add(comp3, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		final JPanel comp4 = new JPanel();
		comp4.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1, false, false));
		comp3.add(comp4, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, new Dimension(-1, 120)));
		comp4.setBorder(BorderFactory.createTitledBorder(null, "设置", 0, 0, null, null));
		final JLabel label = new JLabel();
		(this.homeJLabel = label).setText("UAP HOME");
		comp4.add(label, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, new Dimension(66, 25), null));
		final JButton button5 = new JButton();
		(this.buttonSelectHome = button5).setText("浏览...");
		comp4.add(button5, new GridConstraints(0, 4, 1, 1, 0, 1, 3, 0, null, new Dimension(78, 25), null));
		final JTextField textField = new JTextField();
		(this.homePathText = textField).setEditable(false);
		textField.setEnabled(false);
		textField.setFocusable(false);
		textField.setText("");
		comp4.add(textField, new GridConstraints(0, 1, 1, 3, 8, 1, 6, 0, null, new Dimension(150, 25), null));
		final JButton button6 = new JButton();
		(this.sysConfigAction = button6).setText("SysConfig");
		comp4.add(button6, new GridConstraints(1, 2, 1, 1, 0, 1, 3, 0, null, new Dimension(92, 23), null));
		final JButton button7 = new JButton();
		(this.openHomeAction = button7).setText("打开Home");
		comp4.add(button7, new GridConstraints(1, 1, 1, 1, 0, 1, 3, 0, null, new Dimension(94, 23), null));
		comp4.add(new Spacer(), new GridConstraints(1, 3, 1, 1, 0, 1, 6, 1, null, new Dimension(14, 23), null));
		final JPanel comp5 = new JPanel();
		comp5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
		comp3.add(comp5, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		final JPanel panel = new JPanel();
		(this.datasourcePanel = panel)
				.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
		panel.setEnabled(true);
		comp5.add(panel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		panel.setBorder(BorderFactory.createTitledBorder(null, "数据源配置", 0, 0, null, null));
		final JPanel comp6 = new JPanel();
		comp6.setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 0), -1, -1, false, false));
		panel.add(comp6, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		final JLabel comp7 = new JLabel();
		comp7.setText("数据源列表");
		comp6.add(comp7, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		comp6.add(this.datasourceBox = new JComboBox(), new GridConstraints(0, 1, 1, 1, 8, 1, 2, 0, null, null, null));
		final JButton button8 = new JButton();
		(this.buttonSetDesign = button8).setText("设为开发库");
		comp6.add(button8, new GridConstraints(0, 4, 1, 1, 0, 1, 3, 0, null, null, null));
		final JButton button9 = new JButton();
		(this.buttonTestConnect = button9).setText("测试连接");
		comp6.add(button9, new GridConstraints(0, 5, 1, 1, 0, 1, 3, 0, null, null, null));
		final JButton button10 = new JButton();
		(this.buttonCopyDatabase = button10).setText("拷贝");
		comp6.add(button10, new GridConstraints(0, 6, 1, 1, 0, 1, 3, 0, null, null, null));
		final JButton button11 = new JButton();
		(this.buttonDeleteDatabase = button11).setText("删除");
		comp6.add(button11, new GridConstraints(0, 7, 1, 1, 0, 1, 3, 0, null, null, null));
		comp6.add(new Spacer(), new GridConstraints(0, 3, 1, 1, 0, 1, 6, 1, null, null, null));
		final JButton button12 = new JButton();
		(this.copyDbButton = button12).setText("数据源迁移");
		button12.setToolTipText("旧Home数据源迁移");
		comp6.add(button12, new GridConstraints(0, 2, 1, 1, 0, 1, 3, 0, null, null, null));
		final JPanel comp8 = new JPanel();
		comp8.setLayout(new GridLayoutManager(7, 6, new Insets(0, 0, 0, 0), -1, -1, false, false));
		panel.add(comp8, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		final JLabel comp9 = new JLabel();
		comp9.setText("数据库类型");
		comp8.add(comp9, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		final JComboBox comboBox = new JComboBox();
		(this.databaseTypeBox = comboBox).setModel(new DefaultComboBoxModel());
		comp8.add(comboBox, new GridConstraints(0, 1, 1, 1, 8, 1, 2, 0, null, null, null));
		final JLabel comp10 = new JLabel();
		comp10.setText("驱动类型");
		comp8.add(comp10, new GridConstraints(0, 4, 1, 1, 8, 0, 0, 0, null, null, null));
		final JLabel comp11 = new JLabel();
		comp11.setText("主机名");
		comp8.add(comp11, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		final JLabel comp12 = new JLabel();
		comp12.setText("DB/ODBC名称");
		comp8.add(comp12, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		final JLabel comp13 = new JLabel();
		comp13.setText("用户名");
		comp8.add(comp13, new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		comp8.add(this.addressText = new JTextField(),
				new GridConstraints(1, 1, 1, 2, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		comp8.add(this.dbNameText = new JTextField(),
				new GridConstraints(2, 1, 1, 2, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		comp8.add(this.userNameText = new JTextField(),
				new GridConstraints(3, 1, 1, 2, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		final JLabel comp14 = new JLabel();
		comp14.setText("端口");
		comp8.add(comp14, new GridConstraints(1, 4, 1, 1, 8, 0, 0, 0, null, null, null));
		final JLabel comp15 = new JLabel();
		comp15.setText("OID标志");
		comp8.add(comp15, new GridConstraints(2, 4, 1, 1, 8, 0, 0, 0, null, null, null));
		final JLabel comp16 = new JLabel();
		comp16.setText("密码");
		comp8.add(comp16, new GridConstraints(3, 4, 1, 1, 8, 0, 0, 0, null, null, null));
		comp8.add(this.driverClassNameBox = new JComboBox(),
				new GridConstraints(0, 5, 1, 1, 8, 1, 2, 0, null, null, null));
		comp8.add(this.portText = new JTextField(),
				new GridConstraints(1, 5, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		comp8.add(this.oidText = new JTextField(),
				new GridConstraints(2, 5, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
//		comp8.add(this.passwordText = new JTextField(),
//				new GridConstraints(3, 5, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		comp8.add(this.passwordText = new JPasswordField(),
				new GridConstraints(3, 5, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		final JButton button13 = new JButton();
		(this.addDbtypeButton = button13).setText("增加驱动类型");
		button13.setToolTipText("");
		comp8.add(button13, new GridConstraints(0, 2, 1, 1, 0, 1, 3, 0, null, null, null));
		comp8.add(new Spacer(), new GridConstraints(0, 3, 1, 1, 0, 1, 6, 1, null, null, null));
		comp8.add(new Spacer(), new GridConstraints(1, 3, 1, 1, 0, 1, 6, 1, null, null, null));
		comp8.add(new Spacer(), new GridConstraints(2, 3, 1, 1, 0, 1, 6, 1, null, null, null));
		comp8.add(new Spacer(), new GridConstraints(3, 3, 1, 1, 0, 1, 6, 1, null, null, null));
		final JLabel comp17 = new JLabel();
		comp17.setText("信息自动识别");
		comp8.add(comp17, new GridConstraints(4, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		comp8.add(this.autoTextField = new JTextField(),
				new GridConstraints(4, 1, 1, 5, 8, 1, 6, 0, null, new Dimension(150, -1), null));
		final JLabel comp18 = new JLabel();
		comp18.setText("格式：用户名/密码@IP:port/odbc名称");
		comp8.add(comp18, new GridConstraints(5, 1, 1, 5, 8, 0, 0, 0, null, null, null));
		final JLabel comp19 = new JLabel();
		comp19.setText("示例：yonbip_2023/password@127.0.0.1:1521/orcl");
		comp8.add(comp19, new GridConstraints(6, 1, 1, 5, 8, 0, 0, 0, null, null, null));
		final JPanel panel2 = new JPanel();
		(this.baseDatabasePanel = panel2)
				.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
		comp5.add(panel2, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		panel2.setBorder(BorderFactory.createTitledBorder(null, "基准库配置", 0, 0, null, null));
		final JLabel comp20 = new JLabel();
		comp20.setText("选择数据源");
		panel2.add(comp20, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
		panel2.add(this.baseDatabaseBox = new JComboBox(),
				new GridConstraints(0, 1, 1, 1, 8, 1, 2, 0, null, null, null));
		panel2.add(new Spacer(), new GridConstraints(0, 2, 1, 1, 0, 1, 6, 1, null, null, null));
		final JPanel comp21 = new JPanel();
		comp21.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
		contentPane.add(comp21, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
		final JLabel comp22 = new JLabel();
		final Font $$$getFont$$$ = this.$$$getFont$$$(null, 1, -1, comp22.getFont());
		if ($$$getFont$$$ != null) {
			comp22.setFont($$$getFont$$$);
		}
		comp22.setForeground(new Color(-1757674));
		comp22.setText("⚠️ 注意：本工具专为开发阶段设计，严禁在生产或测试环境中设置 HOME 目录，否则可能导致严重后果！");
		comp21.add(comp22, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
	}

	private Font $$$getFont$$$(final String name, final int n, final int n2, final Font font) {
		if (font == null) {
			return null;
		}
		String name2;
		if (name == null) {
			name2 = font.getName();
		} else {
			final Font font2 = new Font(name, 0, 10);
			if (font2.canDisplay('a') && font2.canDisplay('1')) {
				name2 = name;
			} else {
				name2 = font.getName();
			}
		}
		final Font font3 = new Font(name2, (n >= 0) ? n : font.getStyle(), (n2 >= 0) ? n2 : font.getSize());
		final Font font4 = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac")
				? new Font(font3.getFamily(), font3.getStyle(), font3.getSize())
				: new StyleContext().getFont(font3.getFamily(), font3.getStyle(), font3.getSize());
		return (font4 instanceof FontUIResource) ? font4 : new FontUIResource(font4);
	}
}
