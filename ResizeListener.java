import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ResizeListener implements MouseListener, MouseMotionListener {
    private static final int RESIZE_BORDER_WIDTH = 5; // リサイズ用ボーダーの幅
    
    private JFrame frame;
    private int cursor;
    private Point startPoint;
    private Rectangle originalBounds;
    
    public ResizeListener(JFrame frame) {
        this.frame = frame;
        this.cursor = Cursor.DEFAULT_CURSOR;
    }
    
    // フレームの枠を描画するためのメソッド
    public static void drawResizableBorder(JFrame frame) {
        JRootPane rootPane = frame.getRootPane();
        
        rootPane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), RESIZE_BORDER_WIDTH));
    }
    
    // どの領域にマウスがあるかを判定し、カーソルを設定
    private int detectCursor(Point point) {
        int width = frame.getWidth();
        int height = frame.getHeight();
        
        // 左上隅
        if (point.x <= RESIZE_BORDER_WIDTH && point.y <= RESIZE_BORDER_WIDTH) {
            return Cursor.NW_RESIZE_CURSOR;
        }
        // 右上隅
        else if (point.x >= width - RESIZE_BORDER_WIDTH && point.y <= RESIZE_BORDER_WIDTH) {
            return Cursor.NE_RESIZE_CURSOR;
        }
        // 左下隅
        else if (point.x <= RESIZE_BORDER_WIDTH && point.y >= height - RESIZE_BORDER_WIDTH) {
            return Cursor.SW_RESIZE_CURSOR;
        }
        // 右下隅
        else if (point.x >= width - RESIZE_BORDER_WIDTH && point.y >= height - RESIZE_BORDER_WIDTH) {
            return Cursor.SE_RESIZE_CURSOR;
        }
        // 上辺
        else if (point.y <= RESIZE_BORDER_WIDTH) {
            return Cursor.N_RESIZE_CURSOR;
        }
        // 下辺
        else if (point.y >= height - RESIZE_BORDER_WIDTH) {
            return Cursor.S_RESIZE_CURSOR;
        }
        // 左辺
        else if (point.x <= RESIZE_BORDER_WIDTH) {
            return Cursor.W_RESIZE_CURSOR;
        }
        // 右辺
        else if (point.x >= width - RESIZE_BORDER_WIDTH) {
            return Cursor.E_RESIZE_CURSOR;
        }
        // 枠外
        else {
            return Cursor.DEFAULT_CURSOR;
        }
    }
    
    // フレームをリサイズするメソッド
    private void resizeFrame(MouseEvent e) {
        if (startPoint == null) return;
        
        // 縦横同時に変更するときの動きを改善
        // setBounds を使わずに別々に操作する
        Point currentPoint = e.getPoint();
        int dx = currentPoint.x - startPoint.x;
        int dy = currentPoint.y - startPoint.y;
        
        // 元の値を取得
        int x = frame.getX();
        int y = frame.getY();
        int width = frame.getWidth();
        int height = frame.getHeight();
        
        // 最小サイズ
        int minWidth = 300;
        int minHeight = 200;
        
        boolean changed = false;
        
        switch (cursor) {
            case Cursor.N_RESIZE_CURSOR: // 上
                if (height - dy >= minHeight) {
                    frame.setLocation(x, y + dy);
                    frame.setSize(width, height - dy);
                    changed = true;
                }
                break;
            case Cursor.S_RESIZE_CURSOR: // 下
                if (height + dy >= minHeight) {
                    frame.setSize(width, height + dy);
                    changed = true;
                }
                break;
            case Cursor.W_RESIZE_CURSOR: // 左
                if (width - dx >= minWidth) {
                    frame.setLocation(x + dx, y);
                    frame.setSize(width - dx, height);
                    changed = true;
                }
                break;
            case Cursor.E_RESIZE_CURSOR: // 右
                if (width + dx >= minWidth) {
                    frame.setSize(width + dx, height);
                    changed = true;
                }
                break;
            case Cursor.NW_RESIZE_CURSOR: // 左上
                if (width - dx >= minWidth && height - dy >= minHeight) {
                    frame.setLocation(x + dx, y + dy);
                    frame.setSize(width - dx, height - dy);
                    changed = true;
                } else if (width - dx >= minWidth) {
                    // 幅のみ変更可能な場合
                    frame.setLocation(x + dx, y);
                    frame.setSize(width - dx, height);
                    changed = true;
                } else if (height - dy >= minHeight) {
                    // 高さのみ変更可能な場合
                    frame.setLocation(x, y + dy);
                    frame.setSize(width, height - dy);
                    changed = true;
                }
                break;
            case Cursor.NE_RESIZE_CURSOR: // 右上
                if (width + dx >= minWidth && height - dy >= minHeight) {
                    frame.setLocation(x, y + dy);
                    frame.setSize(width + dx, height - dy);
                    changed = true;
                } else if (width + dx >= minWidth) {
                    // 幅のみ変更可能な場合
                    frame.setSize(width + dx, height);
                    changed = true;
                } else if (height - dy >= minHeight) {
                    // 高さのみ変更可能な場合
                    frame.setLocation(x, y + dy);
                    frame.setSize(width, height - dy);
                    changed = true;
                }
                break;
            case Cursor.SW_RESIZE_CURSOR: // 左下
                if (width - dx >= minWidth && height + dy >= minHeight) {
                    frame.setLocation(x + dx, y);
                    frame.setSize(width - dx, height + dy);
                    changed = true;
                } else if (width - dx >= minWidth) {
                    // 幅のみ変更可能な場合
                    frame.setLocation(x + dx, y);
                    frame.setSize(width - dx, height);
                    changed = true;
                } else if (height + dy >= minHeight) {
                    // 高さのみ変更可能な場合
                    frame.setSize(width, height + dy);
                    changed = true;
                }
                break;
            case Cursor.SE_RESIZE_CURSOR: // 右下
                if (width + dx >= minWidth && height + dy >= minHeight) {
                    frame.setSize(width + dx, height + dy);
                    changed = true;
                } else if (width + dx >= minWidth) {
                    // 幅のみ変更可能な場合
                    frame.setSize(width + dx, height);
                    changed = true;
                } else if (height + dy >= minHeight) {
                    // 高さのみ変更可能な場合
                    frame.setSize(width, height + dy);
                    changed = true;
                }
                break;
        }
        
        // 変更した場合はスタートポイントを更新
        if (changed) {
            startPoint = currentPoint;
            originalBounds = frame.getBounds();
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        cursor = detectCursor(e.getPoint());
        if (cursor != Cursor.DEFAULT_CURSOR) {
            startPoint = e.getPoint();
            originalBounds = frame.getBounds();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        startPoint = null;
        originalBounds = null;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (cursor != Cursor.DEFAULT_CURSOR) {
            resizeFrame(e);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        // カーソルの形状を更新
        int newCursor = detectCursor(e.getPoint());
        if (newCursor != cursor) {
            cursor = newCursor;
            frame.setCursor(Cursor.getPredefinedCursor(cursor));
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {
        // フレームから出た時はカーソルをデフォルトに戻す
        frame.setCursor(Cursor.getDefaultCursor());
    }
}
