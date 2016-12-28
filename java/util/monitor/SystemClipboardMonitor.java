/*
 * @project Test2
 * @package monitor
 * @file SystemClipboardMonitor.java
 * @author oliver.xyy
 * @pubTime 2016-12-26 17:23:28
 */
package monitor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import util.IFile;
 
/**
 * 剪贴板监控器
 * 负责对剪贴板的监控和操作
 * 由于监控需要一个对象作为ClipboardOwner，故不能用静态类
 * 不用FlavorListener是因为它仅监控剪贴板中数据类型的变化.
 */
public class SystemClipboardMonitor implements ClipboardOwner{
    
    /** 剪贴板 */
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    /** 控制开关. */
    private static boolean going; 
    
    /** The Constant df1. */
    private static final SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
    
    /** The Constant df2. */
    private static final SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
    
    private volatile static SystemClipboardMonitor scm = null;
    
    private volatile static JFrame test;
    
    public SystemClipboardMonitor(){}
    
    /**
     * 单例模式防止出现两个监听器，剪贴板监听会出现问题
     * @return new SystemClipboardMonitor()
     */
    public static SystemClipboardMonitor getInstancce(){
    	if(scm == null){
    		synchronized (SystemClipboardMonitor.class) {
				if(scm == null){
					scm = new SystemClipboardMonitor();
				}
			}
    	}
    	return scm;
    }
    /**
     * 开启监听剪贴板，并将剪贴板中内容的ClipboardOwner设置为this
     * 当剪贴板内容发生改变时，就会触发lostOwnership方法
     */
    public void begin(){
        going = true;
        clipboard.setContents(clipboard.getContents(null), scm);
		scm.showWindow();//窗口用来保持监听程序不被销毁（关了就销毁了）
    }
    
    /**
     * 停止监听剪贴板
     */
    public void stop(){
        going = false;
    }
    
    private void showWindow(){
    	if(test == null){
	    	test = new JFrame("note");// 画个窗口示意启动，界面什么的之后再说把
	        test.setVisible(true);
	        test.setSize(100, 100);
	        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	}
    }
    /**
     * 如果剪贴板的内容改变，则自动调用此方法.
     *
     * @param clipboard 剪贴板
     * @param contents 剪贴板内容
     * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
     */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        if (going){ 
        	//暂停一下，可能是系统在使用剪贴板
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //如果剪贴板有内容（并继续注册ClipboardOwner为this）
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                try {
                    clipboard.setContents(clipboard.getContents(DataFlavor.stringFlavor), scm);
                    
                    String text = clipboard.getData(DataFlavor.stringFlavor).toString();
                    IFile.write("/home/oliver/note/"+df1.format(new java.util.Date()), "\nTime: "+df2.format(new java.util.Date()), true);
                    IFile.write("/home/oliver/note/"+df1.format(new java.util.Date()), text, true);
                } catch (Exception e) {
                    //e.printStackTrace();
                } 
            } else { 
                clipboard.setContents(clipboard.getContents(null), scm);
            }
             
        }
    }
 
}

