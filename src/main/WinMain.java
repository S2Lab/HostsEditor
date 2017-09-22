package main;

import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

public class WinMain
{
	final public String pathHost_win10="C:\\Windows\\System32\\drivers\\etc\\hosts";

	private JFrame frmHost;
	private JTextField txt_ip;
	private JTextField txt_url;
	private JTextField txt_status;
	private JTextArea txt_hosts;
	
	private JButton button_clear;
	private JButton button_add_ban;
	private JButton button_remove_ban;
	private JButton button_search_ip;
	private JButton button_search_url;
	private JButton button_reload;
	private JButton button_save_all;
	private JButton button_add_item;
	private JButton button_remove_item;
	private JLabel label_ip;
	private JLabel label_url;
	
	private LinkedList<HostItem> host_items; // 只要窗口存在 就不为null 即使没有内容
	
	private HostItem pin_host_item; // 用来搜索/更改某个特定host项目
	
	public void reloadHostItems(String path_in)
	{
		try {
			
			
			File fileHost;
			if(path_in==null)
				fileHost=new File(pathHost_win10);
			else
				fileHost=new File(path_in);
			
			if(!fileHost.exists() || !fileHost.isFile())
				fileHost.createNewFile();
			
			Scanner in=new Scanner(fileHost);
			while(in.hasNextLine())
			{
				HostItem tempItem;
				String line=in.nextLine().trim();
				if(line.length()==0)
					tempItem=new HostItem("#");
				else
					tempItem=new HostItem(line);
				
				host_items.add(tempItem);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			setStatus("重载文件出错");
			setContent(null);
			host_items.clear();
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WinMain window = new WinMain();
					window.frmHost.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public WinMain() {
		initialize();
		this.frmHost.setVisible(true);
		host_items=new LinkedList<HostItem>();
		reloadHostItems(null);
		setContent();
	}
	
	protected void saveChanges()
	{
		System.out.println("保存改动");
	}
	
	protected void refresh()
	{
		setContent();
		txt_ip.setText("");
		txt_url.setText("");
		
		setButtonsEnablity(true);
	}
	protected void setButtonsEnablity(boolean i)
	{
		button_reload.setEnabled(i);
		button_clear.setEnabled(i);
		button_add_ban.setEnabled(i);
		button_add_item.setEnabled(i);
		button_remove_ban.setEnabled(i);
		button_remove_item.setEnabled(i);
		button_search_ip.setEnabled(i);
		button_search_url.setEnabled(i);
	}
	
	// 用来根据表的内容刷新显示框内容
	protected void setContent()
	{
		StringBuffer content=new StringBuffer("");
		
		for(HostItem tempItem:host_items)
		{
			if(tempItem.isAnnotation())
			{
				
				content.append(tempItem.getAnnotation()+'\n');
			}
			else
			{
				content.append(tempItem.getIP()+' '+tempItem.getURL()+'\n');
			}
		}
		txt_hosts.setText(content.toString());
	}

	// 用来根据输入的内容刷新显示内容
	protected void setContent(String[] lines)
	{
		if(lines==null)
		{
			txt_hosts.setText("");
			return;
		}
		StringBuffer content=new StringBuffer("");
		for(String line:lines)
		{
			content.append(line+'\n');
		}
		txt_hosts.setText(content.toString());
	}
	
	protected void setStatus(String contentIn)
	{
		txt_status.setText(contentIn);
	}
	
	public String gettxtIP()
	{
		return txt_ip.getText();
	}
	
	public String gettxtURL()
	{
		return txt_url.getText();
	}
	
	// 判断txt内容是否是ip
	public boolean istxtIP()
	{
		return HostItem.getIP(txt_ip.getText().trim())!=null;
	}
	// 判断txt内容是否是url
	public boolean istxtURL()
	{
		return txt_url.getText().trim().length()>0;
	}
	
	private void initialize() {
		frmHost = new JFrame();
		frmHost.setTitle("host编辑器");
		frmHost.setResizable(false);
		frmHost.setBounds(100, 100, 660, 600);
		frmHost.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmHost.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 13, 627, 349);
		frmHost.getContentPane().add(scrollPane);
		
		txt_hosts = new JTextArea();
		txt_hosts.setBackground(new Color(204, 255, 204));
		txt_hosts.setEditable(false);
		txt_hosts.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		txt_hosts.setText("hosts文件内容");
		scrollPane.setViewportView(txt_hosts);
		
		button_clear = new JButton("清空所有规则");
		button_clear.setBackground(SystemColor.control);
		button_clear.setToolTipText("清空所有host项目");
		button_clear.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_clear.setBounds(347, 525, 150, 27);
		frmHost.getContentPane().add(button_clear);
		
		button_add_ban = new JButton("添加屏蔽项目");
		button_add_ban.setBackground(SystemColor.control);
		button_add_ban.setToolTipText("将一个网址映射到0.0.0.0");
		button_add_ban.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_add_ban.setBounds(183, 485, 150, 27);
		frmHost.getContentPane().add(button_add_ban);
		
		button_remove_ban = new JButton("移除屏蔽项目");
		button_remove_ban.setBackground(SystemColor.control);
		button_remove_ban.setToolTipText("移除一个网址到0.0.0.0的映射");
		button_remove_ban.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_remove_ban.setBounds(183, 525, 150, 27);
		frmHost.getContentPane().add(button_remove_ban);
		
		button_add_item = new JButton("添加映射项目");
		button_add_item.setBackground(SystemColor.control);
		button_add_item.setToolTipText("添加一个host项目");
		button_add_item.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_add_item.setBounds(19, 485, 150, 27);
		frmHost.getContentPane().add(button_add_item);
		
		button_remove_item = new JButton("移除映射项目");
		button_remove_item.setBackground(SystemColor.control);
		button_remove_item.setToolTipText("移除一个host项目");
		button_remove_item.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_remove_item.setBounds(19, 525, 150, 27);
		frmHost.getContentPane().add(button_remove_item);
		
		button_search_ip = new JButton("搜索IP");
		button_search_ip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStatus( istxtIP()? "yes":"no" );
			}
		});
		button_search_ip.setBackground(SystemColor.control);
		button_search_ip.setToolTipText("根据IP搜索host条目");
		button_search_ip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_search_ip.setBounds(491, 412, 150, 27);
		frmHost.getContentPane().add(button_search_ip);
		
