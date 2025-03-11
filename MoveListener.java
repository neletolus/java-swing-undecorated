import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Date;

public class MoveListener implements MouseListener, MouseMotionListener {

  private Point pressedPoint;
  private Rectangle frameBounds;
  private Date lastTimeStamp;

  private JFrame frame;

  public MoveListener(JFrame frame) {
    this.frame = frame;
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    // ダブルクリックの検出
    if (event.getClickCount() == 2) {
      // 最大化/通常サイズの切り替え
      if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
        frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
      } else {
        frame.setExtendedState(frame.getExtendedState() & ~Frame.MAXIMIZED_BOTH);
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent event) {
    this.frameBounds = frame.getBounds();
    this.pressedPoint = event.getPoint();
    this.lastTimeStamp = new Date();
  }

  @Override
  public void mouseReleased(MouseEvent event) {
    moveJFrame(event);
  }

  @Override
  public void mouseEntered(MouseEvent event) {
  }

  @Override
  public void mouseExited(MouseEvent event) {
  }

  @Override
  public void mouseDragged(MouseEvent event) {
    moveJFrame(event);
  }

  @Override
  public void mouseMoved(MouseEvent event) {
  }

  private void moveJFrame(MouseEvent event) {
    Point endPoint = event.getPoint();

    int xDiff = endPoint.x - pressedPoint.x;
    int yDiff = endPoint.y - pressedPoint.y;

    Date timestamp = new Date();

    // One move action per 60ms to avoid frame glitching
    if (Math.abs(timestamp.getTime() - lastTimeStamp.getTime()) > 60) {
      if ((xDiff > 0 || yDiff > 0) || (xDiff < 0 || yDiff < 0)) {
        frameBounds.x += xDiff;
        frameBounds.y += yDiff;
        System.out.println(frameBounds);
        frame.setBounds(frameBounds);
      }
      this.lastTimeStamp = timestamp;
    }
  }

}