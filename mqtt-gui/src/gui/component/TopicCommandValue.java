package gui.component;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.lang.System.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import gui.model.MQTTFrame;

public class TopicCommandValue {
	public JPanel overall = new JPanel();

	//Create row Labels
	JLabel lblRowOne = new JLabel("1", SwingConstants.CENTER);
	JLabel lblRowTwo = new JLabel("2", SwingConstants.CENTER);
	JLabel lblRowThree = new JLabel("3", SwingConstants.CENTER);
	JLabel lblRowFour = new JLabel("4", SwingConstants.CENTER);
	JLabel lblRowFive = new JLabel("5", SwingConstants.CENTER);

	//Create category (field) label
	JLabel lblRowCount = new JLabel();
	JLabel lblTopic = new JLabel("Topic", SwingConstants.CENTER);
	JLabel lblCommand = new JLabel("Command", SwingConstants.CENTER);
	JLabel lblValue = new JLabel("Value", SwingConstants.CENTER);
	JLabel lblsendbtn = new JLabel();

	//Add fields for each row
	JTextField topicRowOne = new JTextField();
	JTextField topicRowTwo = new JTextField();
	JTextField topicRowThree = new JTextField();
	JTextField topicRowFour = new JTextField();
	JTextField topicRowFive = new JTextField();

	JTextField commandRowOne = new JTextField();
	JTextField commandRowTwo = new JTextField();
	JTextField commandRowThree = new JTextField();
	JTextField commandRowFour = new JTextField();
	JTextField commandRowFive = new JTextField();

	JTextField valueRowOne = new JTextField();
	JTextField valueRowTwo = new JTextField();
	JTextField valueRowThree = new JTextField();
	JTextField valueRowFour = new JTextField();
	JTextField valueRowFive = new JTextField();

	JButton bnSendRowOne = new JButton("Send");
	JButton bnSendRowTwo = new JButton("Send");
	JButton bnSendRowThree = new JButton("Send");
	JButton bnSendRowFour = new JButton("Send");
	JButton bnSendRowFive = new JButton("Send");

	private MQTTFrame mqttMgr = null;

