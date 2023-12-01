package org.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFConverter{

    private static PDFConverter pdfConverter = null;

    private PDFConverter() {
    }

    public static PDFConverter getInstance() {
        if (pdfConverter == null)
            pdfConverter = new PDFConverter();
        return pdfConverter;
    }

    public void imagesToPdf(File[] images, File pdfFile) throws IOException {
        pdfFile.createNewFile();
        PDDocument pdfDocument = new PDDocument();
        int i = 0;
        for (File imgF : images) {
            addAImagePage(imgF, pdfDocument);

        }
        pdfDocument.save(pdfFile);
    }

    public void addAImagePage(File imgF, PDDocument pdfDocument) throws IOException {
        BufferedImage img = ImageIO.read(imgF);
        PDPage page = new PDPage(new PDRectangle(img.getWidth(), img.getHeight()));
        pdfDocument.addPage(page);

        PDImageXObject pdImage = PDImageXObject.createFromFile(imgF.getPath(), pdfDocument);
        PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);

        contentStream.drawImage(pdImage, 0, 0);
        contentStream.close();
    }

    public List<BufferedImage> pdfToImages(File pdfFile, float dpi) throws IOException {
        PDDocument pdfDocument = Loader.loadPDF(pdfFile);
        List<BufferedImage> images = new ArrayList<>();
        for (int i = 0; i < pdfDocument.getNumberOfPages(); i++) {
            images.add(renderAPage(pdfDocument, dpi, i));
        }
        return images;
    }

    public BufferedImage renderAPage(PDDocument pdfDocument, float dpi, int pageIndex) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi);
        return  bufferedImage;
    }

}
