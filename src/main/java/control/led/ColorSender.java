package control.led;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.io.IOException;
import java.util.Stack;

public class ColorSender {

    private SerialPort serialPort;
    private Stack<String> commands;

    private boolean open;

    public ColorSender() {
        this.commands = new Stack<>();

        this.open = false;
    }

    public String[] getAvailablePortList() {
        String[] availablePortList = new String[SerialPort.getCommPorts().length];
        for(int i = 0; i < availablePortList.length; i++) {
            availablePortList[i] = SerialPort.getCommPorts()[i].getPortDescription();
        }
        return availablePortList;
    }

    public void openPort(int portIndex) {
        this.serialPort = SerialPort.getCommPorts()[portIndex];
        this.serialPort.setComPortParameters(500000, 8, 1, 0);
        this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        this.open = this.serialPort.openPort();
        if(this.open) {
            System.out.println("Port " + this.serialPort.getDescriptivePortName() + " is open");
        } else {
            System.out.println("Failed to open port " + this.serialPort.getDescriptivePortName());
        }
    }

    public boolean isPortAvailable() {
        return this.serialPort != null && this.open;
    }

    public void addCommand(int ledNumber, Color color) {

        String ledString = String.format("%02X", ledNumber);
        String redString = String.format("%02X", color.getRed());
        String greenString = String.format("%02X", color.getGreen());
        String blueString = String.format("%02X", color.getBlue());
        String command = ledString + redString + greenString + blueString;

        this.addCommand(command);
    }

    public void addCommand(String command) {
        this.commands.push(command);
    }

    public void flushCommands() {
        int commandSize = this.commands.size();
        String sendString = "";
        for(int i = 0; i < commandSize; i++) {
            String currentCommand = this.commands.pop();
            sendString = sendString + currentCommand;
        }
        sendString = sendString + ";";
        //System.out.println(sendString);

        try {
            this.serialPort.getOutputStream().write(sendString.getBytes());
            this.serialPort.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePort() {
        this.open = !this.serialPort.closePort();
        if (!this.open) {
            System.out.println("Port is closed");
        } else {
            System.out.println("Failed to close port");
        }
    }
}
