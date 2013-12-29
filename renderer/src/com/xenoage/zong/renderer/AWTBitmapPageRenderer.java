package com.xenoage.zong.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.xenoage.utils.graphics.Units;
import com.xenoage.utils.math.geom.Size2f;
import com.xenoage.zong.desktop.renderer.canvas.AWTCanvas;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.renderer.canvas.CanvasDecoration;
import com.xenoage.zong.renderer.canvas.CanvasFormat;
import com.xenoage.zong.renderer.canvas.CanvasIntegrity;


/**
 * This class paints a page of a {@link Layout} into a {@link BufferedImage}
 * using the {@link AWTPageLayoutRenderer}.
 * 
 * @author Andreas Wenger
 */
public class AWTBitmapPageRenderer
{
	

	/**
	 * Returns a {@link BufferedImage} with the given page of the given {@link Layout}
	 * which is rendered at the given zoom level.
	 */
	public static BufferedImage paint(Layout layout, int pageIndex, float zoom)
	{
		Page page = layout.pages.get(pageIndex);
		Size2f pageSize = page.format.size;

		int width = Units.mmToPxInt(pageSize.width, zoom);
		int height = Units.mmToPxInt(pageSize.height, zoom);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		g2d.scale(zoom, zoom);
		AWTPageLayoutRenderer renderer = AWTPageLayoutRenderer.getInstance();
		renderer.paint(layout, pageIndex, new AWTCanvas(g2d, pageSize,
			CanvasFormat.Raster, CanvasDecoration.None, CanvasIntegrity.Perfect));
		g2d.dispose();

		return img;
	}

}
