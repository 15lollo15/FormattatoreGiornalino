package org.example;

import com.fasterxml.jackson.annotation.F;
import com.formdev.flatlaf.FlatLightLaf;
import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.options.convert.PdfConvertOptions;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.example.gui.Controller;
import org.example.gui.MainFrame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final int MAX_PAGE = 3;
    public static void main(String[] args) throws IOException {
        FlatLightLaf.setup();
        Controller c = Controller.getInstance();
        c.showWindow();
//        File inputFile = new File("my_pdf.pdf");
//        DocConverter dc = DocConverter.getInstance();
//        dc.convert(new File("sample.doc"), new File("my_pdf.pdf"));
//
//
//        String outputDirName = "pages";
//        File outputDirFile = new File(outputDirName);
//        Files.createDirectories(Paths.get(outputDirFile.toURI()));
//
//
//        PDFConverter pdfConverter = PDFConverter.getInstance();
//        List<BufferedImage> images = pdfConverter.pdfToImages(inputFile, 1000);
//
//
//
//        SplitImagesGenerator sig = SplitImagesGenerator.getInstance();
//        List<BufferedImage> splittedImages = sig.generateSplitImages(images);
//
//        for (int i = 0; i < splittedImages.size(); i++) {
//            BufferedImage img = splittedImages.get(i);
//            ImageIO.write(img, "png", new File(outputDirFile.getPath() + "/" + i + ".png"));
//        }

    }

}