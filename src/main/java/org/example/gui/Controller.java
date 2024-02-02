package org.example.gui;

import com.fasterxml.jackson.annotation.F;
import org.example.DocConverter;
import org.example.PDFConverter;
import org.example.SplitImagesGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller {
    private static Controller controller;

    private MainFrame mf;
    private PDFConverter pdfConverter;
    private DocConverter docConverter;
    private SplitImagesGenerator splitImagesGenerator;

    private Controller(){
        mf = new MainFrame();
        configureFrame();
        mf.getSrcButton().addActionListener(e -> chooseSourceFile());
        mf.getDestButton().addActionListener(e -> chooseDestinationFolder());
        mf.getConvertButton().addActionListener(e -> convert());

        pdfConverter = PDFConverter.getInstance();
        docConverter = DocConverter.getInstance();
        splitImagesGenerator = SplitImagesGenerator.getInstance();
    }

    private void convert() {
        String srcFilePath = mf.getSrcField().getText();
        String destDirectoryPath = mf.getDestField().getText();

        if (srcFilePath.isEmpty() || destDirectoryPath.isEmpty()) {
            JOptionPane.showMessageDialog(mf,
                    "You need to fill all the fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        File srcFile = new File(srcFilePath);
        File destDirectory = new File(destDirectoryPath);

        // TODO: check if files exists

        String[] splits = srcFile.getName().split("\\.");
        String extension = splits[splits.length - 1];

        if (!extension.equals("pdf")) {
            File pdfFile = new File(destDirectory.getAbsolutePath() + "/" + splits[0] + ".pdf");
            try {
                pdfFile.createNewFile();
                docConverter.convert(srcFile, pdfFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            destDirectory = new File(destDirectory.getAbsolutePath() + "/imgs");
            destDirectory.mkdir();
            srcFile = pdfFile;
        }

        List<BufferedImage> images = null;
        try {
            images = pdfConverter.pdfToImages(srcFile, 1000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<BufferedImage> splittedImages = splitImagesGenerator.generateSplitImages(images);
        for (int i = 0; i < splittedImages.size(); i++) {
            BufferedImage img = splittedImages.get(i);
            try {
                ImageIO.write(img, "png", new File(destDirectory.getPath() + "/" + i + ".png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JOptionPane.showMessageDialog(mf,
                "Conversion completed",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        return;


    }

    private void configureFrame() {
        mf.setTitle("Giornalino Formatter");
        mf.getSrcField().setEnabled(false);
        mf.getDestField().setEnabled(false);
    }

    private void chooseSourceFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (!mf.getSrcField().getText().isEmpty())
            fileChooser.setCurrentDirectory(new File(mf.getSrcField().getText()));

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Doc/Docx/PDF", "doc", "docx", "pdf"));
        int r = fileChooser.showOpenDialog(mf);
        if (r == JFileChooser.APPROVE_OPTION) {
            mf.getSrcField().setText(fileChooser.getSelectedFile().getPath());
        }
    }

    private void chooseDestinationFolder() {
        JFileChooser fileChooser = new JFileChooser();
        if (!mf.getDestField().getText().isEmpty())
            fileChooser.setCurrentDirectory(new File(mf.getSrcField().getText()));

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = fileChooser.showOpenDialog(mf);
        if (r == JFileChooser.APPROVE_OPTION) {
            mf.getDestField().setText(fileChooser.getSelectedFile().getPath());
        }
    }

    public static Controller getInstance() {
        if (controller == null)
            controller = new Controller();
        return controller;
    }

    public void showWindow(){
        mf.setVisible(true);
    }
}
