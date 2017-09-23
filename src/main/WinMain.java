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
import java.awt.event.MouseAdapter;

public class WinMain
{
	final public String pathHost_win10="C:\\Windows\\System32\\drivers\\etc\\hosts";
	
	private String now_path;
	
	final private int PIN_NONE=0;
	final private int PIN_IP=1;
	final private int PIN_URL=2;
	final private int PIN_ALL=4;
	

	private HostItem pin_host_item; // 用来搜索/更改某个特定host项目
	private int pin_type; // 用来记录当前指针状态

	private JFrame frmHost;
	private JTextField txt_ip;
	private JTextField txt_url;
	private JTextField txt_status;
	private JTextArea txt_hosts;
	
	private JButton button_clear;
	private JButton button_add_ban;
	private JButton button_remove;
	private JButton button_search_ip;
	private JButton button_search_url;
	private JButton button_reload;
	private JButton button_save_all;
	private JButton button_add_item;
	private JLabel label_ip;
	private JLabel label_url;
	
	private LinkedList<HostItem> host_items; // 只要窗口存在 就不为null 即使没有内容
	
	private JButton button_auto_add;
	
	public void reloadHostItems(String path_in)
	{
		try {
			
			
			File fileHost;
			if(path_in==null)
				fileHost=new File(now_path=pathHost_win10);
			else
				fileHost=new File(now_path=path_in);
			
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
		// System.out.println("保存改动");

		File fileoutput=new File(now_path);
		try
		{
			if(!fileoutput.exists() || !fileoutput.isFile())
				fileoutput.createNewFile();
			
			Writer out=new FileWriter(fileoutput);
			for(HostItem tempItem:host_items)
			{
				if(tempItem.isAnnotation())
				{
					out.write(tempItem.annotation+"\r\n");
				}
				else
				{
					out.write(tempItem.getIP()+' '+tempItem.url+"\r\n");
				}
			}
			out.flush();
			out.close();
			setStatus("成功保存所有改动");
		}
		catch(Exception e)
		{
			if(fileoutput.exists() && fileoutput.isFile())
				fileoutput.delete();
			setStatus("保存改动失败");
		}
	}
	
	protected void refresh()
	{
		setContent();
		setPinStatus();
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
		button_remove.setEnabled(i);
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
		txt_status.setText((pin_type==PIN_NONE? "未指向条目":"指向条目:"+pin_host_item.toString()) +"\t"+ contentIn);
	}
	
	public String gettxtIP()
	{
		return txt_ip.getText().trim();
	}
	
	public String gettxtURL()
	{
		return txt_url.getText().trim();
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
	// 设定对应内容
	public void setttxtIP(String ipIn)
	{
		txt_ip.setText(ipIn);
	}
	public void settxtIP(short[] ipIn)
	{
		if(ipIn.length!=4)
			;
		else
		{
			txt_ip.setText(""+ipIn[0]+'.'+ipIn[1]+'.'+ipIn[2]+'.'+ipIn[3]);
		}
	}
	public void settxtURL(String urlIn)
	{
		txt_url.setText(urlIn);
	}
	
	private void initialize() {
		frmHost = new JFrame();
		frmHost.getContentPane().setBackground(new Color(240, 255, 255));
		frmHost.getContentPane().setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		frmHost.setTitle("hosts文件编辑器");
		frmHost.setResizable(false);
		frmHost.setBounds(200, 200, 706, 560);
		frmHost.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmHost.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 13, 672, 349);
		frmHost.getContentPane().add(scrollPane);
		
		txt_hosts = new JTextArea();
		txt_hosts.setToolTipText("当前操作内容");
		txt_hosts.setBackground(new Color(204, 255, 204));
		txt_hosts.setEditable(false);
		txt_hosts.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		txt_hosts.setText("hosts文件内容");
		scrollPane.setViewportView(txt_hosts);
		
		button_clear = new JButton("清空项目");
		button_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				action_remove_all();
			}
		});
		button_clear.setBackground(new Color(240, 255, 240));
		button_clear.setToolTipText("清空所有host项目");
		button_clear.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_clear.setBounds(158, 489, 130, 27);
		frmHost.getContentPane().add(button_clear);
		
		button_add_ban = new JButton("添加屏蔽项目");
		button_add_ban.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action_add_ban();
			}
		});
		button_add_ban.setBackground(new Color(240, 255, 240));
		button_add_ban.setToolTipText("将一个网址映射到0.0.0.0");
		button_add_ban.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_add_ban.setBounds(556, 414, 130, 27);
		frmHost.getContentPane().add(button_add_ban);
		
		button_remove = new JButton("移除项目");
		button_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action_remove_item();
			}
		});
		button_remove.setBackground(new Color(240, 255, 240));
		button_remove.setToolTipText("移除一个网址的hosts映射");
		button_remove.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_remove.setBounds(556, 449, 130, 27);
		frmHost.getContentPane().add(button_remove);
		
		button_add_item = new JButton("添加映射项目");
		button_add_item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				action_add_item();
			}
		});
		button_add_item.setBackground(new Color(240, 255, 240));
		button_add_item.setToolTipText("添加一个host项目");
		button_add_item.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_add_item.setBounds(412, 414, 130, 27);
		frmHost.getContentPane().add(button_add_item);
		
		button_search_ip = new JButton("搜索IP");
		button_search_ip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action_search_ip();
			}
		});
		button_search_ip.setBackground(new Color(240, 255, 240));
		button_search_ip.setToolTipText("根据IP搜索host条目");
		button_search_ip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_search_ip.setBounds(298, 414, 100, 27);
		frmHost.getContentPane().add(button_search_ip);
		
		txt_ip = new JTextField();
		txt_ip.setToolTipText("IP操作栏");
		txt_ip.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		txt_ip.setBounds(83, 414, 200, 24);
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
		txt_url.setToolTipText("URL操作栏");
		label_url.setLabelFor(txt_url);
		txt_url.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		txt_url.setColumns(10);
		txt_url.setBounds(83, 451, 200, 24);
		frmHost.getContentPane().add(txt_url);
		
		button_search_url = new JButton("搜索URL");
		button_search_url.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action_search_url();
			}
		});
		button_search_url.setBackground(new Color(240, 255, 240));
		button_search_url.setToolTipText("根据URL搜索host条目");
		button_search_url.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_search_url.setBounds(298, 449, 100, 27);
		frmHost.getContentPane().add(button_search_url);
		
		txt_status = new JTextField();
		txt_status.setText("未指向条目");
		txt_status.setToolTipText("状态栏");
		txt_status.setEditable(false);
		txt_status.setBackground(Color.BLACK);
		txt_status.setForeground(Color.WHITE);
		txt_status.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		txt_status.setColumns(10);
		txt_status.setBounds(14, 375, 672, 24);
		frmHost.getContentPane().add(txt_status);
		
		button_reload = new JButton("重新加载");
		button_reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				action_reload();
			}
		});
		button_reload.setToolTipText("重新加载hosts文件内容");
		button_reload.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_reload.setBackground(new Color(240, 255, 240));
		button_reload.setBounds(14, 489, 130, 27);
		frmHost.getContentPane().add(button_reload);
		
		button_save_all = new JButton("保存改动");
		button_save_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action_save_changes();
			}
		});
		button_save_all.setBounds(302, 489, 130, 27);
		frmHost.getContentPane().add(button_save_all);
		button_save_all.setToolTipText("保存对hosts文件的改动");
		button_save_all.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_save_all.setBackground(new Color(240, 255, 240));
		
		button_auto_add = new JButton("自动映射");
		button_auto_add.setEnabled(false);
		button_auto_add.setToolTipText("自动将网址映射到合适IP (功能未添加)");
		button_auto_add.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
		button_auto_add.setBackground(new Color(240, 255, 240));
		button_auto_add.setBounds(412, 449, 130, 27);
		frmHost.getContentPane().add(button_auto_add);
		
		JLabel lblByFirok = new JLabel("v1.0.0   by S2Lab.Firok");
		lblByFirok.setToolTipText("单击跳转到GitHub页面");
		lblByFirok.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				show_on_github();
			}
		});
		lblByFirok.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		lblByFirok.setBounds(486, 493, 200, 18);
		frmHost.getContentPane().add(lblByFirok);
	}
	
	public void action_reload()
	{
		reloadHostItems(null);
		refresh();
		setStatus("已经重载hosts文件内容");
	}
	public void action_save_changes()
	{
		saveChanges();
	}
	public void action_search_ip()
	{
		if(istxtIP())
		{
			short[] ip=HostItem.getIP(gettxtIP());
			for(HostItem tempItem:host_items)
			{
				if(tempItem.equalsIP(ip)) // 找到了
				{
					setPinStatus(tempItem,PIN_IP);
					settxtURL(tempItem.getURL());
					setStatus("找到IP对应条目");
					return;
				}
			}
			
			setPinStatus();
			setStatus("没有找到对应条目");
			return;
		}
		else
		{
			setPinStatus();
			setStatus("IP格式有误");
			return;
		}
	}
	public void action_search_url()
	{
		if(istxtURL())
		{
			for(HostItem tempItem:host_items)
			{
				if(tempItem.equalsURL(gettxtURL())) // 找到对应条目
				{
					setStatus("找到URL对应项目");
					setPinStatus(tempItem,PIN_URL);
					settxtIP(tempItem.ip);
					return;
				}
			}
			
			setPinStatus();
			setStatus("没有找到对应条目");
			return;
		}
		else
		{
			setPinStatus();
			setStatus("URL格式有误");
			return;
		}
	}
	public void action_remove_all()
	{
		ListIterator<HostItem> iter=host_items.listIterator();
		while(iter.hasNext())
		{
			if(!iter.next().isAnnotation())
				iter.remove();
		}
		refresh();
		setStatus("已经清空所有非注释条目");
	}
	public void action_remove_item()
	{
		if(pin_type!=PIN_NONE)
		{
			// 删除条目
			host_items.remove(pin_host_item);
			refresh();
			setPinStatus();
			setStatus("已删除条目");
		}
		else
		{
			setStatus("未指向条目");
		}
	}
	public void action_add_item()
	{
		// 先搜索 如果能找到相同的 就不添加
		if(istxtIP() && istxtURL())
		{
			HostItem tempHI=new HostItem(gettxtIP()+' '+gettxtURL());
			for(HostItem tempItem:host_items)
			{
				if(tempItem.equalsIP(tempHI) && tempItem.equalsURL(tempHI))
				{
					refresh();
					setPinStatus(tempHI,PIN_ALL);
					setStatus("已存在相同host条目");
					return;
				}
				if(tempItem.equalsURL(tempHI))
				{
					tempItem.ip=tempHI.ip;
					refresh();
					setPinStatus(tempHI,PIN_ALL);
					setStatus("已修改host条目");
					return;
				}
			}
			host_items.add(tempHI);
		
			refresh();
			setPinStatus(tempHI,PIN_ALL);
			setStatus("已添加新host条目");
		}
		else
		{
			setStatus("格式有误");
		}
	}
	public void action_add_ban()
	{
		// 先搜索 如果能找到 优先修改内容
		if(istxtURL())
		{
			HostItem tempHI;
			if(pin_type!=PIN_NONE) // 已经指向一个 优先修改
			{
				tempHI=pin_host_item;
				tempHI.ip=new short[]{0,0,0,0};
				tempHI.url=gettxtURL();
			}
			else // 创建新的
			{
				tempHI=new HostItem("0.0.0.0 "+gettxtURL());
				host_items.add(tempHI);
			}
			refresh();
			setPinStatus(tempHI,PIN_ALL);
			setStatus("已屏蔽新host条目");
		}
		else
		{
			setStatus("格式有误");
		}
	}
	
	private void setPinStatus(HostItem hiIn,int typeIn)
	{
		pin_host_item=hiIn;
		pin_type=typeIn;
	}
	private void setPinStatus()
	{
		pin_host_item=null;
		pin_type=PIN_NONE;
	}
	
	public void show_on_github()
	{
		try {
            java.net.URI uri = java.net.URI.create("https://github.com/S2Lab/HostsEditor/");
            java.awt.Desktop dp = java.awt.Desktop.getDesktop();
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE))
            {
                dp.browse(uri);
            }
		}
		catch(Exception e)
		{
			setStatus("打开网址失败");
		}
	}
}