		txt_ip = new JTextField();
		txt_ip.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		txt_ip.setText("ip");
		txt_ip.setBounds(83, 414, 394, 24);
		frmHost.getContentPane().add(txt_ip);
		txt_ip.setColumns(10);
		
		label_ip = new JLabel("IP");
		label_ip.setLabelFor(txt_ip);
		label_ip.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		label_ip.setBounds(14, 417, 55, 18);
		frmHost.getContentPane().add(label_ip);
		
		label_url = new JLabel("URL");
		label_url.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		label_url.setBounds(14, 454, 55, 18);
		frmHost.getContentPane().add(label_url);
		
		txt_url = new JTextField();
		label_url.setLabelFor(txt_url);
		txt_url.setText("url");
		txt_url.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		txt_url.setColumns(10);
		txt_url.setBounds(83, 451, 394, 24);
		frmHost.getContentPane().add(txt_url);
		
		button_search_url = new JButton("搜索URL");
		button_search_url.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStatus( istxtURL() ? "是网址":"不是网址");
			}
		});
		button_search_url.setBackground(SystemColor.control);
		button_search_url.setToolTipText("根据URL搜索host条目");
		button_search_url.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_search_url.setBounds(491, 449, 150, 27);
		frmHost.getContentPane().add(button_search_url);
		
		txt_status = new JTextField();
		txt_status.setEditable(false);
		txt_status.setBackground(Color.BLACK);
		txt_status.setForeground(Color.WHITE);
		txt_status.setText("操作结果");
		txt_status.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		txt_status.setColumns(10);
		txt_status.setBounds(14, 375, 627, 24);
		frmHost.getContentPane().add(txt_status);
		
		button_reload = new JButton("重新加载");
		button_reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				refresh();
				setStatus("已经重新载入hosts文件");
			}
		});
		button_reload.setToolTipText("重新加载hosts文件内容");
		button_reload.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_reload.setBackground(SystemColor.menu);
		button_reload.setBounds(511, 485, 130, 27);
		frmHost.getContentPane().add(button_reload);
		
		button_save_all = new JButton("保存改动");
		button_save_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveChanges();
				setStatus("已保存改动");
			}
		});
		button_save_all.setBounds(511, 525, 130, 27);
		frmHost.getContentPane().add(button_save_all);
		button_save_all.setToolTipText("保存对hosts文件的改动");
		button_save_all.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_save_all.setBackground(SystemColor.menu);
	}

	;
}
