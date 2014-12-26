package mmb.posadmin.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import mmb.posadmin.domain.Product;
import mmboa.util.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

public class SaleTagUtil {
	
	private static Logger log = Logger.getLogger(SaleTagUtil.class);

	/**
	 * 创建商品销售标签图片，并返回图片相对路径
	 * @param product 商品对象
	 * @return
	 * @throws Exception
	 */
	public static String createSaleTagImg(Product product) throws Exception {
		String path = null;
		FileOutputStream fos = null;
		try {
			//条形码无效
			if(!BarcodeUtil.validateEAN13Barcode(product.getBarCode())) {
				throw new Exception("["+product.getBarCode()+"]商品条形码无效！");
			}
			
			//根据条形码获取条形码图片路径（未生成时返回null）
			String barcodeImgPath = BarcodeUtil.getBarcodeImgPath(product.getBarCode());
			
			//生成条形码图片
			if(StringUtils.isBlank(barcodeImgPath)) {
				barcodeImgPath = BarcodeUtil.createEAN13BarcodeImg(product.getBarCode());
			}
			
			//条形码图片
			BufferedImage barcodeImg = ImageIO.read(new File(ServletActionContext.getServletContext().getRealPath("/") + barcodeImgPath));
			Image zoomBarcode = barcodeImg.getScaledInstance(68, 35, Image.SCALE_SMOOTH);
			
			//LOGO
			BufferedImage logo = ImageIO.read(new File(ServletActionContext.getServletContext().getRealPath("/") + "/images/logo.png"));
			
			//销售标签图片
			BufferedImage mainImage = new BufferedImage(340, 150, BufferedImage.TYPE_INT_RGB);
			
			Graphics g = mainImage.getGraphics();
			g.fillRect(0, 0, mainImage.getWidth(), mainImage.getHeight());
			g.drawImage(logo, 0, 0, null);
			
			//橙色区域
			g.setColor(Color.ORANGE);
			g.drawLine(0, 0, mainImage.getWidth(), 0);
			g.fillRect(0, 85, mainImage.getWidth(), mainImage.getHeight()-85);
			
			//条码区域
			g.setColor(Color.WHITE);
			g.fillRect(4, 110, 140, 36);
			
			String fontFormat = "宋体";
			g.setColor(Color.BLACK);
			g.setFont(new Font(fontFormat, Font.PLAIN, 12));
			g.drawString("品名：" + product.getName(), 10, 102);
			g.drawString("单位：个", 180, 135);
			g.drawString("规格：一级", 260, 135);
			
			g.setFont(new Font(fontFormat, Font.PLAIN, 14));
			g.drawString("条码：", 18, 135);
			g.drawImage(zoomBarcode, 60, 110, null);
			
			g.setFont(new Font(fontFormat, Font.PLAIN, 16));
			g.drawString("零售价:", 160, 50);
			
			g.setFont(new Font(fontFormat, Font.BOLD, 25));
			String price = product.getSalePrice() + "";
			price = (price.endsWith(".0") ? price.substring(0, price.length()-2) : price);
			g.drawString(price, 220, 50);
			
			g.setFont(new Font(fontFormat, Font.PLAIN, 16));
			g.drawString("元", 215 + (16 * price.length()), 50);
			g.dispose();
			
			//商品销售标签图片存放路径
			String relativePath = Constants.config.getProperty("saleTagImgPath") + product.getBarCode() + ".jpg";
			
			//保存销售标签图片
			File saleTagImg = new File(ServletActionContext.getServletContext().getRealPath("/") + relativePath);
			if(!saleTagImg.getParentFile().exists()) {
				saleTagImg.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(saleTagImg);
			ImageIO.write(mainImage, "jpg", fos);
			fos.close();
			
			path = relativePath;
		} catch (Exception e) {
			log.error("创建商品销售标签图片时出现异常：", e);
			throw e;
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
		return path;
	}
	
}
