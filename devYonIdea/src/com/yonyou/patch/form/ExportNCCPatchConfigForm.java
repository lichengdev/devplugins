package com.yonyou.patch.form;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.messages.MessageBusConnection;
import com.yonyou.menu.listener.MouseDoubleClickedListener;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.project.FileTool;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.patch.model.BugsTableModel;
import com.yonyou.patch.model.FilesTableModel;
import com.yonyou.patch.utils.ExportPatchUtil;
import com.yonyou.patch.vo.ExportFileVO;
import com.yonyou.patch.vo.PatchInfoVO;
import com.yonyou.util.StringUtils;
import com.yonyou.util.task.CustomBackgroundableTask;
import com.yonyou.util.task.CustomModalTask;

public class ExportNCCPatchConfigForm extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField patchNameText;
	private JTextField exportPathText;
	private JCheckBox includeSrcBox;
	private JButton selectFileButton;
	private JTabbedPane tabbedPane1;
	private JTextField patchIdText;
	private JComboBox patchTypeCombo;
	private JComboBox priorityCombo;
	private JCheckBox needDeployBox;
	private JCheckBox rebuildAppletJarBox;
	private JTextField providerText;
	private JTextField departmentText;
	private JTable bugsTable;
	private JButton addBugButton;
	private JButton delBugButton;
	private JTabbedPane tabbedPane2;
	private JTextPane preWorkPane;
	private JTextPane patchInstallPane;
	private JTextPane lastWorkPane;
	private JTextPane verifyWorkPane;
	private JTextPane otherWorkPane;
	private JPanel applyVersionPanel;
	private JPanel canAppliedOSPanel;
	private JPanel canAppliedMiddlewarePanel;
	private JPanel canAppliedDBPanel;
	private JTable selectedFileTable;
	private JTextField referPatchText;
	private JTextField editModuleText;
	private JTextField relyPatchText;
	private JTextField keywordText;
	private JTextField patchDescText;
	private JCheckBox client2ModulesCheckBox;
	private JPanel hiddenPanel;
	private JCheckBox client2NcchrCheckBox;
	private MessageBusConnection messageBusConnection;
	private VirtualFile[] files;
	private Project project;

	public ExportNCCPatchConfigForm(final AnActionEvent event) {
		this.$$$setupUI$$$();
		this.setContentPane(this.contentPane);
		this.setModal(true);
		this.getRootPane().setDefaultButton(this.buttonOK);
		this.project = event.getProject();
		NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
		if (configVO.isAutoClient()) {
			this.hiddenPanel.setVisible(false);
		} else {
			this.hiddenPanel.setVisible(true);
		}

		this.includeSrcBox.setSelected(true);
		this.patchIdText.setText(UUID.randomUUID().toString());
		String[] patchTypeItems = new String[] { "BUG修复补丁", "新增功能补丁", "适应性补丁", "多语言补丁", "升级补丁", "文档补丁", "诊断补丁" };
		this.patchTypeCombo.setModel(new DefaultComboBoxModel(patchTypeItems));
		String[] priorityItems = new String[] { "高危补丁", "安全漏洞补丁" };
		this.priorityCombo.setModel(new DefaultComboBoxModel(priorityItems));
		String provider = configVO.getProvider();
		String department = configVO.getDepartment();
		if (StringUtils.isNotBlank(provider)) {
			this.providerText.setText(provider);
		}

		if (StringUtils.isNotBlank(department)) {
			this.departmentText.setText(department);
		}

		String versions = configVO.getVersions();
		if (StringUtils.isBlank(versions)) {
			versions = "1811,1903,2005,2105,2111";
			configVO.setVersions(versions);
		}

		String[] versionArr = versions.split(",");

		for (int i = 0; i < versionArr.length; ++i) {
			String version = versionArr[i];
			JCheckBox checkBox = new JCheckBox(version, true);
			FlowLayout layout = (FlowLayout) this.applyVersionPanel.getLayout();
			layout.setAlignment(0);
			this.applyVersionPanel.add(checkBox, i);
			this.applyVersionPanel.revalidate();
		}

		String OSs = configVO.getOSs();
		if (StringUtils.isBlank(OSs)) {
			OSs = "Linux,Windows,AIX,Solaris";
			configVO.setOSs(OSs);
		}

		String[] OSArr = OSs.split(",");

		for (int i = 0; i < OSArr.length; ++i) {
			String OS = OSArr[i];
			JCheckBox checkBox = new JCheckBox(OS, true);
			FlowLayout layout = (FlowLayout) this.canAppliedOSPanel.getLayout();
			layout.setAlignment(0);
			this.canAppliedOSPanel.add(checkBox, i);
			this.canAppliedOSPanel.revalidate();
		}

		String middlewares = configVO.getMiddlewares();
		if (StringUtils.isBlank(middlewares)) {
			middlewares = "Weblogic,Websphere 7.0,Yonyou Middleware V5,Yonyou Middleware V6";
			configVO.setMiddlewares(middlewares);
		}

		String[] middlewareArr = middlewares.split(",");

		for (int i = 0; i < middlewareArr.length; ++i) {
			String middleware = middlewareArr[i];
			JCheckBox checkBox = new JCheckBox(middleware, true);
			FlowLayout layout = (FlowLayout) this.canAppliedMiddlewarePanel.getLayout();
			layout.setAlignment(0);
			this.canAppliedMiddlewarePanel.add(checkBox, i);
			this.canAppliedMiddlewarePanel.revalidate();
		}

		String DBs = configVO.getDBs();
		if (StringUtils.isBlank(DBs)) {
			DBs = "DB2 V9.7,SQL Server 2008 R2,Oracle 10,Oracle 11";
			configVO.setDBs(DBs);
		}

		String[] DBArr = DBs.split(",");

		for (int i = 0; i < DBArr.length; ++i) {
			String DB = DBArr[i];
			JCheckBox checkBox = new JCheckBox(DB, true);
			FlowLayout layout = (FlowLayout) this.canAppliedDBPanel.getLayout();
			layout.setAlignment(0);
			this.canAppliedDBPanel.add(checkBox, i);
			this.canAppliedDBPanel.revalidate();
		}

		Object[][] bugsRowData = new Object[0][];
		final Object[] bugsColumnNames = new Object[] { "行号", "BUG号", "BUG描述信息" };
		BugsTableModel bugsTableModel = new BugsTableModel(bugsRowData, bugsColumnNames);
		this.bugsTable.setModel(bugsTableModel);
		long currentTimeMillis = System.currentTimeMillis();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssss");
		// String var10000 = simpleDateFormat.format(currentTimeMillis);
		String var10000 = this.providerText.getText();
		// String patchName = "patch_[xxxx]补丁_" + var10000 + "_V1_" +
		// this.providerText.getText();
		String patchName = "patch_[xxxx]补丁_V1_" + var10000 + "_" + simpleDateFormat.format(currentTimeMillis);

		this.patchNameText.setText(patchName);
		String exportPatchPath = configVO.getExportPatchPath();
		if (StringUtils.isNotBlank(exportPatchPath)) {
			this.exportPathText.setText(exportPatchPath);
		}

		final ExportPatchUtil exportPatchUtil = new ExportPatchUtil();
		final List<ExportFileVO> exportPathList = new ArrayList();
		this.files = (VirtualFile[]) event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
		Object[][] filesRowData = new Object[0][];
		Object[] filesColumnNames = new Object[] { "导出后所属模块(双击可编辑)", "选择的文件目录" };
		if (this.files != null) {
			filesRowData = new Object[this.files.length][2];
			ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(event.getProject());

			for (int i = 0; i < this.files.length; ++i) {
				Module module = projectFileIndex.getModuleForFile(this.files[i]);
				String moduleName = module.getName();
				String modulePath = null;
				if (module.getModuleFile() != null) {
					modulePath = module.getModuleFile().getParent().getPath();
				}

				ExportFileVO exportFileVO = new ExportFileVO();
				exportFileVO.setFilePath(this.files[i].getPath());

				String moduleXmlName;
				try {
					moduleXmlName = exportPatchUtil.findModuleXmlName(this.files[i]);
				} catch (Exception var35) {
					moduleXmlName = moduleName;
				}

				exportFileVO.setExportModuleName(moduleXmlName);
				exportPathList.add(exportFileVO);
				String displayPath = this.files[i].getPath();
				if (modulePath != null) {
					displayPath = this.files[i].getPath().replace(modulePath, module.getName());
				}

				filesRowData[i] = new Object[] { moduleXmlName, displayPath };
			}
		}

		FilesTableModel filesTableModel = new FilesTableModel(filesRowData, filesColumnNames);
		this.selectedFileTable.setModel(filesTableModel);
		TableColumn column = this.selectedFileTable.getColumnModel().getColumn(0);
		column.setMinWidth(180);
		column.setMaxWidth(180);
		this.selectedFileTable.addMouseListener(new MouseDoubleClickedListener() {
			public void mouseDoubleClicked(MouseEvent mouseEvent) {
				String str = Messages.showInputDialog("输入模块名称", "导出后所属模块名称", (Icon) null);
				if (StringUtils.isNotBlank(str)) {
					int selectedRow = ExportNCCPatchConfigForm.this.selectedFileTable.getSelectedRow();
					ExportNCCPatchConfigForm.this.selectedFileTable.setValueAt(str.trim(), selectedRow, 0);
				}

			}
		});
		this.addBugButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				int rowCount = ExportNCCPatchConfigForm.this.bugsTable.getRowCount();
				BugsTableModel model = (BugsTableModel) ExportNCCPatchConfigForm.this.bugsTable.getModel();
				model.addRow(new Object[] { rowCount + 1, "", "" });
			}
		});
		this.delBugButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				int[] selectedRows = ExportNCCPatchConfigForm.this.bugsTable.getSelectedRows();
				List<Integer> selectedRowList = (List) Arrays.stream(selectedRows).boxed().collect(Collectors.toList());
				BugsTableModel model = (BugsTableModel) ExportNCCPatchConfigForm.this.bugsTable.getModel();
				Vector<Vector> dataVector = model.getDataVector();
				Vector<Vector> newdata = new Vector();

				for (int i = 0; i < dataVector.size(); ++i) {
					if (!selectedRowList.contains(i)) {
						Vector newv = new Vector(3);
						newv.add(0, newdata.size() + 1);
						newv.add(1, ((Vector) dataVector.get(i)).get(1));
						newv.add(2, ((Vector) dataVector.get(i)).get(2));
						newdata.add(newv);
					}
				}

				model.setDataVector(newdata, exportPatchUtil.convertToVector(bugsColumnNames));
			}
		});
		this.selectFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportNCCPatchConfigForm.this.onSelectFile();
			}
		});
		this.buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PatchInfoVO patchInfoVO = new PatchInfoVO(ExportNCCPatchConfigForm.this, exportPathList);
				ExportNCCPatchConfigForm.this.onOK(patchInfoVO, event.getProject());
			}
		});
		this.buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportNCCPatchConfigForm.this.onCancel();
			}
		});
		this.messageBusConnection = this.project.getMessageBus().connect();
		this.messageBusConnection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
			public void after(@NotNull List<? extends VFileEvent> events) {
				boolean moduleXmlChanged = events.stream().anyMatch((event) -> {
					VirtualFile file = event.getFile();
					return file != null && "module.xml".equals(file.getName());
				});
				if (moduleXmlChanged) {
					SwingUtilities.invokeLater(() -> ExportNCCPatchConfigForm.this.refreshTableData());
				}

			}
		});
		this.setDefaultCloseOperation(0);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ExportNCCPatchConfigForm.this.onCancel();
			}
		});
		this.contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportNCCPatchConfigForm.this.onCancel();
			}
		}, KeyStroke.getKeyStroke(27, 0), 1);
	}

	private void refreshTableData() {
		if (this.files != null) {
			for (int i = 0; i < this.files.length; ++i) {
				VirtualFile file = this.files[i];
				String moduleXmlName = (String) ReadAction.compute(() -> {
					try {
						return (new ExportPatchUtil()).findModuleXmlName(file);
					} catch (Exception var2) {
						return file.getName();
					}
				});
				if (moduleXmlName != null) {
					this.selectedFileTable.setValueAt(moduleXmlName, i, 0);
				}
			}

		}
	}

	private void onSelectFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("选择导出补丁目录路径");
		fileChooser.setApproveButtonText("选择");
		fileChooser.setApproveButtonToolTipText("选择导出补丁目录路径");
		fileChooser.setFileSelectionMode(1);
		fileChooser.setMultiSelectionEnabled(false);
		int res = fileChooser.showOpenDialog(this);
		if (0 == res) {
			String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
			this.exportPathText.setText(absolutePath);
			NCHomeConfigService.getInstance().getState().setExportPatchPath(absolutePath);
		}

	}

	private void onOK(final PatchInfoVO patchInfoVO, final Project project) {
		final String patchName = patchInfoVO.getPatchName();
		final String exportPath = patchInfoVO.getExportPath();
		boolean includeSrc = patchInfoVO.isIncludeSrc();
		if (StringUtils.isBlank(patchName)) {
			Messages.showErrorDialog("请填入补丁名称", "💔错误❌");
		} else if (StringUtils.isBlank(exportPath)) {
			Messages.showErrorDialog("请选择导出目录", "💔错误❌");
		} else if (StringUtils.isBlank(patchInfoVO.getProvider())) {
			Messages.showErrorDialog("请填写【扩展信息】中的提供者", "💔错误❌");
		} else if (StringUtils.isBlank(patchInfoVO.getDepartment())) {
			Messages.showErrorDialog("请填写【扩展信息】中的提供者部门", "💔错误❌");
		} else {
			NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
			configVO.setProvider(patchInfoVO.getProvider());
			configVO.setDepartment(patchInfoVO.getDepartment());
			ProgressManager.getInstance().run((Task) (!configVO.isAsyncTask() ? new CustomModalTask(project, "补丁导出中") {
				public void run(@NotNull ProgressIndicator indicator) {
					ReadAction.run(() -> {
						try {
							ExportPatchUtil.exportPatchFile(patchInfoVO, project);
						} catch (Exception exception) {
							throw new RuntimeException(exception.getMessage(), exception);
						}
					});
				}

				public void onSuccess() {
					super.onSuccess();
					Messages.showInfoMessage("👍导出成功", "成功💞🌸❤✔️");
					try {
						Desktop.getDesktop().open(new File(exportPath));
					} catch (Exception e) {
					}
				}

				public void onFinished() {
					super.onFinished();
					File temp = new File(exportPath + File.separator + patchName);
					if (temp.exists()) {
						FileTool.delete(temp);
					}

					ExportNCCPatchConfigForm.this.dispose();
				}
			} : new CustomBackgroundableTask(project, "补丁导出中👊👊👊") {
				public void run(@NotNull ProgressIndicator indicator) {
					ReadAction.run(() -> {
						try {
							ExportPatchUtil.exportPatchFile(patchInfoVO, project);
						} catch (Exception exception) {
							throw new RuntimeException(exception.getMessage(), exception);
						}
					});
				}

				public void onSuccess() {
					super.onSuccess();
					Messages.showInfoMessage("👍导出成功", "成功💞🌸❤✔️");
					try {
						Desktop.getDesktop().open(new File(exportPath));
					} catch (Exception e) {

					}
				}

				public void onFinished() {
					super.onFinished();
					File temp = new File(exportPath + File.separator + patchName);
					if (temp.exists()) {
						FileTool.delete(temp);
					}

					ExportNCCPatchConfigForm.this.dispose();
				}
			}));
		}
	}

	private void onCancel() {
		if (this.messageBusConnection != null) {
			this.messageBusConnection.disconnect();
		}

		this.dispose();
	}

	public <T> T getComponent(String componentName, Class<T> clazz) {
		try {
			Field declaredField = this.getClass().getDeclaredField(componentName);
			declaredField.setAccessible(true);
			return (T) declaredField.get(this);
		} catch (Exception e) {
			Messages.showErrorDialog(e.getMessage(), "💔错误❌");
			return null;
		}
	}

	private void $$$setupUI$$$() {
        JCheckBox jCheckBox;
        JCheckBox jCheckBox2;
        JPanel jPanel;
        JButton jButton;
        JButton jButton2;
        JTextPane jTextPane;
        JTextPane jTextPane2;
        JTextPane jTextPane3;
        JTextPane jTextPane4;
        JTextPane jTextPane5;
        JTabbedPane jTabbedPane;
        JTable jTable;
        JButton jButton3;
        JButton jButton4;
        JTextField jTextField;
        JTextField jTextField2;
        JTextField jTextField3;
        JTextField jTextField4;
        JTextField jTextField5;
        JPanel jPanel2;
        JPanel jPanel3;
        JPanel jPanel4;
        JPanel jPanel5;
        JCheckBox jCheckBox3;
        JCheckBox jCheckBox4;
        JTextField jTextField6;
        JTextField jTextField7;
        JTable jTable2;
        JButton jButton5;
        JTextField jTextField8;
        JComboBox jComboBox;
        JCheckBox jCheckBox5;
        JComboBox jComboBox2;
        JTextField jTextField9;
        JTextField jTextField10;
        JTabbedPane jTabbedPane2;
        JPanel jPanel6;
        this.contentPane = jPanel6 = new JPanel();
        jPanel6.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1, false, false));
        this.tabbedPane1 = jTabbedPane2 = new JTabbedPane();
        jPanel6.add((Component) jTabbedPane2,
                new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, new Dimension(200, 200), null));
        JPanel jPanel7 = new JPanel();
        jPanel7.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane2.addTab("基本信息", null, jPanel7, null);
        JLabel jLabel = new JLabel();
        jLabel.setText("补丁名称");
        jPanel7.add((Component) jLabel, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, null, new Dimension(59, 17), null));
        JLabel jLabel2 = new JLabel();
        jLabel2.setText("导出路径");
        jPanel7.add((Component) jLabel2,
                new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, null, new Dimension(59, 17), null));
        this.patchNameText = jTextField10 = new JTextField();
        jPanel7.add((Component) jTextField10,
                new GridConstraints(1, 1, 1, 3, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel3 = new JLabel();
        jLabel3.setText("补丁编码");
        jPanel7.add((Component) jLabel3,
                new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, new Dimension(59, 17), null));
        this.patchIdText = jTextField9 = new JTextField();
        ((Component) jTextField9).setEnabled(false);
        jPanel7.add((Component) jTextField9,
                new GridConstraints(0, 1, 1, 4, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel4 = new JLabel();
        jLabel4.setText("补丁类型");
        jPanel7.add((Component) jLabel4,
                new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, new Dimension(59, 17), null));
        this.patchTypeCombo = jComboBox2 = new JComboBox();
        jPanel7.add(jComboBox2, new GridConstraints(2, 1, 1, 1, 8, 1, 2, 0, null, new Dimension(72, 30), null));
        this.includeSrcBox = jCheckBox5 = new JCheckBox();
        jCheckBox5.setText("包含源码");
        jPanel7.add((Component) jCheckBox5, new GridConstraints(1, 4, 1, 1, 8, 0, 3, 0, null, null, null));
        JLabel jLabel5 = new JLabel();
        jLabel5.setText("优先级");
        jPanel7.add((Component) jLabel5, new GridConstraints(2, 2, 1, 1, 8, 0, 0, 0, null, null, null));
        this.priorityCombo = jComboBox = new JComboBox();
        jPanel7.add(jComboBox, new GridConstraints(2, 3, 1, 2, 8, 1, 2, 0, null, null, null));
        this.exportPathText = jTextField8 = new JTextField();
        jTextField8.setEditable(false);
        ((Component) jTextField8).setEnabled(true);
        jTextField8.setText("");
        jPanel7.add((Component) jTextField8,
                new GridConstraints(3, 1, 1, 3, 8, 1, 6, 0, null, new Dimension(194, 30), null));
        this.selectFileButton = jButton5 = new JButton();
        jButton5.setText("选择目录");
        jPanel7.add((Component) jButton5, new GridConstraints(3, 4, 1, 1, 0, 1, 3, 0, null, null, null));
        JPanel jPanel8 = new JPanel();
        jPanel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel7.add((Component) jPanel8, new GridConstraints(5, 0, 1, 5, 0, 3, 3, 3, null, null, null));
        JScrollPane jScrollPane = new JScrollPane();
        jPanel8.add((Component) jScrollPane, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        jScrollPane.setBorder(BorderFactory.createTitledBorder(null, "所选文件目录", 0, 0, null, null));
        this.selectedFileTable = jTable2 = new JTable();
        jScrollPane.setViewportView(jTable2);
        JPanel jPanel9 = new JPanel();
        jPanel9.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel7.add((Component) jPanel9, new GridConstraints(4, 0, 1, 5, 0, 3, 3, 3, null, null, null));
        jPanel9.setBorder(BorderFactory.createTitledBorder(null, "提供人", 0, 0, null, null));
        JLabel jLabel6 = new JLabel();
        jLabel6.setText("提供者");
        jPanel9.add((Component) jLabel6, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.providerText = jTextField7 = new JTextField();
        jTextField7.setText("");
        jPanel9.add((Component) jTextField7,
                new GridConstraints(0, 1, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel7 = new JLabel();
        jLabel7.setText("提供者部门");
        jPanel9.add((Component) jLabel7, new GridConstraints(0, 2, 1, 1, 8, 0, 0, 0, null, null, null));
        this.departmentText = jTextField6 = new JTextField();
        jTextField6.setText("");
        jPanel9.add((Component) jTextField6,
                new GridConstraints(0, 3, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JPanel jPanel10 = new JPanel();
        jPanel10.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane2.addTab("扩展信息", null, jPanel10, null);
        this.needDeployBox = jCheckBox4 = new JCheckBox();
        jCheckBox4.setText("是否需要部署");
        jPanel10.add((Component) jCheckBox4, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 0, null, null, null));
        this.rebuildAppletJarBox = jCheckBox3 = new JCheckBox();
        jCheckBox3.setText("是否需要重新生成客户端Applet Jar包");
        jPanel10.add((Component) jCheckBox3, new GridConstraints(0, 1, 1, 1, 8, 0, 3, 0, null, null, null));
        this.applyVersionPanel = jPanel5 = new JPanel();
        jPanel5.setLayout(new FlowLayout(1, 5, 5));
        jPanel5.setName("");
        jPanel10.add((Component) jPanel5, new GridConstraints(2, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        jPanel5.setBorder(BorderFactory.createTitledBorder(null, "产品版本", 0, 0, null, null));
        this.canAppliedOSPanel = jPanel4 = new JPanel();
        jPanel4.setLayout(new FlowLayout(1, 5, 5));
        jPanel10.add((Component) jPanel4, new GridConstraints(3, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        jPanel4.setBorder(BorderFactory.createTitledBorder(null, "适用操作系统", 0, 0, null, null));
        this.canAppliedMiddlewarePanel = jPanel3 = new JPanel();
        jPanel3.setLayout(new FlowLayout(1, 5, 5));
        jPanel10.add((Component) jPanel3, new GridConstraints(4, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        jPanel3.setBorder(BorderFactory.createTitledBorder(null, "适用中间件", 0, 0, null, null));
        this.canAppliedDBPanel = jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout(1, 5, 5));
        jPanel10.add((Component) jPanel2, new GridConstraints(5, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        jPanel2.setBorder(BorderFactory.createTitledBorder(null, "适用数据库", 0, 0, null, null));
        JPanel jPanel11 = new JPanel();
        jPanel11.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel10.add((Component) jPanel11, new GridConstraints(1, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        JLabel jLabel8 = new JLabel();
        jLabel8.setText("参考补丁");
        jPanel11.add((Component) jLabel8, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.referPatchText = jTextField5 = new JTextField();
        jPanel11.add((Component) jTextField5,
                new GridConstraints(0, 1, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel9 = new JLabel();
        jLabel9.setText("修改模块");
        jPanel11.add((Component) jLabel9, new GridConstraints(0, 2, 1, 1, 8, 0, 0, 0, null, null, null));
        this.editModuleText = jTextField4 = new JTextField();
        jPanel11.add((Component) jTextField4,
                new GridConstraints(0, 3, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel10 = new JLabel();
        jLabel10.setText("补丁依赖");
        jPanel11.add((Component) jLabel10, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.relyPatchText = jTextField3 = new JTextField();
        jPanel11.add((Component) jTextField3,
                new GridConstraints(1, 1, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel11 = new JLabel();
        jLabel11.setText("搜索关键字");
        jPanel11.add((Component) jLabel11, new GridConstraints(1, 2, 1, 1, 8, 0, 0, 0, null, null, null));
        this.keywordText = jTextField2 = new JTextField();
        jPanel11.add((Component) jTextField2,
                new GridConstraints(1, 3, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JLabel jLabel12 = new JLabel();
        jLabel12.setText("补丁描述");
        jPanel11.add((Component) jLabel12, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.patchDescText = jTextField = new JTextField();
        jPanel11.add((Component) jTextField,
                new GridConstraints(2, 1, 1, 3, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        JPanel jPanel12 = new JPanel();
        jPanel12.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane2.addTab("缺陷信息及安装说明", null, jPanel12, null);
        JPanel jPanel13 = new JPanel();
        jPanel13.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel12.add((Component) jPanel13, new GridConstraints(0, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        jPanel13.setBorder(BorderFactory.createTitledBorder(null, "BUG列表", 0, 0, null, null));
        this.addBugButton = jButton4 = new JButton();
        jButton4.setText("添加");
        jPanel13.add((Component) jButton4, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null));
        this.delBugButton = jButton3 = new JButton();
        jButton3.setText("删除");
        jPanel13.add((Component) jButton3, new GridConstraints(1, 1, 1, 1, 0, 1, 3, 0, null, null, null));
        JScrollPane jScrollPane2 = new JScrollPane();
        jPanel13.add((Component) jScrollPane2, new GridConstraints(0, 0, 2, 1, 0, 3, 7, 7, null, null, null));
        this.bugsTable = jTable = new JTable();
        jTable.setCellSelectionEnabled(false);
        jTable.setUpdateSelectionOnSort(true);
        jScrollPane2.setViewportView(jTable);
        JPanel jPanel14 = new JPanel();
        jPanel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel12.add((Component) jPanel14, new GridConstraints(1, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        jPanel14.setBorder(BorderFactory.createTitledBorder(null, "安装说明", 0, 0, null, null));
        this.tabbedPane2 = jTabbedPane = new JTabbedPane();
        jPanel14.add((Component) jTabbedPane,
                new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, new Dimension(200, 200), null));
        JPanel jPanel15 = new JPanel();
        jPanel15.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane.addTab("前置工作", null, jPanel15, null);
        JScrollPane jScrollPane3 = new JScrollPane();
        jPanel15.add((Component) jScrollPane3, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        this.preWorkPane = jTextPane5 = new JTextPane();
        jTextPane5.setToolTipText("");
        jScrollPane3.setViewportView(jTextPane5);
        JPanel jPanel16 = new JPanel();
        jPanel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane.addTab("补丁安装", null, jPanel16, null);
        JScrollPane jScrollPane4 = new JScrollPane();
        jPanel16.add((Component) jScrollPane4, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        this.patchInstallPane = jTextPane4 = new JTextPane();
        jScrollPane4.setViewportView(jTextPane4);
        JPanel jPanel17 = new JPanel();
        jPanel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane.addTab("后置工作", null, jPanel17, null);
        JScrollPane jScrollPane5 = new JScrollPane();
        jPanel17.add((Component) jScrollPane5, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        this.lastWorkPane = jTextPane3 = new JTextPane();
        jScrollPane5.setViewportView(jTextPane3);
        JPanel jPanel18 = new JPanel();
        jPanel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane.addTab("验证工作", null, jPanel18, null);
        JScrollPane jScrollPane6 = new JScrollPane();
        jPanel18.add((Component) jScrollPane6, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        this.verifyWorkPane = jTextPane2 = new JTextPane();
        jScrollPane6.setViewportView(jTextPane2);
        JPanel jPanel19 = new JPanel();
        jPanel19.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jTabbedPane.addTab("其他信息", null, jPanel19, null);
        JScrollPane jScrollPane7 = new JScrollPane();
        jPanel19.add((Component) jScrollPane7, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        this.otherWorkPane = jTextPane = new JTextPane();
        jScrollPane7.setViewportView(jTextPane);
        JPanel jPanel20 = new JPanel();
        jPanel20.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel6.add((Component) jPanel20, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 1, null, null, null));
        Spacer spacer = new Spacer();
        jPanel20.add((Component) spacer, new GridConstraints(0, 2, 1, 1, 0, 1, 6, 1, null, null, null));
        JPanel jPanel21 = new JPanel();
        jPanel21.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        jPanel20.add((Component) jPanel21, new GridConstraints(0, 3, 1, 1, 0, 3, 3, 3, null, null, null));
        this.buttonOK = jButton2 = new JButton();
        jButton2.setText("导出");
        jPanel21.add((Component) jButton2, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null));
        this.buttonCancel = jButton = new JButton();
        jButton.setText("取消");
        jPanel21.add((Component) jButton, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null));
        this.hiddenPanel = jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel20.add((Component) jPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        
        this.client2NcchrCheckBox = jCheckBox = new JCheckBox();
        jCheckBox.setText("client端补丁导出到ncchr");
        jPanel20.add((Component) jCheckBox, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 0, null, null, null));
        
        this.client2ModulesCheckBox = jCheckBox2 = new JCheckBox();
        jCheckBox2.setText("client端补丁导出到重量端");
        jPanel20.add((Component) jCheckBox2, new GridConstraints(0, 1, 1, 1, 8, 0, 3, 0, null, null, null));
        
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }
}
