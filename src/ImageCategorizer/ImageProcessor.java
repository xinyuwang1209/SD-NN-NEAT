package ImageCategorizer;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class ImageProcessor{

	BufferedImage img = null;
	
	public double getPixelValue(int x, int y){		//returns value of grayscaled pixel as value between 0 and 1
		return (img.getRGB(0, 0) & 0xFF)/255.0;
	}
	
    public void loadAndProcessImage(String filePath, int size){ 
    	try{
    		BufferedImage image = ImageIO.read(new File(filePath));
    		int a;
    		int b;
    		if(image.getHeight() > image.getWidth()){
    			a = image.getHeight();
    			b = image.getWidth();
    		}else{
    			a = image.getWidth();
    			b = image.getHeight();
    		}
    		
    		BufferedImage holder = new BufferedImage(a, a, BufferedImage.TYPE_INT_ARGB);		//add buffer to keep image ratios on resize
			for(int x=0; x<a; x++){
				for(int y=0; y<a; y++){
					int blank = a-b;
					if(a == image.getHeight()){
						if(x > blank/2 && x < b+(blank/2))
							holder.setRGB(x, y, image.getRGB(x-(blank/2), y));
						else
							holder.setRGB(x, y, 0);
					}else{
						if(y > blank/2 && y < b+(blank/2))
							holder.setRGB(x, y, image.getRGB(x, y-(blank/2)));
						else
							holder.setRGB(x, y, 0);
					}
    			}
			}
			image = holder;
    		

			ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(image, image);
		
            Image tmp = image.getScaledInstance(size, size, Image.SCALE_FAST);
            
            BufferedImage grayScaledResized = new BufferedImage(tmp.getWidth(null), tmp.getHeight(null), BufferedImage.TYPE_INT_RGB);
            grayScaledResized.getGraphics().drawImage(tmp, 0, 0, null);
            img = grayScaledResized;
        }catch(Exception e){
            System.out.println("FAILED TO LOAD IMAGE: " + filePath);
			return;
        }
    }
    
    public BufferedImage getImage(){
    	return img;
    }

    public void setImage(BufferedImage image){
    	img = image;
    }
    
    public void displayLoadedImage(){
    	try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ex){
        }
    	EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try{
                	frame.add(new TestPane(img));
                }catch(Exception e){
                }
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public class TestPane extends JPanel{
        
    	BufferedImage grayScale;
        public TestPane(BufferedImage gs){
            grayScale = gs;
        }

        @Override
        public Dimension getPreferredSize(){
            Dimension size = super.getPreferredSize();
            if (grayScale != null){
                size = new Dimension(grayScale.getWidth(), grayScale.getHeight());
            }
            return size;
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if (grayScale != null){
                int x = (getWidth() - (grayScale.getWidth()));
                int y = (getHeight() - grayScale.getHeight());

                g.drawImage(grayScale, 0, 0, this);
            }
        }
    }
}