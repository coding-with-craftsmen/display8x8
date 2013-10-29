/**
 * Created with IntelliJ IDEA.
 * User: h_rietman
 * Date: 10/16/13
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */

package nl.codecentric.cc.display8x8;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayTest {


    private static final int I2C_ADDR = 0x70;
    //newer raspberry pi use bus nr 1
    //private static final int I2C_BUS_NR = 0;
    private static final int I2C_BUS_NR = 1;

    private static final byte TURN_ON_OSCILLATOR = (byte) 0x21;
    private static final byte ENABLE_DISPLAY_NO_BLINKING = (byte)0x81;
    private static final byte BRIGHTNESS_FULL = (byte) 0xef;
    public static final int delay = 50;

    private final byte[] rows = new byte[]{ (byte)0x00, (byte)0x02,(byte)0x04,
                                (byte)0x06,(byte)0x08,(byte)0x0a,(byte)0x0c,(byte)0x0e };


    private static List<Byte> displayBuffer = new ArrayList<Byte>();
    static {
        displayBuffer.add((byte) 0x00);
        displayBuffer.add((byte) 0x00);
        displayBuffer.add((byte) 0x00);
        displayBuffer.add((byte) 0x00);

        displayBuffer.add((byte) 0x00);
        displayBuffer.add((byte) 0x00);
        displayBuffer.add((byte) 0x00);
        displayBuffer.add((byte) 0x00);

    }

    private I2CDevice device;

    public DisplayTest() {
        initializeDevice();
    }

    public static void main(String[] args) {
        DisplayTest displayTest = new DisplayTest();
    }

    private I2CDevice initializeDevice() {
        device = null;
        try {
            I2CBus bus = I2CFactory.getInstance(I2C_BUS_NR);
            device = bus.getDevice(I2C_ADDR);

            device.write(TURN_ON_OSCILLATOR);
            device.write(ENABLE_DISPLAY_NO_BLINKING);
            device.write(BRIGHTNESS_FULL);

            // Smiley
            // 0011 1100 3c
            // 0100 0010 42
            // 1010 0101 a5
            // 1000 0001 81
            // 1010 0101 a5
            // 1001 1001 99
            // 0100 0010 42
            // 0011 1100 3c


//            byte[] smile = new byte[]{ (byte)0x3c,(byte)0x42,(byte)0xa5,(byte)0x81,(byte)0xa5,(byte)0x99,(byte)0x42,(byte)0x3c };

            clearDisplay();

//            drawCircle(3,3,2);
//            drawLine(0,0,7,7);
//            drawLine(7,0,0,7);
//            drawRect(0,0,8,8);
            writeChar('2');
            writeDisplay();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return device;
    }

    private void writeChar(char c) {

        // Work in progress..... bytes need to be mirrored
        byte[] no2 = new byte[]{ (byte)0x1c,(byte)0x22,(byte)0x02,(byte)0x04,(byte)0x08,(byte)0x10,(byte)0x3e,(byte)0x00 };

        for(int i=0;i<8;i++) {
            displayBuffer.set(i, no2[i]);
        }


    }

    public void drawCircle(int x0, int y0, int r) throws IOException {

        int f = 1 - r;
        int ddF_x = 1;
        int ddF_y = -2 * r;
        int x = 0;
        int y = r;

        drawPixel(x0  , y0+r, true);
        drawPixel(x0  , y0-r, true);
        drawPixel(x0+r, y0  , true);
        drawPixel(x0-r, y0  , true);

        while (x<y) {
            if (f >= 0) {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x;

            drawPixel(x0 + x, y0 + y, true);
            drawPixel(x0 - x, y0 + y, true);
            drawPixel(x0 + x, y0 - y, true);
            drawPixel(x0 - x, y0 - y, true);
            drawPixel(x0 + y, y0 + x, true);
            drawPixel(x0 - y, y0 + x, true);
            drawPixel(x0 + y, y0 - x, true);
            drawPixel(x0 - y, y0 - x, true);
        }
    }

    public void drawLine(int x0, int y0,
                                int x1, int y1
                                ) throws IOException {

        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

        if (steep) {

            x0 = x0 + y0; //now a is 30 and b is 20
            y0 = x0 - y0; //now a is 30 but b is 10 (original value of a)
            x0 = x0 - y0;
//            swap(x0, y0);

            x1 = x1 + y1; //now a is 30 and b is 20
            y1 = x1 - y1; //now a is 30 but b is 10 (original value of a)
            x1 = x1 - y1;
//            swap(x1, y1);
        }

        if (x0 > x1) {
            x0 = x0 + x1; //now a is 30 and b is 20
            x1 = x0 - x1; //now a is 30 but b is 10 (original value of a)
            x0 = x0 - x1;
//            swap(x0, x1);

            y0 = y0 + y1; //now a is 30 and b is 20
            y1 = y0 - y1; //now a is 30 but b is 10 (original value of a)
            y0 = y0 - y1;
//            swap(y0, y1);
        }

        int dx, dy;
        dx = x1 - x0;
        dy = Math.abs(y1 - y0);

        int err = dx / 2;
        int ystep;

        if (y0 < y1) {
            ystep = 1;
        } else {
            ystep = -1;
        }

        for (; x0<=x1; x0++) {
            if (steep) {
                drawPixel(y0, x0, true);
            } else {
                drawPixel(x0, y0, true);
            }
            err -= dy;
            if (err < 0) {
                y0 += ystep;
                err += dx;
            }
        }
    }

    public void drawRect(int x, int y,
                                int w, int h) throws IOException {
        drawFastHLine(x, y, w);
        drawFastHLine(x, y+h-1, w);
        drawFastVLine(x, y, h);
        drawFastVLine(x+w-1, y, h);
    }

    public void drawFastVLine(int x, int y,
                                     int h) throws IOException {
        // Update in subclasses if desired!
        drawLine(x, y, x, y+h-1);
    }

    public void drawFastHLine(int x, int y,
                                     int w) throws IOException {
        // Update in subclasses if desired!
        drawLine(x, y, x+w-1, y);
    }

    public void drawPixel(int x, int y, boolean on) throws IOException {
        //System.out.println("x: "+x+"y:"+y);
        byte value = displayBuffer.get(y);
        byte newValue;
        if (on) {
            newValue = (byte) (value | (1 << x));
        } else {
            newValue = (byte) (value ^ (1 << x));
        }
        displayBuffer.set(y, newValue);
    }

    public void writeDisplay() throws IOException {

        int i = 0;
        for (byte rowValue : displayBuffer) {
            byte line = (byte) ((0xfe & rowValue) >> 1 | (0x01 & rowValue) << 7);
            device.write((byte) i, line);
            i+=2;
        }

    }

    public void clearDisplay() throws IOException {

        for(int i=0;i<8;i++) {
            displayBuffer.set(i, (byte) 0x00);
        }

        writeDisplay();

    }

    public void drawSnake() throws IOException, InterruptedException {

        while(true) {
            for (int i = 0 ; i < 8 ; i++) {
                drawPixel(i, 0, true);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 0 ; i < 8 ; i++) {
                drawPixel(i, 0, false);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 0 ; i < 8 ; i++) {
                drawPixel(7, i, true);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 0 ; i < 8 ; i++) {
                drawPixel(7, i, false);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 7 ; i >= 0 ; i--) {
                drawPixel(i, 7, true);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 7 ; i >= 0 ; i--) {
                drawPixel(i, 7, false);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 7 ; i >= 0 ; i--) {
                drawPixel(0, i, true);

                writeDisplay();

                Thread.sleep(delay);
            }

            for (int i = 7 ; i >= 0 ; i--) {
                drawPixel(0, i, false);

                writeDisplay();

                Thread.sleep(delay);
            }



        }
    }
}
