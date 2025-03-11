import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
  private JFrame frame;
  private JPanel sidebarPanel;
  private JPanel mainPanel;
  private JLabel statusLabel;
  private JTable dataTable;
  private DefaultTableModel tableModel;
  private JLabel timeLabel;

  public static void main(String[] args) {
    // macOS用の設定
    System.setProperty("apple.awt.brushMetalLook", "true");
    // 自由な位置設定を許可
    System.setProperty("java.awt.Window.locationByPlatform", "false");
    // macOSでの特殊な制限を無視
    System.setProperty("apple.awt.draggableWindowBackground", "true");
    // 全画面表示の許可（これが複数モニター環境での移動に影響することがある）
    System.setProperty("apple.awt.fullscreenable", "true");
    // ウィンドウ位置移動の制限を無効化（重要）
    System.setProperty("apple.awt.windowMoveLimit", "100000");
    // JFrameの透明化を許可（上部モニターへの移動に効果あり）
    System.setProperty("apple.awt.windowTranslucent", "true");

    SwingUtilities.invokeLater(() -> {
      try {
        // Set look and feel to system
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }

      new Main();
    });
  }

  public Main() {
    initialize();
  }

  private void initialize() {
    // Create main frame
    frame = new JFrame("業務管理システム");
    frame.setSize(1200, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);

    // 必ずsetVisibleの前にsetUndecoratedを設定
    frame.setUndecorated(true);

    // フレームを自由に配置できるようにする
    // マルチモニター環境のサポート向上
    try {
      // JFramesの位置取得に関する機能を有効化
      System.setProperty("java.awt.Window.locationByPlatform", "false");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // Macスタイルの丸い角を適用
    int arcSize = 15; // 丸みの大きさ
    setRoundedCorners(frame, arcSize);
    
    // フレームの枠を作成（リサイズ用）
    ResizeListener.drawResizableBorder(frame);
    
    // リサイズリスナーを追加
    ResizeListener resizeListener = new ResizeListener(frame);
    frame.addMouseListener(resizeListener);
    frame.addMouseMotionListener(resizeListener);

    // カスタムタイトルバーを作成
    JPanel titleBar = createCustomTitleBar();

    // メインコンテンツ用のパネルを作成
    JPanel contentPanel = new JPanel(new BorderLayout());

    // メニューバーを作成
    JMenuBar menuBar = createMenuBar();

    // メニューバーをパネルに配置
    JPanel menuPanel = new JPanel(new BorderLayout());
    menuPanel.add(menuBar, BorderLayout.NORTH);

    // ツールバーを作成
    JToolBar toolBar = createToolBar();

    // ツールバーをメニューパネルに追加
    menuPanel.add(toolBar, BorderLayout.SOUTH);

    // メニューパネルをコンテンツパネルの上部に追加
    contentPanel.add(menuPanel, BorderLayout.NORTH);

    // Create main content area with split pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setDividerLocation(250);
    splitPane.setDividerSize(5);

    // Create sidebar
    createSidebar();
    splitPane.setLeftComponent(sidebarPanel);

    // Create main content
    createMainContent();
    splitPane.setRightComponent(mainPanel);

    // スプリットペインをコンテンツパネルの中央に追加
    contentPanel.add(splitPane, BorderLayout.CENTER);

    // ステータスバーを作成
    JPanel statusBar = createStatusBar();
    contentPanel.add(statusBar, BorderLayout.SOUTH);

    // Start status bar timer
    startStatusBarTimer();

    // メインパネルにすべてのコンテンツを配置
    JPanel rootPanel = new JPanel(new BorderLayout());
    rootPanel.add(titleBar, BorderLayout.NORTH);
    rootPanel.add(contentPanel, BorderLayout.CENTER);

    // メインパネルをフレームに追加
    frame.setContentPane(rootPanel);

    // 最後に可視化
    frame.setVisible(true);
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // File menu
    JMenu fileMenu = new JMenu("ファイル");
    fileMenu.add(new JMenuItem("新規作成"));

    JMenuItem openItem = new JMenuItem("開く");
    openItem.addActionListener(e -> openFileDialog());
    fileMenu.add(openItem);

    fileMenu.add(new JMenuItem("保存"));
    fileMenu.addSeparator();

    JMenuItem exitItem = new JMenuItem("終了");
    exitItem.addActionListener(e -> System.exit(0));
    fileMenu.add(exitItem);

    // Edit menu
    JMenu editMenu = new JMenu("編集");
    editMenu.add(new JMenuItem("切り取り"));
    editMenu.add(new JMenuItem("コピー"));
    editMenu.add(new JMenuItem("貼り付け"));

    // View menu
    JMenu viewMenu = new JMenu("表示");
    viewMenu.add(new JMenuItem("詳細"));
    viewMenu.add(new JMenuItem("概要"));

    // Tools menu
    JMenu toolsMenu = new JMenu("ツール");
    toolsMenu.add(new JMenuItem("設定"));
    toolsMenu.add(new JMenuItem("カスタマイズ"));

    // Help menu
    JMenu helpMenu = new JMenu("ヘルプ");
    helpMenu.add(new JMenuItem("ヘルプトピック"));
    helpMenu.add(new JMenuItem("バージョン情報"));

    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    menuBar.add(viewMenu);
    menuBar.add(toolsMenu);
    menuBar.add(helpMenu);

    return menuBar;
  }

  private JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);

    JButton newButton = new JButton("新規");
    newButton.setFocusable(false);
    toolBar.add(newButton);

    JButton openButton = new JButton("開く");
    openButton.setFocusable(false);
    openButton.addActionListener(e -> openFileDialog());
    toolBar.add(openButton);

    JButton saveButton = new JButton("保存");
    saveButton.setFocusable(false);
    toolBar.add(saveButton);

    toolBar.addSeparator();

    JButton searchButton = new JButton("検索");
    searchButton.setFocusable(false);
    toolBar.add(searchButton);

    JTextField searchField = new JTextField(20);
    toolBar.add(searchField);

    toolBar.addSeparator();

    JButton reportButton = new JButton("レポート");
    reportButton.setFocusable(false);
    toolBar.add(reportButton);

    JButton settingsButton = new JButton("設定");
    settingsButton.setFocusable(false);
    toolBar.add(settingsButton);

    return toolBar;
  }

  private void createSidebar() {
    sidebarPanel = new JPanel(new BorderLayout());
    sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // Create a label at the top of the sidebar
    JLabel sidebarTitle = new JLabel("メニュー");
    sidebarTitle.setFont(new Font("Dialog", Font.BOLD, 14));
    sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    sidebarPanel.add(sidebarTitle, BorderLayout.NORTH);

    // Create a list of options
    String[] options = { "ダッシュボード", "顧客管理", "売上管理", "製品管理", "在庫管理", "社員管理", "設定" };
    JList<String> optionList = new JList<>(options);
    optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    optionList.setSelectedIndex(0);
    optionList.setFont(new Font("Dialog", Font.PLAIN, 14));

    // Add selection listener
    optionList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        String selected = optionList.getSelectedValue();
        updateMainContent(selected);
      }
    });

    JScrollPane listScrollPane = new JScrollPane(optionList);
    listScrollPane.setBorder(null);
    sidebarPanel.add(listScrollPane, BorderLayout.CENTER);
  }

  private void createMainContent() {
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Create a header panel
    JPanel headerPanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel("顧客管理");
    titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
    headerPanel.add(titleLabel, BorderLayout.WEST);

    // Add some action buttons to header
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    actionPanel.add(new JButton("新規登録"));
    actionPanel.add(new JButton("一括編集"));
    actionPanel.add(new JButton("エクスポート"));
    headerPanel.add(actionPanel, BorderLayout.EAST);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Create table for data display
    createDataTable();
    mainPanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);

    // Create pagination panel
    JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    paginationPanel.add(new JButton("<<"));
    paginationPanel.add(new JButton("<"));
    paginationPanel.add(new JLabel("1 / 10 ページ"));
    paginationPanel.add(new JButton(">"));
    paginationPanel.add(new JButton(">>"));
    paginationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    mainPanel.add(paginationPanel, BorderLayout.SOUTH);
  }

  private void createDataTable() {
    String[] columnNames = { "ID", "顧客名", "会社名", "電話番号", "メール", "登録日", "最終購入日", "ステータス" };

    // Sample data
    Object[][] data = {
        { "1001", "田中 太郎", "株式会社タナカ", "03-1234-5678", "tanaka@example.com", "2024-01-15", "2024-03-05", "アクティブ" },
        { "1002", "佐藤 花子", "佐藤商事", "03-2345-6789", "sato@example.com", "2024-01-20", "2024-02-28", "アクティブ" },
        { "1003", "鈴木 一郎", "鈴木工業", "03-3456-7890", "suzuki@example.com", "2024-01-25", "2024-03-01", "休眠" },
        { "1004", "高橋 直子", "高橋建設", "03-4567-8901", "takahashi@example.com", "2024-02-01", "2024-02-15", "アクティブ" },
        { "1005", "渡辺 健太", "渡辺製作所", "03-5678-9012", "watanabe@example.com", "2024-02-05", "2024-03-02", "アクティブ" },
        { "1006", "伊藤 美加", "伊藤商店", "03-6789-0123", "ito@example.com", "2024-02-10", "2024-02-20", "休眠" },
        { "1007", "山本 龍太郎", "山本電機", "03-7890-1234", "yamamoto@example.com", "2024-02-15", "", "新規" },
        { "1008", "中村 由美", "中村食品", "03-8901-2345", "nakamura@example.com", "2024-02-20", "2024-03-10", "アクティブ" },
        { "1009", "小林 俊介", "小林商事", "03-9012-3456", "kobayashi@example.com", "2024-02-25", "2024-03-08", "アクティブ" },
        { "1010", "加藤 恵", "加藤工業", "03-0123-4567", "kato@example.com", "2024-03-01", "", "新規" }
    };

    tableModel = new DefaultTableModel(data, columnNames) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Make table non-editable
      }
    };

    dataTable = new JTable(tableModel);
    dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.setRowHeight(25);
    dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    dataTable.getTableHeader().setReorderingAllowed(false);

    // Add a row selection listener
    dataTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting() && dataTable.getSelectedRow() != -1) {
        int row = dataTable.getSelectedRow();
        String customerId = (String) dataTable.getValueAt(row, 0);
        String customerName = (String) dataTable.getValueAt(row, 1);
        statusLabel.setText("選択: " + customerId + " - " + customerName);
      }
    });
  }

  private JPanel createStatusBar() {
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setBorder(new CompoundBorder(
        new MatteBorder(1, 0, 0, 0, Color.GRAY),
        new EmptyBorder(3, 5, 3, 5)));

    // Status message on the left
    statusLabel = new JLabel("準備完了");
    statusPanel.add(statusLabel, BorderLayout.WEST);

    // Current date and time on the right
    timeLabel = new JLabel();
    updateTimeLabel(timeLabel);
    statusPanel.add(timeLabel, BorderLayout.EAST);

    return statusPanel;
  }

  private void updateTimeLabel(JLabel timeLabel) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    timeLabel.setText(sdf.format(new Date()));
  }

  private void startStatusBarTimer() {
    // Swingタイマーを使用して1秒ごとに更新
    Timer timer = new Timer(1000, e -> {
      updateTimeLabel(timeLabel);
    });
    timer.start();
  }
  
  /**
   * Macスタイルの丸い角をウィンドウに適用するメソッド
   * @param frame 丸い角を適用するJFrame
   * @param arcSize 角の丸みの大きさ（ピクセル単位）
   */
  private void setRoundedCorners(JFrame frame, int arcSize) {
    // 丸い角の形状を設定
    frame.setShape(new java.awt.geom.RoundRectangle2D.Double(
      0, 0, frame.getWidth(), frame.getHeight(), arcSize, arcSize
    ));
    
    // ウィンドウサイズが変更されたときに丸い角を更新するリスナーを追加
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        // サイズ変更時に形状を更新
        frame.setShape(new java.awt.geom.RoundRectangle2D.Double(
          0, 0, frame.getWidth(), frame.getHeight(), arcSize, arcSize
        ));
      }
    });
  }

  /**
   * ファイルダイアログを開く
   */
  private void openFileDialog() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("ファイルを開く");

    // ファイルフィルターの設定（例：テキストファイルのみ）
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "テキストファイル (*.txt)", "txt");
    fileChooser.addChoosableFileFilter(filter);

    // すべてのファイルも選択可能に
    fileChooser.setAcceptAllFileFilterUsed(true);

    int result = fileChooser.showOpenDialog(frame);

    if (result == JFileChooser.APPROVE_OPTION) {
      // 選択されたファイル
      java.io.File selectedFile = fileChooser.getSelectedFile();

      // ファイルの処理（例：状態バーに表示）
      statusLabel.setText("選択されたファイル: " + selectedFile.getAbsolutePath());

      // ここにファイルを開く処理を追加
      // 例: readFile(selectedFile);
    }
  }

  private void updateMainContent(String selected) {
    JLabel titleLabel = (JLabel) ((JPanel) mainPanel.getComponent(0)).getComponent(0);
    titleLabel.setText(selected);
    statusLabel.setText(selected + "を表示しています");
  }

  /**
   * カスタムタイトルバーを作成する
   * デフォルトのタイトルバーの代わりに使用するためのカスタムタイトルバー
   * 
   * @return 作成したタイトルバーパネル
   */
  private JPanel createCustomTitleBar() {
    // タイトルバーパネルの作成
    JPanel titleBar = new JPanel();
    titleBar.setLayout(new BorderLayout());
    titleBar.setBackground(new Color(51, 102, 153)); // 濃い青色
    titleBar.setPreferredSize(new Dimension(frame.getWidth(), 30));

    MoveListener listener = new MoveListener(frame);
    titleBar.addMouseListener(listener);
    titleBar.addMouseMotionListener(listener);

    // タイトルラベル（左側）
    JLabel titleLabel = new JLabel("  業務管理システム");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
    titleBar.add(titleLabel, BorderLayout.WEST);

    // コントロールボタンパネル（右側）
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    controlPanel.setOpaque(false);

    // 最小化ボタン
    JButton minimizeButton = createTitleBarButton("ー", new Color(204, 51, 51), e -> {
      frame.setState(Frame.ICONIFIED);
    });

    // 最大化ボタン
    JButton maximizeButton = createTitleBarButton("◻︎", new Color(204, 51, 51), e -> {
      // 最大化/通常サイズの切り替え
      if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
        frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
      } else {
        frame.setExtendedState(frame.getExtendedState() & ~Frame.MAXIMIZED_BOTH);
      }
    });

    // 閉じるボタン
    JButton closeButton = createTitleBarButton("×", new Color(204, 51, 51), e -> {
      frame.dispose();
      System.exit(0);
    });

    controlPanel.add(minimizeButton);
    controlPanel.add(maximizeButton);
    controlPanel.add(closeButton);

    titleBar.add(controlPanel, BorderLayout.EAST);

    // 作成したタイトルバーを返す
    return titleBar;
  }

  /**
   * タイトルバー用のボタンを作成するヘルパーメソッド
   */
  private JButton createTitleBarButton(String text, Color hoverColor, ActionListener actionListener) {
    JButton button = new JButton(text);
    button.setPreferredSize(null);
    button.setFocusable(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Dialog", Font.BOLD, 14));

    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setContentAreaFilled(true);
        button.setBackground(hoverColor);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setContentAreaFilled(false);
      }
    });

    button.addActionListener(actionListener);
    return button;
  }
}