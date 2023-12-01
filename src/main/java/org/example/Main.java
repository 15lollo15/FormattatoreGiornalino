package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String API_KEY = "raccalorenzo11@gmail.com_71f9de3f01b8415d0b41b776ca6729b32311cfafa80bcc6f39594fe55fe5b0ee9e8d98dd";
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Parametri mancanti");
            return;
        }

        String inputFileName = args[0];
        String outputDirName = args[1];

        File inputFile = new File(inputFileName);
        File outputDirFile = new File(outputDirName);
        Files.createDirectories(Paths.get(outputDirFile.toURI()));



        String pdfPath = inputFileName;
        if (!inputFile.getName().split("\\.")[1].equals("pdf")) {
            System.out.println("Non ancora supportata :(");
        }


        PDFConverter pdfConverter = PDFConverter.getInstance();

        List<BufferedImage> images = pdfConverter.pdfToImages(new File(pdfPath), 1000);

        List<BufferedImage> splittedImages = new ArrayList<>();
        for (BufferedImage img : images) {
            BufferedImage[] splits = splitInTwo(img);
            splittedImages.add(splits[0]);
            splittedImages.add(splits[1]);
        }

        BufferedImage lastPage = splittedImages.remove(0);
        splittedImages.add(lastPage);

        for (int i = 0; i < splittedImages.size(); i++) {
            BufferedImage img = splittedImages.get(i);
            ImageIO.write(img, "png", new File(outputDirFile.getPath() + "/" + i + ".png"));
        }

    }


    public static BufferedImage[] splitInTwo(BufferedImage img) {
        int width = img.getWidth() / 2;
        BufferedImage left = img.getSubimage(0, 0, width, img.getHeight());
        BufferedImage right = img.getSubimage(width, 0, img.getWidth() - width, img.getHeight());

        return new BufferedImage[] {left, right};
    }

}