package org.example;

import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.options.convert.PdfConvertOptions;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class DocConverter {
    private static final int MAX_PAGE = 3;
    private static DocConverter docConverter;

    private DocConverter(){}

    public static DocConverter getInstance() {
        if (docConverter == null)
            docConverter = new DocConverter();
        return docConverter;
    }

    private int getNumPages(File srcDocFile){
        String srcFilePath = srcDocFile.getAbsolutePath();
        Converter converter = new Converter(srcFilePath);
        converter.close();
        return converter.getDocumentInfo().getPagesCount();
    }

    private void convert(File srcDocFile, File destPdfFile, int startPage) {
        String srcFilePath = srcDocFile.getAbsolutePath();
        Converter conv = new Converter(srcFilePath);
        PdfConvertOptions options = new PdfConvertOptions();
        options.setPageNumber(startPage);
        options.setPagesCount(MAX_PAGE);
        conv.convert(destPdfFile.getAbsolutePath(), options);
        conv.close();
    }

    private void assemblePdf(List<File> parts, File destPdfFile) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(destPdfFile.getAbsolutePath());

        for (File f : parts) {
            pdfMerger.addSource(f);
        }

        pdfMerger.mergeDocuments(null);
    }

    public void convert(File srcDocFile, File destPdfFile) throws IOException {
        int numPages = getNumPages(srcDocFile);
        List<File> tempFiles = new LinkedList<>();
        for (int startPage = 1; startPage <= numPages; startPage += MAX_PAGE) {
            File f = File.createTempFile("123", ".pdf");
            f.deleteOnExit();
            convert(srcDocFile, f, startPage);
            tempFiles.add(f);
        }


        assemblePdf(tempFiles, destPdfFile);
    }
}
