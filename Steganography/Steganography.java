import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.awt.Color;

public class Steganography {

    private static void decode(String fileName, String outputFile) throws IOException{
        BufferedImage img = null;
        
        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException e) {
        
            System.out.println(e);
        }

        BufferedWriter output = new BufferedWriter(new FileWriter(new File(outputFile)));
        int height = img.getHeight();
        int width = img.getWidth();

        int numPixels = height * width;

        char[] bitArr = new char[numPixels];
        
        int bitArrIndex = 0;
        // loop thru pixels, adding every bit to bitArr
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                int color = img.getRGB(r, c);
                int red = (color >> 16)&0xFF;
                int green = (color >> 8)&0xFF;
                int blue = (color)&0xFF;
                if (bitArrIndex >= numPixels - 3)
                    break;

                if (red % 2 == 0)
                    bitArr[bitArrIndex] = '0';
                else
                    bitArr[bitArrIndex] = '1';

                bitArrIndex++;

                if (green % 2 == 0)
                    bitArr[bitArrIndex] = '0';
                else
                    bitArr[bitArrIndex] = '1';

                bitArrIndex++;

                if (blue % 2 == 0)
                    bitArr[bitArrIndex] = '0';
                else
                    bitArr[bitArrIndex] = '1';

                bitArrIndex++;
            }
        }
        
        char[] byteArr = new char[bitArr.length / 8];
        int bytI = 0;
        // TODO: figure out how to put the bytes into the byteArr, and then get the ascii chars from the bytes
        for (int i = 0; i < bitArr.length; i+=8) {
            String byt = "";
            for (int j = i; j < i + 8; j++) {
                if (bitArr[j] == '0' || bitArr[j] == '1')
                    byt += bitArr[j];
                
            }
          
            
            //check if the byte == 0 byte, then break.
            if (byt.equals("00000000")){
                break;
            }
            int val =  Integer.parseInt(byt, 2);
            
            byteArr[bytI] = (char) val;
            
            bytI++;
            
        }

        char[] finalOut = new char[bytI];
        for (int f = bytI - 1; f >= 0; f--){
            finalOut[f] = byteArr[f];
        }
        
       
        for (char c : finalOut) {
            output.write(c);
        }
        
        output.close();
    }


    private static boolean writeToPixel(int r, int c, int bitArrIndex, char[] bitArr, BufferedImage img, char[] inputBits) {
        
        int color = img.getRGB(r, c);
        Color col = new Color(color);
       
        int red = col.getRed();
        int green = col.getGreen();
        int blue = col.getBlue();
        
        for (int i = 0; i < 3; i++) {
            if (bitArrIndex < bitArr.length){
                char bit = bitArr[bitArrIndex];
                
                switch (i){
                    case 0 :
                    if (bit == '0') {
                        if (red % 2 != 0) {
                            red++;
                        }
                    } else {
                        if (red % 2 == 0){
                            red++;
                        }
                    }
                    break;
                    case 1 : 
                    if (bit == '0') {
                        if (green % 2 != 0) {
                            green++;
                        }
                    }else {
                        if (green % 2 == 0) {
                            green++;
                        }
                    }
                    break;
                    case 2:
                    if (bit == '0') {
                        if (blue % 2 != 0) {
                            blue++;
                        }
                    } else {
                        if (blue % 2 == 0) {
                            blue++;
                        }
                    }
                    break;

                }
            } else {
                return true;
            }
            bitArrIndex++;
        }

        if (bitArrIndex >= bitArr.length){
            return true;
        }

        if (red == 256) {
            red -= 2;
        }
        if (green == 256) {
            green -= 2;
        }
        if (blue == 256) {
            blue -=2;
        }
        
        red = (red<<16);
        green = (green<<8);
        int newColor = red | green | blue;
        Color newCol = new Color(newColor);
        
        img.setRGB(r, c, newColor);
        return false;
    }

    private static void encode(String fileName, String inputFile) throws IOException {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException e) {

        }
        int height = img.getHeight();
        int width = img.getWidth();

        int numPixels = height * width;
        int imgCap = numPixels - 3;

        // Leave space at the end for 0 byte, even it message can't fit into pixels
        String[] msgBytes = new String[numPixels - 1];

        FileInputStream fis = new FileInputStream(new File(inputFile));
        int i = 0;
        while (fis.available() > 0 && i < numPixels - 1) {
            String curByte = String.format("%08d", Integer.parseInt(Integer.toBinaryString(fis.read())));
            msgBytes[i] = curByte;
        
            i++;
        }
        char[] bitArr = new char[(i * 8) + 8];
        for (int last = bitArr.length - 1; last >= bitArr.length - 8; last--){
            bitArr[last] = '0';
        }

        int bitArrIndex = 0;
        for (String byteStr : msgBytes) {
            
            if (byteStr == null)
                break;
            for (int j = 0; j < 8; j++) {
                bitArr[bitArrIndex] = byteStr.charAt(j);
                bitArrIndex++;
            }
        }
       
        bitArrIndex = 0;
        int limit = Math.min(imgCap, bitArr.length);
        int r = 0;
        boolean messageComplete = false;
        int c = 0;
        char[] inputBits = new char[limit];
        for (r = 0 ; r < width; r++) {
            for (c = 0; c < height; c++) {
                messageComplete = writeToPixel(r, c, bitArrIndex, bitArr, img, inputBits);
                bitArrIndex += bitArrIndex + 3 < bitArr.length ? 3 : bitArr.length - bitArrIndex;
                if (messageComplete)
                    break;
            }
            if (messageComplete)
                break;
        }

       
        if (!messageComplete){
            // then we need to reset the r and c to 3 pixels back
            r--;
            c-=3;
            System.out.println("Message Size to large. Truncated Message");
        } 
        // write the next 3 pixel to 0
        for (int pixel = 0; pixel < 3; pixel++) {
            Color col = new Color(img.getRGB(r, c));
            int red = col.getRed();
            int green = col.getGreen();
            int blue = col.getBlue();
            int[] colArr =  {red, green, blue};
            for (int color = 0; color < 3; color ++){
                if (colArr[color] % 2 != 0)
                    colArr[color] ++;
                if (colArr[color] == 256)
                    colArr[color] -= 2;
            }

            int newColor = colArr[0] | colArr[1] | colArr[2];
            img.setRGB(r, c, newColor);
            c++;
        }
        
        String[] toks = fileName.split("[.]");
        ImageIO.write(img, toks[1], new File(toks[0] + "-steg." + toks[1]));
    }

    public static void main(String[] args) throws IOException {
       
        
        if (args[0].equals("-E")){
            encode(args[1], args[2]);
        } else if (args[0].equals("-D")){
            decode(args[1], args[2]);
        }
    }
}
