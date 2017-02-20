/*
 * @project Test2
 * @package monitor
 * @file SystemClipboardMonitor.java
 * @author oliver.xyy
 * @pubTime 2016-12-26 17:23:28
 */
package monitor;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.IFile;

/**
 * 剪贴板监控器 负责对剪贴板的监控和操作 由于监控需要一个对象作为ClipboardOwner，故不能用静态类
 * 不用FlavorListener是因为它仅监控剪贴板中数据类型的变化.
 */
public enum SystemClipboardMonitor implements ClipboardOwner {

	INSTANCE;

	private SystemClipboardMonitor() {
	}

	/** 剪贴板 */
	private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	/** 控制开关. */
	private static boolean switcher = false;
	
	private static JButton bt = new JButton("Off");

	/** The Constant df1. */
	private static final SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");

	/** The Constant df2. */
	private static final SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");

	private static class NoteWindow {
		private static final JFrame note = new JFrame("note");// 画个窗口示意启动，界面什么的之后再说吧
	}

	public static final void show() {
		bt.setFont(new Font("宋体", Font.PLAIN, 24));
		bt.setBounds(0, 0, 100, 50);
		bt.addActionListener(e->{switcher = !switcher;if(switcher)bt.setText("On");else bt.setText("Off");});
		
        JPanel panel = new JPanel(); 
        panel.add(bt);
        panel.setSize(120, 70);
        NoteWindow.note.add(panel);
		NoteWindow.note.setVisible(true);
		NoteWindow.note.setSize(120, 70);
		NoteWindow.note.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		StringBuffer sb = new StringBuffer();
	}

	/**
	 * 设置50000端口作为note端口
	 */
	@SuppressWarnings("resource")
	private static boolean checkUniqueByPort() {
		try {
			return !new ServerSocket(50000, 1, InetAddress.getByName("localhost")).isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 开启监听剪贴板，并将剪贴板中内容的ClipboardOwner设置为this 当剪贴板内容发生改变时，就会触发lostOwnership方法
	 */
	public synchronized void begin() {
		if (checkUniqueByPort()) {
			switcher = true;
			clipboard.setContents(clipboard.getContents(DataFlavor.stringFlavor), this);
			show();
		}
	}

	/**
	 * 如果剪贴板的内容改变，则自动调用此方法.
	 *
	 * @param clipboard
	 *            剪贴板
	 * @param contents
	 *            剪贴板内容
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		if (switcher) {
			try {
				Thread.sleep(10); // 暂停一下，可能是系统在使用剪贴板
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 如果剪贴板有内容（并继续注册ClipboardOwner为this）
			if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				try {
					clipboard.setContents(clipboard.getContents(DataFlavor.stringFlavor), this);
					String text = clipboard.getData(DataFlavor.stringFlavor).toString();
					StringBuffer sb = new StringBuffer();
					sb.append("/home/oliver/note/").append(df1.format(new java.util.Date())).append(".html");
					String filename = sb.toString();
					if(!new File(sb.toString()).exists())
						filename = getClass().getResource("").getPath()+"template.html";
					IFile.write(sb.toString(), HtmlParser.addItem(filename, text).html(), false);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			} else {
				clipboard.setContents(clipboard.getContents(null), this);
			}
		}
	}

}