	public TopicCommandValue(MQTTFrame aMqttMgr) {
		System.out.println("An instance of TopicCommandValue is created");
		mqttMgr = aMqttMgr;
		//Layout
		overall.setLayout(new GridLayout(6,5));

		overall.add(lblRowCount);
		overall.add(lblTopic);
		overall.add(lblCommand);
		overall.add(lblValue);
		overall.add(lblsendbtn);

		//Row 1
		overall.add(lblRowOne);
		overall.add(topicRowOne);
		overall.add(commandRowOne);
		overall.add(valueRowOne);
		overall.add(bnSendRowOne);

		//Row 2
		overall.add(lblRowTwo);
		overall.add(topicRowTwo);
		overall.add(commandRowTwo);
		overall.add(valueRowTwo);
		overall.add(bnSendRowTwo);

		//Row 3
		overall.add(lblRowThree);
		overall.add(topicRowThree);
		overall.add(commandRowThree);
		overall.add(valueRowThree);
		overall.add(bnSendRowThree);

		//Row 4
		overall.add(lblRowFour);
		overall.add(topicRowFour);
		overall.add(commandRowFour);
		overall.add(valueRowFour);
		overall.add(bnSendRowFour);

		//Row 5
		overall.add(lblRowFive);
		overall.add(topicRowFive);
		overall.add(commandRowFive);
		overall.add(valueRowFive);
		overall.add(bnSendRowFive);


		//Button 1
		bnSendRowOne.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!topicRowOne.getText().trim().isEmpty() && !commandRowOne.getText().trim().isEmpty() && !valueRowOne.getText().trim().isEmpty()) {
					if(commandRowOne.getText().length() == 3) {
						String msg = new String();
						try {
							float valueOne = Float.parseFloat(valueRowOne.getText());
							//Input is either a float or an integer
							if(valueRowOne.getText().contains(".")) {
								// the number is a float
								String wholeNumberPart = valueRowOne.getText().split("\\.")[0];
								String fractionalPart = valueRowOne.getText().split("\\.")[1];
								if (!wholeNumberPart.contains("-")) {
									if(wholeNumberPart.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowOne.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowOne.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowOne.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowOne.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowOne.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowOne.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowOne.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowOne.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowOne.getText() + "D+" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowOne.getText() + "D+" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowOne.getText() + "D+" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowOne.getText() + "D+" + wholeNumberPart + fractionalPart;
										}
									}
								} else if (wholeNumberPart.contains("-")) {
									StringBuilder sb = new StringBuilder(wholeNumberPart);
									String wholeNumberString = sb.deleteCharAt(0).toString();
									if (wholeNumberString.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowOne.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowOne.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowOne.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowOne.getText() + "D-"+ "00" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowOne.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowOne.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowOne.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowOne.getText() + "D-"+ "0" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowOne.getText() + "D-" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowOne.getText() + "D-" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowOne.getText() + "D-" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowOne.getText() + "D-" + wholeNumberString + fractionalPart;
										}
									}
								}
								try {
									mqttMgr.publish(topicRowOne.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowOne, "Invalid Decimal", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}


							} else {
								//the number is an integer
								if(Integer.parseInt(valueRowOne.getText()) >= -9999999 && Integer.parseInt(valueRowOne.getText()) <= 9999999) {
									if(Integer.parseInt(valueRowOne.getText()) >= 0) {
										if(valueRowOne.getText().length() == 1) {
											msg = commandRowOne.getText() + "I+"+ "000000" + valueRowOne.getText();
										} else if (valueRowOne.getText().length() == 2) {
											msg = commandRowOne.getText() + "I+"+ "00000" + valueRowOne.getText();
										} else if (valueRowOne.getText().length() == 3) {
											msg = commandRowOne.getText() + "I+"+ "0000" + valueRowOne.getText();
										} else if (valueRowOne.getText().length() == 4) {
											msg = commandRowOne.getText() + "I+"+ "000" + valueRowOne.getText();
										} else if (valueRowOne.getText().length() == 5) {
											msg = commandRowOne.getText() + "I+"+ "00" + valueRowOne.getText();
										} else if (valueRowOne.getText().length() == 6) {
											msg = commandRowOne.getText() + "I+"+ "0" + valueRowOne.getText();
										} else {
											msg = commandRowOne.getText() + "I+"+ valueRowOne.getText();
										}
									} else if (Integer.parseInt(valueRowOne.getText()) < 0) {
										String posVal = String.valueOf(Integer.parseInt(valueRowOne.getText()) * -1);
										if(posVal.length() == 1) {
											msg = commandRowOne.getText() + "I-"+ "000000" + posVal;
										} else if (posVal.length() == 2) {
											msg = commandRowOne.getText() + "I-"+ "00000" + posVal;
										} else if (posVal.length() == 3) {
											msg = commandRowOne.getText() + "I-"+ "0000" + posVal;
										} else if (posVal.length() == 4) {
											msg = commandRowOne.getText() + "I-"+ "000" + posVal;
										} else if (posVal.length() == 5) {
											msg = commandRowOne.getText() + "I-"+ "00" + posVal;
										} else if (posVal.length() == 6) {
											msg = commandRowOne.getText() + "I-"+ "0" + posVal;
										} else {
											msg = commandRowOne.getText() + "I-" + posVal;
										}
									}
								}

								try {
									mqttMgr.publish(topicRowOne.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowOne, "Invalid Integer", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}
							}


						} catch (Exception error) {
							error.printStackTrace();
							JOptionPane.showMessageDialog (bnSendRowOne, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog (bnSendRowOne, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}

				//				if (!topicRowOne.getText().isBlank() && !commandRowOne.getText().isBlank() && !valueRowOne.getText().isBlank()) {
				//					if (commandRowOne.getText().length() == 3 && Integer.parseInt(valueRowOne.getText()) >= 0 && Integer.parseInt(valueRowOne.getText()) <= 1000) {
				//						try {
				//							if(valueRowOne.getText().length() == 3) {
				//								String msg = commandRowOne.getText() + "0" + valueRowOne.getText();
				//								mqttMgr.publish(topicRowOne.getText(), msg.getBytes(), 0, false);
				//							} else if(valueRowOne.getText().length() == 2) {
				//								String msg = commandRowOne.getText() + "00" + valueRowOne.getText();
				//								mqttMgr.publish(topicRowOne.getText(), msg.getBytes(), 0, false);
				//							} else if(valueRowOne.getText().length() == 1) {
				//								String msg = commandRowOne.getText() + "000" + valueRowOne.getText();
				//								mqttMgr.publish(topicRowOne.getText(), msg.getBytes(), 0, false);
				//							} else {
				//								String msg = commandRowOne.getText() + valueRowOne.getText();
				//								mqttMgr.publish(topicRowOne.getText(), msg.getBytes(), 0, false);
				//							}
				//						} catch (Exception e1) {
				//							e1.printStackTrace();
				//						}
				//					} else {
				//						JOptionPane.showMessageDialog (bnSendRowOne, "Invalid Input [Command: 3 letters; Value: 0 - 1000]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
				//					}
				//				}
			}
		});

		//Button 2
		bnSendRowTwo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!topicRowTwo.getText().trim().isEmpty() && !commandRowTwo.getText().trim().isEmpty() && !valueRowTwo.getText().trim().isEmpty()) {
					if(commandRowTwo.getText().length() == 3) {
						String msg = new String();
						try {
							float valueTwo = Float.parseFloat(valueRowTwo.getText());
							//Input is either a float or an integer
							if(valueRowTwo.getText().contains(".")) {
								// the number is a float
								String wholeNumberPart = valueRowTwo.getText().split("\\.")[0];
								String fractionalPart = valueRowTwo.getText().split("\\.")[1];
								if (!wholeNumberPart.contains("-")) {
									if(wholeNumberPart.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowTwo.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowTwo.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowTwo.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowTwo.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowTwo.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowTwo.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowTwo.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowTwo.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowTwo.getText() + "D+" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowTwo.getText() + "D+" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowTwo.getText() + "D+" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowTwo.getText() + "D+" + wholeNumberPart + fractionalPart;
										}
									}
								} else if (wholeNumberPart.contains("-")) {
									StringBuilder sb = new StringBuilder(wholeNumberPart);
									String wholeNumberString = sb.deleteCharAt(0).toString();
									if (wholeNumberString.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowTwo.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowTwo.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowTwo.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowTwo.getText() + "D-"+ "00" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowTwo.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowTwo.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowTwo.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowTwo.getText() + "D-"+ "0" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowTwo.getText() + "D-" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowTwo.getText() + "D-" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowTwo.getText() + "D-" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowTwo.getText() + "D-" + wholeNumberString + fractionalPart;
										}
									}
								}
								try {
									mqttMgr.publish(topicRowTwo.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowTwo, "Invalid Decimal", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}


							} else {
								//the number is an integer
								if(Integer.parseInt(valueRowTwo.getText()) >= -9999999 && Integer.parseInt(valueRowTwo.getText()) <= 9999999) {
									if(Integer.parseInt(valueRowTwo.getText()) >= 0) {
										if(valueRowTwo.getText().length() == 1) {
											msg = commandRowTwo.getText() + "I+"+ "000000" + valueRowTwo.getText();
										} else if (valueRowTwo.getText().length() == 2) {
											msg = commandRowTwo.getText() + "I+"+ "00000" + valueRowTwo.getText();
										} else if (valueRowTwo.getText().length() == 3) {
											msg = commandRowTwo.getText() + "I+"+ "0000" + valueRowTwo.getText();
										} else if (valueRowTwo.getText().length() == 4) {
											msg = commandRowTwo.getText() + "I+"+ "000" + valueRowTwo.getText();
										} else if (valueRowTwo.getText().length() == 5) {
											msg = commandRowTwo.getText() + "I+"+ "00" + valueRowTwo.getText();
										} else if (valueRowTwo.getText().length() == 6) {
											msg = commandRowTwo.getText() + "I+"+ "0" + valueRowTwo.getText();
										} else {
											msg = commandRowTwo.getText() + "I+"+ valueRowTwo.getText();
										}
									} else if (Integer.parseInt(valueRowTwo.getText()) < 0) {
										String posVal = String.valueOf(Integer.parseInt(valueRowTwo.getText()) * -1);
										if(posVal.length() == 1) {
											msg = commandRowTwo.getText() + "I-"+ "000000" + posVal;
										} else if (posVal.length() == 2) {
											msg = commandRowTwo.getText() + "I-"+ "00000" + posVal;
										} else if (posVal.length() == 3) {
											msg = commandRowTwo.getText() + "I-"+ "0000" + posVal;
										} else if (posVal.length() == 4) {
											msg = commandRowTwo.getText() + "I-"+ "000" + posVal;
										} else if (posVal.length() == 5) {
											msg = commandRowTwo.getText() + "I-"+ "00" + posVal;
										} else if (posVal.length() == 6) {
											msg = commandRowTwo.getText() + "I-"+ "0" + posVal;
										} else {
											msg = commandRowTwo.getText() + "I-" + posVal;
										}
									}
								}

								try {
									mqttMgr.publish(topicRowTwo.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowTwo, "Invalid Integer", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}
							}
						} catch (Exception error) {
							error.printStackTrace();
							JOptionPane.showMessageDialog (bnSendRowTwo, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog (bnSendRowTwo, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		//Button 3
		bnSendRowThree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!topicRowThree.getText().trim().isEmpty() && !commandRowThree.getText().trim().isEmpty() && !valueRowThree.getText().trim().isEmpty()) {
					if(commandRowThree.getText().length() == 3) {
						String msg = new String();
						try {
							float valueThree = Float.parseFloat(valueRowThree.getText());
							//Input is either a float or an integer
							if(valueRowThree.getText().contains(".")) {
								// the number is a float
								String wholeNumberPart = valueRowThree.getText().split("\\.")[0];
								String fractionalPart = valueRowThree.getText().split("\\.")[1];
								if (!wholeNumberPart.contains("-")) {
									if(wholeNumberPart.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowThree.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowThree.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowThree.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowThree.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowThree.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowThree.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowThree.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowThree.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowThree.getText() + "D+" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowThree.getText() + "D+" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowThree.getText() + "D+" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowThree.getText() + "D+" + wholeNumberPart + fractionalPart;
										}
									}
								} else if (wholeNumberPart.contains("-")) {
									StringBuilder sb = new StringBuilder(wholeNumberPart);
									String wholeNumberString = sb.deleteCharAt(0).toString();
									if (wholeNumberString.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowThree.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowThree.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowThree.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowThree.getText() + "D-"+ "00" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowThree.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowThree.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowThree.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowThree.getText() + "D-"+ "0" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowThree.getText() + "D-" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowThree.getText() + "D-" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowThree.getText() + "D-" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowThree.getText() + "D-" + wholeNumberString + fractionalPart;
										}
									}
								}
								try {
									mqttMgr.publish(topicRowThree.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowThree, "Invalid Decimal", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}


							} else {
								//the number is an integer
								if(Integer.parseInt(valueRowThree.getText()) >= -9999999 && Integer.parseInt(valueRowThree.getText()) <= 9999999) {
									if(Integer.parseInt(valueRowThree.getText()) >= 0) {
										if(valueRowThree.getText().length() == 1) {
											msg = commandRowThree.getText() + "I+"+ "000000" + valueRowThree.getText();
										} else if (valueRowThree.getText().length() == 2) {
											msg = commandRowThree.getText() + "I+"+ "00000" + valueRowThree.getText();
										} else if (valueRowThree.getText().length() == 3) {
											msg = commandRowThree.getText() + "I+"+ "0000" + valueRowThree.getText();
										} else if (valueRowThree.getText().length() == 4) {
											msg = commandRowThree.getText() + "I+"+ "000" + valueRowThree.getText();
										} else if (valueRowThree.getText().length() == 5) {
											msg = commandRowThree.getText() + "I+"+ "00" + valueRowThree.getText();
										} else if (valueRowThree.getText().length() == 6) {
											msg = commandRowThree.getText() + "I+"+ "0" + valueRowThree.getText();
										} else {
											msg = commandRowThree.getText() + "I+"+ valueRowThree.getText();
										}
									} else if (Integer.parseInt(valueRowThree.getText()) < 0) {
										String posVal = String.valueOf(Integer.parseInt(valueRowThree.getText()) * -1);
										if(posVal.length() == 1) {
											msg = commandRowThree.getText() + "I-"+ "000000" + posVal;
										} else if (posVal.length() == 2) {
											msg = commandRowThree.getText() + "I-"+ "00000" + posVal;
										} else if (posVal.length() == 3) {
											msg = commandRowThree.getText() + "I-"+ "0000" + posVal;
										} else if (posVal.length() == 4) {
											msg = commandRowThree.getText() + "I-"+ "000" + posVal;
										} else if (posVal.length() == 5) {
											msg = commandRowThree.getText() + "I-"+ "00" + posVal;
										} else if (posVal.length() == 6) {
											msg = commandRowThree.getText() + "I-"+ "0" + posVal;
										} else {
											msg = commandRowThree.getText() + "I-" + posVal;
										}
									}
								}

								try {
									mqttMgr.publish(topicRowThree.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowThree, "Invalid Integer", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}
							}


						} catch (Exception error) {
							error.printStackTrace();
							JOptionPane.showMessageDialog (bnSendRowThree, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog (bnSendRowThree, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		//Button 4
		bnSendRowFour.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!topicRowFour.getText().trim().isEmpty() && !commandRowFour.getText().trim().isEmpty() && !valueRowFour.getText().trim().isEmpty()) {
					if(commandRowFour.getText().length() == 3) {
						String msg = new String();
						try {
							float valueFour = Float.parseFloat(valueRowFour.getText());
							//Input is either a float or an integer
							if(valueRowFour.getText().contains(".")) {
								// the number is a float
								String wholeNumberPart = valueRowFour.getText().split("\\.")[0];
								String fractionalPart = valueRowFour.getText().split("\\.")[1];
								if (!wholeNumberPart.contains("-")) {
									if(wholeNumberPart.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFour.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFour.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFour.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFour.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFour.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFour.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFour.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFour.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFour.getText() + "D+" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFour.getText() + "D+" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFour.getText() + "D+" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFour.getText() + "D+" + wholeNumberPart + fractionalPart;
										}
									}
								} else if (wholeNumberPart.contains("-")) {
									StringBuilder sb = new StringBuilder(wholeNumberPart);
									String wholeNumberString = sb.deleteCharAt(0).toString();
									if (wholeNumberString.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFour.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFour.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFour.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFour.getText() + "D-"+ "00" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFour.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFour.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFour.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFour.getText() + "D-"+ "0" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFour.getText() + "D-" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFour.getText() + "D-" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFour.getText() + "D-" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFour.getText() + "D-" + wholeNumberString + fractionalPart;
										}
									}
								}
								try {
									mqttMgr.publish(topicRowFour.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowFour, "Invalid Decimal", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}


							} else {
								//the number is an integer
								if(Integer.parseInt(valueRowFour.getText()) >= -9999999 && Integer.parseInt(valueRowFour.getText()) <= 9999999) {
									if(Integer.parseInt(valueRowFour.getText()) >= 0) {
										if(valueRowFour.getText().length() == 1) {
											msg = commandRowFour.getText() + "I+"+ "000000" + valueRowFour.getText();
										} else if (valueRowFour.getText().length() == 2) {
											msg = commandRowFour.getText() + "I+"+ "00000" + valueRowFour.getText();
										} else if (valueRowFour.getText().length() == 3) {
											msg = commandRowFour.getText() + "I+"+ "0000" + valueRowFour.getText();
										} else if (valueRowFour.getText().length() == 4) {
											msg = commandRowFour.getText() + "I+"+ "000" + valueRowFour.getText();
										} else if (valueRowFour.getText().length() == 5) {
											msg = commandRowFour.getText() + "I+"+ "00" + valueRowFour.getText();
										} else if (valueRowFour.getText().length() == 6) {
											msg = commandRowFour.getText() + "I+"+ "0" + valueRowFour.getText();
										} else {
											msg = commandRowFour.getText() + "I+"+ valueRowFour.getText();
										}
									} else if (Integer.parseInt(valueRowFour.getText()) < 0) {
										String posVal = String.valueOf(Integer.parseInt(valueRowFour.getText()) * -1);
										if(posVal.length() == 1) {
											msg = commandRowFour.getText() + "I-"+ "000000" + posVal;
										} else if (posVal.length() == 2) {
											msg = commandRowFour.getText() + "I-"+ "00000" + posVal;
										} else if (posVal.length() == 3) {
											msg = commandRowFour.getText() + "I-"+ "0000" + posVal;
										} else if (posVal.length() == 4) {
											msg = commandRowFour.getText() + "I-"+ "000" + posVal;
										} else if (posVal.length() == 5) {
											msg = commandRowFour.getText() + "I-"+ "00" + posVal;
										} else if (posVal.length() == 6) {
											msg = commandRowFour.getText() + "I-"+ "0" + posVal;
										} else {
											msg = commandRowFour.getText() + "I-" + posVal;
										}
									}
								}

								try {
									mqttMgr.publish(topicRowFour.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowFour, "Invalid Integer", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}
							}


						} catch (Exception error) {
							error.printStackTrace();
							JOptionPane.showMessageDialog (bnSendRowFour, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog (bnSendRowFour, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		//Button 5
		bnSendRowFive.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!topicRowFive.getText().trim().isEmpty() && !commandRowFive.getText().trim().isEmpty() && !valueRowFive.getText().trim().isEmpty()) {
					if(commandRowFive.getText().length() == 3) {
						String msg = new String();
						try {
							float valueFive = Float.parseFloat(valueRowFive.getText());
							//Input is either a float or an integer
							if(valueRowFive.getText().contains(".")) {
								// the number is a float
								String wholeNumberPart = valueRowFive.getText().split("\\.")[0];
								String fractionalPart = valueRowFive.getText().split("\\.")[1];
								if (!wholeNumberPart.contains("-")) {
									if(wholeNumberPart.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFive.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFive.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFive.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFive.getText() + "D+"+ "00" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFive.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFive.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFive.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFive.getText() + "D+"+ "0" + wholeNumberPart + fractionalPart;
										}
									} else if(wholeNumberPart.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFive.getText() + "D+" + wholeNumberPart + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFive.getText() + "D+" + wholeNumberPart + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFive.getText() + "D+" + wholeNumberPart + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFive.getText() + "D+" + wholeNumberPart + fractionalPart;
										}
									}
								} else if (wholeNumberPart.contains("-")) {
									StringBuilder sb = new StringBuilder(wholeNumberPart);
									String wholeNumberString = sb.deleteCharAt(0).toString();
									if (wholeNumberString.length() == 1) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFive.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFive.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFive.getText() + "D-"+ "00" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFive.getText() + "D-"+ "00" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 2) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFive.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFive.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFive.getText() + "D-"+ "0" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFive.getText() + "D-"+ "0" + wholeNumberString + fractionalPart;
										}
									} else if (wholeNumberString.length() == 3) {
										if(fractionalPart.length() == 1) {
											msg = commandRowFive.getText() + "D-" + wholeNumberString + fractionalPart + "000";
										} else if (fractionalPart.length() == 2) {
											msg = commandRowFive.getText() + "D-" + wholeNumberString + fractionalPart + "00";
										} else if (fractionalPart.length() == 3) {
											msg = commandRowFive.getText() + "D-" + wholeNumberString + fractionalPart + "0";
										} else if (fractionalPart.length() == 4) {
											msg = commandRowFive.getText() + "D-" + wholeNumberString + fractionalPart;
										}
									}
								}
								try {
									mqttMgr.publish(topicRowFive.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowFive, "Invalid Decimal", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}


							} else {
								//the number is an integer
								if(Integer.parseInt(valueRowFive.getText()) >= -9999999 && Integer.parseInt(valueRowFive.getText()) <= 9999999) {
									if(Integer.parseInt(valueRowFive.getText()) >= 0) {
										if(valueRowFive.getText().length() == 1) {
											msg = commandRowFive.getText() + "I+"+ "000000" + valueRowFive.getText();
										} else if (valueRowFive.getText().length() == 2) {
											msg = commandRowFive.getText() + "I+"+ "00000" + valueRowFive.getText();
										} else if (valueRowFive.getText().length() == 3) {
											msg = commandRowFive.getText() + "I+"+ "0000" + valueRowFive.getText();
										} else if (valueRowFive.getText().length() == 4) {
											msg = commandRowFive.getText() + "I+"+ "000" + valueRowFive.getText();
										} else if (valueRowFive.getText().length() == 5) {
											msg = commandRowFive.getText() + "I+"+ "00" + valueRowFive.getText();
										} else if (valueRowFive.getText().length() == 6) {
											msg = commandRowFive.getText() + "I+"+ "0" + valueRowFive.getText();
										} else {
											msg = commandRowFive.getText() + "I+"+ valueRowFive.getText();
										}
									} else if (Integer.parseInt(valueRowFive.getText()) < 0) {
										String posVal = String.valueOf(Integer.parseInt(valueRowFive.getText()) * -1);
										if(posVal.length() == 1) {
											msg = commandRowFive.getText() + "I-"+ "000000" + posVal;
										} else if (posVal.length() == 2) {
											msg = commandRowFive.getText() + "I-"+ "00000" + posVal;
										} else if (posVal.length() == 3) {
											msg = commandRowFive.getText() + "I-"+ "0000" + posVal;
										} else if (posVal.length() == 4) {
											msg = commandRowFive.getText() + "I-"+ "000" + posVal;
										} else if (posVal.length() == 5) {
											msg = commandRowFive.getText() + "I-"+ "00" + posVal;
										} else if (posVal.length() == 6) {
											msg = commandRowFive.getText() + "I-"+ "0" + posVal;
										} else {
											msg = commandRowFive.getText() + "I-" + posVal;
										}
									}
								}

								try {
									mqttMgr.publish(topicRowFive.getText(), msg.getBytes(), 0, false);
								} catch (Exception err) {
									err.printStackTrace();
									JOptionPane.showMessageDialog (bnSendRowFive, "Invalid Integer", "Invalid Input", JOptionPane.ERROR_MESSAGE);
								}
							}


						} catch (Exception error) {
							error.printStackTrace();
							JOptionPane.showMessageDialog (bnSendRowFive, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog (bnSendRowFive, "Invalid Input [Command: 3 letters; Value: Integer from -9999999 to 9999999 or Decimal from -999.9999 to 999.9999 ]", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
}