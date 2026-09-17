package gui.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import gui.model.MQTTFrame;

public class XYSeriesGraph {
	public JPanel graphPanel;
	public XYSeries goals;
	public XYSeries goals1;
	public XYSeries goals2;
	public XYSeries goals3;

	public ChartPanel cp;

	public JPanel options = new JPanel();
	public JPanel optionsLeft = new JPanel();
	public JPanel optionsRight = new JPanel();

	public JPanel lowerValueLimit = new JPanel();
	public JPanel upperValueLimit = new JPanel();
	public JPanel recordingLengthInSecs = new JPanel();
	public JPanel filename = new JPanel();
	public JPanel startClearButtons = new JPanel();
	public JPanel topicOne = new JPanel();
	public JPanel topicOneLineTwo = new JPanel();
	public JPanel topicOneLineThree = new JPanel();
	public JPanel topicTwoLineThree = new JPanel();
	public JPanel topicTwo = new JPanel();
	public JPanel topicTwoLineTwo = new JPanel();
	public JPanel pnInterval = new JPanel();

	JTextField txtLowerValueLimit = new JTextField(7);
	JTextField txtUpperValueLimit = new JTextField(7);
	JTextField txtRecordingLength = new JTextField("100", 7);
	public JTextField txtFilename = new JTextField(7);
	public JTextField txtTopicOne = new JTextField(7);
	public JTextField txtTopicOne_Val1 = new JTextField("Topic 1.1", 7);
	public JTextField txtTopicOne_Val2 = new JTextField("Topic 1.2", 7);
	public JTextField txtTopicTwo_Val1 = new JTextField("Topic 2.1", 7);
	public JTextField txtTopicTwo_Val2 = new JTextField("Topic 2.2", 7);
	public JTextField txtTopicTwo = new JTextField(7);
	public JTextField interval = new JTextField("0.0",7);

	JButton setLowerValueLimit = new JButton("Set");
	JButton setUpperValueLimit = new JButton("Set");
	JButton setRecordingLength = new JButton("Set");
	JButton setFilename = new JButton("Set");
	JButton setTopicOne = new JButton("Set");
	JButton setTopicTwo = new JButton("Set");
	JButton setTopicThree = new JButton("Set");
	JButton setTopicFour = new JButton("Set");
	Icon icon = new ImageIcon("image/unsubscribe.png");
	JButton unsubTopicOne = new JButton(icon);
	JButton unsubTopicTwo = new JButton(icon);
	JButton unsubTopicThree = new JButton(icon);
	JButton unsubTopicFour = new JButton(icon);

	public JButton btnStart = new JButton("Start");
	JButton btnClear = new JButton("Clear");

	public long tref;
	public long trefPrivate;
	public double doubleRecordingLength = 10;

	public String goalName = "Topic 1";
	public String goalOneName = " Topic 2";
	public String goalTwoName = " Topic 3";
	public String goalThreeName = " Topic 4";

	public JCheckBox cbTopicOne_1 = new JCheckBox("Display", true);
	public JCheckBox cbTopicOne_2 = new JCheckBox("Display", true);
	public JTextField txt_scale_one_lineTwo = new JTextField("1", 7);
	public JTextField txt_scale_one_lineThree = new JTextField("1", 7);
	
	public JCheckBox cbTopicTwo_1 = new JCheckBox("Display", true);
	public JCheckBox cbTopicTwo_2 = new JCheckBox("Display", true);
	public JTextField txt_scale_two_lineTwo = new JTextField("1", 7);
	public JTextField txt_scale_two_lineThree = new JTextField("1", 7);

	
	JLabel lbl_scale_topicOne_1 = new JLabel("Scale");
	JLabel lbl_scale_topicOne_2 = new JLabel("Scale");
	
	JLabel lbl_scale_topicTwo_1 = new JLabel("Scale");
	JLabel lbl_scale_topicTwo_2 = new JLabel("Scale");
	
	public JCheckBox readyToSaveFile = new JCheckBox("Save",false);
	JPanel displays_topicOne = new JPanel();
	JPanel displays_topicTwo = new JPanel();

	public JTabbedPane tabbedPane;
	
	public NumberAxis range;
	public NumberAxis domain;
	public XYSeriesCollection xyDataset;

	public MQTTFrame mqttMgr = null;
	private File fileChooserCurrentDir = null;
	public byte[] fileContent = null;
	
	public MQTTFrame getMQTTframe() {
		return mqttMgr;
	}

	
	public XYSeriesGraph(String title, String xAxisName, String yAxisName, MQTTFrame aMqttMgr) {
		//Graph
		tabbedPane = new JTabbedPane();
		System.out.println("An instance are created by this XYSeriesGraph constructor");
		mqttMgr = aMqttMgr;
		
		goals = new XYSeries(goalName);
		goals1 = new XYSeries(goalOneName);
		goals2 = new XYSeries(goalTwoName);
		goals3 = new XYSeries(goalThreeName);
		System.out.println("goals = new XYSeries() - low frequency");

		xyDataset = new XYSeriesCollection(goals);
		xyDataset.addSeries(goals1);
		xyDataset.addSeries(goals2);
		xyDataset.addSeries(goals3);

		JFreeChart chart = ChartFactory.createXYLineChart(
				title, xAxisName, yAxisName,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);

		//	    CategoryPlot catPlot = (CategoryPlot) chart.getPlot();
		//	    catPlot.getRenderer().setSeriesStroke(1, new BasicStroke(3.0f));

		XYPlot plot = chart.getXYPlot();
		range = (NumberAxis)plot.getRangeAxis();
		domain = (NumberAxis)plot.getDomainAxis();
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		new Thread(() -> renderer.setSeriesStroke(0, new BasicStroke(2.0f))).start();
		new Thread(() -> renderer.setSeriesStroke(1, new BasicStroke(2.0f))).start();
		new Thread(() -> renderer.setSeriesStroke(2, new BasicStroke(2.0f))).start();
		new Thread(() -> renderer.setSeriesStroke(3, new BasicStroke(2.0f))).start();
		renderer.setSeriesShapesVisible(0,false);
		renderer.setSeriesShapesVisible(1,false);
		renderer.setSeriesShapesVisible(2,false);
		renderer.setSeriesShapesVisible(3,false);

		plot.setRenderer(renderer);

		cp = new ChartPanel(chart) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(320, 240);
			}
		};
		
		graphPanel = new JPanel();
		graphPanel.setLayout(new BorderLayout());
		graphPanel.add(cp, BorderLayout.CENTER);
		cp.setMouseWheelEnabled(true);
		
		

		//Add graphs to the tab panel
		LowFreqOptions lowFrqOptPanel = new LowFreqOptions(this);
		tabbedPane.addTab("Graph", graphPanel);
		tabbedPane.addTab("Options", lowFrqOptPanel.options);
	}	
}